/*
 * DueDateReminderJobTest.java
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
public class DueDateReminderJobTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private DueDateReminderJob dueDateReminderJob;
    @Autowired
    private DeclarationService declarationService;
    
    private Declaration prepareDeclaration() {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        Declaration declaration = declarationService.prepare(assujetti);
        declaration.setFiscaleDate(DateHelper.today());
        declaration.setLastModificationDate(DateHelper.now());
        
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
    
    public void testSendReminderWithoutEmail() throws Exception {
        Declaration declaration6 = prepareDeclaration();
        declaration6.getBienTaxe().getAssujetti().setUser(null);
        declaration6.getBienTaxe().getAssujetti().getAdresse().setEmail("");
        declaration6.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration6);

        dueDateReminderJob.remindDueDate();
        Assert.assertEquals(declaration6.getCommentaires().size(), 0);
    }
    
    public void testSendReminder() throws Exception {
        Declaration declaration1 = prepareDeclaration();
        declaration1.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration1);
        
        Declaration declaration2 = prepareDeclaration();
        declaration2.setDueDate(DateUtils.addDays(DateHelper.today(), -14));
        declarationService.saveOrUpdate(declaration2);
        
        Declaration declaration3 = prepareDeclaration();
        declaration3.setDueDate(DateUtils.addDays(DateHelper.today(), -15));
        declarationService.saveOrUpdate(declaration3);
        
        Declaration declaration4 = prepareDeclaration();
        declaration4.setDueDate(DateUtils.addDays(DateHelper.today(), -15));
        declaration4.setStatus(StatusTypeEnum.FILLED);
        declarationService.saveOrUpdate(declaration4);
        
        Declaration declaration5 = prepareDeclaration();
        declaration5.setDueDate(DateUtils.addDays(DateHelper.today(), -16));
        declarationService.saveOrUpdate(declaration5);
        
        dueDateReminderJob.remindDueDate();
        
        Assert.assertEquals(declaration1.getCommentaires().size(), 1);
        Assert.assertEquals(declaration2.getCommentaires().size(), 0);
        Assert.assertEquals(declaration3.getCommentaires().size(), 1);
        Assert.assertEquals(declaration4.getCommentaires().size(), 0);
        Assert.assertEquals(declaration5.getCommentaires().size(), 0);
    }
    
// CHECKSTYLE:ON MagicNumber
}
