/*
 * BienTaxeServiceImpl.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.arcam.cyberadmin.dao.utils.DaoUtils;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.service.core.BienTaxeService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of {@link BienTaxeService}.
 * 
 * @author mmn
 *
 */
@Service("bienTaxeService")
@Transactional
public class BienTaxeServiceImpl extends GenericDataServiceDefaultImpl<BienTaxe> implements BienTaxeService {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Long> getPeriodicityBientaxeIds() {
        DetachedCriteria criteria = DetachedCriteria.forClass(BienTaxe.class);
        criteria.add(Restrictions.ne("periodiciteCode", PeriodiciteTypeEnum.NONE));
        ProjectionList proList = Projections.projectionList().add(Projections.property("id"));
        criteria.setProjection(proList);
        
        List ids = daoFor(BienTaxe.class).findByCriteria(criteria);
        return new HashSet<Long>(ids);
    }

    @Override
    public BienTaxe findByIdWithDeclarationList(long id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(BienTaxe.class);
        criteria.add(Restrictions.eq("id", id));
        DaoUtils.fetchEager("declarations", criteria);
        
        List<BienTaxe> bienTaxes = daoFor(BienTaxe.class).findByCriteria(criteria);
        if (CollectionUtils.isEmpty(bienTaxes)) {
            throw new IllegalArgumentException("Can not find the Bien Taxe with id=" + id + " in database.");
        }
        
        return bienTaxes.get(0);
    }

}
