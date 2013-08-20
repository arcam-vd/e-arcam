/*
 * CresusFileExportService.java
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

package org.arcam.cyberadmin.service.cresus.exporter;

import java.io.IOException;
import java.io.OutputStream;

import org.arcam.cyberadmin.service.cresus.exception.ExportLockingException;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;


/**
 * A service for transferring files to the Cresus.
 * 
 * @author mmn
 *
 */
public interface CresusFileExportService {
    
    /**
     * Starts an export and make sure no one else is doing an export; this can be achieved by writing a lock file; the
     * returned export ID is the date in format yyyyMMdd; an exception should be thrown if someone else is already
     * exporting (lock file exists).
     * 
     * @param exportParameter
     * @return
     * @throws ExportLockingException
     *             if there is a *.lock file.
     * @throws IOException
     *             if the system can not access to the Cresus export storage.
     */
    Export createExport(ExportParameter exportParameter) throws ExportLockingException, IOException;
    
    /**
     * Creates a file '<exportname>_<exportid>.txt'.
     * 
     * @param export
     * @param exportName
     * @throws IOException .
     * @return
     */
    OutputStream openTable(Export export, String exportName) throws IOException;
    
    /**
     * Finishes the export of a table - for now, this just closes the stream and removes it from the export.
     * 
     * @param export
     * @param exportName
     * @throws IOException .
     */
    void closeTable(Export export, String exportName) throws IOException;
    
    /**
     * Deletes the lock file. 
     * 
     * @param export
     * @throws IOException .
     */
    void closeExport(Export export) throws IOException;
    
    /**
     * Deletes all files related to the export and the lock file.
     * 
     * @param export
     * @throws IOException .
     */
    void abortExport(Export export) throws IOException;
    
}
