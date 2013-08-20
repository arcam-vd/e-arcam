/*
 * UserService.java
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

package org.arcam.cyberadmin.service.core;

import java.util.List;
import java.util.Set;

import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service for Cyber Admin users and taxpayer.
 * 
 * @author mmn
 *
 */
public interface UserService extends GenericDataService<User> {

    /**
     * Gets the user by username or email.
     * 
     * @param userNameOrEmail 
     *              username or email.
     * @param allowSystemUser
     *              A flag indicates that if the system account can logon the application.
     *              
     * @return the user detail object.
     */
    UserDetails logon(String userNameOrEmail, boolean allowSystemUser);
    
    /**
     * Gets assujetti and its bien taxes by the given id.
     * 
     * @param id
     *            id of the assujetti.
     * @return
     */
    Assujetti getAssujettiAndBienTaxes(long id);
    
    /**
     * Find the amount of users which <code>field</code>'s value equal <code>value</code> in parameter.
     * @param field
     * @param value
     * @return
     */
    int countByField(String field, String value, Long entityIdToIgnore);
    
    /**
     * Counts the number of users which match the given criteria.
     * 
     * @param criteria the user criteria.
     * @return
     */
    int countByCriteria(UserCriteria criteria);
    
    /**
     * Finds all users which match the given criteria.
     * 
     * @param criteria the user criteria.
     *        isExport flag for function exporting excel.              
     * @return list of users.
     */
    List<User> findByCriteria(UserCriteria criteria, boolean isExport);
    
    /**
     * Finds all commune user email addresses within the given commune.
     * 
     * @param communeCode the commune code.
     * @return
     */
    Set<String> getCommuneUserMailAddressesIn(String communeCode);
    
    /**
     * Find all user email which will be sent to after a declaration is refused or 'Signaler a la Commune'.
     * All the COM users of the right commune and the "arcam" GES/ADM are notified by email
     * @return
     */
    Set<String> getGetCommuneAndArcamUserMailAddress(String communeCode);

    /**
     * Find all user is created by arcam with role Ges or Adm.
     * @param criteria
     * @return
     */
    List<User> getUsersArcamAsGesAdm();
    
    /**
     * @return the mail address of system user.
     */
    User getSystemUser();
    
    /**
     * @return the anonymous user, the user represents for an unauthorized user.
     */
    User getAnonymousUser();
    
    /**
     * The taxpayer reference in the BIENTAXE objects of the selectedTaxpayer, will be changed with a
     * reference to the TAXPAYER of the user to be validated. The « old » TAXPAYER object, that has no more
     * declarations, will be removed from the system.
     * 
     * @param user
     * @param selectedTaxpayer
     * @return
     */
    User associateWithSelectedTaxpayer(User user, Assujetti selectedTaxpayer);
    
    /**
     * Saves or updates the given user into database and then log the changes into LOG table.
     * 
     * @param user
     * @return the up-to-date user.
     */
    User saveAndLog(User user);
}
