/*
 * ReferenceDataServiceImpl
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

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.arcam.cyberadmin.dao.utils.DaoUtils;
import org.arcam.cyberadmin.dom.core.AbstractReferenceEntity;
import org.arcam.cyberadmin.dom.reference.Commune;
import org.arcam.cyberadmin.dom.reference.Country;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.dom.reference.Tarif;
import org.arcam.cyberadmin.service.core.ReferenceDataService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ReferenceDataService}.
 * @author nln
 *
 */
@Service("referenceDataService")
@Transactional
public class ReferenceDataServiceImpl extends GenericDataServiceDefaultImpl<AbstractReferenceEntity> implements
        ReferenceDataService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Commune> getAllCommune() {
        return daoFor(Commune.class).getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Country> getAllCountry(String language) {
        DetachedCriteria hibernateCriteria = DetachedCriteria.forClass(Country.class).add(
                Restrictions.eq("languageCode", language));
        
        return daoFor(Country.class).findByCriteria(hibernateCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Localite> getAllLocalite() {
        return daoFor(Localite.class).getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Country getCountry(String code, String language) {
        DetachedCriteria hibernateCriteria = DetachedCriteria.forClass(Country.class).add(
                Restrictions.eq("languageCode", language))
                .add(Restrictions.eq("code", code));
        
        List<Country> countries = daoFor(Country.class).findByCriteria(hibernateCriteria);
        if (!countries.isEmpty()) {
            return countries.get(0);
        }
        return null;
    }

    @Override
    public Tarif getTarif(Date queryDate) {
        DetachedCriteria hibernateCriteria = DetachedCriteria.forClass(Tarif.class)
                .add(DaoUtils.equalsOrLessThanIgnoreTime("dateDebut", queryDate))
                .add(DaoUtils.equalsOrGreaterThanIgnoreTime("dateFin", queryDate))
                .addOrder(Order.desc("dateFin"));
        List<Tarif> tarifs = daoFor(Tarif.class).findByCriteria(hibernateCriteria);
        if (CollectionUtils.isNotEmpty(tarifs)) {
            return tarifs.get(0);
        }
        
        hibernateCriteria = DetachedCriteria.forClass(Tarif.class).addOrder(Order.asc("dateDebut"));
        tarifs = daoFor(Tarif.class).findByCriteria(hibernateCriteria, 0, 1);
        if (CollectionUtils.isEmpty(tarifs)) {
            throw new DataRetrievalFailureException("Can not find the tarification for " + queryDate + " in the DB.");
        }
        return tarifs.get(0);
    }
}
