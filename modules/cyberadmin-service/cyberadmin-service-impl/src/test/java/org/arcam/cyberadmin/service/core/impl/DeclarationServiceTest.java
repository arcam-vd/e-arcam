/*
 * DeclarationServiceTest.java
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria.SearchMode;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoRegistry;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.dom.reference.Tarif;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A test class for {@link DeclarationService}.
 * 
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class DeclarationServiceTest extends AbstractCyberAdminServiceTest {

    @Autowired
    private DeclarationService declarationService;
    @Autowired
    private DaoRegistry daoRegistry;
    
    public void testSearchDeclaration() throws Exception {
        DeclarationCriteria criteria = new DeclarationCriteria();
        
        // search without mode, an empty list is returned
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, false)));
        
        criteria.setSearchMode(SearchMode.FULL);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CyberAdminUserDetail) authentication.getPrincipal()).getUserInfo();
        user.setUserType(UserTypeEnum.ASJ);
        criteria.setCurrentUser(user);
        
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, true)));
        
        criteria.setFrom(DateHelper.today());
        criteria.setTo(DateUtils.addDays(DateHelper.today(), 30));
        
        criteria.setCommune("Test commune");
        criteria.setType(DeclarationTypeEnum.CHAMBRE);
        criteria.setStatus(StatusTypeEnum.FILLED);
        criteria.setDenomination("Test demonimation");
        criteria.setId(1000L);
        criteria.setName("Test name");
        criteria.setFirstname("Test firstname");
        criteria.setOrganisation("Test organisation");
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, false)));
        
        criteria.setSearchMode(SearchMode.QUICK);
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, false)));
        
        criteria.setFreetext("Test freetext");
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, false)));
        
        user.setUserType(UserTypeEnum.COM);
        Assert.assertTrue(CollectionUtils.isEmpty(declarationService.findDeclarationByCriteria(criteria, false)));
    }
    
    public void testCountDeclaration() throws Exception {
        DeclarationCriteria criteria = new DeclarationCriteria();
        
        Assert.assertEquals(declarationService.countDeclarationByCriteria(criteria), 0);
        
        criteria.setSearchMode(SearchMode.QUICK);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CyberAdminUserDetail) authentication.getPrincipal()).getUserInfo();
        user.setUserType(UserTypeEnum.ASJ);
        criteria.setCurrentUser(user);
        
        Assert.assertEquals(declarationService.countDeclarationByCriteria(criteria), 0);
    }
    
    public void testSearchTaxpayerDeclarationByCriteria() throws Exception {
        DeclarationCriteria criteria = new DeclarationCriteria();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CyberAdminUserDetail) authentication.getPrincipal()).getUserInfo();
        user.setUserType(UserTypeEnum.ASJ);
        criteria.setCurrentUser(user);
        
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        
        Assert.assertEquals(declarationService.findTaxpayerDeclarationByCriteria(criteria, assujetti, true).size(), 0);
        
        criteria.setFrom(DateHelper.today());
        criteria.setTo(DateUtils.addDays(DateHelper.today(), 30));
        criteria.setFreetext("Test freetext");
        user.setUserType(UserTypeEnum.COM);
        Assert.assertEquals(declarationService.findTaxpayerDeclarationByCriteria(criteria, assujetti, false).size(), 0);
    }
    
    public void testCountTaxpayerDeclarationByCriteria() throws Exception {
        DeclarationCriteria criteria = new DeclarationCriteria();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CyberAdminUserDetail) authentication.getPrincipal()).getUserInfo();
        user.setUserType(UserTypeEnum.ASJ);
        criteria.setCurrentUser(user);
        
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Assert.assertEquals(declarationService.countTaxpayerDeclarationByCriteria(criteria, assujetti), 0);
    }
    
    public void testPrepareDeclaration() throws Exception {
        Declaration declaration = declarationService.prepare(createTestAssujetti());
        Assert.assertNotNull(declaration.getBienTaxe());
        Assert.assertNotNull(declaration.getAdresse());
        Assert.assertNotNull(declaration.getBienTaxe().getAssujetti());
        
        Adresse decAdresse = declaration.getAdresse();
        Adresse assujettiAdresse = declaration.getBienTaxe().getAssujetti().getAdresse();
        Assert.assertEquals(assujettiAdresse.getRue(), decAdresse.getRue());
        Assert.assertEquals(assujettiAdresse.getNo(), decAdresse.getNo());
        Assert.assertEquals(assujettiAdresse.getLocalite(), decAdresse.getLocalite());
        Assert.assertEquals(assujettiAdresse.getNpa(), decAdresse.getNpa());
        Assert.assertEquals(assujettiAdresse.getPays(), decAdresse.getPays());
    }
    
    private Assujetti createTestAssujetti() {
        Assujetti assujetti = new Assujetti();
        Adresse adresse = new Adresse();
        adresse.setId(1L);
        adresse.setRue("Rue");
        adresse.setNo("No");
        adresse.setLocalite("Localite");
        adresse.setNpa("npa");
        adresse.setPays("CH");
        assujetti.setAdresse(adresse);
        return assujetti;
    }
    
    public void testDemandDeclaration() throws Exception {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Declaration declaration = declarationService.prepare(assujetti); 
        declaration.setFiscaleDate(DateHelper.getDate(15, Calendar.FEBRUARY, 2012));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        
        declarationService.demand(declaration);
        
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.TO_FILLED);
        Assert.assertEquals(DateHelper.getDateStart(declaration.getLastModificationDate()), DateHelper.today());
        
        // RESIDENCE_SECONDAIRE
        // DueDate = 30th September of the current year (year of the fiscale date)
        Assert.assertTrue(DateHelper.compareIgnoreTime(declaration.getDueDate(),
                DateHelper.getDate(30, Calendar.SEPTEMBER, 2012)) == 0);
        
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.LOCATION);
        declarationService.demand(declaration);
        
        // LOCATION && departDate == null
        // DueDate = 10th January of the next year (year fiscale date + 1)
        Assert.assertTrue(DateHelper.compareIgnoreTime(declaration.getDueDate(),
                DateHelper.getDate(10, Calendar.JANUARY, 2013)) == 0);
        
        declaration.setDepartDate(DateHelper.getDate(28, Calendar.FEBRUARY, 2013));
        declarationService.demand(declaration);
        
        // LOCATION && departDate != null
        // DueDate = DepartDate + 10 days
        Assert.assertTrue(DateHelper.compareIgnoreTime(declaration.getDueDate(),
                DateHelper.getDate(10, Calendar.MARCH, 2013)) == 0);
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.HOTEL);
        declarationService.demand(declaration);
        
        // other cases
        // DueDate = FiscaleDate + periodicity[months] + 9 days   
        // (10th of the first month after the end of the period)
        Assert.assertTrue(DateHelper.compareIgnoreTime(declaration.getDueDate(),
                DateHelper.getDate(24, Calendar.MAY, 2012)) == 0);
    }
    
    public void testSubmitDeclaration() throws Exception {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Declaration declaration = declarationService.prepare(assujetti); 
        declaration.setFiscaleDate(DateHelper.getDate(3, Calendar.OCTOBER, 2012));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        
        declarationService.submit(declaration);
        
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.EGID_VALIDATED);
        Assert.assertEquals(DateHelper.getDateStart(declaration.getLastModificationDate()), DateHelper.today());
        Assert.assertTrue(DateHelper.compareIgnoreTime(declaration.getDueDate(),
                DateHelper.getDate(30, Calendar.SEPTEMBER, 2012)) == 0);
        Assert.assertEquals(DateHelper.getDateStart(declaration.getSubmissionDate()), DateHelper.today());
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.INSTITUT);
        declarationService.submit(declaration);
        
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.FILLED);
    }
    
    public void testSaveDeclaration() throws Exception {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Declaration declaration = declarationService.prepare(assujetti); 
        declaration.setFiscaleDate(DateHelper.getDate(3, Calendar.OCTOBER, 2012));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        
        declarationService.save(declaration);
        
        // status remains 'TO_FILLED'
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.TO_FILLED);
    }

    public void testCreateDeclaration() throws Exception {
        Declaration declaration = declarationService.prepare(createTestAssujetti());
        declaration.setFiscaleDate(DateHelper.today());
        BienTaxe bienTaxe = declaration.getBienTaxe();
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.LOCATION);
        declarationService.create(declaration);
        Assert.assertNotNull(declaration.getGuestExemptions());
        Assert.assertTrue(CollectionUtils.isEmpty(declaration.getGuestExemptions()));
        Assert.assertTrue(CollectionUtils.isEmpty(declaration.getExemptions()));
        Assert.assertNull(declaration.getTotalExemption());
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.HOTEL);
        declarationService.create(declaration);
        Assert.assertTrue(CollectionUtils.isNotEmpty(declaration.getGuestExemptions()));
        Assert.assertNotNull(declaration.getTotalExemption());
        Assert.assertTrue(CollectionUtils.isNotEmpty(declaration.getExemptions()));
        
        bienTaxe.setDeclarationType(DeclarationTypeEnum.CAMPING);
        declarationService.create(declaration);
        Assert.assertTrue(CollectionUtils.isNotEmpty(declaration.getGuestExemptions()));
        Assert.assertNotNull(declaration.getTotalExemption());
        Assert.assertTrue(CollectionUtils.isNotEmpty(declaration.getExemptions()));
    }
    
    public void testAddCommentDeclaration() {

        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Declaration declaration = declarationService.prepare(assujetti); 
        declaration.setFiscaleDate(DateHelper.getDate(3, Calendar.OCTOBER, 2012));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        
        declarationService.save(declaration);
        
        // status remains 'TO_FILLED'
        Assert.assertEquals(declaration.getStatus(), StatusTypeEnum.TO_FILLED);
        
        Commentaire comment = new Commentaire();
        comment.setDeclaration(declaration);
        comment.setMessage("vnt message");
        comment.setTimestamp(DateHelper.now());
        comment.setUser(userService.getSystemUser());
        declaration.getCommentaires().add(comment);

        // save the comment
        Declaration declaration2 = declarationService.addComment(declaration, comment);
        Assert.assertFalse(declaration2.getCommentaires().isEmpty());
    }
    
    private Declaration prepareDeclaration() {
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(3);
        Declaration declaration = declarationService.prepare(assujetti);
        declaration.setFiscaleDate(DateHelper.today());
        declaration.setLastModificationDate(DateHelper.now());
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        
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
    
    public void testGetOverdueDeclarations() throws Exception {
        Declaration declaration1 = prepareDeclaration();
        declaration1.setDueDate(DateHelper.today());
        declarationService.saveOrUpdate(declaration1);
        
        Declaration declaration2 = prepareDeclaration();
        declaration2.setDueDate(DateUtils.addDays(DateHelper.today(), -14));
        declarationService.saveOrUpdate(declaration2);
        
        Declaration declaration3 = prepareDeclaration();
        declaration3.setDueDate(DateUtils.addDays(DateHelper.today(), 1));
        declarationService.saveOrUpdate(declaration3);
        
        List<Long> ids = declarationService.getOverdueDeclarationIds(0);
        Assert.assertEquals(ids.size(), 1);
        Assert.assertEquals(new Long(declaration1.getId()), ids.get(0));
        
        ids = declarationService.getOverdueDeclarationIds(14);
        Assert.assertEquals(ids.size(), 1);
        Assert.assertEquals(new Long(declaration2.getId()), ids.get(0));
        
        ids = declarationService.getOverdueDeclarationIds(-1);
        Assert.assertEquals(ids.size(), 1);
        Assert.assertEquals(new Long(declaration3.getId()), ids.get(0));
    }

    public void testCalculateTaxAmountForResidenceSecondaire() throws Exception {
        prepareTarif();
        
        Declaration declaration = new Declaration();
        declaration.setBienTaxe(new BienTaxe());
        declaration.getBienTaxe().setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        declaration.setEstimation(new Long(20000));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTaxAmount(), 150d);
        
        declaration.setEstimation(new Long(150000));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTaxAmount(), 150d);
        
        declaration.setEstimation(new Long(300000));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTaxAmount(), 300d);
        
        declaration.setEstimation(new Long(1500000));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTaxAmount(), 1500d);
        
        declaration.setEstimation(new Long(3000000));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTaxAmount(), 1500d);
    }

    public void testCalculateTaxAmountForLocation() throws Exception {
        prepareTarif();
        
        Declaration declaration = new Declaration();
        declaration.setTaille(false);
        declaration.setBienTaxe(new BienTaxe());
        declaration.getBienTaxe().setDeclarationType(DeclarationTypeEnum.LOCATION);
        
        declaration.setDepartDate(DateHelper.getDate(30, Calendar.JUNE, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 5);
        Assert.assertEquals(declaration.getTaxAmount(), 100d);
        
        declaration.setDepartDate(DateHelper.getDate(5, Calendar.AUGUST, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 10);
        Assert.assertEquals(declaration.getTaxAmount(), 200d);
        
        declaration.setDepartDate(DateHelper.getDate(10, Calendar.AUGUST, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 11);
        Assert.assertEquals(declaration.getTaxAmount(), 200d);
        
        declaration.setTaille(true);
        declaration.setDepartDate(DateHelper.getDate(30, Calendar.JUNE, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 5);
        Assert.assertEquals(declaration.getTaxAmount(), 200d);
        
        declaration.setDepartDate(DateHelper.getDate(5, Calendar.AUGUST, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 10);
        Assert.assertEquals(declaration.getTaxAmount(), 400d);
        
        declaration.setDepartDate(DateHelper.getDate(10, Calendar.AUGUST, 2012));
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getWeeks(), 11);
        Assert.assertEquals(declaration.getTaxAmount(), 400d);
    }
    
    public void testCalculateTaxAmountForOthers() throws Exception {
        prepareTarif();
        
        Declaration declaration = new Declaration();
        declaration.setTaille(false);
        declaration.setBienTaxe(new BienTaxe());
        declaration.setFiscaleDate(DateHelper.getDate(1, Calendar.JUNE, 2012));
        declaration.getBienTaxe().setDeclarationType(DeclarationTypeEnum.CHAMBRE);
        
        createGuestExemptions(declaration);
        
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTotalNuits(), 4d);
        Assert.assertEquals(declaration.getTotalResidentiel(), 0d);
        Assert.assertEquals(declaration.getTaxAmount(), 4d);
        Assert.assertEquals(declaration.getCalculatedExemption().getHotes(), new Integer(6));
        Assert.assertEquals(declaration.getCalculatedExemption().getNuits(), new Integer(2));
        
        declaration.getBienTaxe().setDeclarationType(DeclarationTypeEnum.INSTITUT);
        
        declarationService.calculateTax(declaration);
        Assert.assertEquals(declaration.getTotalNuits(), 1.6d);
        Assert.assertEquals(declaration.getTotalResidentiel(), 2850d);
        Assert.assertEquals(declaration.getTaxAmount(), 2851.6d);
        Assert.assertEquals(declaration.getCalculatedExemption().getHotes(), new Integer(6));
        Assert.assertEquals(declaration.getCalculatedExemption().getNuits(), new Integer(2));
        Assert.assertEquals(declaration.getCalculatedExemption().getResidentiel(), new Integer(19));
    }

    private void createGuestExemptions(Declaration declaration) {
        int id = 1;
        for (ExonerationTypeEnum type : ExonerationTypeEnum.values()) {
            GuestExemptions exemption = new GuestExemptions();
            exemption.setId(id++);
            exemption.setExemptionType(type);
            if (type == ExonerationTypeEnum.AUCUNE) {
                exemption.setHotes(20);
                exemption.setNuits(30);
                exemption.setResidentiel(40);
            } else {
                exemption.setHotes(2);
                exemption.setNuits(4);
                exemption.setResidentiel(3);
            }
            declaration.getGuestExemptions().add(exemption);
        }
    }
    
    private void prepareTarif() {
        Tarif tarif = new Tarif();
        tarif.setCoefficientResidence(0.001d);
        tarif.setDateDebut(DateHelper.getDate(1, Calendar.JANUARY, 2011));
        tarif.setDateFin(DateHelper.getDate(31, Calendar.DECEMBER, 2099));
        tarif.setLocataire2p(20d);
        tarif.setLocataire3p(40d);
        tarif.setMaxLocataire2p(200d);
        tarif.setMaxLocataire3p(400d);
        tarif.setMaxResidence(1500d);
        tarif.setMinResidence(150d);
        tarif.setNuitHotel(3);
        tarif.setNuitChambre(2);
        tarif.setNuitInstitut(0.8d);
        tarif.setNuitcamping(1.5d);
        tarif.setResidentielCamping(100d);
        tarif.setResidentielInstitut(150d);
        
        daoRegistry.getFor(Tarif.class).saveOrUpdate(tarif);
    }

    public void testSaveDeclarations() throws Exception {
        declarationService.saveAndLogDeclarations(null);
        
        Assujetti assujetti = userService.getAssujettiAndBienTaxes(1);
        Declaration declaration = declarationService.prepare(assujetti); 
        declaration.setFiscaleDate(DateHelper.getDate(3, Calendar.OCTOBER, 2012));
        declaration.setDueDate(DateHelper.today());
        declaration.setLastModificationDate(DateHelper.today());
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        bienTaxe.setAdresse(assujetti.getAdresse().clone());
        bienTaxe.setPeriodiciteCode(PeriodiciteTypeEnum.QUARTER);
        bienTaxe.setDeclarationType(DeclarationTypeEnum.RESIDENCE_SECONDAIRE);
        bienTaxe.setCommuneCode("PAMPIGNY");
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        declaration.setPeriodiciteCode(bienTaxe.getPeriodiciteCode());
        
        declarationService.saveAndLogDeclarations(Arrays.asList(declaration));
    }
    
    public void testUpdateExportDeclaration() throws Exception {
        Assert.assertEquals(declarationService.updateExportedDeclaration(null, null, StatusTypeEnum.BILLED), 0);
        Assert.assertEquals(declarationService.updateExportedDeclaration(null, null, StatusTypeEnum.PAID), 0);
        Assert.assertEquals(declarationService.updateExportedDeclaration(null, null, StatusTypeEnum.CANCELLED), 0);
        Assert.assertEquals(declarationService.updateExportedDeclaration(0, 0, StatusTypeEnum.BILLED), 0);
    }
    
    // CHECKSTYLE:ON
}
