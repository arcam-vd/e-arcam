/*
 * BienTaxe.java
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Copiable;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "BIENTAXE")
@AttributeOverride(name = "id", column = @Column(name = "BIEN_ID"))
public class BienTaxe extends AbstractCyberAdminEntity implements Copiable<BienTaxe> {

    private static final long serialVersionUID = 1L;

    private String eGid;
    private String eWid;
    private DeclarationTypeEnum declarationType = DeclarationTypeEnum.RESIDENCE_SECONDAIRE;
    private Adresse adresse;
    private PeriodiciteTypeEnum periodiciteCode;
    private Assujetti assujetti;
    private String etablissement;
    private String communeCode;
    private List<Declaration> declarations = new ArrayList<Declaration>();
    
    // Using to display communecode on screen. For example : Screen 6.
    private String communeCodeDisplayText;
    
    @ManyToOne
    @JoinColumn(name = "ASJ_ID", nullable = false, updatable = false)
    @ForeignKey(name = "FK_BIENTAXE_ASSUJETTI")
    @NotNull
    public Assujetti getAssujetti() {
        return assujetti;
    }

    public void setAssujetti(Assujetti assujetti) {
        this.assujetti = assujetti;
    }

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "ADRESSE_DE_OBJECT_ID", nullable = false)
    @ForeignKey(name = "FK_BIENTAXE_ADRESSE")
    @NotNull
    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @Column(name = "EGID")
    public String geteGid() {
        return eGid;
    }

    public void seteGid(String egid) {
        this.eGid = egid;
    }

    @Column(name = "EWID")
    public String geteWid() {
        return eWid;
    }

    public void seteWid(String ewid) {
        this.eWid = ewid;
    }

    @Column(name = "DECLARATION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    public DeclarationTypeEnum getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(DeclarationTypeEnum declarationType) {
        this.declarationType = declarationType;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bienTaxe", cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OrderBy("fiscaleDate desc")
    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    @Column(name = "ETABLISSEMENT")
    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    @Column(name = "COMMUNE_CODE", nullable = false)
    @NotBlank
    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    @Column(name = "PERIODICITE_CODE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    public PeriodiciteTypeEnum getPeriodiciteCode() {
        return periodiciteCode;
    }

    public void setPeriodiciteCode(PeriodiciteTypeEnum periodiciteCode) {
        this.periodiciteCode = periodiciteCode;
    }

    @Formula(value = "(SELECT cm.NAME FROM COMMUNE cm WHERE cm.CODE = COMMUNE_CODE) ")
    public String getCommuneCodeDisplayText() {
        return communeCodeDisplayText;
    }

    public void setCommuneCodeDisplayText(String communeCodeDisplayText) {
        this.communeCodeDisplayText = communeCodeDisplayText;
    }
    
    @Transient
    public BienTaxe cloneToBienTaxe() {
        BienTaxe clone = new BienTaxe();
        clone.setCommuneCode(getCommuneCode());
        clone.setDeclarations(getDeclarations());
        clone.seteGid(geteGid());
        clone.seteWid(geteWid());
        clone.setEtablissement(getEtablissement());
        clone.setAssujetti(getAssujetti());
        clone.setDeclarationType(getDeclarationType());
        clone.setPeriodiciteCode(getPeriodiciteCode());
        clone.setAdresse(getAdresse());
        return clone;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void copyChangeableProperties(BienTaxe bienTaxe) {
        this.setAdresse(bienTaxe.getAdresse());
        this.setCommuneCode(bienTaxe.getCommuneCode());
        this.setCommuneCodeDisplayText(bienTaxe.getCommuneCodeDisplayText());
        this.setDeclarationType(bienTaxe.getDeclarationType());
        this.seteGid(bienTaxe.geteGid());
        this.setEtablissement(bienTaxe.getEtablissement());
        this.seteWid(bienTaxe.geteWid());
        this.setLocaliteSuggestion(bienTaxe.getLocaliteSuggestion());
        this.setPeriodiciteCode(bienTaxe.getPeriodiciteCode());
    }
    
    public boolean hasLocationInfo() {
        return StringUtils.isNotBlank(eGid) && StringUtils.isNotBlank(eWid);
    }
}
