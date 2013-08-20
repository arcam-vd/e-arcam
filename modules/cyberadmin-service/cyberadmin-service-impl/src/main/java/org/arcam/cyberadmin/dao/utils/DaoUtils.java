/*
 * DaoUtils.java
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

package org.arcam.cyberadmin.dao.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * Collections of utility methods that help dealing with DAO.
 * 
 * @author mmn
 *
 */
public final class DaoUtils {

    private DaoUtils() {
        // to hide
    }
    
    /**
     * Uses this method when you want to have a query "COLUMN = queryDate" but you don't want to interfere by the time
     * part.
     */
    public static Criterion equalIgnoreTime(String propertyName, Date queryDate) {
        Date dateNoTime = DateUtils.truncate(queryDate, Calendar.DATE);
        Date nextDayNoTime = DateUtils.addDays(dateNoTime, 1);

        // dateNoTime <= propertyName < nextDayNoTime
        return Restrictions.and(
                Restrictions.ge(propertyName, dateNoTime), Restrictions.lt(propertyName, nextDayNoTime));
    }
    
    /**
     * Uses this method when you want to have a query "COLUMN = queryDate" 
     * but you don't want to interfere by the time part.
     * 
     * <br>We want to compare <b><code>propertyName</code> >= <code>queryDate</code></b>.</br>
     */
    public static Criterion equalsOrGreaterThanIgnoreTime(String propertyName, Date queryDate) {
        Date dateNoTime = DateUtils.truncate(queryDate, Calendar.DATE);

        // >= dd.mm.yyyy 00:00:00
        return Restrictions.ge(propertyName, dateNoTime);
    }
    
    /**
     * Uses this method when you want to have a query "COLUMN = queryDate" but you don't want to interfere by the time
     * part.
     * 
     * <br>We want to compare <b><code>propertyName</code> <= <code>queryDate</code></b>.</br> 
     */
    public static Criterion equalsOrLessThanIgnoreTime(String propertyName, Date queryDate) {
        Date dateNoTime = DateUtils.truncate(queryDate, Calendar.DATE);
        Date nextDayNoTime = DateUtils.addDays(dateNoTime, 1);

        // < (dd.mm.yyyy 00:00:00 + 1d)
        return Restrictions.lt(propertyName, nextDayNoTime);
    }
    
    /**
     * Change fetch mode of the mapping specified by the <code>path</code> so that the association will be loaded at
     * the same time when the <code>criteria</code> is executed (e.g no more lazy).
     * And set ResultTransformer to DetachedCriteria.DISTINCT_ROOT_ENTITY to avoid duplicate result.
     * 
     * Using this method when you are sure the association will be used anyway after the query.
     */
    public static void fetchEager(String path, DetachedCriteria criteria) {
        criteria.setFetchMode(path, FetchMode.JOIN);
        criteria.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
    }
    
    /**
     * Change fetch mode of the mapping specified by the <code>path</code> to lazy.
     * 
     * Using this method when you are sure the association will be used anyway after the query.
     */
    public static void fetchLazy(String path, DetachedCriteria criteria) {
        criteria.setFetchMode(path, FetchMode.SELECT);
    }
}
