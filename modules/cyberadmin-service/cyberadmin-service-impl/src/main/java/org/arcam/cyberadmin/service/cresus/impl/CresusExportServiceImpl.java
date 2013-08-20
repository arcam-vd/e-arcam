/*
 * CresusExportServiceImpl.java
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

package org.arcam.cyberadmin.service.cresus.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.arcam.cyberadmin.service.cresus.CresusExportService;
import org.arcam.cyberadmin.service.cresus.data.ExportDataService;
import org.arcam.cyberadmin.service.cresus.exception.ExportLockingException;
import org.arcam.cyberadmin.service.cresus.exporter.CresusFileExportService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportDataCategoryEnum;
import org.arcam.cyberadmin.service.cresus.type.ExportInfo;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.service.cresus.type.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mmn
 *
 */
@Service("cresusExportService")
public class CresusExportServiceImpl implements CresusExportService, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CresusFileExportService cresusFileExportService;
    @Autowired
    @Qualifier("exportBillingService")
    private ExportDataService exportBillingService; 
    @Autowired
    @Qualifier("exportTaxpayerService")
    private ExportDataService exportTaxpayerService;
    
    private List<ExportDataService> exportDataServices = new ArrayList<ExportDataService>();
    
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Map<String, Integer> export() throws ExportLockingException, IOException {
        logger.info("Begin Cresus export.");
        
        ExportParameter parameter = new ExportParameter();
        Export export = null;
        Map<String, Integer> result = new HashMap<String, Integer>();

        try {
            // IMPORTANT: There should rather be three separate loops for findDataToExport, exportData and updateStatus
            // (this was the reason for making 4 methods in the export service rather than just 1; 
            // otherwise, the entities of the first export service get updated very soon, 
            // but get committed at the end, so there is a big risk of optimistic locking problems)
            
            // a map cotains list of export ids grouped by data category
            Map<ExportDataCategoryEnum, List<ExportInfo>> groupedIdsToExport =
                    new HashMap<ExportDataCategoryEnum, List<ExportInfo>>(exportDataServices.size());
            
            // Step 1: find data to export for each service, filter data according to parameters
            findDataToExport(parameter, groupedIdsToExport);
            
            // Step 2: create export
            export = cresusFileExportService.createExport(parameter);
            
            // Step 3: export data for each service
            result = exportData(parameter, export, groupedIdsToExport);
            
            // Step 4: close export
            cresusFileExportService.closeExport(export);
            
            // Step 5: update the status of exported entities
            updateStatus(parameter, groupedIdsToExport);
            
        } catch (ExportLockingException e) {
            handleExportException(export, e);
            throw e;
        } catch (IOException e) {
            handleExportException(export, e);
            throw e;
        }
        
        return result;
    }
    
    private void findDataToExport(ExportParameter parameter,
            Map<ExportDataCategoryEnum, List<ExportInfo>> groupedIdsToExport) {
        for (ExportDataService service : exportDataServices) {
            groupedIdsToExport.put(service.getCategory(), service.findDataToExport(parameter));
        }
    }
    
    private Map<String, Integer> exportData(ExportParameter parameter, Export export,
            Map<ExportDataCategoryEnum, List<ExportInfo>> groupedIdsToExport) throws IOException {
        
        Map<String, Integer> result = new HashMap<String, Integer>(exportDataServices.size());
        for (ExportDataService service : exportDataServices) {
            Map<String, Integer> exportResult =
                    service.exportData(export, groupedIdsToExport.get(service.getCategory()), parameter);
            // log export result
            for (Entry<String, Integer> entry : exportResult.entrySet()) {
                String message = "Export " + entry.getKey() + ": " + entry.getValue() + " record(s)";
                logger.debug(message);
            }
            result.putAll(exportResult);
        }
        return result;
    }
    
    private void handleExportException(Export export, Exception e) {
        logger.error(e.getMessage(), e);
        if (export != null) {
            try {
                cresusFileExportService.abortExport(export);
            } catch (IOException e1) {
                logger.error("IOException while trying to abort the current export.");
            }
        }
    }

    private void updateStatus(ExportParameter parameter,
            Map<ExportDataCategoryEnum, List<ExportInfo>> groupedIdsToExport) {
        for (ExportDataService service : exportDataServices) {
            UpdateResult updateResult =
                    service.updateStatus(groupedIdsToExport.get(service.getCategory()), parameter);
            // log the update status result
            if (updateResult != null) {
                logger.debug("Update status result for " + updateResult.getExportName());
                logger.debug("Successful records: " + updateResult.getSuccessfulRecords());
                logger.debug("Failed records " + updateResult.getFailedRecords());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        exportDataServices.add(exportBillingService);
        exportDataServices.add(exportTaxpayerService);
    }

}
