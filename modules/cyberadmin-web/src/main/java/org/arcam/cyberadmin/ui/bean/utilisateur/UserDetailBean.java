/*
 * UserDetailBean
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

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.ui.bean.assujetti.TaxpayerRegistrationBean;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.Utility;
import org.primefaces.event.SelectEvent;

/**
 * Managing all action relating to user detail screen.
 * 
 * @author dtl
 * 
 */

@ManagedBean(name = "userDetailBean")
@ViewScoped
public class UserDetailBean extends AbstractDetailBean<User> {
    private static final String AUTH_ERROR_PAGE = "../authError.xhtml";

    private static final String TO_VALIDATE_USERS = "TO_VALIDATE_USERS";

    private static final String TO_LIST_USERS = "TO_LIST_USERS";

    private static final long serialVersionUID = 1L;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    private boolean editMode = false;
    private boolean viewMode = false;
    private boolean userProfileMode = false;
    
    // Keep the old Localite of current editing user. This will be used to restore npa value if 
    // user cancels the current editing action.
    private Localite oldLocalite;

    /**
     * {@inheritDoc}
     */
    @Override
    public String saveEntity() {
        if (entity.getAssujetti() != null) {
            entity.getAssujetti().getAdresse().setNpa(entity.getLocaliteSuggestion().getCode());
        }
        userService.saveAndLog(entity);
        if (editMode) {
            setScreenMode(false/* edit = false */, true/* view = true */);
            // Save successfully, then update oldLocalite to keep the newest value.
            oldLocalite = entity.getLocaliteSuggestion();
            // Stay at current page in edit mode.
            return "";
        }
        
        // Build navigation key.
        if (entity.getUserType() == UserTypeEnum.ASJ) {
            return TO_VALIDATE_USERS;
        }
        return TO_LIST_USERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User loadEntity(Long id) {
        setScreenMode(false/* edit = false */, true/* view= true */);
        User userObj = userService.findById(id);
        userObj.setProtectedPassword(userObj.getPassword());
        updateLocaliteSuggestion(userObj);
        
        setLocaliteSuggestion(userObj);

        return userObj;
    }
    
    /**
     * @param userObj
     */
    private void updateLocaliteSuggestion(User userObj) {
        if (userObj.getAssujetti() != null) {
            // Only set value for localiteSuggestion when user is an ASJ.
            oldLocalite = localiteService.findLocaliteByNpa(userObj.getAssujetti().getAdresse().getNpa());
            userObj.setLocaliteSuggestion(oldLocalite);
        }
    }
    
    public void cancelEditingUser() {
        entity.setLocaliteSuggestion(oldLocalite);
        
        // Restore npa value from the previous change but not submit yet.
        if (entity.getAssujetti() != null) {
            
            entity.getAssujetti().getAdresse().fetchValuesFromLocalite(oldLocalite);
        }
        setScreenMode(false/* edit = false */, true /* view = true */);
    }

    public void deactivateUser() {
        entity.setActivated(!entity.getActivated());
        this.save();
    }

    @Override
    protected void doAfterNewEntityInstance() {

        // Initialize person and taxpayer object in User.
        entity.setPerson(new Person());
        Assujetti taxPayer = new Assujetti();
        taxPayer.setAdresse(new Adresse());
        taxPayer.setUser(entity);
        taxPayer.getAdresse().setPays(CyberAdminConstants.SWITZERLAND_CODE);
        entity.setAssujetti(taxPayer);

        // Set default value.
        entity.setUserType(UserTypeEnum.ASJ);
        entity.setActivated(true);
        setScreenMode(false/* edit mode = false */, false/* view mode = false */);
    }

    public void setScreenMode(boolean isEditMode, boolean isViewMode) {
        editMode = isEditMode;
        viewMode = isViewMode;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Used when open current user detail screen.
     * @throws IOException 
     */
    public void populateEntityForUserProfile() throws IOException {
        // Check that current user is not navigating to an invalid user.
        if (getCurrentUser().getUserInfo().getId() != id) {
            FacesUtils.getFacesContext().getExternalContext().redirect(AUTH_ERROR_PAGE);
            return;
        }
        
        // If user is eligible.
        userProfileMode = true;
        super.populateEntity();
    }

    /**
     * Check data of current entity before doing a real saving to database. {@inheritDoc}
     */
    @Override
    protected boolean beforeSave() {
        if (!checkNpaSwitzerland()) {
            return false;
        }
        // Using the same business checking in TaxpayerRegistrationBean
        boolean haveError = TaxpayerRegistrationBean.checkUserInfo(entity, userService);
        if (haveError) {
            return false;
        }

        if (userProfileMode) {
            if (!entity.getPassword().equals(entity.getProtectedPassword())) {
                entity.setPassword(Utility.encodePassword(entity.getProtectedPassword()));
                entity.setProtectedPassword(entity.getPassword());
            }
            entity.setAdminCreated(true);
        }
        
        // Could change password in creation mode or edit user profile mode.
        if (!editMode && !viewMode) {
            // This is in creation mode, encode password before save to db.
            // In other cases, don't need to do because password can not be changed in GUI.
            entity.setPassword(Utility.encodePassword(entity.getPassword()));
            entity.setProtectedPassword(entity.getPassword());
            entity.setAdminCreated(true);
        }

        // User is not a ASJ, then remove field Assujetti in User before saving.
        if (entity.getUserType() != UserTypeEnum.COM) {
            // Don't set communeCode for user if it is not COM user.
            entity.setCommuneCode(null);
        }
        if (entity.getUserType() != UserTypeEnum.ASJ) {
            entity.setAssujetti(null);
            // COM, GES,ADM user all are validated by default.
            entity.setValidated(true);
        } else {
            // Assujetti and User share the same person object.
            entity.getAssujetti().setPerson(entity.getPerson());
            // Also reset arcam field from previous selection when choose other userTypes differing from ASJ.
            entity.setArcam(false);
            
            // Transfer email, telephone from user to adresse in Assujetti.
            entity.getAssujetti().getAdresse().setEmail(entity.getEmail());
            entity.getAssujetti().getAdresse().setTelephone(entity.getTelephone());
        }
        
        return true;
    }
    
    private boolean checkNpaSwitzerland() {
        if (entity.getUserType() != UserTypeEnum.ASJ) {
            return true;
        }        
        if (WebConstants.SWITZERLAND_CODE.equals(entity.getAssujetti().getAdresse().getPays())) {
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
    
    private void setLocaliteSuggestion(User user) {
        if (user.getAssujetti() != null) {
            Localite localiteSuggestion = new Localite();
            localiteSuggestion.setCode(user.getAssujetti().getAdresse().getNpa());
            localiteSuggestion.setText(user.getAssujetti().getAdresse().getLocalite());
            user.setLocaliteSuggestion(localiteSuggestion);
            oldLocalite = localiteSuggestion;
        }
    }
    
    /**
     * Process the selected localite in autocomplete component.
     * @param event : event sent from component after select one item.
     */
    public void handleSelect(SelectEvent event) {
        Localite localite = (Localite) event.getObject();
        if (entity.getAssujetti() != null) {
            entity.getAssujetti().getAdresse().fetchValuesFromLocalite(localite);
        }
    }
    
    public String getHelpText() {
        if (getEntity().isPersisted()) {
            return FacesUtils.getHelperMessage("cyberadmin.userDetail.help.view");
        }
        return FacesUtils.getHelperMessage("cyberadmin.userDetail.help.new");
    }
    
    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isUserProfileMode() {
        return userProfileMode;
    }

    public void setUserProfileMode(boolean userProfileMode) {
        this.userProfileMode = userProfileMode;
    }

}
