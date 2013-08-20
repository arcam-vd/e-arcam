/*
 * UserServiceTest
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

import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dao.core.CyberadminDao;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoRegistry;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author dtl
 * 
 */
@Test(groups = { "broken" })
public class UserServiceTest extends AbstractCyberAdminServiceTest {
    
    User user;
    Assujetti taxpayer;
    @Autowired
    private DaoRegistry daoRegistry;
    
    @BeforeMethod
    public void beforeMethod() {
        prepareData();
    }
    
    private void prepareData() {
        user = new User();
        user.setActivated(false);
        user.setArcam(false);
        user.setEmail("testdtl2@dtl.com");
        user.setPassword("testpassword");
        user.setTelephone("098775");
        user.setUserType(UserTypeEnum.ASJ);
        user.setUsername("dtltest2");
        user.setAdminCreated(true);
        user.setValidated(false);
        Person person = new Person();
        person.setNom("dtltest name");
        person.setPrenom("dtltest firstname");
        user.setPerson(person);
        userService.saveOrUpdate(user);
    }

    public void testCountbyField() {
        Assert.assertTrue(userService.countByField("username", "dtltest2", null) >= 1);
        Assert.assertTrue(userService.countByField("username", "dtltesT2", null) >= 1);
        Assert.assertTrue(userService.countByField("username", "DTLTEST2", null) >= 1);
        Assert.assertTrue(userService.countByField("username", "dtltes", null) == 0);
        Assert.assertTrue(userService.countByField("email", "testdtl2@dtl.com", null) >= 1);

        Assert.assertTrue(userService.countByField("email", "testdtl2@dtl.com", user.getId()) == 0);
    }

    public void testFindByCriteria() {
        UserCriteria criteria = new UserCriteria();
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
        
        criteria.setFreetext("dtl");
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
        
        criteria.setFreetext("@dtl.com");
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
        
        criteria.setFreetext("ASJ");
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
        
        criteria.setFreetext("dtltest name");
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
        
        criteria.setFreetext("dtltest firstname");
        Assert.assertTrue(userService.findByCriteria(criteria, false).size() >= 1);
    }
    
    @SuppressWarnings("unchecked")
    public void testAssociateWithSelectedTaxpayer() {
        prepareBienTaxeData();
        // Add Assujetti for user object.
        Assujetti taxpayerForUser = prepareTaxpayerObject(prepareAdresseData("localite", "2000", "VN"));
        CyberadminDao.class.cast(daoRegistry.getFor(Assujetti.class)).saveOrUpdate(taxpayerForUser);
        user.setAssujetti(taxpayerForUser);
        taxpayerForUser.setUser(user);
        userService.saveOrUpdate(user);
        
        userService.associateWithSelectedTaxpayer(user, taxpayer);
    }

    @SuppressWarnings("unchecked")
    private BienTaxe prepareBienTaxeData() {
        Adresse adresse = prepareAdresseData("localite1", "1000", "CH");
        Adresse adresseForAssujetti = prepareAdresseData("localite2", "2000", "CH");
        
        // Create taxpayer.
        taxpayer = prepareTaxpayerObject(adresseForAssujetti);
        CyberadminDao.class.cast(daoRegistry.getFor(Assujetti.class)).saveOrUpdate(taxpayer);
        // Create bienTaxe
        BienTaxe bienTaxe = new BienTaxe();
        bienTaxe.setDeclarationType(DeclarationTypeEnum.CAMPING);
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        bienTaxe.setCommuneCode("CommuneCode");
        bienTaxe.setAdresse(adresse);
        bienTaxe.setAssujetti(taxpayer);
        CyberadminDao.class.cast(daoRegistry.getFor(BienTaxe.class)).saveOrUpdate(bienTaxe);
        return bienTaxe;
    }

    private Assujetti prepareTaxpayerObject(Adresse adresseForAssujetti) {
        Assujetti txpayer = new Assujetti();
        txpayer.setAdresse(adresseForAssujetti);
        Person person = new Person();
        person.setAssujetti(txpayer);
        person.setNom("test name");
        person.setPrenom("test first name");
        person.setOrganisation("org");
        txpayer.setPerson(person);
        return txpayer;
    }

    private Adresse prepareAdresseData(String localite, String npa, String pays) {
        Adresse adresse = new Adresse();
        adresse.setLocalite(localite);
        adresse.setNpa(npa);
        adresse.setPays(pays);
        return adresse;
    }

}
