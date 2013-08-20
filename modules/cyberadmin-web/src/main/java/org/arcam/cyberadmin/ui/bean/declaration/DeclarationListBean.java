/*
 * DeclarationListBean.java
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria.SearchMode;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.reference.Commune;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractFilterListBean;
import org.arcam.cyberadmin.ui.bean.utilisateur.AccountValidationListBean;
import org.arcam.cyberadmin.ui.core.comparator.SelectItemComparator;
import org.arcam.cyberadmin.ui.core.utils.CyberAdminBackingBeanUtils;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;
import org.arcam.cyberadmin.utils.DateHelper;

/**
 * Backing bean for declaration list screen.
 * 
 * @author vtn, mmn
 * 
 */
@ManagedBean(name = "declarationListBean")
@ViewScoped
public class DeclarationListBean extends AbstractFilterListBean<Declaration, DeclarationCriteria> {

    private static final long serialVersionUID = 1L;
    
    private static final String PREDEFINED_FILTER_PARAM_NAME = "type";
    private static final String FILLED_FILTER = "filled";
    private static final String TO_FILL_FILTER = "toFill";
    private static final String LATE_FILTER = "late";
    
    // statistic information for GES & ADM
    private int filledDeclarationCount;
    private int toFilledDeclarationCount;
    private int newTaxpayerCount;
    
    // statistic information for ASJ
    private int asjToFilledDecCount;
    private int asjCurrentToFilledDecCount;
    
    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;
    
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    // Keep all exported declarations to update status of them after export to billing.
    private List<Declaration> declarationsToExport;
    
    private String screenFilter;
    
    @Override
    protected DeclarationCriteria instantiateCriteria() {
        CyberAdminUserDetail currentUser = getCurrentUser();
        
        DeclarationCriteria criteria = new DeclarationCriteria();
        if (currentUser.isGestionnaire() || currentUser.isAdministrator()) {
            // get the predefined filter from request params
            String filter = getScreenFilter();
            if (FILLED_FILTER.equals(filter)) {
                criteria.setStatus(StatusTypeEnum.FILLED);
            } else if (TO_FILL_FILTER.equals(filter)) {
                criteria.setStatus(StatusTypeEnum.TO_FILLED);
            } else if (LATE_FILTER.equals(filter)) {
                criteria.setStatus(StatusTypeEnum.TO_FILLED);
                criteria.setTo(DateHelper.today());
            }
        }
        
        return criteria;
    }
    
    @Override
    protected List<Declaration> loadData(int startRow, int endRow) {
        // determine the kind of searching
        determineSearchingMode();
        return declarationService.findDeclarationByCriteria(criteria, false);
    }

    @Override
    protected boolean validateCriteria() {
        criteria.getDeclarationTypes().clear();
        criteria.getStatusTypes().clear();
        if (getCurrentUser().isAssujetti() || getCurrentUser().isCommune()) {
            // Convert from freeText to some of UserTypeEnum & JournalMessageTypeEnum.
            CyberAdminBackingBeanUtils.matchingDeclarationAndStatusTypesFromResource(criteria);
        }
        
        if (criteria.getFrom() != null && criteria.getTo() != null) {
            if (DateHelper.compareIgnoreTime(criteria.getFrom(), criteria.getTo()) >= 0) {
                FacesMessage msg = FacesUtils.getI18nErrorMessage(WebConstants.INVALID_DATE_RANGE_ERROR_KEY,
                        FacesMessage.SEVERITY_ERROR);
                FacesUtils.getFacesContext().addMessage(null, msg);
                return false;
            }
        }
        
        return super.validateCriteria();
    }

    private void determineSearchingMode() {
        CyberAdminUserDetail currentUser = getCurrentUser();
        criteria.setCurrentUser(currentUser.getUserInfo());
        
        if (currentUser.isAssujetti()) {
            criteria.setSearchMode(SearchMode.QUICK);
        } else if (currentUser.isCommune()) {
            criteria.setSearchMode(SearchMode.QUICK);
        } else if (currentUser.isGestionnaire() || currentUser.isAdministrator()) {
            criteria.setSearchMode(SearchMode.FULL);
        }
    }

    @Override
    protected int countData() {
        determineSearchingMode();
        return declarationService.countDeclarationByCriteria(criteria);
    }

    @Override
    public List<ColumnModel> getColumns() {
        // The data table on this screen is built manually (not use the <arcam:dataTable> tag).
        // Thus There is no need to defined column model.
        return new ArrayList<ColumnModel>(0);
        
    }

    @Override
    protected void resetCriteria() {
        if (getCurrentUser().isAssujetti()) {
            // reset the status critera as the ASJ can not filter on status except this link
            criteria.setStatus(null);
        }
    }
    
