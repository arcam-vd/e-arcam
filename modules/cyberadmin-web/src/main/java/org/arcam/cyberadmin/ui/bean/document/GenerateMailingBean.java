/*
 * GenerateMailingBean.java
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
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.arcam.cyberadmin.criteria.business.GenerateMailingCriteria;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.service.document.PublipostagesService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractCyberAdminBean;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.type.MailingTypeEnum;

/**
 * Backing bean for execute the screen generate mailing.
 * 
 * @author vtn
 * 
 */

@ManagedBean(name = "generateMailingBean")
@ViewScoped
public class GenerateMailingBean  extends AbstractCyberAdminBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Link to file template mailing export.
     */
    private static final String TEMPLATES_MAILING_1_EXPORT = "/templates/Mailing_1_v0.1.xls";
    /**
     * Link to file template mailing export.
     */
    private static final String TEMPLATES_MAILING_2_EXPORT = "/templates/Mailing_2_v0.1.xls";

    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private boolean email;
    private boolean courier;
    private MailingTypeEnum mailingType = MailingTypeEnum.PERIODIC_INVITATION;
    
    @ManagedProperty(value = "#{publipostagesService}")
    private PublipostagesService publipostagesService;
    
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    
    public void export() {
        if (!validate()) {
            return;
        }

        ServletOutputStream out = null;
        String filename = null;
        if (isLetter()) {
            filename = WebConstants.EXPORTED_MAILING_ENROLMENT_FILENAME;
        } else {
            filename = WebConstants.EXPORTED_MAILING_FILENAME;
        }
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

    private boolean isLetter() {
        return mailingType == MailingTypeEnum.ENROLMENT_LETTER;
    }
    
    private boolean validate() {
        if (isInvitationOrReminder()) {
            if (!courier) {
                FacesUtils.getFacesContext().addMessage(
                        null,
                        FacesUtils.getI18nErrorMessage(WebConstants.GENERATE_MAILING_MANDATORY_MAIL,
                                FacesMessage.SEVERITY_ERROR));
                return false;
            }
        } else {
            if (!email && !courier) {
                FacesUtils.getFacesContext().addMessage(
                        null,
                        FacesUtils.getI18nErrorMessage(WebConstants.GENERATE_MAILING_MANDATORY_EMAIL_MAIL,
                                FacesMessage.SEVERITY_ERROR));
                return false;
            }
        }
        return true;
    }

    private byte[] prepareDataToExport() throws IOException {
        GenerateMailingCriteria criteria = new GenerateMailingCriteria(Assujetti.class);
        criteria.setEmail(email);
        criteria.setCourier(courier);
        List<Assujetti> assujettis = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SheetDataDto sheetDeclaration = null;
        InputStream input = null;
        switch (mailingType) {
        case PERIODIC_INVITATION:
            assujettis = publipostagesService.getMailingPeriodicInvitation(criteria);
            sheetDeclaration = ExportUtils.prepareDataMailingsInvitationReminder(assujettis);
            input = getClass().getResourceAsStream(TEMPLATES_MAILING_1_EXPORT);
            break;
        case REMINDER:
            assujettis = publipostagesService.getMailingReminder(criteria);
            sheetDeclaration = ExportUtils.prepareDataMailingsInvitationReminder(assujettis);
            input = getClass().getResourceAsStream(TEMPLATES_MAILING_1_EXPORT);
            break;
        case ENROLMENT_LETTER:
            assujettis = publipostagesService.getEnrolmentLetter(criteria);
            sheetDeclaration = ExportUtils.prepareDataMailingsEnrolmentLetter(assujettis);
            input = getClass().getResourceAsStream(TEMPLATES_MAILING_2_EXPORT);
            break;
        default:
            break;
        }
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheetDeclaration), ExcelFormatTypeEnum.XLS);
        return out.toByteArray();

    }

    public void refreshCanal(AjaxBehaviorEvent event) {
        if (isInvitationOrReminder()) {
            email = false;
        }
    }

    public boolean isInvitationOrReminder() {
        return mailingType == MailingTypeEnum.PERIODIC_INVITATION || mailingType == MailingTypeEnum.REMINDER;
    }
    
    public boolean isCourier() {
        return courier;
    }

    public void setCourier(boolean courier) {
        this.courier = courier;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public void setPublipostagesService(PublipostagesService publipostagesService) {
        this.publipostagesService = publipostagesService;
    }

    public MailingTypeEnum getMailingType() {
        return mailingType;
    }

    public void setMailingType(MailingTypeEnum mailingType) {
        this.mailingType = mailingType;
    }

    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }
}
