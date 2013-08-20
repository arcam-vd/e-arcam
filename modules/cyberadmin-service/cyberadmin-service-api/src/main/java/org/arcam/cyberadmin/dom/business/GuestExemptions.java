/*
 * GuestExemptions.java
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

package org.arcam.cyberadmin.dom.business;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Copiable;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.hibernate.annotations.ForeignKey;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "GUESTEXEMPTIONS", uniqueConstraints = @UniqueConstraint(columnNames = {"DEC_ID", "EXEMPTION_TYPE" }))
@AttributeOverride(name = "id", column = @Column(name = "EX_ID"))
public class GuestExemptions extends AbstractCyberAdminEntity implements Comparable<GuestExemptions>,
        Copiable<GuestExemptions> {

    private static final long serialVersionUID = 1L;

    private Declaration declaration;
    private Integer hotes;
    private Integer nuits;
    private Integer residentiel;
    private ExonerationTypeEnum exemptionType = ExonerationTypeEnum.AUCUNE;
    
    // Transient fields
    private String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEC_ID")
    @ForeignKey(name = "FK_GUESTEXEMPTIONS_DECLARATION")
    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    @Column(name = "HOTES")
    public Integer getHotes() {
        return hotes;
    }

    public void setHotes(Integer hotes) {
        this.hotes = hotes;
    }

    @Column(name = "NUITS")
    public Integer getNuits() {
        return nuits;
    }

    public void setNuits(Integer nuits) {
        this.nuits = nuits;
    }

    @Column(name = "RESIDENTIEL")
    public Integer getResidentiel() {
        return residentiel;
    }

    public void setResidentiel(Integer residentiel) {
        this.residentiel = residentiel;
    }

    @Column(name = "EXEMPTION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    public ExonerationTypeEnum getExemptionType() {
        return exemptionType;
    }

    public void setExemptionType(ExonerationTypeEnum exemptionType) {
        this.exemptionType = exemptionType;
    }

    @Transient
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(exemptionType)
                    .append(hotes)
                    .append(nuits)
                    .append(residentiel)
                    .toHashCode();
    }   

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        
        GuestExemptions other = (GuestExemptions) obj;
        return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(exemptionType, other.exemptionType)
                    .append(hotes, other.hotes)
                    .append(nuits, other.nuits)
                    .append(residentiel, other.residentiel)
                    .isEquals();
    }

    @Override
    public int compareTo(GuestExemptions o) {
        if (o == this) {
            return 0;
        }
        
        ExonerationTypeEnum type = o.getExemptionType();
        if (exemptionType.getOrder() > type.getOrder()) {
            return 1;
        }
        if (exemptionType.getOrder() == type.getOrder()) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "GuestExemptions [hotes=" + hotes + ", nuits=" + nuits + ", residentiel=" + residentiel + "]";
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void copyChangeableProperties(GuestExemptions guestExemptions) {
        this.setExemptionType(guestExemptions.getExemptionType());
        this.setHotes(guestExemptions.getHotes());
        this.setLocaliteSuggestion(guestExemptions.getLocaliteSuggestion());
        this.setNuits(guestExemptions.getNuits());
        this.setResidentiel(guestExemptions.getResidentiel());
        this.setShortName(guestExemptions.getShortName());
    }
    
    public boolean validatedValues() {
        if (hotes == null && nuits == null && residentiel == null) {
            return false;
        }
        return true;
    }
    

}
