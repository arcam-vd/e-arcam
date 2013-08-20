/*
 * PublipostagesServiceImpl.java
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

import org.apache.commons.lang3.RandomStringUtils;
import org.arcam.cyberadmin.criteria.business.GenerateMailingCriteria;
import org.arcam.cyberadmin.criteria.hibernate.CyberAdminExpression;
import org.arcam.cyberadmin.criteria.hibernate.CyberAdminProjection;
import org.arcam.cyberadmin.criteria.hibernate.CyberAdminSearchCriteria;
import org.arcam.cyberadmin.criteria.hibernate.HibernateCriteriaBuilder;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.core.impl.GenericDataServiceDefaultImpl;
import org.arcam.cyberadmin.service.document.PublipostagesService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.Utility;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for generating mailing.
 * 
 * @author vtn
 * 
 */
@Service("publipostagesService")
public class PublipostagesServiceImpl extends GenericDataServiceDefaultImpl<Assujetti> implements
        PublipostagesService {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    
    @Override
    public List<Assujetti> getMailingPeriodicInvitation(GenerateMailingCriteria criteria) {
        CyberAdminSearchCriteria hCrit = buildSearchGenerateMailingCriteria(criteria, false);
        DetachedCriteria createSearchCriteria = HibernateCriteriaBuilder.createSearchCriteria(hCrit, sessionFactory);
        return daoFor(Assujetti.class).findByCriteria(createSearchCriteria);

    }

    @Override
    public List<Assujetti> getMailingReminder(GenerateMailingCriteria criteria) {
        CyberAdminSearchCriteria hCrit = buildSearchGenerateMailingCriteria(criteria, true);
        DetachedCriteria createSearchCriteria = HibernateCriteriaBuilder.createSearchCriteria(hCrit, sessionFactory);
        return daoFor(Assujetti.class).findByCriteria(createSearchCriteria);
    }

    @Override
    public List<Assujetti> getEnrolmentLetter(GenerateMailingCriteria criteria) {
        DetachedCriteria createSearchCriteria = buildSearchGenerateEnrolmentLetterCriteria(criteria);
        List<Assujetti> assujettis = daoFor(Assujetti.class).findByCriteria(createSearchCriteria);
        
        for (Assujetti as : assujettis) {
            String newPassword = RandomStringUtils
                    .random(CyberAdminConstants.AUTO_GENERATED_PASSWORD_LENGTH, true, true);
            String encodePassword = Utility.encodePassword(newPassword);
            as.getUser().setDecodePassword(newPassword);
            as.getUser().setPassword(encodePassword);
            getDefaultDao().saveOrUpdate(as);
            if (criteria.isEmail()) {
                String from = Utility.getEmailFromUserDefaultIfEmpty(userService.getSystemUser());
                mailService.enrolAccount(as, from, newPassword);
            }
        }
        return assujettis;
    }

    /**
     * Build a criteria to search assujetties for Enrollment letters. 
     * It's different from other queries because beside
     * search assujetti, the results also need to be updated after that.
     * 
     * @param criteria
     * @return
     */
    private DetachedCriteria buildSearchGenerateEnrolmentLetterCriteria(GenerateMailingCriteria criteria) {
        // Not using projection when query, because we will encounter a problem(duplicate entity id exception)
        // when save object again. The problem can be described briefly as follow : 
        // 1. Do search with a projection on some properties.
        // 2. DeepAliasBeanResultTransformer will be run to transform the result set.
        // 3. Because of this, new objects are returned and they are different(in memory address) from objects saved in
        // Persistent context.
        // 4. SaveOrUpdate the result objects again, 
        // Hibernate tries to attach a detached-object again which existed in 
        // hibernate session with the same technical id but different memory address --> we got the problem.
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Assujetti.class);
        detachedCriteria.createAlias("user", "user");
        detachedCriteria.createAlias("person", "person");
        detachedCriteria.createAlias("adresse", "adresse");
        detachedCriteria.add(Restrictions.isNull("user.lastLogOnDate"));
        detachedCriteria.add(Restrictions.eq("user.adminCreated", true));
        
        if (criteria.isEmail() && !criteria.isCourier()) {
            detachedCriteria.add(Restrictions.eq("user.validated", true));
        } else if (!criteria.isEmail() && criteria.isCourier()) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.isNull("user"));
            disjunction.add(Restrictions.eq("user.validated", false));
            detachedCriteria.add(disjunction);
        }
        
        return detachedCriteria;
    }

    private CyberAdminSearchCriteria buildSearchGenerateMailingCriteria(GenerateMailingCriteria criteria,
            boolean expiredDeclaration) {
    
        CyberAdminSearchCriteria subQuery = GenerateMailingCriteria.forClass(BienTaxe.class, "bien")
                 .project("assujetti.id")
                 .with(CyberAdminExpression.eqProperty("assujetti.id", "id").setOtherSideAlias("as"));
        
        subQuery.with(CyberAdminExpression.eq("declarations.status", StatusTypeEnum.TO_FILLED));
        if (expiredDeclaration) {
            subQuery.with(CyberAdminExpression.le("declarations.dueDate", DateHelper.today()));
        } else {
            subQuery.with(CyberAdminExpression.ge("declarations.dueDate", DateHelper.today()));
        }
        
        CyberAdminSearchCriteria outerQuery = GenerateMailingCriteria.forClass(Assujetti.class, "as")
                .project(CyberAdminProjection.distinct())        
                .project(new String[] {
                            "id",
                            "person.nom",
                            "person.prenom",
                            "person.organisation",
                            "adresse.adresse",
                            "adresse.rue",
                            "adresse.no",
                            "adresse.npa",
                            "adresse.localite",
                            "adresse.pays",
                            "email"})
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='RESIDENCE_SECONDAIRE') as decTypeResient",
                                new String[] { "decTypeResient" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='LOCATION') as decTypeLocation",
                                new String[] { "decTypeLocation" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='INSTITUT') as decTypeInsitut",
                                new String[] { "decTypeInsitut" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='HOTEL') as decTypeHotel",
                                new String[] { "decTypeHotel" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='CHAMBRE') as decTypeChambre",
                                new String[] { "decTypeChambre" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(
                        CyberAdminProjection.sqlProjection(
                                "(select count(1) from BIENTAXE bi where bi.ASJ_ID = this_.ASJ_ID"
                                        + " AND bi.DECLARATION_TYPE='CAMPING') as decTypeCamping",
                                new String[] { "decTypeCamping" }, new Type[] { StandardBasicTypes.INTEGER }))
                .project(CyberAdminProjection.groupProperty("id"))
                .with(CyberAdminExpression.subqueryExists(subQuery));
        
        outerQuery.with(CyberAdminExpression.isNull("user").or(CyberAdminExpression.isFalse("user.validated")));
        return outerQuery;
    }
    
}
