/*
 * JournalListBean.java
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

package org.arcam.cyberadmin.ui.bean.journal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.criteria.business.JournalCriteria;
import org.arcam.cyberadmin.dom.authorisation.Log;
import org.arcam.cyberadmin.service.core.LogService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractFilterListBean;
import org.arcam.cyberadmin.ui.core.utils.CyberAdminBackingBeanUtils;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;
import org.primefaces.event.SelectEvent;

/**
 * Backing bean for taxpayer list screen.
 * 
 * @author mmn
 *
 */
@ManagedBean(name = "journalListBean")
@ViewScoped
public class JournalListBean extends AbstractFilterListBean<Log, JournalCriteria> {

    private static final String TEMPLATES_JOURNALS_EXPORT = "/templates/Journals.xls";

    private static final long serialVersionUID = 1L;

    private Log selectedItem;
    
    @ManagedProperty(value = "#{logService}")
    private LogService logService;
    
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    
    @Override
    public List<ColumnModel> getColumns() {
        // The data table on this screen is built manually (not use the <arcam:dataTable> tag).
        // Thus There is no need to defined column model.
        return new ArrayList<ColumnModel>(0);
    }

    @Override
    protected JournalCriteria instantiateCriteria() {
        return new JournalCriteria();
    }

    @Override
    protected List<Log> loadData(int startRow, int endRow) {
        return logService.findByCriteria(criteria, false);
    }

    @Override
    protected int countData() {
        return logService.countByCriteria(criteria);
    }
    
    @Override
    protected void enrichCriteria() {
        selectedItem = null;
    }
    
    @Override
    protected boolean validateCriteria() {
        // Convert from freeText to some of UserTypeEnum & JournalMessageTypeEnum.
        CyberAdminBackingBeanUtils.matchingUserAndJournalTypesFromResource(criteria);
        
        return super.validateCriteria();
    }

    @Override
    protected byte[] prepareDataToExport() {
        List<Log> journals = logService.findByCriteria(criteria, true);
        SheetDataDto sheetDeclaration = ExportUtils.prepareDataJournals(journals);
        InputStream input = getClass().getResourceAsStream(TEMPLATES_JOURNALS_EXPORT);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheetDeclaration), ExcelFormatTypeEnum.XLS);
        return out.toByteArray();

    }
    
    @Override
    protected String getExportFileName() {
        return WebConstants.EXPORTED_JOURNAL_FILENAME;
    }
    
    public void onRowSelect(SelectEvent event) {  
        selectedItem = (Log) event.getObject();
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public Log getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Log selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }

}
