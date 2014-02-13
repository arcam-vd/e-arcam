/*
 * ReminderJobTest.java
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
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class ReminderJobTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private ReminderJob dueDateReminderJob;
    @Autowired
    private DeclarationService declarationService;
    
    private Declaration prepareDeclaration() {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        Declaration declaration = declarationService.prepare(assujetti);
        declaration.setFiscaleDate(DateHelper.getFirstDateOfCurrentYear());
        declaration.setLastModificationDate(DateHelper.now());
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setDeclarationType(DeclarationTypeEnum.LOCATION);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setPeriodiciteCode(bienTaxe.getPeriodiciteCode());
        
        return declaration;
    }
    
    public void testSendReminderWithoutEmail() throws Exception {
        Declaration declaration6 = prepareDeclaration();
        declaration6.getBienTaxe().getAssujetti().setUser(null);
        declaration6.getBienTaxe().getAssujetti().getAdresse().setEmail("");
        declaration6.setDueDate(DateUtils.addDays(DateHelper.today(), 1));
        declarationService.saveOrUpdate(declaration6);

        dueDateReminderJob.remindDueDate();
        Assert.assertEquals(declaration6.getCommentaires().size(), 0);
    }
    
    public void testSendReminder() throws Exception {
        // case 1: reminder 'onRemind' is sent on dueDate for LOCATION
        Declaration declaration1 = prepareDeclaration();
        declaration1.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration1);
        
        // case 2: reminder 'onDemand' is sent one month later for RESIDENCE_SECONDAIRE
        Declaration declaration2 = prepareDeclaration();
        declaration2.getBienTaxe().setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        declaration2.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declaration2.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declaration2.setFiscaleDate(DateUtils.addMonths(DateHelper.today(), -1));
        declaration2.setDueDate(DateUtils.addMonths(DateHelper.today(), 9));
        declarationService.saveOrUpdate(declaration2);
        
        // case 3: reminder 'onRemind' is sent 3 month later for RESIDENCE_SECONDAIRE
        Declaration declaration3 = prepareDeclaration();
        declaration3.getBienTaxe().setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        declaration3.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declaration3.setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        declaration3.setFiscaleDate(DateUtils.addMonths(DateHelper.today(), -3));
        declaration3.setDueDate(DateUtils.addMonths(DateHelper.today(), 9));
        declarationService.saveOrUpdate(declaration3);
        
        // No reminder for filled declaration
        Declaration declaration4 = prepareDeclaration();
        declaration4.setDueDate(DateUtils.addDays(DateHelper.today(), -10));
        declaration4.setStatus(StatusTypeEnum.FILLED);
        declarationService.saveOrUpdate(declaration4);
        
        // case 5: second reminder 'onRemind' is sent on 'dueDate + 10' for LOCATION
        Declaration declaration5 = prepareDeclaration();
        declaration5.setDueDate(DateUtils.addDays(DateHelper.today(), -10));
        declarationService.saveOrUpdate(declaration5);
        
        // case 6: reminder 'onDemand' is sent on departDate for LOCATION
        Declaration declaration6 = prepareDeclaration();
        declaration6.setDueDate(DateUtils.addDays(DateHelper.today(), 50));
        declaration6.setDepartDate(DateHelper.now());
        declarationService.saveOrUpdate(declaration6);
        
        // case 7: reminder 'onDemand' is sent on fiscalDate+period for HOTEL
        Declaration declaration7 = prepareDeclaration();
        declaration7.getBienTaxe().setDeclarationType(DeclarationTypeEnum.HOTEL);
        declaration7.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declaration7.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declaration7.setFiscaleDate(DateUtils.addMonths(DateHelper.today(), -1));
        declaration7.setDueDate(DateUtils.addDays(DateHelper.today(), 10));
        declarationService.saveOrUpdate(declaration7);    
        
        // case 8: reminder 'onRemind' is sent on dueDate for HOTEL
        Declaration declaration8 = prepareDeclaration();
        declaration8.getBienTaxe().setDeclarationType(DeclarationTypeEnum.HOTEL);
        declaration8.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declaration8.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        //declaration8.setFiscaleDate(DateUtils.addMonths(DateHelper.today(), -1));
        declaration8.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration8);  
        
        // case 9: reminder 'onRemind' is sent on dueDate+10 for HOTEL
        Declaration declaration9 = prepareDeclaration();
        declaration9.getBienTaxe().setDeclarationType(DeclarationTypeEnum.HOTEL);
        declaration9.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        declaration9.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        //declaration9.setFiscaleDate(DateUtils.addMonths(DateHelper.today(), -1));
        declaration9.setDueDate(DateUtils.addDays(DateHelper.today(), -10));
        declarationService.saveOrUpdate(declaration9);  
        
        dueDateReminderJob.remindDueDate();
        
        Assert.assertEquals(declaration1.getCommentaires().size(), 1);
        Assert.assertEquals(declaration2.getCommentaires().size(), 1);
        Assert.assertEquals(declaration3.getCommentaires().size(), 1);
        Assert.assertEquals(declaration4.getCommentaires().size(), 0);
        Assert.assertEquals(declaration5.getCommentaires().size(), 1);
        Assert.assertEquals(declaration6.getCommentaires().size(), 1);
        Assert.assertEquals(declaration7.getCommentaires().size(), 1);
        Assert.assertEquals(declaration8.getCommentaires().size(), 1);
        Assert.assertEquals(declaration9.getCommentaires().size(), 1);
    }
    
// CHECKSTYLE:ON MagicNumber
}
