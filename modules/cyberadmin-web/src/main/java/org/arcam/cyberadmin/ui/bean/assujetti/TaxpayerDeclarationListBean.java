/*
 * TaxpayerDeclarationListBean.java
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractFilterListBean;
import org.arcam.cyberadmin.ui.core.utils.CyberAdminBackingBeanUtils;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;
import org.arcam.cyberadmin.utils.DateHelper;

/**
 * Bean for search declarationi in taxpayer.
 * 
 * @author vtn
 * 
 */
@ManagedBean(name = "taxpayerDeclarationListBean")
@ViewScoped
public class TaxpayerDeclarationListBean extends AbstractFilterListBean<Declaration, DeclarationCriteria> {

    private static final long serialVersionUID = 1L;

    //private static final String TAXPAYER_DETAIL = "TAXPAYER_DETAIL";

    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;
    
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    
    private Assujetti assujetti;
    
    @Override
    public List<ColumnModel> getColumns() {
        // The data table on this screen is built manually (not use the <arcam:dataTable> tag).
        // Thus There is no need to defined column model.
        return new ArrayList<ColumnModel>(0);
    }

    @Override
    protected DeclarationCriteria instantiateCriteria() {
        DeclarationCriteria criteria = new DeclarationCriteria();
        CyberAdminUserDetail currentUser = (CyberAdminUserDetail) getCurrentUser();
        criteria.setCurrentUser(currentUser.getUserInfo());
        return criteria;
    }

    @Override
    protected List<Declaration> loadData(int startRow, int endRow) {
        return declarationService.findTaxpayerDeclarationByCriteria(criteria, assujetti, false);
    }

    @Override
    protected int countData() {
        return declarationService.countTaxpayerDeclarationByCriteria(criteria, assujetti);
    }
    
    @Override
    protected byte[] prepareDataToExport() {
        List<Declaration> declarations = declarationService
                .findTaxpayerDeclarationByCriteria(criteria, assujetti, true);
        for (Declaration dec : declarations) {
            declarationService.calculateTax(dec);
        }
        
        SheetDataDto sheetDeclaration = ExportUtils.prepareDataDeclarations(declarations);
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
    
    @Override
    protected boolean validateCriteria() {
        // Convert from freeText to some of declaration type & declaration status.
        CyberAdminBackingBeanUtils.matchingDeclarationAndStatusTypesFromResource(criteria);
        
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
    
    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    public Assujetti getAssujetti() {
        return assujetti;
    }

    public void setAssujetti(Assujetti assujetti) {
        this.assujetti = assujetti;
    }

    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }

}
