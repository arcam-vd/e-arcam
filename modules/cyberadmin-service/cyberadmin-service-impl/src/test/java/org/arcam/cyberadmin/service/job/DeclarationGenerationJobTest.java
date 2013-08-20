/*
 * DeclarationGenerationJobTest.java
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

package org.arcam.cyberadmin.service.job;

import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A test class for {@link DeclarationGenerationJob}.
 * 
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class DeclarationGenerationJobTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private DeclarationGenerationJob declarationGenerationJob;
    @Autowired
    private DeclarationService declarationService;
    
    private Declaration prepareDeclaration() {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        Declaration declaration = declarationService.prepare(assujetti);
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        
        return declaration;
    }
    
    public void testGenerateNonPeriod() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateUtils.addYears(DateHelper.getMonthStart(DateHelper.today()), -1));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 1);
    }
    
    public void testGenerateMonthlySuccess() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateUtils.addMonths(DateHelper.getMonthStart(DateHelper.today()), -1));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 2);
    }

    public void testGenerateMonthlyFail() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateHelper.getMonthStart(DateHelper.today()));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 1);
    }
    
    public void testGenerateQuaterlySuccess() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateUtils.addMonths(DateHelper.getMonthStart(DateHelper.today()), -3));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 2);
    }
    
    public void testGenerateQuaterlyFail() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateHelper.getMonthStart(DateHelper.today()));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 1);
    }
    
    public void testGenerateSemeterlySuccess() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateUtils.addMonths(DateHelper.getMonthStart(DateHelper.today()), -6));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.HALF_YEAR);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 2);
    }
    
    public void testGenerateSemeterlyFail() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateHelper.getMonthStart(DateHelper.today()));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.HALF_YEAR);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 1);
    }
    
    public void testGenerateYearlySuccess() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateUtils.addYears(DateHelper.getMonthStart(DateHelper.today()), -1));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 2);
    }
    
    public void testGenerateYearlyFail() throws Exception {
        Declaration declaration = prepareDeclaration();
        declaration.setFiscaleDate(DateHelper.getMonthStart(DateHelper.today()));
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
        Assert.assertEquals(bienTaxe.getDeclarations().size(), 1);
    }
    
    /**
     * This test case tests the process to create a new declaration accorrding to changes in ARCANCYBERADM-115.
     * 
     * @throws Exception
     */
    public void testGenerateDeclarationWithPeriodChange() throws Exception {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        BienTaxe bienTaxe = new BienTaxe();
        
        // assujetti for the bien taxe
        bienTaxe.setAssujetti(assujetti);
        assujetti.getBienTaxes().add(bienTaxe);
        
        // address for the bien taxe
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        
        Declaration declaration = new Declaration();
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setAdresse(assujetti.getAdresse().clone());
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        declaration.setFiscaleDate(DateUtils.addYears(DateHelper.getMonthStart(DateHelper.today()), -1));
        declarationService.save(declaration);
        
        declaration = new Declaration();
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setAdresse(assujetti.getAdresse().clone());
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        declaration.setFiscaleDate(DateUtils.addMonths(DateHelper.getMonthStart(DateHelper.today()), 1));
        declarationService.save(declaration);
        
        declaration = new Declaration();
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setAdresse(assujetti.getAdresse().clone());
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        declaration.setFiscaleDate(DateUtils.addMonths(DateHelper.getMonthStart(DateHelper.today()), -5));
        declarationService.save(declaration);
        
        declarationGenerationJob.generateDeclarations();
    }

// CHECKSTYLE:ON MagicNumber
}
