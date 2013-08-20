/*
 * ExportDataService.java
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

package org.arcam.cyberadmin.service.cresus.data;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportDataCategoryEnum;
import org.arcam.cyberadmin.service.cresus.type.ExportInfo;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.service.cresus.type.UpdateResult;

/**
 * A service for exporting different kind of data to Cresus.
 * 
 * @author mmn
 *
 */
public interface ExportDataService {
    /**
     * @return the IDs and optimistic locking version number (if available) for the elements to export.
     * 
     * @param exportParameter
     */
    List<ExportInfo> findDataToExport(ExportParameter exportParameter);

    /**
     * Creates CSVWriter for all concerned tables, loop over the control operations, passes, lists and points and write
     * each of them using the CSVWriter.
     * 
     * @param export
     * @param ids
     * @param exportParameter
     * @throws IOException
     *             when the system can not write to the CSV file.
     */
    Map<String, Integer> exportData(Export export, List<ExportInfo> ids, ExportParameter exportParameter) 
            throws IOException;
    
    /**
     * Updates the DB to tell that the elements have been exported (in general, this means setting the exportDate); for
     * entities with a optimistic locking version number, if it has changed (this means that a user has edited it during
     * export), the entity shouldn't be flagged as exported, so that it will be exported again during the next run.
     * 
     * @param exportInfos
     * @param exportParameter
     * @return
     */
    UpdateResult updateStatus(List<ExportInfo> exportInfos, ExportParameter exportParameter);
    
    /**
     * Gets the data category which the service is responsible for.
     * 
     * @return
     */
    ExportDataCategoryEnum getCategory();
}
