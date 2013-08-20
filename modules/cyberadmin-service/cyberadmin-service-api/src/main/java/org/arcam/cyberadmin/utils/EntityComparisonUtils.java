/*
 * EntityComparisonUtils.java
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

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;

/**
 * @author mmn
 *
 */
public final class EntityComparisonUtils {

    private EntityComparisonUtils() {
        // to hide
    }
    
    public static String getDifference(Assujetti newVal, Assujetti oldVal) {
        StringBuilder result = new StringBuilder();
        
        // person is never null in taxpayer
        Person newPerson = newVal.getPerson();
        Person oldPerson = oldVal.getPerson();
        result.append(getDifference(newPerson, oldPerson));
        
        Adresse newAddress = newVal.getAdresse();
        Adresse oldAddress = oldVal.getAdresse();
        result.append(getDifferences(newAddress, oldAddress));
        
        User newUser = newVal.getUser();
        User oldUser = oldVal.getUser();
        if (newUser != null && oldUser != null) {
            result.append(getDifference(newUser, oldUser, false));
        }
        
        return result.toString();
    }

    public static String getDifferences(Adresse newVal, Adresse oldVal) {
        StringBuilder result = new StringBuilder();
        
        result.append(getDifference(newVal.getAdresse(), oldVal.getAdresse(), "Adresse"));
        result.append(getDifference(newVal.getRue(), oldVal.getRue(), "Rue"));
        result.append(getDifference(newVal.getNo(), oldVal.getNo(), "No"));
        result.append(getDifference(newVal.getLocalite(), oldVal.getLocalite(), "Localite"));
        result.append(getDifference(newVal.getNpa(), oldVal.getNpa(), "NPA"));
        result.append(getDifference(newVal.getPays(), oldVal.getPays(), "Pays"));
        result.append(getDifference(newVal.getTelephone(), oldVal.getTelephone(), "Telephone"));
        result.append(getDifference(newVal.getEmail(), oldVal.getEmail(), "Email"));
        
        return result.toString();
    }

    public static String getDifference(Person newVal, Person oldVal) {
        StringBuilder result = new StringBuilder();
        
        result.append(getDifference(newVal.getNom(), oldVal.getNom(), "Person nom"));
        result.append(getDifference(newVal.getPrenom(), oldVal.getPrenom(), "Person prenom"));
        result.append(getDifference(newVal.getOrganisation(), oldVal.getOrganisation(), "Person organisation"));
        
        return result.toString();
    }
    
    public static String getDifference(User newVal, User oldVal, boolean includeTaxpayer) {
        StringBuilder result = new StringBuilder();
        
        // person is never null in taxpayer
        Person newPerson = newVal.getPerson();
        Person oldPerson = oldVal.getPerson();
        result.append(getDifference(newPerson, oldPerson));
        
        if (includeTaxpayer && newVal.getAssujetti() != null && oldVal.getAssujetti() != null) {
            result.append(getDifference(newVal.getAssujetti(), oldVal.getAssujetti()));
        }
        
        result.append(getDifference(newVal.getCommuneCode(), oldVal.getCommuneCode(), "Utilisateur commune"));
        result.append(getDifference(newVal.getEmail(), oldVal.getEmail(), "Utilisateur email"));
        result.append(getDifference(newVal.getTelephone(), oldVal.getTelephone(), "Utilisateur telephone"));
        result.append(getDifference(newVal.getActivated(), oldVal.getActivated(), "Utilisateur activated"));
        result.append(getDifference(newVal.getValidated(), oldVal.getValidated(), "Utilisateur validated"));
        result.append(getDifference(newVal.getArcam(), oldVal.getArcam(), "Utilisateur arcam"));
        
        return result.toString();
    }
    
    /**
     * Util to build the message for changing status.
     * This method can be reused when save manually a declaration.
     * @param result
     * @param fromStt
     * @param toStt
     * @return
     */
    public static StringBuilder buildMessageForChangingStatus(StringBuilder result, StatusTypeEnum fromStt,
                                                              StatusTypeEnum toStt) {
        if (result == null) {
            result = new StringBuilder();
        }
        
        return result.append("\n")
                     .append("Status ")
                     .append("Ancien: ").append(fromStt)
                     .append("; ")
                     .append("Nouveau: ").append(toStt);
    }
    
