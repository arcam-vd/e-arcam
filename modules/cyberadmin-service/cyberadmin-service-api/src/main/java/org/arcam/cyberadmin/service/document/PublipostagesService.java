/*
 * PublipostagesService.java
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

package org.arcam.cyberadmin.service.document;

import java.util.List;

import org.arcam.cyberadmin.criteria.business.GenerateMailingCriteria;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.service.core.GenericDataService;

/**
 * Interface for all Generate mailing service.
 * 
 * @author vtn
 * 
 */
public interface PublipostagesService extends GenericDataService<Assujetti> {

    /**
     * A list of unique Taxpayers that have at least an unexpired "a saisir" declaration for the current period.
     */
    List<Assujetti> getMailingPeriodicInvitation(GenerateMailingCriteria criteria);

    /**
     * A list of unique Taxpayers that have at least an expired "a saisir" declaration.
     */
    List<Assujetti> getMailingReminder(GenerateMailingCriteria criteria);

    /**
     * A list of unique ASJ user that have never logged on the application (LAST_LOG_ON is null) and have been created
     * by an admin .
     */
    List<Assujetti> getEnrolmentLetter(GenerateMailingCriteria criteria);
}
