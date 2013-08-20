/*
 * CyberAdminUserDetail
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

package org.arcam.cyberadmin.dom.core;

import java.util.Collection;

import org.arcam.cyberadmin.dom.authorisation.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Object represents information of Cyber Admin users.
 * 
 * @author mmn
 *
 */
public class CyberAdminUserDetail extends User {

    private static final long serialVersionUID = 1L;
    
    private org.arcam.cyberadmin.dom.authorisation.User userInfo;
    
    public CyberAdminUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
    
    public CyberAdminUserDetail(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public org.arcam.cyberadmin.dom.authorisation.User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(org.arcam.cyberadmin.dom.authorisation.User userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isAssujetti() {
        return userInfo.getUserType() == UserTypeEnum.ASJ;
    }
    
    public boolean isCommune() {
        return userInfo.getUserType() == UserTypeEnum.COM;
    }
    
    public boolean isGestionnaire() {
        return userInfo.getUserType() == UserTypeEnum.GES;
    }
    
    public boolean isAdministrator() {
        return userInfo.getUserType() == UserTypeEnum.ADM;
    }
    
    public boolean isValidated() {
        return userInfo.getValidated();
    }
    
    public String getFullname() {
        Person person = getUserInfo().getPerson();
        return person.getPrenom() + " " + person.getNom();
    }
}
