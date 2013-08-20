/*
 * LogServiceImpl.java
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

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.business.JournalCriteria;
import org.arcam.cyberadmin.dao.utils.DaoUtils;
import org.arcam.cyberadmin.dom.authorisation.Log;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.core.Auditable;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.LogService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.EntityComparisonUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The implementation of {@link LogService}.
 * 
 * @author mmn
 *
 */
@Service("logService")
@Transactional
public class LogServiceImpl extends GenericDataServiceDefaultImpl<Log> implements LogService {

    @Value("${log.msg.creation}")
    private String creationMessage;
    @Value("${log.msg.modification}")
    private String modificationMessage;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<Log> findByCriteria(JournalCriteria criteria, boolean isExport) {
        DetachedCriteria crit = buildSearchTaxpayerCriteria(criteria, isExport);
        if (isExport) {
            return daoFor(Log.class).findByCriteria(crit);
        }
        return daoFor(Log.class).findByCriteria(crit, criteria.getFirstResult(), criteria.getMaxResults());
    }

    @Override
    public int countByCriteria(JournalCriteria criteria) {
        DetachedCriteria crit = buildSearchTaxpayerCriteria(criteria, false);
        return daoFor(Log.class).findCountByCriteria(crit);
    }
    
    private DetachedCriteria buildSearchTaxpayerCriteria(JournalCriteria criteria, boolean isExport) {
        JournalCriteria.denormalizeFreetextCriteria(criteria);
        DetachedCriteria rootCrit = DetachedCriteria.forClass(Log.class);
        rootCrit.createAlias("user", "user");
        
        // add period from, to restriction
        Date from = criteria.getFrom();
        if (from != null) {
            rootCrit.add(DaoUtils.equalsOrGreaterThanIgnoreTime("timestamp", from));
        }
        Date to = criteria.getTo();
        if (to != null) {
            rootCrit.add(DaoUtils.equalsOrLessThanIgnoreTime("timestamp", to));
        }
        
        String freetext = criteria.getFreetext();
        if (StringUtils.isNotBlank(freetext)) {
            Disjunction disjunction = Restrictions.disjunction();

            disjunction.add(Restrictions.ilike("user.username", freetext, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("message", freetext, MatchMode.ANYWHERE));
            
            if (CollectionUtils.isNotEmpty(criteria.getUserTypes())) {
                disjunction.add(Restrictions.in("user.userType", criteria.getUserTypes()));
            }
            
            if (CollectionUtils.isNotEmpty(criteria.getJournalTypes())) {
                disjunction.add(Restrictions.in("messageType", criteria.getJournalTypes()));
            }

            rootCrit.add(disjunction);
        }
        
        if (isExport) {
            criteria.getAscs().add("id");
        }
        addOrderingInfo(rootCrit, criteria);
        return rootCrit;
    }

    @Override
    public void logModification(Auditable newObject, Auditable oldObject) {
        Log log = new Log();
        log.setEntityId(newObject.getId());
        log.setTimestamp(DateHelper.now());
        log.setUser(getCurrentUser());
        if (oldObject == null) {
            log.setMessageType(newObject.getCreationLogMessageType());
            log.setMessage(MessageFormat.format(creationMessage, newObject.getId()));
        } else {
            log.setMessageType(newObject.getModificationLogMessageType());
            StringBuilder message = new StringBuilder(MessageFormat.format(modificationMessage, newObject.getId()));
            message.append(newObject.getDifference(oldObject));
            log.setMessage(message.toString());
        }

        daoFor(Log.class).saveOrUpdate(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logForChangingDeclarationStatus(Long decId, StatusTypeEnum fromStatus, StatusTypeEnum toStatus) {
        Log log = new Log();
        log.setEntityId(decId);
        log.setTimestamp(DateHelper.now());
        log.setUser(getCurrentUser());
        log.setMessageType(toStatus.getModificationMessageType());
        
        StringBuilder message = new StringBuilder(MessageFormat.format(modificationMessage, decId));
        message = EntityComparisonUtils.buildMessageForChangingStatus(message, fromStatus, toStatus);
        log.setMessage(message.toString());
        
        // Save the log info.
        daoFor(Log.class).saveOrUpdate(log);
    }
    
    private User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null) {
            return userService.getSystemUser();
        } else {
            return ((CyberAdminUserDetail) context.getAuthentication().getPrincipal()).getUserInfo();
        }
    }
    
}
