/*
 * LogService.java
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

package org.arcam.cyberadmin.service.core;

import java.util.List;

import org.arcam.cyberadmin.criteria.business.JournalCriteria;
import org.arcam.cyberadmin.dom.authorisation.Log;
import org.arcam.cyberadmin.dom.core.Auditable;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;

/**
 * Interface for all logging services.
 * 
 * @author mmn
 *
 */
public interface LogService extends GenericDataService<Log> {

    /**
     * Finds all log which match the given criteria.
     * 
     * @param criteria the taxpayer criteria.
     * @return list of declarations.
     */
    List<Log> findByCriteria(JournalCriteria criteria, boolean isExport);
    
    /**
     * Counts the number of logs which match the given criteria.
     * 
     * @param criteria the taxpayer criteria.
     * @return
     */
    int countByCriteria(JournalCriteria criteria);
    
    /**
     * <p>
     * Logs the changes (creation or modification) in the given new auditable object into LOG table. <br />
     * <b>Note:</b> in case the old one is null, that is the creation case.
     * </p>
     * 
     * @param newObject
     * @param oldObject
     *            the old one from database (can be null in creation case).
     */
    void logModification(Auditable newObject, Auditable oldObject);
    
    
    /**
     * Manually log for changing on the status of declaration.
     * @param decId declarationId to be changed.
     * @param fromStatus the status of declaration before change.
     * @param toStatus the status to be changed to.
     */
    void logForChangingDeclarationStatus(Long decId, StatusTypeEnum fromStatus, StatusTypeEnum toStatus);
}
