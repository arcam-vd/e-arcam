/*
 * CyberAdminDataBean
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.arcam.cyberadmin.dom.core.AbstractReferenceEntity;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.service.core.ReferenceDataService;
import org.arcam.cyberadmin.ui.core.comparator.SelectItemComparator;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.type.MailingTypeEnum;
import org.arcam.cyberadmin.utils.DateHelper;

/**
 * The application bean contains all static reference data to be filled into combobox.
 * 
 * @author dtl
 * 
 */
@ManagedBean(name = "cyberAdminDataBean")
@SessionScoped
public class CyberAdminDataBean {
    
    // Service to collect all reference data from db.
    @ManagedProperty(value = "#{referenceDataService}")
    private ReferenceDataService referenceDataService;

    @ManagedProperty(value = "#{localeBean}")
    private LocaleBean localeBean;
    
    // Contains all country items to be used in whole user session.
    private List<SelectItem> allCountryItems;
    private List<SelectItem> communeItems;

    /**
     * Retrieve all country items to be filled into country combobox.
     * 
     * @return
     */
    public List<SelectItem> getCountryItems() {
        if (allCountryItems == null) {
            allCountryItems = buildSelectItems(referenceDataService.getAllCountry(localeBean.getLang()));
        }

        return allCountryItems;
    }
    
    /**
     * @return all available declaration types.
     */
    public List<SelectItem> getShortDeclarationTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DeclarationTypeEnum type : DeclarationTypeEnum.values()) {
            items.add(new SelectItem(type, FacesUtils
                    .getI18nEnumLabel(WebConstants.PREFIX_SHORT_DECLARATION_KEY, type)));
        }

        Collections.sort(items, new SelectItemComparator());
        return items;
    }

    public List<SelectItem> getCommunes() {
        if (communeItems == null) {
            communeItems = buildSelectItems(referenceDataService.getAllCommune());
        }
        return communeItems;
    }

    public List<SelectItem> getPeriodicites() {
        return buildEnumSelectItems(PeriodiciteTypeEnum.values(), WebConstants.PREFIX_PERIODCITE_KEY);
    }

    public List<SelectItem> getDeclarationTypes() {
        return buildEnumSelectItems(DeclarationTypeEnum.values(), WebConstants.PREFIX_DECLARATION_KEY);
    }

    public List<SelectItem> getExonerationTypes() {
        return buildEnumSelectItems(ExonerationTypeEnum.values(), WebConstants.PREFIX_SHORT_TYPE_EXONERATION);
    }
    
    public List<SelectItem> getUserTypes() {
        return buildEnumSelectItems(UserTypeEnum.values(), WebConstants.PREFIX_TYPE_USER);
    }
    
    public List<SelectItem> getMailingTypes() {
        return buildEnumSelectItems(MailingTypeEnum.values(), WebConstants.PREFIX_TYPE_MAILING);
    }
    
    public List<SelectItem> getUserTypesForCreation() {
        return buildEnumSelectItems(UserTypeEnum.getUserTypesForCreation(), WebConstants.PREFIX_TYPE_USER);
    }
    
    public List<SelectItem> getYears() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (int i = DateHelper.YEAR_START; i <= DateHelper.YEAR_END; i++) {
            selectItems.add(new SelectItem(i, String.valueOf(i)));
        }
        return selectItems;
    }
    
    public List<SelectItem> getMonths(PeriodiciteTypeEnum type) {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (Integer month : type.getStartingMonths()) {
            items.add(new SelectItem(month, FacesUtils.getMessage((WebConstants.PREFIX_MONTH_KEY + month))));
        }
        return items;
    }
    
    /**
     * Build list of select items from a collection of enums.
     * @param enumTypes
     * @param keyMsgPrefix
     * @return
     */
    public <T extends Enum<?>> List<SelectItem> buildEnumSelectItems(T[] enumTypes, String keyMsgPrefix) {
        List<SelectItem> items = new ArrayList<SelectItem>(enumTypes.length);
        for (Enum<?> item : enumTypes) {
            items.add(new SelectItem(item, FacesUtils.getI18nEnumLabel(keyMsgPrefix, item)));
        }
        return items;
    }
    
    /**
     * Build select items from a list of items which is an instance of AbstractReferenceEntity.
     * @param items
     * @return
     */
    public List<SelectItem> buildSelectItems(List<? extends AbstractReferenceEntity> items) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (AbstractReferenceEntity item : items) {
            selectItems.add(new SelectItem(item.getCode(), item.getText()));
        }
        return selectItems;

    }

    public void setReferenceDataService(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    public void setLocaleBean(LocaleBean localeBean) {
        this.localeBean = localeBean;
    }

}
