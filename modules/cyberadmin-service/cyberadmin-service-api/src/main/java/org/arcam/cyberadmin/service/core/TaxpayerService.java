/*
 * TaxpayerService.java
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
import java.util.Map;

import org.arcam.cyberadmin.criteria.business.TaxpayerCriteria;
import org.arcam.cyberadmin.dom.business.Assujetti;

/**
 * Inteface for all taxpayer services.
 * 
 * @author mmn
 *
 */
public interface TaxpayerService extends GenericDataService<Assujetti> {

    /**
     * Finds all taxpayers which match the given criteria.
     * 
     * @param criteria the taxpayer criteria.
     * @return list of declarations.
     */
    List<Assujetti> findByCriteria(TaxpayerCriteria criteria, boolean isExport);
    
    /**
     * Counts the number of taxpayers which match the given criteria.
     * 
     * @param criteria the taxpayer criteria.
     * @return
     */
    int countByCriteria(TaxpayerCriteria criteria);
    
    Assujetti prepare();
    
    /**
     * Saves or updates the given taxpayer into database and then log the changes into LOG table.
     * 
     * @param assujetti
     * @return the up-to-date taxpayer.
     */
    Assujetti saveAndLog(Assujetti assujetti);
    
    Assujetti load(long id);
    
    /**
     * Finds all taxpayers who have UPDATED = TRUE.
     * 
     * @return a map of taxpayer ids and their version numbers.
     */
    Map<Integer, Integer> findUnexportedTaxpayerIds();
    
    /**
     * Updates the flag 'updated' to false for all exported taxpayer.
     * 
     * @param id
     *            the taxpayer's id
     * @param version
     *            the version number.
     * @return number of success row.
     */
    int updateExportedTaxpayers(Integer id, Integer version);
}

