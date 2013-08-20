/*
 * DueDateReminderJob.java
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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Batch job sends due date reminder for the taxpayer who has overdue declaration.
 * </p>
 * This batch is configured running every day.
 * 
 * @author mmn
 * 
 */
@Service
public class DueDateReminderJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(DueDateReminderJob.class);
    
    @Value("${job.secondReminder.period}")
    private int period;
    @Autowired
    private DeclarationService declarationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    
    @Scheduled(cron = "${job.reminder.cron}")
    public void remindDueDate() {
        User sysUser = userService.getSystemUser();
        String from = Utility.getEmailFromUserDefaultIfEmpty(sysUser);
        
        // get first time reminder declarations ids
        List<Long> overdueDeclarationIds = declarationService.getOverdueDeclarationIds(0);
        sendReminders(overdueDeclarationIds, from, sysUser);
        
        overdueDeclarationIds = declarationService.getOverdueDeclarationIds(period);
        sendReminders(overdueDeclarationIds, from, sysUser);
    }

    private void sendReminders(List<Long> declarationIds, String from, User sysUser) {
        for (Long id : declarationIds) {
            try {
                LOGGER.debug("Processing Declaration [id=" + id + "]: ");
                
                Declaration declaration = declarationService.load(id);
                
                String message = mailService.remindOverdueDeclaration(declaration, from);
                if (StringUtils.isNotBlank(message)) {
                    // log the mail content in the comments for the declaration
                    Commentaire comment = new Commentaire();
                    comment.setDeclaration(declaration);
                    comment.setMessage(message);
                    comment.setTimestamp(DateHelper.now());
                    comment.setUser(sysUser);
                    declaration.getCommentaires().add(comment);
                }
                
                // save the comment
                declarationService.saveOrUpdate(declaration);
                
            } catch (Exception e) {
                LOGGER.error("There is a problem while processing Declaration [id=" + id + "]", e);
            }
        }
    }
    
}
