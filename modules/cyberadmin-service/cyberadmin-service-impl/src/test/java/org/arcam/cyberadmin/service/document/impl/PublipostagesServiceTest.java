/*
 * PublipostagesServiceTest.java
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

import java.util.List;

import org.arcam.cyberadmin.criteria.business.GenerateMailingCriteria;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.service.document.PublipostagesService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test generate mailing service.
 * 
 * @author vtn
 * 
 */
// CHECKSTYLE:OFF MagicNumber
public class PublipostagesServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PublipostagesService publipostagesService;
    @Autowired
    private TaxpayerService taxpayerService;
    @Autowired
    private DeclarationService declarationService;

    private Assujetti prepareTaxpayer() {
        Assujetti assujetti = taxpayerService.prepare();
        assujetti.getAdresse().setLocalite("Lausanne");
        assujetti.getAdresse().setNpa("1000");
        return assujetti;
    }

    private User addUser() {
        User user = new User();
        user.setActivated(false);
        user.setArcam(false);
        user.setEmail("vtn@vtn123.com");
        user.setPassword("vtn123");
        user.setTelephone("12134");
        user.setUserType(UserTypeEnum.ASJ);
        user.setUsername("vtn");
        user.setValidated(false);
        user.setAdminCreated(true);
        user.setLastLogOnDate(null);
        
        Person person = new Person();
        person.setNom("vtn name");
        person.setPrenom("vtn firstname");
        user.setPerson(person);
        return user;
    }

    private Declaration prepareDeclarationMailing(Assujetti savedAssujetti, boolean expired) {
        Declaration declaration = declarationService.prepare(savedAssujetti);

        if (expired) {
            declaration.setFiscaleDate(DateHelper.today());
        } else {
            declaration.setFiscaleDate(DateHelper.today());
        }

        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(savedAssujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);

        declarationService.save(declaration);
        return declaration;
    }

    @Test
    public void testGetMailingPeriodicInvitation() {
        Assujetti assujetti = prepareTaxpayer();
        
        User user = addUser();
        assujetti.setUser(user);
        
        Assujetti savedAssujetti = taxpayerService.saveAndLog(assujetti);
        Assert.assertFalse(savedAssujetti.getId() == -1);
        
        Declaration declaration = prepareDeclarationMailing(savedAssujetti, false);
        
        // status remains 'TO_FILLED'
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.TO_FILLED);
        
        GenerateMailingCriteria criteria = new GenerateMailingCriteria(Assujetti.class);
        criteria.setEmail(true);
        criteria.setCourier(false);
        List<Assujetti> assujettis = publipostagesService.getMailingPeriodicInvitation(criteria);
        Assert.assertEquals(assujettis.isEmpty(),true);
    }

    @Test
    public void testGetReminder() {
        Assujetti assujetti = prepareTaxpayer();
        
        Assujetti savedAssujetti = taxpayerService.saveAndLog(assujetti);
        Assert.assertFalse(savedAssujetti.getId() == -1);
        
        Declaration declaration = prepareDeclarationMailing(savedAssujetti, true);
        // status remains 'TO_FILLED'
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.TO_FILLED);

        GenerateMailingCriteria criteria = new GenerateMailingCriteria(Assujetti.class);
        criteria.setCourier(true);
        criteria.setEmail(false);
        List<Assujetti> assujettis = publipostagesService.getMailingReminder(criteria);
        Assert.assertNotNull(assujettis);
    }

    @Test(groups = { "broken" })
    public void testGetEnrolmentLetter() {
        Assujetti assujetti = taxpayerService.prepare();
        assujetti.getAdresse().setLocalite("Lausanne");
        assujetti.getAdresse().setNpa("1000");
        User user = addUser();
        user.setAssujetti(assujetti);
        assujetti.setUser(user);
        Assujetti savedAssujetti = taxpayerService.saveAndLog(assujetti);
        Assert.assertFalse(savedAssujetti.getId() == -1);
        sessionFactory.getCurrentSession().evict(assujetti);
        GenerateMailingCriteria criteria = new GenerateMailingCriteria(Assujetti.class);
        List<Assujetti> assujettis = publipostagesService.getEnrolmentLetter(criteria);
        Assert.assertEquals(assujettis.get(0).getUser().getUsername(), "vtn");
    }
}
// CHECKSTYLE:ON
