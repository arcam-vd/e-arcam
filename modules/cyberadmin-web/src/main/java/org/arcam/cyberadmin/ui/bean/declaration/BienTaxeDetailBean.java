/*
 * DemandeDeclarationBean.java
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

package org.arcam.cyberadmin.ui.bean.declaration;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.utils.DateHelper;
import org.primefaces.event.SelectEvent;

/**
 * Backing bean for demande Declaration.
 * 
 * @author vtn
 * 
 */
@ManagedBean(name = "bienTaxeDetailBean")
@ViewScoped
public class BienTaxeDetailBean extends AbstractDetailBean<BienTaxe> {

    /**
     * ARCANCYBERADM-92  browser "back" button issues
     * Session key fill mode using when user using the back button in browers.
     */
    public static final String FILL_MODE = "FILL_MODE";

    /**
     * ARCANCYBERADM-92  browser "back" button issues
     * Session key taxpayer id using when user using the back button in browers.
     */
    public static final String TAXPAYER_ID = "TAXPAYER_ID";

    private static final long serialVersionUID = 1L;

    /**
     * OutcomeID page new declration XHTML.
     */
    private static final String DECLARATION_DETAIL = "DECLARATION_DETAIL";
    private static final String TAXPAYER_DETAIL = "TAXPAYER_DETAIL";

    /**
     * Session key for bien taxe.
     */
    public static final String SESSION_DECLARATION_KEY = "declaration";
    
    /**
     * Using for fiscale date.
     */
    private int month;
    private int year = DateHelper.getYear(DateHelper.now());
    
    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    private Declaration declaration;

    private Boolean fillMode;
    
    public BienTaxeDetailBean() {
        FacesUtils.removeSessionMapValue(SESSION_DECLARATION_KEY);
    }

    @Override
    protected void doAfterNewEntityInstance() {
        declaration = declarationService.prepare(getAssujetti());
        entity = declaration.getBienTaxe();
        
        setAdresseDeFacturationDeclaration();
        setDefaultPeriodciteCodeBienTaxe();
        setCommuneCodeBienTaxe();
    }
    
    private Assujetti getAssujetti() {
        long assujettiId = -1;
        String taxpayerId = FacesUtils.getRequestParam("taxpayerId");
        if (StringUtils.isNotBlank(taxpayerId)) {
            FacesUtils.setSessionMapValue(TAXPAYER_ID, taxpayerId);
        } else {
            taxpayerId = (String) FacesUtils.getSessionMapValue(TAXPAYER_ID);
        }
        if (NumberUtils.isNumber(taxpayerId)) {
            assujettiId = Long.valueOf(taxpayerId).longValue();
        }
        
        if (getCurrentUser().isAssujetti()) {
            assujettiId = getCurrentUser().getUserInfo().getAssujetti().getId();
        }
        return userService.getAssujettiAndBienTaxes(assujettiId);
    }
    
    public void setAdresseDeFacturationDeclaration() {
        entity.getAdresse().setPays(WebConstants.SWITZERLAND_CODE);
    }

    public void setPeriodiciteCode() {
        DeclarationTypeEnum declarationType = entity.getDeclarationType();
        if (declarationType == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            entity.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        } else if (declarationType == DeclarationTypeEnum.LOCATION) {
            entity.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        } else {
            entity.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        }
        //reset the address
        entity.setLocaliteSuggestion(null);
        entity.getAdresse().setLocalite(null);
    }

    public void setDefaultPeriodciteCodeBienTaxe() {
        entity.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
    }

    public void setCommuneCodeBienTaxe() {
        if (getCurrentUser().isCommune()) {
            entity.setCommuneCode(getCurrentUser().getUserInfo().getCommuneCode());
        }
    }
    
    public void handleSelect(SelectEvent event) {
        Localite localite = (Localite) event.getObject();
        entity.getAdresse().fetchValuesFromLocalite(localite);
    }
    
    @Override
    public BienTaxe loadEntity(Long id) {
        return null;
    }
    
