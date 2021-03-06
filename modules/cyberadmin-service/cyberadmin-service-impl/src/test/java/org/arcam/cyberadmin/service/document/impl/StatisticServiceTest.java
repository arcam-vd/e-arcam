/*
 * StatisticServiceTest.java
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

package org.arcam.cyberadmin.service.document.impl;

import java.util.Calendar;
import java.util.List;

import org.arcam.cyberadmin.criteria.business.GenerateStatisticCriteria;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.service.document.StatisticService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test generate statistic service.
 * 
 * @author vtn
 * 
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class StatisticServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private StatisticService statisticSerive;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private TaxpayerService taxpayerService;

    private Assujetti createTestAssujetti() {
        Assujetti assujetti = taxpayerService.prepare();
        assujetti.getAdresse().setLocalite("Lausanne");
        assujetti.getAdresse().setNpa("1000");
        Assujetti savedAssujetti = taxpayerService.saveAndLog(assujetti);
        return savedAssujetti;
    }

    public void testGetStatisticDeclarations() {

        Assujetti assujetti = createTestAssujetti();
        Declaration declaration = declarationService.prepare(assujetti);
        declaration.setFiscaleDate(DateHelper.getDate(3, Calendar.OCTOBER, 2012));

        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.CAMPING);
        declarationService.create(declaration);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);

        declaration.setStatus(StatusTypeEnum.PAID);
        declarationService.save(declaration);

        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.PAID);

        GenerateStatisticCriteria criteria = new GenerateStatisticCriteria();
        criteria.setDeclarationTypeEnum(DeclarationTypeEnum.CAMPING);
        criteria.setFromDate(DateHelper.getDate(1, 1, 2012));
        List<Declaration> declarations = statisticSerive.generateDeclarationStatistic(criteria);
        Assert.assertEquals(declarations.get(0).getBienTaxe().getDeclarationType(), DeclarationTypeEnum.CAMPING);
    }

}
// CHECKSTYLE:ON
