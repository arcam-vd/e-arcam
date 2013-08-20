/*
 * CresusFileExportServiceTest.java
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
import java.io.FileFilter;
import java.io.OutputStream;

import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.service.cresus.exception.ExportLockingException;
import org.arcam.cyberadmin.service.cresus.exporter.CresusFileExportService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.utils.CresusExportConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A unit test class for {@link CresusFileExportServiceImpl}.
 * 
 * @author mmn
 *
 */
@Test
public class CresusFileExportServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private CresusFileExportService cresusFileExportService;
    @Value("${cresus.location}")
    private String cresusLocation;
    
    @BeforeMethod
    @Override
    protected void setup() {
        super.setup();
        
        cleanupFiles(".lock");
        cleanupFiles(".txt");
    }
    
    @AfterMethod
    public void cleanup() throws Exception {
        cleanupFiles(".lock");
        cleanupFiles(".txt");
    }
    
    private void cleanupFiles(final String extension) {
        FileFilter fileFilter = new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(extension);
            }
        };
        
        File folder = new File(cresusLocation);
        File[] files = folder.listFiles(fileFilter);
        for (File file : files) {
            file.delete();
        }
    }
    
    @Test(expectedExceptions = { ExportLockingException.class })
    public void testCreateExportWithLockFileExist() throws Exception {
        String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(filename);
        lockFile.createNewFile();
        cresusFileExportService.createExport(new ExportParameter());
    }
    
    public void testCreateExport() throws Exception {
        Export export = cresusFileExportService.createExport(new ExportParameter());
        Assert.assertNotNull(export.getExportId());
        
        String filename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(filename);
        Assert.assertTrue(lockFile.exists());
    }
    
    public void testOpenTable() throws Exception {
        Export export = cresusFileExportService.createExport(new ExportParameter());
        OutputStream out = cresusFileExportService.openTable(export, "TEST_TABLE");
        Assert.assertNotNull(out);
        Assert.assertTrue(export.getExportedTables().containsKey("TEST_TABLE"));
        cresusFileExportService.closeTable(export, "TEST_TABLE");
        cresusFileExportService.closeExport(export);
    }
    
    public void testCloseTable() throws Exception {
        Export export = cresusFileExportService.createExport(new ExportParameter());
        cresusFileExportService.openTable(export, "TEST_TABLE");
        cresusFileExportService.closeTable(export, "TEST_TABLE");
        Assert.assertFalse(export.getExportedTables().containsKey("TEST_TABLE"));
        cresusFileExportService.closeExport(export);
    }
    
    public void testCloseExport() throws Exception {
        Export export = cresusFileExportService.createExport(new ExportParameter());
        String lockFilename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(lockFilename);
        Assert.assertTrue(lockFile.exists());
        
        cresusFileExportService.closeExport(export);
        Assert.assertFalse(lockFile.exists());
    }
    
    public void testAbortExport() throws Exception {
        Export export = cresusFileExportService.createExport(new ExportParameter());
        cresusFileExportService.openTable(export, "TEST_TABLE");
        
        String lockFilename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + CresusExportConstant.LOCK_FILE;
        File lockFile = new File(lockFilename);
        Assert.assertTrue(lockFile.exists());
        
        String exportedFilename = cresusLocation + CresusExportConstant.FILE_SEPARATOR + "TEST_TABLE_"
                        + export.getExportId() + ".txt";
        File exportedFile = new File(exportedFilename);
        Assert.assertTrue(exportedFile.exists());
        
        cresusFileExportService.abortExport(export);
        Assert.assertFalse(lockFile.exists());
        Assert.assertFalse(exportedFile.exists());
    }
    
}
