/*
 * AbstractExportDataService.java
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

package org.arcam.cyberadmin.service.cresus.data.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.arcam.cyberadmin.service.cresus.data.ExportDataService;
import org.arcam.cyberadmin.service.cresus.exporter.CresusFileExportService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportInfo;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.service.cresus.type.UpdateResult;
import org.arcam.cyberadmin.service.cresus.writer.AbstractCSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The abstract implementation for {@link ExportDataService}.
 * 
 * @author mmn
 *
 */
public abstract class AbstractExportDataService implements ExportDataService {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected CresusFileExportService cresusFileExportService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExportInfo> findDataToExport(ExportParameter exportParameter) {
        if (!exportParameter.hasCategory(getCategory())) {
            return new ArrayList<ExportInfo>(0);
        }
        return findDataToExportInternal(exportParameter);
    }
    
    protected abstract List<ExportInfo> findDataToExportInternal(ExportParameter exportParameter);

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> exportData(Export export, List<ExportInfo> ids, ExportParameter exportParameter) 
            throws IOException {
        // calls FileExportService.openTable to get the output streams for the related tables
        prepareExportedTables(export);
        
        // performs exportation
        Map<String, Integer> result = exportDataInternal(export, ids, exportParameter);
        
        // calls FileExportService.closeTable to close the output streams for the related tables
        cleanupExportedTables(export);
        
        return result;
    }

    protected abstract void prepareExportedTables(Export export) throws IOException;
    
    protected abstract void cleanupExportedTables(Export export) throws IOException;
    
    protected abstract Map<String, Integer> exportDataInternal(Export export, List<ExportInfo> ids, 
            ExportParameter exportParameter) throws IOException;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final UpdateResult updateStatus(List<ExportInfo> exportInfos, ExportParameter exportParameter) {
        if (exportParameter.isKeepStatus() || exportParameter.isTestrun()
                || !exportParameter.hasCategory(getCategory())) {
            return null;
        }
        return updateStatusInternal(exportInfos);
    }

    protected abstract UpdateResult updateStatusInternal(List<ExportInfo> exportInfos);
    
    protected void close(AbstractCSVWriter<?> csvWriter) {
        if (csvWriter != null) {
            try {
                csvWriter.close();
            } catch (IOException e) {
                log.error("Close CSV Writer error", e);
            }
        }
    }
    
    protected ExportInfo createExportInfo(Entry<Integer, Integer> idAndVersion) {
        ExportInfo exportInfo = new ExportInfo();
        exportInfo.setIntId(idAndVersion.getKey().intValue());
        exportInfo.setStringId(exportInfo.getIntId() + "");
        exportInfo.setVersion(idAndVersion.getValue());
        
        return exportInfo;
    }

}
