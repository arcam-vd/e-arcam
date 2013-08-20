/*
 * ExportTaxpayerService.java
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportDataCategoryEnum;
import org.arcam.cyberadmin.service.cresus.type.ExportInfo;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.service.cresus.type.UpdateResult;
import org.arcam.cyberadmin.service.cresus.writer.TaxpayerCSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service to export taxpayer information to Cresus.
 * 
 * @author mmn
 *
 */
@Service("exportTaxpayerService")
public class ExportTaxpayerService extends AbstractExportDataService {

    @Autowired
    private TaxpayerService taxpayerService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExportDataCategoryEnum getCategory() {
        return ExportDataCategoryEnum.TAXPAYER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ExportInfo> findDataToExportInternal(ExportParameter exportParameter) {
        List<ExportInfo> exportInfos = new ArrayList<ExportInfo>();
        
        log.debug("Load the updated taxpayer from DB.");
        Map<Integer, Integer> ids = taxpayerService.findUnexportedTaxpayerIds();
        for (Entry<Integer, Integer> entry : ids.entrySet()) {
            ExportInfo exportInfo = new ExportInfo();
            exportInfo.setIntId(entry.getKey().intValue());
            exportInfo.setStringId(entry.getKey().toString());
            exportInfo.setVersion(entry.getValue().intValue());
            exportInfos.add(exportInfo);
        }
        
        return exportInfos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareExportedTables(Export export) throws IOException {
        cresusFileExportService.openTable(export, getCategory().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cleanupExportedTables(Export export) throws IOException {
        cresusFileExportService.closeTable(export, getCategory().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Integer> exportDataInternal(Export export, List<ExportInfo> ids,
            ExportParameter exportParameter) throws IOException {
        // create CSV writer for taxpayer export
        Map<String, OutputStream> tables = export.getExportedTables();
        TaxpayerCSVWriter writer = null;
        
        int recordCount = 0;
        try {
            writer = new TaxpayerCSVWriter(tables.get(getCategory().getName()));
            if (exportParameter.hasCategory(getCategory())) {
                for (ExportInfo exportInfo : ids) {
                    // load the taxpayer object
                    Assujetti taxpayer = taxpayerService.findById(new Long(exportInfo.getIntId()));
                    writer.writeRecord(taxpayer);
                    recordCount++;
                }
            }
        } finally {
            close(writer);
        }
        
        Map<String, Integer> result = new HashMap<String, Integer>(1);
        result.put(getCategory().getName(), recordCount);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UpdateResult updateStatusInternal(List<ExportInfo> exportInfos) {
        UpdateResult result = new UpdateResult(getCategory().getName());
        
        List<Integer> successfulRecords = new ArrayList<Integer>(exportInfos.size());
        List<Integer> failedRecords = new ArrayList<Integer>(exportInfos.size());
        // retrieve taxpayer object and update its status
        for (ExportInfo exportInfo : exportInfos) {
            int id = exportInfo.getIntId();
            int updatedRow = taxpayerService.updateExportedTaxpayers(id, exportInfo.getVersion());
            if (updatedRow == 0) {
                failedRecords.add(id);
            } else {
                successfulRecords.add(id);
            }
        }
        
        result.setSuccessfulRecords(successfulRecords);
        result.setFailedRecords(failedRecords);
        return result;
    }

}
