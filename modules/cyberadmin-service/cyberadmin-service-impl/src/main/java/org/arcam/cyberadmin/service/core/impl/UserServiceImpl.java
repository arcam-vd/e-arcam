/*
 * UserServiceImpl.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dao.utils.DaoUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.dom.reference.Role;
import org.arcam.cyberadmin.service.core.LogService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implement the method to find the logged user when login in db.
 * 
 * @author dtl
 * 
 */
@Service("userService")
@Transactional
public class UserServiceImpl extends GenericDataServiceDefaultImpl<User> implements UserService, UserDetailsService {
    
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private LogService logService; 
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetails logon(String userNameOrEmail, boolean allowSystemUser) {
        CyberAdminUserDetail userDetail = null;
        // Query an user from db which matches the login information.
        // Should allow to query "disabled" user, after that let Spring security check the status of user to show the
        // correct message.
        DetachedCriteria hibernateCriteria = DetachedCriteria
                .forClass(User.class)
                .add(Restrictions.or(Restrictions.eq("username", userNameOrEmail),
                        Restrictions.eq("email", userNameOrEmail)));
        // ARCANCYBERADM-100: System account should not be able to logon the application
        if (!allowSystemUser) {
            hibernateCriteria.add(Restrictions.ne("userType", UserTypeEnum.SYS));
        }

        List<User> users = getDefaultDao().findByCriteria(hibernateCriteria);
        if (CollectionUtils.isNotEmpty(users)) {
            final User user = users.get(0);
            // Granted authority created from user type
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            SimpleGrantedAuthority userTypeAuthority = new SimpleGrantedAuthority(user.getUserType().name());
            authorities.add(userTypeAuthority);

            // Get all business functions for this user.
            List<Role> roles = getAllRoleForUserType(user.getUserType());
            for (Role role : roles) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getPermission().name());
                authorities.add(authority);
            }
            userDetail = new CyberAdminUserDetail(user.getUsername(), user.getPassword(), user.getActivated(), true,
                    true, true, authorities);
            userDetail.setUserInfo(user);
            if (user.getActivated()) {
                // Update user logon date. Be sure that in this case user will be able to login. So, update the
                // lastLogonDate.
                user.setLastLogOnDate(DateHelper.now());
                daoFor(User.class).saveOrUpdate(user);
            }
           
        } else {
            throw new UsernameNotFoundException(userNameOrEmail);
        }
        return userDetail;
    }

    private List<Role> getAllRoleForUserType(UserTypeEnum userType) {
        DetachedCriteria hibernateCriteria = DetachedCriteria.forClass(Role.class).add(
                Restrictions.eq("role", userType));

        return daoFor(Role.class).findByCriteria(hibernateCriteria);
    }

    @Override
    public Assujetti getAssujettiAndBienTaxes(long id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Assujetti.class).add(Restrictions.eq("id", id));
        DaoUtils.fetchEager("bienTaxes", criteria);

        List<Assujetti> assujettis = daoFor(Assujetti.class).findByCriteria(criteria);
        if (CollectionUtils.isEmpty(assujettis)) {
            throw new DataRetrievalFailureException("Can not find the assujetti with id=" + id + " in the DB.");
        }
        return assujettis.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByField(String field, String value, Long entityIdToIgnore) {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.ilike(field, value, MatchMode.EXACT));
        if (entityIdToIgnore != null) {
            criteria.add(Restrictions.ne("id", entityIdToIgnore));
        }
        return daoFor(User.class).findCountByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByCriteria(UserCriteria criteria) {
        DetachedCriteria hCrit = buildSearchUserCriteria(criteria, false);
        return daoFor(User.class).findCountByCriteria(hCrit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByCriteria(UserCriteria criteria, boolean isExport) {
        DetachedCriteria hCrit = buildSearchUserCriteria(criteria, isExport);
        if (isExport) {
            return daoFor(User.class).findByCriteria(hCrit);
        }
        return daoFor(User.class).findByCriteria(hCrit, criteria.getFirstResult(), criteria.getMaxResults());
    }
    
    /**
     * Build a DetachedCriteria from input criteria.
     * @param criteria 
     * @return a DetachedCriteria to be searched on.
     */
    private DetachedCriteria buildSearchUserCriteria(UserCriteria criteria, boolean isExport) {
        DetachedCriteria rootCriteria = DetachedCriteria.forClass(User.class);
        rootCriteria.createAlias("person", "person");
        rootCriteria.createAlias("assujetti", "assujetti", Criteria.LEFT_JOIN);
        rootCriteria.createAlias("assujetti.adresse", "adresse", Criteria.LEFT_JOIN);
        String freetext = criteria.getFreetext();
        if (StringUtils.isNotBlank(freetext)) {
            // This case for user list screen.
            Disjunction disjunction = Restrictions.disjunction();

            disjunction.add(Restrictions.ilike("username", freetext, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("email", freetext, MatchMode.ANYWHERE));
            if (!criteria.getUserTypes().isEmpty()) {
                disjunction.add(Restrictions.in("userType", criteria.getUserTypes()));
            }
            disjunction.add(Restrictions.ilike("person.nom", freetext, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("person.prenom", freetext, MatchMode.ANYWHERE));
            if (NumberUtils.isDigits(freetext)) {
                disjunction.add(Restrictions.eq("id", new Long(freetext)));
            }

            rootCriteria.add(disjunction);
        } else if (!criteria.getUserTypes().isEmpty()) {
            rootCriteria.add(Restrictions.in("userType", criteria.getUserTypes()));
        }
        
        // Search by 'validated' value if being set.
        if (criteria.getValidated() != null) {
            rootCriteria.add(Restrictions.eq("validated", criteria.getValidated().booleanValue()));
        }
        
        // Search by 'activated' value if being set.
        if (criteria.getActivated() != null) {
            rootCriteria.add(Restrictions.eq("activated", criteria.getActivated().booleanValue()));
        }
        // Search by 'email' value if being set.
        if (criteria.getEmail() != null) {
            rootCriteria.add(Restrictions.eq("email", criteria.getEmail()));
        }
        
        addOrderingInfo(rootCriteria, criteria);
        return rootCriteria;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = logon(username, false);
        if (user == null) {
            throw new UsernameNotFoundException("User with username or email is " + username + " does not exist "
                    + "or invalided or inactice.");
        }
        return user;
    }

    @Override
    public Set<String> getCommuneUserMailAddressesIn(String communeCode) {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("communeCode", communeCode));
        criteria.add(Restrictions.eq("userType", UserTypeEnum.COM));
        
        return buildEmailResults(criteria);
    }

    private Set<String> buildEmailResults(DetachedCriteria criteria) {
        List<User> users = daoFor(User.class).findByCriteria(criteria);
        Set<String> results = new HashSet<String>();
        for (User user : users) {
            results.add(user.getEmail());
        }
        
        return results;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getGetCommuneAndArcamUserMailAddress(String communeCode) {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        // Prepare or expression. It contains 2 AND EXPRESSION.
        Disjunction orExp = Restrictions.disjunction();
        Conjunction andExp1 = Restrictions.conjunction();
        // All the COM users of the right commune
        andExp1.add(Restrictions.eq("communeCode", communeCode));
        andExp1.add(Restrictions.eq("userType", UserTypeEnum.COM));
        Conjunction andExp2 = Restrictions.conjunction();
        // The "arcam" GES/ADM are notified by email
        andExp2.add(Restrictions.eq("arcam", true));
        andExp2.add(Restrictions.in("userType", Arrays.asList(UserTypeEnum.ADM, UserTypeEnum.GES)));
        // Add expression to final criteria. And do execute it.
        orExp.add(andExp1);
        orExp.add(andExp2);
        criteria.add(orExp);
        return buildEmailResults(criteria);
    }

    @Override
    public User getSystemUser() {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("userType", UserTypeEnum.SYS));
        
        List<User> users = daoFor(User.class).findByCriteria(criteria);
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        
        return users.get(0);
    }

    @Override
    public User getAnonymousUser() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User associateWithSelectedTaxpayer(User user, Assujetti selectedTaxpayer) {
        if (user == null || selectedTaxpayer == null) {
            // Nothing will be done if one of parameters is null.
            return null;
        }
        updateBienTaxeToNewTaxpayer(user, selectedTaxpayer);
        removeSelectedTaxpayerIfNecessary(selectedTaxpayer);
        
        // Save current user.
        return saveAndLog(user);
    }

    private void updateBienTaxeToNewTaxpayer(User user, Assujetti selectedTaxpayer) {
        // Update reference of Assujetti from BIENTAXE table to the new Assujetti.
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("update BIENTAXE set ASJ_ID = :newAsjId where ASJ_ID = :currentAsjId")
                             .setParameter("newAsjId", user.getAssujetti().getId())
                             .setParameter("currentAsjId", selectedTaxpayer.getId());
        query.executeUpdate();
    }

    private void removeSelectedTaxpayerIfNecessary(Assujetti selectedTaxpayer) {
        // Check if selectedTaxpayer has no more declarations , then remove it.
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        criteria.add(Restrictions.eq("adresse.id", selectedTaxpayer.getAdresse().getId()));
        if (daoFor(Declaration.class).findCountByCriteria(criteria) == 0) {
            // Have no more declarations.
            daoFor(Assujetti.class).delete(selectedTaxpayer);
        }
    }

    @Override
    public User saveAndLog(User user) {
        // load the old one from database
        User oldUser = null;
        if (user.isPersisted()) {
            oldUser = findById(user.getId());
            // Remove oldUser from session to avoid hibernate exception while saving user.
            sessionFactory.getCurrentSession().evict(oldUser);
        }
        
        daoFor(User.class).saveOrUpdate(user);
        logService.logModification(user, oldUser);
        return user;
    }

    @Override
    public List<User> getUsersArcamAsGesAdm() {
        DetachedCriteria hibernateCriteria = DetachedCriteria.forClass(User.class);
        hibernateCriteria.add(Restrictions.eq("arcam", Boolean.TRUE));
        hibernateCriteria.add(Restrictions.or(Restrictions.eq("userType", UserTypeEnum.GES),
                    Restrictions.eq("userType", UserTypeEnum.ADM)));
        return daoFor(User.class).findByCriteria(hibernateCriteria);
    }

}
