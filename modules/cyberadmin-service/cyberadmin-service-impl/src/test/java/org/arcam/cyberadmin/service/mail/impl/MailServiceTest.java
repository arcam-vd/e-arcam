/*
 * MailServiceTest.java
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

package org.arcam.cyberadmin.service.mail.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.impl.AbstractCyberAdminServiceTest;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A test class for {@link MailService}.
 * 
 * @author mmn
 *
 */
@Test(groups = { "mail" })
// CHECKSTYLE:OFF MagicNumber
public class MailServiceTest extends AbstractCyberAdminServiceTest {

    private static final String TEST_EMAIL_ADDRESS = "abc@elca.vn";
    @Autowired
    private MailService mailService;
    @Autowired
    private DeclarationService declarationService;
    
    Declaration declaration;
    DateFormat df;
    
    @BeforeMethod
    public void setup() {
        LocaleContextHolder.setLocale(Locale.FRENCH);
        
        declaration = declarationService.prepare(createTestAssujetti());
        declaration.setId(1000L);
        declaration.setFiscaleDate(DateHelper.today());
        declaration.setDueDate(DateUtils.addDays(DateHelper.today(), 10));
        declaration.setDenomination("Test denomination");
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setEtablissement("Etablissement");
        bienTaxe.setCommuneCode("LUSSY-SUR-MORGES");
        bienTaxe.getAdresse().setAdresse("Bien adresse");
        bienTaxe.getAdresse().setRue("Bien rue");
        bienTaxe.getAdresse().setNo("Bien No.");
        bienTaxe.getAdresse().setNpa("Bien npa");
        bienTaxe.getAdresse().setLocalite("Bien localite");
        
        df = new SimpleDateFormat(CyberAdminConstants.DATE_PATTERN);
    }
    
    private Assujetti createTestAssujetti() {
        Assujetti assujetti = new Assujetti();
        assujetti.setEmail(TEST_EMAIL_ADDRESS);
        Adresse adresse = new Adresse();
        adresse.setId(1L);
        adresse.setAdresse("Adresse");
        adresse.setRue("Rue");
        adresse.setNo("No");
        adresse.setLocalite("Localite");
        adresse.setNpa("1001");
        adresse.setPays("CH");
        adresse.setEmail(TEST_EMAIL_ADDRESS);
        assujetti.setAdresse(adresse);
        
        User user = new User();
        user.setEmail(TEST_EMAIL_ADDRESS);
        user.setUsername("test");
        assujetti.setUser(user);
        
        Person person = new Person();
        person.setNom("Test Nom");
        person.setPrenom("Test preNom");
        person.setOrganisation("Test organisation");
        assujetti.setPerson(person);
        
        return assujetti;
    }
    
    public void testRequestEGidEWidMail() throws Exception {
        Set<String> to = new HashSet<String>();
        to.add(TEST_EMAIL_ADDRESS);
        String message = mailService.requestEGridEwid(declaration, CyberAdminConstants.DEFAULT_SYSTEM_MAIL, to);
        
        Assert.assertNotNull(message);
        assertTaxpayerAndBienTaxeInfo(message);
    }

    private void assertTaxpayerAndBienTaxeInfo(String message) {
        Assert.assertTrue(StringUtils.contains(message, "Test Nom"));
        Assert.assertTrue(StringUtils.contains(message, "Test preNom"));
        Assert.assertTrue(StringUtils.contains(message, "Test organisation"));
        Assert.assertTrue(StringUtils.contains(message, "Adresse"));
        Assert.assertTrue(StringUtils.contains(message, "Rue"));
        Assert.assertTrue(StringUtils.contains(message, "No"));
        Assert.assertTrue(StringUtils.contains(message, "1001"));
        Assert.assertTrue(StringUtils.contains(message, "Localite"));
        
        Assert.assertTrue(StringUtils.contains(message, "Etablissement"));
        Assert.assertTrue(StringUtils.contains(message, "Bien adresse"));
        Assert.assertTrue(StringUtils.contains(message, "Bien rue"));
        Assert.assertTrue(StringUtils.contains(message, "Bien No."));
        Assert.assertTrue(StringUtils.contains(message, "Bien npa"));
        Assert.assertTrue(StringUtils.contains(message, "Bien localite"));
    }
    
    public void testDemandDeclarationMail() throws Exception {
        String message = mailService.demandDeclaration(declaration, CyberAdminConstants.DEFAULT_SYSTEM_MAIL);
        
        Assert.assertNotNull(message);
        Assert.assertTrue(StringUtils.contains(message, df.format(declaration.getDueDate())));
        Assert.assertTrue(StringUtils.contains(message, declaration.getId() + ""));
    }
    
    public void testRejectionDeclarationMail() throws Exception {
        Set<String> to = new HashSet<String>();
        to.add(TEST_EMAIL_ADDRESS);
        String message = mailService.rejectDeclaration(declaration, CyberAdminConstants.DEFAULT_SYSTEM_MAIL, to,
                "Test justification");
        
        Assert.assertNotNull(message);
        assertTaxpayerAndBienTaxeInfo(message);
        Assert.assertTrue(StringUtils.contains(message, declaration.getId() + ""));
    }
    
    public void testDemandSupportMail() throws Exception {
        Set<String> to = new HashSet<String>();
        to.add(TEST_EMAIL_ADDRESS);
        String message = mailService.demandSupport(declaration, "Demand message",
                CyberAdminConstants.DEFAULT_SYSTEM_MAIL, to);
        
        Assert.assertNotNull(message);
        assertTaxpayerAndBienTaxeInfo(message);
        Assert.assertTrue(StringUtils.contains(message, declaration.getId() + ""));
    }
    
    public void remindOverdueMail() throws Exception {
        String message = mailService.remindOverdueDeclaration(declaration, CyberAdminConstants.DEFAULT_SYSTEM_MAIL);
        
        Assert.assertNotNull(message);
        Assert.assertTrue(StringUtils.contains(message, df.format(declaration.getDueDate())));
        Assert.assertTrue(StringUtils.contains(message, declaration.getId() + ""));
    }
    
    public void testNewPasswordMail() throws Exception {
        User user = new User();
        user.setEmail(TEST_EMAIL_ADDRESS);
        
        String password = RandomStringUtils.random(CyberAdminConstants.AUTO_GENERATED_PASSWORD_LENGTH, true, true);
        String message = mailService.newPassword(user, CyberAdminConstants.DEFAULT_SYSTEM_MAIL, password);
        
        Assert.assertNotNull(message);
        Assert.assertTrue(StringUtils.contains(message, password));
    }
    
    public void testConfirmRegistrationMail() throws Exception {
        mailService.confirmRegistration(createTestAssujetti(), CyberAdminConstants.DEFAULT_SYSTEM_MAIL);
    }
    
    public void testInformAccountValidationMail() throws Exception {
        mailService.informValidatedAccount(createTestAssujetti(), CyberAdminConstants.DEFAULT_SYSTEM_MAIL);
    }
    
    public void testEnrolAccountMail() throws Exception {
        String password = RandomStringUtils.random(CyberAdminConstants.AUTO_GENERATED_PASSWORD_LENGTH, true, true);
        mailService.enrolAccount(createTestAssujetti(), CyberAdminConstants.DEFAULT_SYSTEM_MAIL, password);
    }
    
// CHECKSTYLE:ON MagicNumber    
}
