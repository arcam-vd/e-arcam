/*
 * DeclarationToValidateBean
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

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.BooleanUtils;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.CollectionUtils;

/**
 * Managing all actions for screen << Validation factures >> (Screen 14c).
 * 
 * @author dtl
 *
 */
@ManagedBean(name = "declarationToValidateBean")
@ViewScoped
public class DeclarationToValidateBean extends DeclarationListBean {
    private static final String RELOAD_PAGE = "RELOAD_PAGE";

    private static final long serialVersionUID = 1L;
    
    private List<Declaration> currentShowingDeclarations;
    
    @Override
    protected DeclarationCriteria instantiateCriteria() {
        DeclarationCriteria declarationCriteria = new DeclarationCriteria();
        declarationCriteria.setStatus(StatusTypeEnum.BILLED);
        return declarationCriteria;
    }
    
    /**
     * Save the status of declarations corresponding with the user selection.
     * Copied the handler for exception from save method of AbstractDetailBean.
     * @return
     */
    public String saveDeclarationValidated() {
        try {
            changeDeclarationStatus(currentShowingDeclarations);
            // Do change status.
            getDeclarationService().saveAndLogDeclarations(currentShowingDeclarations);
            return RELOAD_PAGE;
        } catch (OptimisticLockingFailureException ex) {
            logger.error("OptimisticLockingFailureException happened during saving", ex);
            displayErrorMessage(AbstractDetailBean.CYBERADMIN_COMMON_OPTIMISTIC_LOCKING);
        } catch (StaleObjectStateException ex) {
            logger.error("StaleObjectStateException happened during saving", ex);
            displayErrorMessage(AbstractDetailBean.CYBERADMIN_COMMON_STALE_OBJECT_STATE);
        } catch (DataIntegrityViolationException ex) {
            logger.error("DataIntegrityViolationException happened during saving", ex);
            displayErrorMessage(AbstractDetailBean.CYBERADMIN_COMMON_DATA_INTEGRITY_VIOLATION);
        } catch (ConstraintViolationException ex) {
            logger.error("Unexpected error happened during saving", ex);
            displayErrorMessage(AbstractDetailBean.CYBERADMIN_COMMON_CONSTRAINT_VIOLATION);
        }
        return null;
    }
    
    private void displayErrorMessage(String msgKey) {
        FacesUtils.getFacesContext().addMessage(null,
                FacesUtils.getI18nErrorMessage(msgKey, FacesMessage.SEVERITY_ERROR));
    }
    
    /**
     * Collect the list of ids of declaration to prepare for updating status.
     * @param declarations
     * @param declarationIdsPaid
     * @param declarationIdsCancel
     * @param declarationIdsRefuse
     */
    private void changeDeclarationStatus(List<Declaration> declarations) {
        if (CollectionUtils.isEmpty(declarations)) {
            return;
        }
        
        // Push to the correct array.
        for (Declaration dec : declarations) {
            if (BooleanUtils.isTrue(dec.getNotPaid())) {
                dec.setStatus(StatusTypeEnum.NOT_PAID);
            } else if (BooleanUtils.isTrue(dec.getPaid())) {
                dec.setStatus(StatusTypeEnum.PAID);
            } else if (BooleanUtils.isTrue(dec.getCancelBilling())) {
                dec.setStatus(StatusTypeEnum.CANCELLED);
            } else if (BooleanUtils.isTrue(dec.getRefuseBilling())) {
                dec.setStatus(StatusTypeEnum.REFUSED);
            }
        }
    }

    @Override
    protected List<Declaration> loadData(int startRow, int endRow) {
        currentShowingDeclarations = super.loadData(startRow, endRow);
        return currentShowingDeclarations;
    }
}
