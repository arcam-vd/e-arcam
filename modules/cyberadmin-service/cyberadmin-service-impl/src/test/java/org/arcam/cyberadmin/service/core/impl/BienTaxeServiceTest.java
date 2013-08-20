/*
 * BienTaxeServiceTest.java
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

package org.arcam.cyberadmin.service.core.impl;

import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.service.core.BienTaxeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A test class for {@link BienTaxeService}.
 * 
 * @author mmn
 *
 */
@Test
//CHECKSTYLE:OFF MagicNumber
public class BienTaxeServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private BienTaxeService bienTaxeService;
    
    public void testGetAllIds() throws Exception {
        Assert.assertNotNull(bienTaxeService.getPeriodicityBientaxeIds());
    }
    
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFindByIdWithDeclarationListWithException() throws Exception {
        bienTaxeService.findByIdWithDeclarationList(0);
    }
    
    public void testFindByIdWithDeclarationList() throws Exception {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        
        BienTaxe bienTaxe = new BienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setAssujetti(assujetti);
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        
        bienTaxeService.saveOrUpdate(bienTaxe);
        
        bienTaxe = bienTaxeService.findByIdWithDeclarationList(bienTaxe.getId());
    }
    
 // CHECKSTYLE:ON
}
