/*
 * AccountValidationListBean
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

package org.arcam.cyberadmin.ui.bean.utilisateur;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;

/**
 * Managing all actions relating to validate accounts screen.
 * @author dtl
 *
 */
@ManagedBean(name = "accountValidationListBean")
@ViewScoped
public class AccountValidationListBean extends UserListBean {
    private static final long serialVersionUID = 1L;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected UserCriteria instantiateCriteria() {
        return buildCriteria();
    }
    
    /**
     * Move to static method to be reused when count the number of to-be-validated taxpayers.
     * @return
     */
    public static UserCriteria buildCriteria() {
        UserCriteria userCriteria = new UserCriteria();
        userCriteria.setValidated(false);
        userCriteria.setActivated(true);
        userCriteria.getUserTypes().add(UserTypeEnum.ASJ);
        return userCriteria;
    }
}