    public static String getDifference(Declaration newVal, Declaration oldVal) {
        StringBuilder result = new StringBuilder();
        
        // address is never null in declaration
        Adresse newAddress = newVal.getAdresse();
        Adresse oldAddress = oldVal.getAdresse();
        result.append(getDifferences(newAddress, oldAddress));

        if (newVal.getStatus() != oldVal.getStatus()) {
            result = buildMessageForChangingStatus(result, oldVal.getStatus(), newVal.getStatus());
        }
        
        result.append(getDifference(newVal.getFiscaleDate(), oldVal.getFiscaleDate(), "Fiscale date"));
        result.append(getDifference(newVal.getDueDate(), oldVal.getDueDate(), "Due date"));
        result.append(getDifference(newVal.getDepartDate(), oldVal.getDepartDate(), "Depart date"));
        result.append(getDifference(newVal.getSubmissionDate(), oldVal.getSubmissionDate(), "Submission date"));
        result.append(getDifference(newVal.getAjustComment(), oldVal.getAjustComment(), "Ajustement commentaire"));
        result.append(getDifference(newVal.getUserComment(), oldVal.getUserComment(), "Utilisateur commentaire"));
        result.append(getDifference(newVal.getAjustValue(), oldVal.getAjustValue(), "Ajustement montant"));
        result.append(getDifference(NumberUtils.defaultIfNull(newVal.getEstimation(), 0L),
                NumberUtils.defaultIfNull(oldVal.getEstimation(), 0L), "Estimation"));
        result.append(getDifference(newVal.getNom(), oldVal.getNom(), "Declaration nom"));
        result.append(getDifference(newVal.getPrenom(), oldVal.getPrenom(), "Declaration prenom"));
        
        if (newVal.getExoneration() != oldVal.getExoneration()) {
            result.append("\n")
                    .append("Exoneration ")
                    .append("Ancien: ").append(oldVal.getExoneration())
                    .append("; ")
                    .append("Nouveau: ").append(newVal.getExoneration());
        }

        result.append(getDifference(newVal.getLocation(), oldVal.getLocation(), "Location"));
        result.append(getDifference(newVal.getTaille(), oldVal.getTaille(), "Taille"));
        result.append(getDifference(newVal.getGuestExemptions(), oldVal.getGuestExemptions()));
        
        return result.toString();
    }
    
    private static String getDifference(Date newVal, Date oldVal, String fieldname) {
        StringBuilder result = new StringBuilder();
        
        if (DateHelper.compare(newVal, oldVal) != 0) {
            result.append("\n")
                    .append(fieldname).append(" ")
                    .append("Ancien: ").append(DateHelper.getFormattedDate(oldVal))
                    .append("; ")
                    .append("Nouveau: ").append(DateHelper.getFormattedDate(newVal));
        }
        
        return result.toString();
    }
    
    private static String getDifference(String newVal, String oldVal, String fieldname) {
        StringBuilder result = new StringBuilder();
        
        if (!StringUtils.equals(newVal, oldVal)) {
            result.append("\n")
                    .append(fieldname).append(" ")
                    .append("Ancien: ").append(StringUtils.defaultString(oldVal))
                    .append("; ")
                    .append("Nouveau: ").append(StringUtils.defaultString(newVal));
        }
        
        return result.toString();
    }
    
    private static String getDifference(Boolean newVal, Boolean oldVal, String fieldname) {
        StringBuilder result = new StringBuilder();
        
        if (BooleanUtils.toBoolean(newVal) ^ BooleanUtils.toBoolean(oldVal)) {
            result.append("\n")
                    .append(fieldname).append(" ")
                    .append("Ancien: ").append(BooleanUtils.toBoolean(oldVal))
                    .append("; ")
                    .append("Nouveau: ").append(BooleanUtils.toBoolean(newVal));
        }
        
        return result.toString();
    }
    
    private static String getDifference(double newVal, double oldVal, String fieldname) {
        StringBuilder result = new StringBuilder();
        
        if (newVal != oldVal) {
            result.append("\n")
                    .append(fieldname).append(" ")
                    .append("Ancien: ").append(oldVal)
                    .append("; ")
                    .append("Nouveau: ").append(newVal);
        }
        
        return result.toString();
    }
    
    private static String getDifference(Set<GuestExemptions> newVal, Set<GuestExemptions> oldVal) {
        StringBuilder result = new StringBuilder();
        for (ExonerationTypeEnum type : ExonerationTypeEnum.values()) {
            GuestExemptions newGuest = DeclarationUtils.get(newVal, type);
            GuestExemptions oldGuest = DeclarationUtils.get(oldVal, type);
            result.append(getDifference(newGuest, oldGuest));
        }
        
        return result.toString();
    }
    
    private static String getDifference(GuestExemptions newVal, GuestExemptions oldVal) {
        if (newVal == null && oldVal == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        if (newVal != null && oldVal == null) {
            result.append("\n")
                    .append(newVal.getExemptionType()).append(" ")
                    .append("Ancien: ")
                    .append("; ")
                    .append("Nouveau: ").append(newVal.toString());
        } else if (newVal == null && oldVal != null) {
            result.append("\n")
                    .append(oldVal.getExemptionType()).append(" ")
                    .append("Ancien: ").append(oldVal.toString())
                    .append("; ")
                    .append("Nouveau: ");
        } else {
            if (!newVal.equals(oldVal)) {
                result.append("\n")
                    .append(oldVal.getExemptionType()).append(" ")
                    .append("Ancien: ").append(oldVal.toString())
                    .append("; ")
                    .append("Nouveau: ").append(newVal.toString());
            }
        }
        
        return result.toString();
    }
}
