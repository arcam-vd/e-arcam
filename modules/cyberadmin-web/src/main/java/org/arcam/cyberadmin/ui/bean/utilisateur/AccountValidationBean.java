/*
 * AccountValidationBean
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

package org.arcam.cyberadmin.ui.bean.utilisateur;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.utils.Utility;

/**
 * Managing all actions relating to validate accounts screen.
 * @author dtl
 *
 */
@ManagedBean(name = "accountValidationBean")
@ViewScoped
public class AccountValidationBean extends AbstractDetailBean<User>  {
    private static final String TO_LIST_ACC_TO_VALIDATED = "TO_LIST_ACC_TO_VALIDATED";
    private static final long serialVersionUID = 1L;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    @ManagedProperty(value = "#{taxpayerValidationListBean}")
    private TaxpayerValidationListBean taxpayerValidationListBean;
    
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String saveEntity() {
        userService.saveAndLog(entity);
        return TO_LIST_ACC_TO_VALIDATED;
    }
    
    public String associateWithSelectedTaxpayer() {
        // Validate the current user.
        entity.setValidated(true);
        userService.associateWithSelectedTaxpayer(entity, taxpayerValidationListBean.getSelectedTaxpayer());
        
        // Send email to notify for taxpayer to know his account has been validated.
        mailService.informValidatedAccount(entity.getAssujetti(),
                Utility.getEmailFromUserDefaultIfEmpty(userService.getSystemUser()));
        return TO_LIST_ACC_TO_VALIDATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User loadEntity(Long id) {
        return userService.findById(id);
    }
    
    public String validateAccount() {
        entity.setValidated(true);
        
        // Send email to notify for taxpayer to know his account has been validated.
        mailService.informValidatedAccount(entity.getAssujetti(),
                Utility.getEmailFromUserDefaultIfEmpty(userService.getSystemUser()));
        return this.save();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public TaxpayerValidationListBean getTaxpayerValidationListBean() {
        return taxpayerValidationListBean;
    }

    public void setTaxpayerValidationListBean(TaxpayerValidationListBean taxpayerValidationListBean) {
        this.taxpayerValidationListBean = taxpayerValidationListBean;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
    
}
