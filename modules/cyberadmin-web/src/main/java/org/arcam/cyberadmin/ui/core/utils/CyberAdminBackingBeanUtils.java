/*
 * CyberAdminBackingBeanUtils.java
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

package org.arcam.cyberadmin.ui.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.criteria.business.JournalCriteria;
import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.JournalMessageTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;

/**
 * @author mmn
 *
 */
public final class CyberAdminBackingBeanUtils {

    private CyberAdminBackingBeanUtils() {
        // to hide
    }
    
    public static void matchingDeclarationAndStatusTypesFromResource(DeclarationCriteria criteria) {
        criteria.getDeclarationTypes().clear();
        for (DeclarationTypeEnum decType : DeclarationTypeEnum.values()) {
            String messageFromBundle = FacesUtils.getMessage(WebConstants.PREFIX_SHORT_DECLARATION_KEY + "."
                    + decType.name());
            if (messageFromBundle != null
                    && StringUtils.startsWithIgnoreCase(messageFromBundle, criteria.getFreetext())) {
                criteria.getDeclarationTypes().add(decType);
            }
        }
        
        criteria.getStatusTypes().clear();
        for (StatusTypeEnum type : StatusTypeEnum.values()) {
            String messageFromBundle = FacesUtils
                    .getMessage("cyberadmin.common.declaration.status" + "." + type.name());
            if (messageFromBundle != null
                    && StringUtils.startsWithIgnoreCase(messageFromBundle, criteria.getFreetext())) {
                criteria.getStatusTypes().add(type);
            }
        }
    }
    
    public static void matchingUserAndJournalTypesFromResource(JournalCriteria criteria) {
        criteria.getUserTypes().clear();
        for (UserTypeEnum userType : UserTypeEnum.values()) {
            String messageFromBundle = FacesUtils.getMessage(WebConstants.PREFIX_TYPE_USER + "." + userType.name());
            if (messageFromBundle != null
                    && StringUtils.startsWithIgnoreCase(messageFromBundle, criteria.getFreetext())) {
                criteria.getUserTypes().add(userType);
            }
        }
        
        criteria.getJournalTypes().clear();
        for (JournalMessageTypeEnum type : JournalMessageTypeEnum.values()) {
            String messageFromBundle = FacesUtils.getMessage(WebConstants.PREFIX_JOURNAL_TYPE + "." + type.name());
            if (messageFromBundle != null
                    && StringUtils.startsWithIgnoreCase(messageFromBundle, criteria.getFreetext())) {
                criteria.getJournalTypes().add(type);
            }
        }
    }
    
    public static void matchingUserTypesFromResource(UserCriteria criteria) {
        criteria.getUserTypes().clear();
        for (UserTypeEnum userType : UserTypeEnum.values()) {
            String messageFromBundle = FacesUtils.getMessage(WebConstants.PREFIX_TYPE_USER + "." + userType.name());
            if (messageFromBundle != null
                    && StringUtils.startsWithIgnoreCase(messageFromBundle, criteria.getFreetext())) {
                criteria.getUserTypes().add(userType);
            }
        }
    }
}
