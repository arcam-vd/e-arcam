/*
 * ExportDeclarationServiceTest.java
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

import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.service.cresus.data.ExportDataService;
import org.arcam.cyberadmin.service.cresus.type.Export;
import org.arcam.cyberadmin.service.cresus.type.ExportParameter;
import org.arcam.cyberadmin.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test for export declarations to csv.
 * @author dtl
 *
 */
@Test
//CHECKSTYLE:OFF MagicNumber
public class ExportDeclarationServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    @Qualifier("exportBillingService")
    private ExportDataService exportBillingService;
    
    @Autowired
    private DeclarationService declarationService;
    private Declaration declaration1;
    
    @BeforeMethod
    void prepareData() {
        declaration1 = prepareDeclaration();
        declaration1.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration1);
    }
    
    private Declaration prepareDeclaration() {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        Declaration declaration = declarationService.prepare(assujetti);
        declaration.setFiscaleDate(DateHelper.today());
        declaration.setLastModificationDate(DateHelper.now());
        declaration.setStatus(StatusTypeEnum.VALIDATED);
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setPeriodiciteCode(bienTaxe.getPeriodiciteCode());
        
        return declaration;
    }
    
    
    //this test fails because the messages are in the "web" package
    @Test(enabled=false)
    public void testExportData() throws Exception {
        Export export = new Export();
        export.setExportId("test");
        ExportParameter exportParam = new ExportParameter();
        exportBillingService.exportData(export, exportBillingService.findDataToExport(exportParam), exportParam);
    }
}
// CHECKSTYLE:ON
