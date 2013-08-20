/*
 * DeclarationToBillingBean
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

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.cresus.CresusExportService;
import org.arcam.cyberadmin.service.cresus.exception.ExportLockingException;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;

/**
 * Managing for all actions relating screen export for billing(14d).
 * 
 * @author dtl
 *
 */
@ManagedBean(name = "declarationToBillingBean")
@ViewScoped
public class DeclarationToBillingBean extends DeclarationListBean {
    
    private static final long serialVersionUID = 1L;
    
    @ManagedProperty(value = "#{cresusExportService}")
    private CresusExportService cresusExportService;
    
    @Override
    protected DeclarationCriteria instantiateCriteria() {
        DeclarationCriteria declarationCriteria = new DeclarationCriteria();
        declarationCriteria.setStatus(StatusTypeEnum.VALIDATED);
        return declarationCriteria;
    }
    
    public void exportCresus(ActionEvent event) {
        try {
            cresusExportService.export();
            FacesUtils.getFacesContext().addMessage(null,
                    FacesUtils.getI18nMessage("cyberadmin.billExport.exportSuccessfulMsg", 
                            FacesMessage.SEVERITY_INFO));
            search(event);
        } catch (ExportLockingException e) {
            logger.error("ExportLockingException happened during exporting", e);
            FacesUtils.getFacesContext().addMessage(null,
                    FacesUtils.getI18nErrorMessage("cyberadmin.billExport.cresusExportLockingException",
                            FacesMessage.SEVERITY_ERROR));
        } catch (IOException e) {
            logger.error("IOException happened during exporting", e);
            FacesUtils.getFacesContext().addMessage(null,
                    FacesUtils.getI18nErrorMessage("cyberadmin.billExport.cresusIOException", 
                            FacesMessage.SEVERITY_ERROR));
        }
    }

    public void setCresusExportService(CresusExportService cresusExportService) {
        this.cresusExportService = cresusExportService;
    }
    
}
