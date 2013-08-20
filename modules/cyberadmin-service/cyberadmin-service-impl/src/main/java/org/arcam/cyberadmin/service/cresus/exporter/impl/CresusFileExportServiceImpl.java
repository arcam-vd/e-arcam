/*
 * CresusFileExportServiceImpl.java
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

package org.arcam.cyberadmin.service.cresus.exporter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Map.Entry;

import org.arcam.cyberadmin.service.cresus.exception.ExportLockingException;
import org.arcam.cyberadmin.service.cresus.exporter.CresusFileExportService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.utils.CresusExportConstant;
import org.arcam.cyberadmin.utils.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The default implementation for export service {@link CresusFileExportService}.
 * 
 * @author mmn
 *
 */
@Service("cresusFileExportService")
public class CresusFileExportServiceImpl implements CresusFileExportService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${cresus.location}")    
    private String cresusLocation;
    
    @Value("${cresus.user}")
    private String cresusUser;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Export createExport(ExportParameter exportParameter) throws ExportLockingException, IOException {
        // make sure no one else is doing an export
        String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(filename);
        if (lockFile.exists()) {
            throw new ExportLockingException("There is one export process that has not finished yet "
                    + "as the lock file still exists in the exportation folder.");
        }
        
        // create the lock file
        if (!exportParameter.isTestrun()) {
            lockFile.createNewFile();
        }
        
        // create export id
        String exportId = DateHelper.now().getTime() + ""; 
        Export export = new Export();
        export.setExportId(exportId);
        export.setTestrun(exportParameter.isTestrun());
        return export;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream openTable(Export export, String exportName) throws IOException {
        logger.debug("Try to open an output stream for export " + exportName);
        
        OutputStream out = null;
        if (!export.isTestrun()) {
            String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR
                    + MessageFormat.format(CresusExportConstant.EXPORT_FILE, exportName, export.getExportId());
            out = new FileOutputStream(filename);
        }
        export.getExportedTables().put(exportName, out);
        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeTable(Export export, String exportName) throws IOException {
        logger.debug("Try to close an output stream for export " + exportName);
        // remove it from export
        OutputStream out = export.getExportedTables().get(exportName);
        export.getExportedTables().remove(exportName);
        
        String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR
                + MessageFormat.format(CresusExportConstant.EXPORT_FILE, exportName, export.getExportId());
        
        // then, close the output stream
        if (out != null) {
            out.flush();
            out.close();
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") < 0){
        	setExportfileOwner(filename);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeExport(Export export) throws IOException {
        if (export.isTestrun()) {
            return;
        }
        
        // delete lock file
        deleteLockFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortExport(Export export) throws IOException {
        if (export.isTestrun()) {
            return;
        }
        
        // delete csv files
        for (Entry<String, OutputStream> entry : export.getExportedTables().entrySet()) {
            // close the output stream
            OutputStream out = entry.getValue();
            out.flush();
            out.close();
            String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR
                    + MessageFormat.format(CresusExportConstant.EXPORT_FILE, entry.getKey(), export.getExportId());
            File exportFile = new File(filename);
            if (exportFile.exists()) {
                exportFile.delete();
            }
        }
        export.getExportedTables().clear();
        
        // delete lock file
        deleteLockFile();
    }
    
    private void deleteLockFile() {
        String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(filename);
        if (lockFile.exists()) {
            lockFile.delete();
        }
    }
    
    private void setExportfileOwner(String filename) throws IOException {    
        
        String[] commands = new String[]{"chown", cresusUser, filename};
    
        Process child = Runtime.getRuntime().exec(commands);
    }

}
