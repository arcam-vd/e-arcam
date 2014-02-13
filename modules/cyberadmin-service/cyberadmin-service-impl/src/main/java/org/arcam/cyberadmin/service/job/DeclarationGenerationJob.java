/*
 * DeclarationGenerationJob.java
 * 
 * Project: Cyber Admin
 * 
  * 
 * Copyright (c) 2013, ARCAM - Association de la Region Cossonay-Aubonne-Morges
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification,are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * Neither the name of the ARCAM - Association de la Region 
 * Cossonay-Aubonne-Morges nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package org.arcam.cyberadmin.service.job;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.service.core.BienTaxeService;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.listener.ScheduleTaskEndEvent;
import org.arcam.cyberadmin.service.listener.ScheduleTaskStartEvent;
import org.arcam.cyberadmin.utils.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Batch job to generate 'TO_FILLED' declaration for all bien taxes in the application.
 * </p>
 * This batch is configured running every 1st day of the month.
 * 
 * @author mmn
 * 
 */
@Service
public class DeclarationGenerationJob implements ApplicationEventPublisherAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationGenerationJob.class);
    
    @Autowired
    private BienTaxeService bienTaxeService;
    @Autowired
    private DeclarationService declarationService;
    
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * <p>
     * The main task of the batch job. This method is configured without transaction and relies on the backend service
     * one. The task will process all bientaxe in database one by one. If there is one item fail, it will log the reason
     * and object's information before treating the next one.
     * </p>
     */
    @Scheduled(cron = "${job.declarationGeneration.cron}")
    public void generateDeclarations() {
        LOGGER.debug("Start DeclarationGenerationJob.");
        eventPublisher.publishEvent(new ScheduleTaskStartEvent(this));
        
        Set<Long> ids = bienTaxeService.getPeriodicityBientaxeIds();
        
        Set<Long> successIds = new HashSet<Long>(ids.size());
        Set<Long> failedIds = new HashSet<Long>(ids.size());
        for (Long id : ids) {
            try {
                LOGGER.debug(String.format("Processing BienTaxe [id=%s]: ", id));
                
                BienTaxe bienTaxe = bienTaxeService.findByIdWithDeclarationList(id);
                if (CollectionUtils.isEmpty(bienTaxe.getDeclarations())) {
                    LOGGER.error(String.format("The BienTaxe [id=%s] contains no declaration.", id));
                    continue;
                }
                
                Declaration lastDec = bienTaxe.getDeclarations().get(0);
                if (DateHelper.compareIgnoreTime(DateHelper.today(),
                        DateUtils.addMonths(lastDec.getFiscaleDate(), lastDec.getPeriodiciteCode().getMonths())) >= 0) {
                    LOGGER.debug(String.format("Automatically generate a declaration for BienTaxe [id=%s]", id));
                    
                    Declaration declaration = generateDeclaration(lastDec);
                    declarationService.demand(declaration, false);
                }
                
                successIds.add(id);
            } catch (Exception e) {
                LOGGER.error(String.format("There is a problem while processing BienTaxe [id=%s]", id), e);
                failedIds.add(id);
            }
        }
        
        LOGGER.debug("List of success bien taxe ids: " + successIds);
        LOGGER.debug("List of failed bien taxe ids: " + failedIds);
        
        eventPublisher.publishEvent(new ScheduleTaskEndEvent(this));
        LOGGER.debug("End DeclarationGenerationJob.");
    }
    
    private Declaration generateDeclaration(Declaration previousDec) {
        Declaration declaration = new Declaration();
        declaration.setFiscaleDate(DateHelper.getMonthStart(DateHelper.now()));
        declaration.setAdresse(previousDec.getAdresse().clone());
        declaration.setTaille(previousDec.getTaille());
        declaration.setExoneration(previousDec.getExoneration());
        BienTaxe bienTaxe = previousDec.getBienTaxe();
        declaration.setBienTaxe(bienTaxe);
        
        // determine periodicity of the newly declaration
        if (bienTaxe.getPeriodiciteCode().getStartingMonths()
                .contains(DateHelper.getMonth(declaration.getFiscaleDate()))) {
            declaration.setPeriodiciteCode(bienTaxe.getPeriodiciteCode());
        } else {
            declaration.setPeriodiciteCode(previousDec.getPeriodiciteCode());
        }
        
        bienTaxe.getDeclarations().add(declaration);
        
        return declaration;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
    
}