    /**
     * @return all available communes in DB.
     */
    public List<SelectItem> getCommunes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        List<Commune> communes = referenceDataService.getAllCommune();
        for (Commune commune : communes) {
            items.add(new SelectItem(commune.getCode(), commune.getText()));
        }
        return items;
    }

    /**
     * @return all available declaration types.
     */
    public List<SelectItem> getTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DeclarationTypeEnum type : DeclarationTypeEnum.values()) {
            items.add(new SelectItem(type, FacesUtils
                    .getI18nEnumLabel(WebConstants.PREFIX_SHORT_DECLARATION_KEY, type)));
        }
        
        Collections.sort(items, new SelectItemComparator());
        return items;
    }
    
    /**
     * @return all available declaration statuses.
     */
    public List<SelectItem> getStatuses() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (StatusTypeEnum type : StatusTypeEnum.values()) {
            items.add(new SelectItem(type, FacesUtils.getI18nEnumLabel("cyberadmin.common.declaration.status", type)));
        }
        
        Collections.sort(items, new SelectItemComparator());
        return items;
    }

    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }
    
    public DeclarationService getDeclarationService() {
        return this.declarationService;
    }
 
    public String getNotificationHeader() {
        return FacesUtils.getMessage("cyberadmin.common.msg.msgTitle", getCurrentUser().getFullname());
    }
    
    public int getFilledDeclarationCount() {
        return filledDeclarationCount;
    }
    
    public int getToFilledDeclarationCount() {
        return toFilledDeclarationCount;
    }
    
    public int getNewTaxpayerCount() {
        return newTaxpayerCount;
    }

    public int getAsjToFilledDecCount() {
        return asjToFilledDecCount;
    }

    public int getAsjCurrentToFilledDecCount() {
        return asjCurrentToFilledDecCount;
    }

    @Override
    protected byte[] prepareDataToExport() {
        declarationsToExport = declarationService.findDeclarationByCriteria(criteria, true);
        for (Declaration dec : declarationsToExport) {
            declarationService.calculateTax(dec);
        }
        
        SheetDataDto sheetDeclaration = ExportUtils.prepareDataDeclarations(declarationsToExport);
        InputStream input = getClass().getResourceAsStream(WebConstants.TEMPLATES_DECLARATIONS_EXPORT);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheetDeclaration), ExcelFormatTypeEnum.XLS);
        return out.toByteArray();
    }

    public void exportDeclaration() {
        this.export();
    }
    
    @Override
    protected String getExportFileName() {
        return WebConstants.EXPORTED_DECLARATION_FILENAME;
    }
    
    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }
    
    /**
     * Shows only FILLED declarations.
     * 
     * @param event
     */
    public void showFilledDeclarations(ActionEvent event) {
        criteria = new DeclarationCriteria();
        criteria.setStatus(StatusTypeEnum.FILLED);
        search(event);
    }
    
    /**
     * Shows only TO_FILLED declarations.
     * 
     * @param event
     */
    public void showToFilledDeclarations(ActionEvent event) {
        criteria = new DeclarationCriteria();
        criteria.setStatus(StatusTypeEnum.TO_FILLED);
        criteria.setTo(DateHelper.today());
        search(event);
    }
    
    public void showAsjToFilledDeclarations(ActionEvent event) {
        criteria = new DeclarationCriteria();
        criteria.setStatus(StatusTypeEnum.TO_FILLED);
        search(event);
    }
    
    public void showAsjCurrentToFilledDeclarations(ActionEvent event) {
        criteria = new DeclarationCriteria();
        criteria.setStatus(StatusTypeEnum.TO_FILLED);
        criteria.setTo(DateHelper.today());
        search(event);
    }
    
    /**
     * This method is called when 'PreRenderView' to get information for notification area.
     * 
     * @param event
     */
    public void countNotificationInfo(ComponentSystemEvent event) {
        CyberAdminUserDetail currentUser = getCurrentUser();
        
        if (currentUser.isGestionnaire() || currentUser.isAdministrator()) {
            filledDeclarationCount = declarationService.countFilledDeclarations();
            toFilledDeclarationCount = declarationService.countCurrentToFilledDeclarations();
            newTaxpayerCount = userService.countByCriteria(AccountValidationListBean.buildCriteria());
        } else if (currentUser.isAssujetti()) {
            asjToFilledDecCount = declarationService.countToFilledDeclarationsFor(currentUser.getUserInfo());
            asjCurrentToFilledDecCount = declarationService.countCurrentToFilledDeclarationFor(currentUser
                    .getUserInfo());
        }
    }
    
    private static final String FIRST_PLACEHOLDER = "{0}";
    private static final String SECOND_PLACEHOLDER = "{1}";
    
    public String getAsjNotificationPart1() {
        String message = FacesUtils.getMessage("cyberadmin.declarationList.toFilledDeclarationCountMsg");
        return StringUtils.split(message, FIRST_PLACEHOLDER)[0];
    }
    
    public String getAsjNotificationPart2() {
        String message = FacesUtils.getMessage("cyberadmin.declarationList.toFilledDeclarationCountMsg");
        return StringUtils.substring(message, message.indexOf(FIRST_PLACEHOLDER) + FIRST_PLACEHOLDER.length(),
                message.indexOf(SECOND_PLACEHOLDER));
    }
    
    public String getAsjNotificationPart3() {
        String message = FacesUtils.getMessage("cyberadmin.declarationList.toFilledDeclarationCountMsg");
        return StringUtils.substring(message, message.indexOf(SECOND_PLACEHOLDER) + SECOND_PLACEHOLDER.length());
    }

    public List<Declaration> getDeclarationsToExport() {
        return declarationsToExport;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getScreenFilter() {
        if (screenFilter == null) {
            // Get the predefined filter from request params
            screenFilter = FacesUtils.getRequestParam(PREDEFINED_FILTER_PARAM_NAME);
        }
        return screenFilter;
    }

    public void setScreenFilter(String screenFilter) {
        this.screenFilter = screenFilter;
    }
}
