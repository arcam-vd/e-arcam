/*
 * TaxpayerDetailBean.java
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
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.reference.Country;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.ui.bean.common.LocaleBean;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.primefaces.event.SelectEvent;

/**
 * Backing bean for detail taxpayer.
 * 
 * @author vtn
 * 
 */
@ManagedBean(name = "taxpayerDetailBean")
@ViewScoped
public class TaxpayerDetailBean extends AbstractDetailBean<Assujetti> {

    private static final long serialVersionUID = 1L;

    private static final String TAXPAYER_LIST = "TAXPAYER_LIST";

    @ManagedProperty(value = "#{taxpayerService}")
    protected TaxpayerService taxpayerService;
    
    @ManagedProperty(value = "#{taxpayerDeclarationListBean}")
    private TaxpayerDeclarationListBean taxpayerDeclarationListBean;
    
    @ManagedProperty(value = "#{localeBean}")
    private LocaleBean localeBean;
    
    // Keep the old Localite of current editing user. This will be used to restore npa value if 
    // user cancels the current editing action.
    private Localite oldLocalite;
    

    private boolean modeView;
    private boolean modeEdit;
    private boolean modeNew = true;
    
    @Override
    protected void doAfterNewEntityInstance() {
        entity = taxpayerService.prepare();
        changeModeView(false, false, true);
    }

    @Override
    protected boolean beforeSave() {
        if (!checkNpaSwitzerland()) {
            return false;
        }
        // the Assujetti detail requires name + firstname OR organisation (but accept if all three)
        // Not require all three
        
        boolean personOk = false;
        Person person = entity.getPerson();
        if(StringUtils.isBlank(person.getOrganisation())){
        	 if(StringUtils.isNotBlank(person.getNom()) && StringUtils.isNotBlank(person.getPrenom())){
        		 personOk = true; 
        	 }      		 
        } else {
        	if(StringUtils.isNotBlank(person.getNom()) && StringUtils.isNotBlank(person.getPrenom())){
        		personOk = true;
	       	} else if(StringUtils.isBlank(person.getNom()) && StringUtils.isBlank(person.getPrenom())) {
	       		personOk = true;
	       	}      	
        }
        if(!personOk){
    		FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage("cyberadmin.taxpayerDetail.mandatory.personInfo",
                            FacesMessage.SEVERITY_ERROR));
            return false;	       	   
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
    
    @Override
    public String saveEntity() {
        entity.getAdresse().setNpa(entity.getLocaliteSuggestion().getCode());
        oldLocalite = entity.getLocaliteSuggestion();
        taxpayerService.saveAndLog(entity);
        // change mode form edit to view
        changeModeView(true, false, false);
        return TAXPAYER_LIST;
    }

    @Override
    public Assujetti loadEntity(Long id) {
        changeModeView(true, false, false);
        Assujetti assujetti = taxpayerService.load(id);
        taxpayerDeclarationListBean.setAssujetti(assujetti);
        assujetti.setLocaliteSuggestion(localiteService.findLocaliteByNpa(assujetti.getAdresse().getNpa()));
        String pays = assujetti.getAdresse().getPays();
        Country country = referenceDataService.getCountry(pays, localeBean.getLang());
        assujetti.getAdresse().setCountry(country.getText());
        
        setLocaliteSuggestion(assujetti);

        return assujetti;
    }

    private void setLocaliteSuggestion(Assujetti assujetti) {
        Localite localiteSuggestion = new Localite();
        localiteSuggestion.setCode(assujetti.getAdresse().getNpa());
        localiteSuggestion.setText(assujetti.getAdresse().getLocalite());
        assujetti.setLocaliteSuggestion(localiteSuggestion);
        oldLocalite = localiteSuggestion;
    }

    public void cancel() {
        entity.setLocaliteSuggestion(oldLocalite);
        // Restore npa value from the previous change but not submit yet.
        entity.getAdresse().fetchValuesFromLocalite(oldLocalite);
        // change mode form edit to view
        changeModeView(true, false, false);
    }
    
    public void changeModeEditToView(ActionEvent event) {
        if (!checkNpaSwitzerland()) {
            return;
        }
        save();
        // update country for entity
        String pays = entity.getAdresse().getPays();
        Country country = referenceDataService.getCountry(pays, localeBean.getLang());
        entity.getAdresse().setCountry(country.getText());
    }

    public void changeModeViewToEdit(ActionEvent event) {
        changeModeView(false, true, false);
    }
    
    private void changeModeView(boolean isModeView, boolean isModeEdit, boolean isModeNew) {
        this.modeView = isModeView;
        this.modeEdit = isModeEdit;
        this.modeNew = isModeNew;
    }
    
    public void handleSelect(SelectEvent event) {
        Localite localite = (Localite) event.getObject();
        entity.getAdresse().fetchValuesFromLocalite(localite);
    }

    public String getHelpText() {
        if (getEntity().isPersisted()) {
            return FacesUtils.getHelperMessage("cyberadmin.taxpayerDetail.help.view");
        }
        return FacesUtils.getHelperMessage("cyberadmin.taxpayerDetail.help.new");
    }
    
    public void setTaxpayerService(TaxpayerService taxpayerService) {
        this.taxpayerService = taxpayerService;
    }

    public boolean isModeView() {
        return modeView;
    }

    public void setModeView(boolean modeView) {
        this.modeView = modeView;
    }

    public boolean isModeEdit() {
        return modeEdit;
    }

    public void setModeEdit(boolean modeEdit) {
        this.modeEdit = modeEdit;
    }

    public boolean isModeNew() {
        return modeNew;
    }

    public void setModeNew(boolean modeNew) {
        this.modeNew = modeNew;
    }

   
    public TaxpayerDeclarationListBean getTaxpayerDeclarationListBean() {
        return taxpayerDeclarationListBean;
    }

    public void setTaxpayerDeclarationListBean(TaxpayerDeclarationListBean taxpayerDeclarationListBean) {
        this.taxpayerDeclarationListBean = taxpayerDeclarationListBean;
    }

    public void setLocaleBean(LocaleBean localeBean) {
        this.localeBean = localeBean;
    }
}
