/*
 * ReminderJob.java
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

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.listener.ScheduleTaskEndEvent;
import org.arcam.cyberadmin.service.listener.ScheduleTaskStartEvent;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Batch job sends reminders of types 'onDemand' and 'onRemind' for the taxpayer who has related declaration.
 * </p>
 * This batch is configured running every day.
 * 
 * @author mmn
 * 
 */
@Service
public class ReminderJob implements ApplicationEventPublisherAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderJob.class);
    
    @Value("${job.secondReminder.period}")
    private int period;
    @Autowired
    private DeclarationService declarationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    
    private ApplicationEventPublisher eventPublisher;
    
    @Scheduled(cron = "${job.reminder.cron}")
    public void remindDueDate() {
        LOGGER.debug("Start ReminderJob.");
        eventPublisher.publishEvent(new ScheduleTaskStartEvent(this));
        
        User sysUser = userService.getSystemUser();
        String from = Utility.getEmailFromUserDefaultIfEmpty(sysUser);
        
        Set<Long> toFillDeclarationIds = declarationService.getToFillDeclarationIds();
        sendReminders(toFillDeclarationIds, from, sysUser);
        
        eventPublisher.publishEvent(new ScheduleTaskEndEvent(this));
        LOGGER.debug("End ReminderJob.");
    }

    private void sendReminders(Set<Long> declarationIds, String from, User sysUser) {
        for (Long id : declarationIds) {
            try {
                LOGGER.debug(String.format("Processing declaration[id=%s]...", id));
                
                Declaration declaration = declarationService.load(id);
                Date fiscaleDate = declaration.getFiscaleDate();
                
                String message = null;
                Date today = DateHelper.today();
                
                // sending notification of type 'declaration.onDemand'
                if (declaration.getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
                    // RESIDENCE_SECONDAIRE: 'declaration.onDemand' reminder is sent on Feb 1st.
                    if (DateHelper.compareIgnoreTime(DateUtils.addMonths(fiscaleDate, 1), today) == 0) {
                        message = mailService.demandDeclaration(declaration, from);
                    }
                   
                } else {
                    // other declarations: 'declaration.onDemand' reminder is sent on FISCALE_DATE + PERIODICITY.
                    if (declaration.getPeriodiciteCode() != PeriodiciteTypeEnum.NONE) {
                        if (DateHelper.compareIgnoreTime(DateUtils.addMonths(
                                fiscaleDate, declaration.getPeriodiciteCode().getMonths()), today) == 0) {
                            message = mailService.demandDeclaration(declaration, from);
                        }
                        
                    } else if (declaration.getDepartDate() != null 
                            && DateHelper.compareIgnoreTime(declaration.getDepartDate(), today) == 0) {
                        message = mailService.demandDeclaration(declaration, from);
                    }
                    
                }
                
                // sending notification of type 'declaration.onRemind'
                if (declaration.getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
                    // RESIDENCE_SECONDAIRE: reminder is sent on April 1st and June 1st
                    if (DateHelper.compareIgnoreTime(DateUtils.addMonths(fiscaleDate, 3), today) == 0 
                            || DateHelper.compareIgnoreTime(DateUtils.addMonths(fiscaleDate, 5), today) == 0) {
                        message = mailService.remindOverdueDeclaration(declaration, from);
                    }
                    
                } else if (declaration.getDueDate() != null) {
                    // other declarations: reminder is sent on dueDate & dueDate + 10
                    Date dueDate = declaration.getDueDate();
                    if (DateHelper.compareIgnoreTime(dueDate, today) == 0 
                            || DateHelper.compareIgnoreTime(DateUtils.addDays(dueDate, period), today) == 0) {
                        message = mailService.remindOverdueDeclaration(declaration, from);
                    }
                }
                
                logMailContent(message, from, sysUser, declaration);
                
            } catch (Exception e) {
                LOGGER.error(String.format("There is a problem while processing declaration[id=%s]", id), e);
            }
        }
    }
    
    private void logMailContent(String message, String from, User user, Declaration declaration) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        
        // log the mail content in the comments for the declaration
        Commentaire comment = new Commentaire();
        comment.setDeclaration(declaration);
        comment.setMessage(message);
        comment.setTimestamp(DateHelper.now());
        comment.setUser(user);
        declaration.getCommentaires().add(comment);
        
        // save the comment
        declarationService.saveOrUpdate(declaration);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
    
}
