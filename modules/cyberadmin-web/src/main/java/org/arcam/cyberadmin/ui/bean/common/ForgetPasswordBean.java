/*
 * ForgetPasswordBean
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

package org.arcam.cyberadmin.ui.bean.common;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.Utility;

/**
 * Managing for actions in screen 1a,1b.
 * @author dtl
 *
 */
@ManagedBean(name = "forgetPasswordBean")
@ViewScoped
public class ForgetPasswordBean extends AbstractDetailBean<User> {
    private static final String TO_PASSWORD_CONFIRM = "TO_PASSWORD_CONFIRM";

    private static final long serialVersionUID = 1L;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    
    private String email;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String saveEntity() {
        userService.saveAndLog(entity);
        return TO_PASSWORD_CONFIRM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User loadEntity(Long id) {
        // Not used.
        return null;
    }
    
    /**
     * Reset the forgotten password for user with provided email.
     */
    public String resetPassword() {
        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(email);
        // Only activated user is processed in this screen.
        criteria.setActivated(true);
        List<User> users = userService.findByCriteria(criteria, false);
        if (CollectionUtils.isNotEmpty(users)) {
            entity = users.get(0);
            // Retrieve a new password for user.
            String newPassword = RandomStringUtils.random(CyberAdminConstants.AUTO_GENERATED_PASSWORD_LENGTH, true,
                    true);
            entity.setPassword(Utility.encodePassword(newPassword));
            
            // Send mail to user with the new password.
            mailService.newPassword(entity, Utility.getEmailFromUserDefaultIfEmpty(userService.getSystemUser()),
                    newPassword);
            return this.save();
        }
        return TO_PASSWORD_CONFIRM;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}
