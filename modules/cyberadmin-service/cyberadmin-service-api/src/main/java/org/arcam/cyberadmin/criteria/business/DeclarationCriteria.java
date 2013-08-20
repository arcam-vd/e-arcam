/*
 * DeclarationCriteria.java
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

package org.arcam.cyberadmin.criteria.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.core.CyberAdminCriteria;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;

/**
 * Criteria for querying {@link Declaration}.
 * 
 * @author mmn
 *
 */
public class DeclarationCriteria extends CyberAdminCriteria {

    /**
     * Enum contains all possible search mode on {@link Declaration}.
     * 
     * @author mmn
     *
     */
    public static enum SearchMode {
        /**
         * Search mode with a free text and a period.
         */
        QUICK,
        /**
         * Search mode with many criteria: denomination, assujetti information, ...
         */
        FULL,
    }
    
    private static final long serialVersionUID = 1L;

    private User currentUser;
    private String freetext;
    private Date from;
    private Date to;
    private String commune;
    private DeclarationTypeEnum type;
    private StatusTypeEnum status;
    private String denomination;
    private String name;
    private String firstname;
    private String organisation;
    private SearchMode searchMode;
    
    private List<DeclarationTypeEnum> declarationTypes = new ArrayList<DeclarationTypeEnum>();
    private List<StatusTypeEnum> statusTypes = new ArrayList<StatusTypeEnum>();

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getFreetext() {
        return freetext;
    }

    public void setFreetext(String freetext) {
        this.freetext = freetext;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public DeclarationTypeEnum getType() {
        return type;
    }

    public void setType(DeclarationTypeEnum type) {
        this.type = type;
    }

    public StatusTypeEnum getStatus() {
        return status;
    }

    public void setStatus(StatusTypeEnum status) {
        this.status = status;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }
    
    public List<DeclarationTypeEnum> getDeclarationTypes() {
        return declarationTypes;
    }

    public List<StatusTypeEnum> getStatusTypes() {
        return statusTypes;
    }

    /**
     * Makes all freetext restrictions in the given criteria become wildcard search pattern (%<text>%).
     * 
     * @param criteria
     */
    public static void denormalizeFreetextCriteria(DeclarationCriteria criteria) {
        if (StringUtils.isNotBlank(criteria.getFreetext())) {
            criteria.setFreetext(StringUtils.trim(criteria.getFreetext()));
        }
        
        if (StringUtils.isNotBlank(criteria.getDenomination())) {
            criteria.setDenomination(StringUtils.trim(criteria.getDenomination()));
        }
        
        if (StringUtils.isNotBlank(criteria.getName())) {
            criteria.setName(StringUtils.trim(criteria.getName()));
        }
        
        if (StringUtils.isNotBlank(criteria.getFirstname())) {
            criteria.setFirstname(StringUtils.trim(criteria.getFirstname()));
        }
        
        if (StringUtils.isNotBlank(criteria.getOrganisation())) {
            criteria.setOrganisation(StringUtils.trim(criteria.getOrganisation()));
        }
    }
}
