/*
 * TaxpayerServiceImpl.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.arcam.cyberadmin.criteria.business.TaxpayerCriteria;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.service.core.LogService;
import org.arcam.cyberadmin.service.core.TaxpayerService;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The implementation for {@link TaxpayerService}.
 * 
 * @author mmn
 * 
 */
@Service("taxpayerService")
@Transactional
public class TaxpayerServiceImpl extends GenericDataServiceDefaultImpl<Assujetti> implements TaxpayerService {

    @Autowired
    private LogService logService;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Assujetti> findByCriteria(TaxpayerCriteria criteria, boolean isExport) {
        DetachedCriteria hCrit = buildSearchTaxpayerCriteria(criteria, isExport);
        if (isExport) {
            return daoFor(Assujetti.class).findByCriteria(hCrit);
        }
        return daoFor(Assujetti.class).findByCriteria(hCrit, criteria.getFirstResult(), criteria.getMaxResults());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByCriteria(TaxpayerCriteria criteria) {
        DetachedCriteria hCrit = buildSearchTaxpayerCriteria(criteria, false);
        return daoFor(Assujetti.class).findCountByCriteria(hCrit);
    }

    private DetachedCriteria buildSearchTaxpayerCriteria(TaxpayerCriteria criteria, boolean isExport) {
        TaxpayerCriteria.denormalizeFreetextCriteria(criteria);
        DetachedCriteria rootCrit = DetachedCriteria.forClass(Assujetti.class);
        rootCrit.createAlias("person", "person");
        rootCrit.createAlias("adresse", "adresse");

        String freetext = criteria.getFreetext();
        if (StringUtils.isNotBlank(freetext)) {
            Disjunction disjunction = Restrictions.disjunction();

            // querying in person's infor
            disjunction.add(Restrictions.ilike("person.nom", freetext, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("person.prenom", freetext, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("person.organisation", freetext, MatchMode.ANYWHERE));

            // querying in address's infor
            disjunction.add(Restrictions.ilike("adresse.localite", freetext, MatchMode.ANYWHERE));
            // Address combines Rue and No
            disjunction.add(Restrictions.ilike("address", freetext, MatchMode.ANYWHERE));

            if (NumberUtils.isDigits(freetext)) {
                disjunction.add(Restrictions.eq("id", new Long(freetext)));
            }

            rootCrit.add(disjunction);
        }
        
        // Search by user associated id if needed.
        if (BooleanUtils.isTrue(criteria.getOnlyTaxpayerWithoutUser())) {
            rootCrit.createAlias("user", "user", Criteria.LEFT_JOIN);
            rootCrit.add(Restrictions.isNull("user.id"));
        }

        addOrderingInfo(rootCrit, criteria);
        return rootCrit;
    }

    @Override
    public Assujetti saveAndLog(Assujetti assujetti) {
        assujetti.setUpdated(true);
        // load the old one from database
        Assujetti oldTaxpayer = null;
        if (assujetti.isPersisted()) {
            oldTaxpayer = findById(assujetti.getId());
            // Remove oldTaxpayer from session to avoid hibernate exception while saving user.
            sessionFactory.getCurrentSession().evict(oldTaxpayer);
        }
        
        daoFor(Assujetti.class).saveOrUpdate(assujetti);
        logService.logModification(assujetti, oldTaxpayer);
        return assujetti;
    }

    @Override
    public Assujetti load(long id) {
        return daoFor(Assujetti.class).findById(id);
    }

    @Override
    public Assujetti prepare() {
        Assujetti assujetti = new Assujetti();

        Person person = new Person();
        assujetti.setPerson(person);
        person.setAssujetti(assujetti);
        
        Adresse adresse = new Adresse();
        adresse.setPays(CyberAdminConstants.SWITZERLAND_CODE);
        assujetti.setAdresse(adresse);
        
        return assujetti; 
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Map<Integer, Integer> findUnexportedTaxpayerIds() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Assujetti.class);
        criteria.add(Restrictions.eq("updated", true));
        ProjectionList proList = Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("version"));
        criteria.setProjection(proList);
        
        List ids = daoFor(Assujetti.class).findByCriteria(criteria);
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Object item : ids) {
            Object[] row = (Object[]) item;
            result.put(((Long) row[0]).intValue(), (Integer) row[1]);
        }
        
        return result;
    }

    @Override
    public int updateExportedTaxpayers(Integer id, Integer version) {
        if (id == null || version == null) {
            // No row is updated
            return 0;
        }
        
        Session session = sessionFactory.getCurrentSession();
        Query query = session
                .createSQLQuery(
                        "update ASSUJETTI set UPDATED = 'F', VERSION = :version + 1 "
                        + "where ASJ_ID = :id and VERSION = :version")
                    .setParameter("version", version)
                    .setParameter("id", id);
        return query.executeUpdate();
    }
    
}
