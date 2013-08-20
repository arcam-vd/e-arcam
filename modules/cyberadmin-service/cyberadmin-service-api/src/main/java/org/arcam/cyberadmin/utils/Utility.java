/*
 * Utility
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

package org.arcam.cyberadmin.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Copiable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * Centralize all utility methods to be used in CyberAdmin.
 * 
 * @author dtl
 * 
 */
public final class Utility {
    private Utility() {
        // To hide default constructor.
    }
    
    /**
     * Using to encode password of user before saving to database.
     * @param password
     * @return
     */
    public static String encodePassword(String password) {
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        return passwordEncoder.encodePassword(password, null);
    }
    
    /**
     * Return list of ids from entities.
     * @param entities
     * @return
     */
    public static <T extends AbstractCyberAdminEntity> List<Long> collectEntityIds(Collection<T> entities) {
        if (entities == null) {
            return Collections.<Long> emptyList();
        }
        List<Long> ids = new ArrayList<Long>(entities.size());
        for (T entity : entities) {
            ids.add(entity.getId());
        }
        return ids;
    }
    
    /**
     * Retrieve the email address from user. If user is null, the default system email is returned.
     * @param user
     * @return
     */
    public static String getEmailFromUserDefaultIfEmpty(User user) {
        if (user == null) {
            return CyberAdminConstants.DEFAULT_SYSTEM_MAIL;
        }
        return user.getEmail();
    }
    
    /**
     * Execute copyChangableProperties to copy some properties from <<from>> to <<to>>.
     * @param from
     * @param to : MUST BE NOT NULL.
     * @return to after copied.
     */
    public static  <T extends Copiable<T>> T copyProperties(T from, T to) {
        if (from != null) {
            to.copyChangeableProperties(from);
            return to;
        } else {
            return null;
        }
    }
    
    public static String truncate(String text, int maxLen) {
        if (StringUtils.isEmpty(text) || text.length() <= maxLen) {
            return text;
        }
        return StringUtils.substring(text, 0, maxLen) + CyberAdminConstants.TRUNCATE_MARK;
    }
}
