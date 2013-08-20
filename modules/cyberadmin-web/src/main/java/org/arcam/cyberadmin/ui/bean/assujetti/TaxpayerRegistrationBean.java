/*
 * TaxpayerRegistrationBean
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

package org.arcam.cyberadmin.ui.bean.assujetti;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.Utility;
import org.primefaces.event.SelectEvent;

/**
 * Managing all action for register taxpayer page.
 * 
 * @author dtl
 * 
 */
@ManagedBean(name = "taxpayerRegistrationBean")
@ViewScoped
public class TaxpayerRegistrationBean extends AbstractDetailBean<Assujetti>  {
    private static final String USERNAME_UNIQUE_MSG_KEY = "cyberadmin.user.username.notUnique";
    private static final String EMAIL_UNIQUE_MSG_KEY = "cyberadmin.user.email.notUnique";

    private static final String TO_CONFIRM_PAGE = "toConfirmPage";
    
    private static final long serialVersionUID = 1L;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    @ManagedProperty(value = "#{taxpayerService}")
    private TaxpayerService taxpayerService;
    
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    
    @Override
    protected void doAfterNewEntityInstance() {
        // Only create user and adresse object, not for person.
        // If also initialize the person object, when save Assujetti, it will automatically be cascaded
        // and save an empty Person object.
        User emptyUser = new User();
        emptyUser.setPerson(new Person());
        entity.setUser(emptyUser);
        entity.setAdresse(new Adresse());
        
        // Pre-set for default values of taxpayer object.
        entity.getAdresse().setPays(CyberAdminConstants.SWITZERLAND_CODE);
        
        // Waiting for Assujetti to confirm.
        entity.getUser().setActivated(false);
        entity.getUser().setArcam(false);
        entity.getUser().setUserType(UserTypeEnum.ASJ);
        entity.getUser().setValidated(false);
    }
    
    /**
     * Override from superclass to change the navigation after save successfully.
     * {@inheritDoc}
     */
    @Override
    public String saveEntity() {
        taxpayerService.saveAndLog(entity);
        
        // Send email to taxpayer to let him confirm the registration.
        // Push email from Adresse of Assujetti to transient property email of Assujetti to make mail service work.
        entity.setEmail(entity.getAdresse().getEmail());
        mailService.confirmRegistration(entity, Utility.getEmailFromUserDefaultIfEmpty(userService.getSystemUser()));
        return TO_CONFIRM_PAGE;
    }
    
    /**
     * Check data of current entity before doing a real saving to database. {@inheritDoc}
     */
    @Override
    protected boolean beforeSave() {
        if (!checkNpaSwitzerland()) {
            return false;
        }
        entity.getAdresse().setNpa(entity.getLocaliteSuggestion().getCode());
        User userInTaxpayer = entity.getUser();
        boolean errorInFields = checkUserInfo(userInTaxpayer, userService);
        if (errorInFields) {
            return false;
        }
        
//        // the Assujetti detail requires name + firstname OR organisation (but accept if all three)
//        // Not require all three        
//        Person person = userInTaxpayer.getPerson();
//        if ( person == null || (StringUtils.isBlank(person.getOrganisation())) ||
//                 (StringUtils.isBlank(person.getNom()) && StringUtils.isBlank(person.getPrenom()))) {
//        	//TODO HIGHLIGHT FIRSTNAME, LASTNAME AND ORGANISATIONS AS ERRORS        	
//            FacesUtils.getFacesContext().addMessage(
//                    null,
//                    FacesUtils.getI18nErrorMessage("cyberadmin.taxpayerDetail.mandatory.personInfo",
//                            FacesMessage.SEVERITY_ERROR));
//            return false;
//        }

        if (userInTaxpayer != null) {
            // Set value for fields before saving
            entity.getAdresse().setTelephone(userInTaxpayer.getTelephone());
            entity.getAdresse().setEmail(userInTaxpayer.getEmail());
            entity.setPerson(userInTaxpayer.getPerson());
            // Encoding password before save to db.
            userInTaxpayer.setPassword(Utility.encodePassword(userInTaxpayer.getPassword()));
        }
        return true;
    }
    
    private boolean checkNpaSwitzerland() {
        if (WebConstants.SWITZERLAND_CODE.equals(entity.getAdresse().getPays())) {
            Localite localite = localiteService.findLocaliteByNpa(entity.getLocaliteSuggestion().getCode());
            if (localite == null) {
                FacesUtils.getFacesContext().addMessage(
                        null,
                        FacesUtils.getI18nErrorMessage(WebConstants.FILLING_WRONG_NPA_LOCALITE_ERROR_KEY,
                                FacesMessage.SEVERITY_ERROR));
                return false;
            }
        }
        return true;
    }
    
    public void handleSelect(SelectEvent event) {
        Localite localite = (Localite) event.getObject();
        entity.getAdresse().fetchValuesFromLocalite(localite);
    }
    
    /**
     * Static method to check user information : email, username.
     * @param user
     * @param userService
     * @return the result.
     */
    public static boolean checkUserInfo(User user, UserService userService) {
        boolean errorInFields = false;
        if (user != null) { // Only count if current taxpayer contains an user information to be saved.
            if (userService.countByField("email", user.getEmail(), user.getId()) > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        FacesUtils.getI18nErrorMessage(EMAIL_UNIQUE_MSG_KEY, FacesMessage.SEVERITY_ERROR));
                
                errorInFields = true;
            }
            if (userService.countByField("username", user.getUsername(), user.getId()) > 0) {
                FacesContext.getCurrentInstance().addMessage(
                        null, FacesUtils.getI18nErrorMessage(USERNAME_UNIQUE_MSG_KEY,
                              FacesMessage.SEVERITY_ERROR));
                errorInFields = true;
            }
        }
        return errorInFields;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assujetti loadEntity(Long id) {
        // Don't need when register taxpayer.
        return null;
    }

    public void setTaxpayerService(TaxpayerService taxpayerService) {
        this.taxpayerService = taxpayerService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