    private boolean validatePeriodDate(Date fiscaleDate, Date departDate) {
        if (fiscaleDate != null && departDate != null) {
            if (DateHelper.compareIgnoreTime(fiscaleDate, departDate) >= 0) {
                FacesMessage msg = FacesUtils.getI18nErrorMessage(WebConstants.INVALID_DATE_RANGE_ERROR_KEY,
                        FacesMessage.SEVERITY_ERROR);
                FacesUtils.getFacesContext().addMessage(null, msg);
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected boolean beforeSave() {
        if (!checkNpaSwitzerland()) {
            return false;
        }
        setFiscaleDate();
        return true;
    }
    
    @Override
    protected void afterSave() {
        //ARCANCYBERADM-92  browser "back" button issues
        FacesUtils.removeSessionMapValue(TAXPAYER_ID);
    }

    private void setFiscaleDate() {
        Date newFiscaleDate = declaration.getFiscaleDate();
        if (entity.getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            newFiscaleDate = DateHelper.getDate(1, Calendar.JANUARY, year);
        } else if (entity.getDeclarationType() != DeclarationTypeEnum.LOCATION) {
            newFiscaleDate = DateHelper.getDate(1, month, year);
        }
        declaration.setFiscaleDate(newFiscaleDate);
    }
    
    private boolean checkNpaSwitzerland() {
        Localite localite = localiteService.findLocaliteByNpa(entity.getLocaliteSuggestion().getCode());
        if (localite == null) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(WebConstants.FILLING_WRONG_NPA_LOCALITE_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            return false;
        }
        return true;
    }

    @Override
    public String saveEntity() {
        if (!validateData()) {
            return null;
        }
        setNpaForAdresseBienTaxe();
        declarationService.demand(declaration);
        return TAXPAYER_DETAIL;
    }

    private void setNpaForAdresseBienTaxe() {
        declaration.getBienTaxe().getAdresse().setNpa(entity.getLocaliteSuggestion().getCode());
    }

    public String fillDeclaration() {
        if (!checkNpaSwitzerland()) {
            return null;
        }
        if (!validateData()) {
            return null;
        }
        setFiscaleDate();
        setLocaliteSuggestionDeclaration();
        setLocaliteForSuggestionBienTaxe();
        FacesUtils.setSessionMapValue(SESSION_DECLARATION_KEY, declaration);
        declarationService.create(declaration);
        return DECLARATION_DETAIL;
    }

    private void setLocaliteSuggestionDeclaration() {
        Localite localiteSuggestion = new Localite();
        localiteSuggestion.setCode(declaration.getAdresse().getNpa());
        localiteSuggestion.setText(declaration.getAdresse().getLocalite());
        declaration.setLocaliteSuggestion(localiteSuggestion);
    }

    private void setLocaliteForSuggestionBienTaxe() {
        Localite localiteSuggestion = entity.getLocaliteSuggestion();
        entity.getAdresse().setNpa(localiteSuggestion.getCode());
        
        /*TODO: 
         * because of this code, when a new BienTaxe was created with a valid NPA, and a "new" locality,
         * the Localite table was updated with the new user provided value.
         * 
         * I did not see any side effects when I tested the creation of a new BienTaxe without this code
         * 
         *  i.e.: 
         *      - the db contains    1000 / Lausanne
         *      - the user types     1000
         *      - the users overrite the name:    Lausanne 1
         *      - The bien taxe is created with   Lausanne 1 (correct)
         *      - The Localite table now contains 1000 / Lausanne 1 (wrong!!)
         *
         *  if (localiteSuggestion != null) {
         *      localiteSuggestion.setText(entity.getAdresse().getLocalite());
         *  }
         *  
        */
        
    }
    
    private boolean validateData() {
        Date fiscaleDate = declaration.getFiscaleDate();
        Date departDate = declaration.getDepartDate();
        if (entity.getDeclarationType() == DeclarationTypeEnum.LOCATION) {
            return validatePeriodDate(fiscaleDate, departDate);
        }
        return true;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    public Boolean getFillMode() {
        if (fillMode == null) {
            String requestParam = FacesUtils.getRequestParam("fillMode");
            if (StringUtils.isBlank(requestParam)) {
                fillMode = (Boolean) FacesUtils.getSessionMapValue(FILL_MODE);
            } else {
                fillMode = Boolean.parseBoolean(requestParam);
                FacesUtils.setSessionMapValue(FILL_MODE, new Boolean(fillMode));
            }
        }
        return fillMode;
    }

    public void setFillMode(Boolean fillMode) {
        this.fillMode = fillMode;
    }
    
    public String getHelpText() {
        if ((getCurrentUser().isCommune() || getCurrentUser().isGestionnaire() || getCurrentUser().isAdministrator())
                && (BooleanUtils.isFalse(getFillMode()))) {
            return FacesUtils.getHelperMessage("cyberadmin.declarationDemand.help");
        }
        return FacesUtils.getHelperMessage("cyberadmin.declarationGeneral.help");
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}