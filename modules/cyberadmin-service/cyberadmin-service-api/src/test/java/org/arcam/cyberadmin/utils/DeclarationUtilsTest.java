/*
 * DeclarationUtilsTest.java
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

package org.arcam.cyberadmin.utils;

import java.util.Calendar;

import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class DeclarationUtilsTest {

    public void testGetDisplayedFiscalePeriod() throws Exception {
        Declaration declaration = null;
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "");
        
        declaration = new Declaration();
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "");
        
        declaration.setFiscaleDate(DateHelper.getDate(22, Calendar.FEBRUARY, 2012));
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "");
        
        declaration.setBienTaxe(new BienTaxe());
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "02.2012");
        
        declaration.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.YEAR);
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "2012");
        
        declaration.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "02.2012");
        
        declaration.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.MONTH);
        Assert.assertEquals(DeclarationUtils.getDisplayedFiscalePeriod(declaration), "02.2012");
    }
    
// CHECKSTYLE:ON MagicNumber
}
