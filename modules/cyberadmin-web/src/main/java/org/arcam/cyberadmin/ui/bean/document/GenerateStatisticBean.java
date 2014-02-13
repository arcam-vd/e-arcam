/*
 * GenerateStatisticBean.java
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

package org.arcam.cyberadmin.ui.bean.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.arcam.cyberadmin.criteria.business.GenerateStatisticCriteria;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.document.StatisticService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractCyberAdminBean;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.utils.DateHelper;

/**
 * Backing bean for execute generating statistic.
 * 
 * @author vtn
 * 
 */
@ManagedBean(name = "generateStatisticBean")
@RequestScoped
public class GenerateStatisticBean extends AbstractCyberAdminBean implements Serializable {

    private static final String TEMPLATES_STATISTICS_EXPORT = "/templates/Statistics_v0.3.xls";

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{statisticService}")
    private StatisticService statisticSerive;
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;
    
    private DeclarationTypeEnum declarationType;

    private String communeCode;

    private Date fromDate = DateHelper.getFirstDateOfCurrentYear();
    private Date toDate = DateHelper.now();

    public GenerateStatisticBean() {
        if (getCurrentUser().isCommune()) {
            communeCode = getCurrentUser().getUserInfo().getCommuneCode();
        }
    }
    
    public void exportStatistic() {
        ServletOutputStream out = null;
        String filename = WebConstants.EXPORTED_STATISTIC_FREFIX_FILENAME
                + DateHelper.getFormattedDate(new Date());
        if (declarationType != null) {
            filename += "_" + declarationType.name();
        }
        filename += WebConstants.EXCEL_TYPE;
        
        try {
            HttpServletResponse res = (HttpServletResponse) FacesUtils.getFacesContext().getExternalContext()
                    .getResponse();
            res.setContentType(WebConstants.MIME_TYPE_EXCEL);
            res.setHeader("Content-disposition", "attachment; filename=" + filename);
            Cookie cookie = new Cookie(screenUUID, "true");
            res.addCookie(cookie);
            out = res.getOutputStream();
            out.write(prepareDataToExport());
            out.flush();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            logger.error("Cannot export the list of declaration ", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private byte[] prepareDataToExport() throws IOException {
        GenerateStatisticCriteria criteria = new GenerateStatisticCriteria();
        criteria.setDeclarationTypeEnum(declarationType);
        criteria.setCommuneCode(communeCode);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        List<Declaration> declarations = statisticSerive.generateDeclarationStatistic(criteria);
        for (Declaration dec : declarations) {
            declarationService.calculateTax(dec);
        }
        
        SheetDataDto sheetDeclaration = ExportUtils.prepareDataGenerateStatistic(declarations);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream input = getClass().getResourceAsStream(TEMPLATES_STATISTICS_EXPORT);
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheetDeclaration), ExcelFormatTypeEnum.XLS);
        return out.toByteArray();
    }

    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    public DeclarationTypeEnum getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(DeclarationTypeEnum declarationType) {
        this.declarationType = declarationType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }

    public void setStatisticSerive(StatisticService statisticSerive) {
        this.statisticSerive = statisticSerive;
    }

    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

}
