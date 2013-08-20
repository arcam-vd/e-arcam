/*
 * Declaration.java
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.BooleanUtils;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Auditable;
import org.arcam.cyberadmin.dom.core.Copiable;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.JournalMessageTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.reference.Tarif;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.EntityComparisonUtils;
import org.arcam.cyberadmin.utils.ExportDataUtils;
import org.arcam.cyberadmin.utils.Utility;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "DECLARATION", uniqueConstraints = @UniqueConstraint(columnNames = { "BIEN_ID", "FISCALE_DATE" }))
@AttributeOverride(name = "id", column = @Column(name = "DEC_ID"))
public class Declaration extends AbstractCyberAdminEntity implements Auditable, Copiable<Declaration>,
        Comparable<Declaration> {

    private static final long serialVersionUID = 1L;

    private BienTaxe bienTaxe;
    private StatusTypeEnum status;
    private Date fiscaleDate;
    private Date lastModificationDate;
    private Date dueDate;
    private Adresse adresse;
    private String ajustComment;
    private String userComment;
    private double ajustValue;
    private PeriodiciteTypeEnum periodiciteCode;
    
    private Long estimation;
    private Boolean location = Boolean.TRUE;

    private String nom;
    private String prenom;
    private ExonerationTypeEnum exoneration = ExonerationTypeEnum.AUCUNE;
    private Boolean taille = Boolean.FALSE;
    private Date departDate;
    private Date submissionDate;

    private Set<GuestExemptions> guestExemptions = new HashSet<GuestExemptions>();
    private Set<Commentaire> commentaires = new HashSet<Commentaire>();
    
    private Date calculatedDate;
    private String denomination;
    private String assujetti;
    
    // transient fields
    private GuestExemptions totalExemption;
    private List<GuestExemptions> exemptions = new ArrayList<GuestExemptions>();
    private GuestExemptions calculatedExemption;
    private String description;
    
    // Transient fields for Validate billing working. 14c.
    // Only one field equals 'true' at one time.
    private Boolean billed = true; // By default, this is true.
    private Boolean notPaid = false; 
    private Boolean paid = false;
    private Boolean cancelBilling = false;
    private Boolean refuseBilling = false;
    
    // The number of weeks different between the arrival & departure dates
    private int weeks;
    private boolean fixedRate;
    private double totalNuits;
    private double totalResidentiel;
    private double taxAmount;
    private double montant;
    
    // This field using for export statistic
    private Date arrivalDate;
    private String nomPreNomLocation;
    
    private int year;
    
    private Tarif tarif;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ADRESSE_DE_FACTURATION_ID", nullable = false)
    @ForeignKey(name = "FK_DECLARATION_ADRESSE")
    @NotNull
    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BIEN_ID", nullable = false)
    @ForeignKey(name = "FK_DECLARATION_BIENTAXE")
    @NotNull
    public BienTaxe getBienTaxe() {
        return bienTaxe;
    }

    public void setBienTaxe(BienTaxe bienTaxe) {
        this.bienTaxe = bienTaxe;
    }

    @Column(name = "STATUS", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull
    public StatusTypeEnum getStatus() {
        return status;
    }

    public void setStatus(StatusTypeEnum status) {
        this.status = status;
    }

    @Column(name = "FISCALE_DATE", nullable = false)
    @NotNull
    public Date getFiscaleDate() {
        return fiscaleDate;
    }

    public void setFiscaleDate(Date fiscaleDate) {
        this.fiscaleDate = fiscaleDate;
    }

    @Column(name = "LAST_MODIFICATION_DATE", nullable = false)
    @NotNull
    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Column(name = "DUE_DATE", nullable = false)
    @NotNull
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = "USER_COMMENT", length = 2500)
    @Length(max = 2500)
    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    @Column(name = "ESTIMATION")
    public Long getEstimation() {
        return estimation;
    }

    public void setEstimation(Long estimation) {
        this.estimation = estimation;
    }

    @Column(name = "LOCATION")
    @Type(type = "true_false")
    public Boolean getLocation() {
        return location;
    }

    public void setLocation(Boolean location) {
        this.location = location;
    }

    @Column(name = "EXONERATION", nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    @NotNull
    public ExonerationTypeEnum getExoneration() {
        return exoneration;
    }

    public void setExoneration(ExonerationTypeEnum exoneration) {
        this.exoneration = exoneration;
    }

    @Column(name = "TAILLE")
    @Type(type = "true_false")
    public Boolean getTaille() {
        return taille;
    }

    public void setTaille(Boolean taille) {
        this.taille = taille;
    }

    @Column(name = "NOM")
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Column(name = "PRENOM")
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Column(name = "DEPART_DATE")
    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    @Column(name = "AJUST_VALUE")
    public double getAjustValue() {
        return ajustValue;
    }

    public void setAjustValue(double ajustValue) {
        this.ajustValue = ajustValue;
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

    @Column(name = "AJUST_COMMENT")
    public String getAjustComment() {
        return ajustComment;
    }

    public void setAjustComment(String ajustComment) {
        this.ajustComment = ajustComment;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "declaration", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<GuestExemptions> getGuestExemptions() {
        return guestExemptions;
    }

    public void setGuestExemptions(Set<GuestExemptions> guestExemptions) {
        this.guestExemptions = guestExemptions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "declaration", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(Set<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    @Column(name = "SUBMISSION_DATE")
    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    @Formula(value = "IF(status = 'TO_FILLED', DUE_DATE, SUBMISSION_DATE)")
    public Date getCalculatedDate() {
        return calculatedDate;
    }

    public void setCalculatedDate(Date calculatedDate) {
        this.calculatedDate = calculatedDate;
    }

    @Formula(value = 
            "(SELECT IF(bienTaxe.DECLARATION_TYPE in ('HOTEL', 'CHAMBRE', 'CAMPING', 'INSTITUT'), "
            + "bienTaxe.ETABLISSEMENT, "
            + "CONCAT(CONCAT(CONCAT(CONCAT(address.RUE, ', '), address.NO), ', '), address.LOCALITE)) "
            + "FROM BIENTAXE bienTaxe, ADRESSE address "
            + "WHERE BIEN_ID = bienTaxe.BIEN_ID "
            + "AND bienTaxe.ADRESSE_DE_OBJECT_ID = address.ADRESSE_ID)")
    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public void setAssujetti(String assujetti) {
        this.assujetti = assujetti;
    }

    @Formula(value = 
            "(SELECT CONCAT(CONCAT(person.NOM, ' '), person.PRENOM)"
            + "FROM BIENTAXE bienTaxe, ASSUJETTI assujetti, PERSON person "
            + "WHERE BIEN_ID = bienTaxe.BIEN_ID "
            + "AND bienTaxe.ASJ_ID = assujetti.ASJ_ID "
            + "AND assujetti.PERSON_ID = person.PERSON_ID)")
    public String getAssujetti() {
        return assujetti;
    }
    
    @Transient
    public boolean isOverDue() {
        if (status == StatusTypeEnum.TO_FILLED && dueDate != null) {
            return DateHelper.compareIgnoreTime(DateHelper.today(), dueDate) >= 0;
        }
        return false;
    }
    
    @Transient
    public void clone(Declaration declaration) {

    }

    @Transient
    public GuestExemptions getTotalExemption() {
        return totalExemption;
    }

    public void setTotalExemption(GuestExemptions totalExemption) {
        this.totalExemption = totalExemption;
    }

    @Transient
    public List<GuestExemptions> getExemptions() {
        return exemptions;
    }

    public void setExemptions(List<GuestExemptions> exemptions) {
        this.exemptions = exemptions;
    }

    @Transient
    public GuestExemptions getCalculatedExemption() {
        return calculatedExemption;
    }

    public void setCalculatedExemption(GuestExemptions calculatedExemption) {
        this.calculatedExemption = calculatedExemption;
    }

    @Transient
    public double getTotalNuits() {
        return totalNuits;
    }

    public void setTotalNuits(double totalNuits) {
        this.totalNuits = totalNuits;
    }

    @Transient
    public double getTotalResidentiel() {
        return totalResidentiel;
    }

    public void setTotalResidentiel(double totalResidentiel) {
        this.totalResidentiel = totalResidentiel;
    }

    @Transient
    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    @Transient
    public boolean isFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(boolean fixedRate) {
        this.fixedRate = fixedRate;
    }

    @Transient
    public double getTaxAmount() {
        return taxAmount;
    }   

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Transient
    public double getMontant() {
        montant = taxAmount + ajustValue;
        return montant;
    }
       

    public void setMontant(double montant) {
        this.montant = montant;
    }

    @Override
    @Transient
    public JournalMessageTypeEnum getModificationLogMessageType() {
        return status.getModificationMessageType();
    }

    @Override
    @Transient
    public JournalMessageTypeEnum getCreationLogMessageType() {
        return JournalMessageTypeEnum.DECLARATION_CREATION;
    }

    @Override
    public String getDifference(Auditable another) {
        if (!(another instanceof Declaration)) {
            throw new IllegalArgumentException("Can not retrieve the differences between " + this.getClass() + " and "
                    + another.getClass());
        }
        return EntityComparisonUtils.getDifference(this, (Declaration) another);
    }

    @Transient
    public Date getArrivalDate() {
        if (bienTaxe.getDeclarationType() == DeclarationTypeEnum.LOCATION) {
            arrivalDate = fiscaleDate;
        }
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public int compareTo(Declaration o) {
        if (o == this) {
            return 0;
        }
        
        return DateHelper.compare(fiscaleDate, o.fiscaleDate);
    }

    @Formula(value = 
            "(SELECT CONCAT(CONCAT(NOM, ' '), PRENOM))")
    public String getNomPreNomLocation() {
        return nomPreNomLocation;
    }

    public void setNomPreNomLocation(String nomPreNomLocation) {
        this.nomPreNomLocation = nomPreNomLocation;
    }

    @Transient
    public Boolean getBilled() {
        return billed;
    }

    public void setBilled(Boolean billed) {
        this.billed = billed;
        if (BooleanUtils.isTrue(billed)) {
            this.notPaid = !billed;
            this.paid = !billed;
            this.cancelBilling = !billed;
            this.refuseBilling = !billed;
        }
    }

    @Transient
    public Boolean getNotPaid() {
        return notPaid;
    }

    public void setNotPaid(Boolean notPaid) {
        this.notPaid = notPaid;
        // Only change value of other fields if submitted value is true.
        if (BooleanUtils.isTrue(notPaid)) {
            this.billed = !notPaid;
            this.paid = !notPaid;
            this.cancelBilling = !notPaid;
            this.refuseBilling = !notPaid;
        }
        
    }
    
    @Transient
    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
        if (BooleanUtils.isTrue(paid)) {
            this.billed = !paid;
            this.notPaid = !paid;
            this.cancelBilling = !paid;
            this.refuseBilling = !paid;
        }
    }
    
    @Transient
    public Boolean getCancelBilling() {
        return cancelBilling;
    }

    public void setCancelBilling(Boolean cancelBilling) {
        this.cancelBilling = cancelBilling;
        if (BooleanUtils.isTrue(cancelBilling)) {
            this.billed = !cancelBilling;
            this.paid = !cancelBilling;
            this.notPaid = !cancelBilling;
            this.refuseBilling = !cancelBilling;
        }
    }
    
    @Transient
    public Boolean getRefuseBilling() {
        return refuseBilling;
    }

    public void setRefuseBilling(Boolean refuseBilling) {
        this.refuseBilling = refuseBilling;
        if (BooleanUtils.isTrue(refuseBilling)) {
            this.billed = !refuseBilling;
            this.cancelBilling = !refuseBilling;
            this.paid = !refuseBilling;
            this.notPaid = !refuseBilling;
        }
    }

    @Transient
    public int getYear() {
        year = DateHelper.getYear(fiscaleDate);
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    @Transient
    public Tarif getTarif() {
        return tarif;
    }

    public void setTarif(Tarif tarif) {
        this.tarif = tarif;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void copyChangeableProperties(Declaration dec) {
        this.setAdresse(dec.getAdresse());
        this.setAjustComment(dec.getAjustComment());
        this.setAjustValue(dec.getAjustValue());
        this.setArrivalDate(dec.getArrivalDate());
        this.setCalculatedDate(dec.getCalculatedDate());
        
        // Prepare property's values to avoid NPE when set value after that.
        if (this.bienTaxe == null) {
            this.bienTaxe = new BienTaxe();
        }
        if (this.calculatedExemption == null) {
            this.calculatedExemption = new GuestExemptions();
        }
        if (this.totalExemption == null) {
            this.totalExemption = new GuestExemptions();
        }
        this.setBienTaxe(Utility.copyProperties(dec.getBienTaxe(), this.bienTaxe));
        this.setCalculatedExemption(Utility.copyProperties(dec.getCalculatedExemption(), this.calculatedExemption));
        this.setTotalExemption(Utility.copyProperties(dec.getTotalExemption(), this.totalExemption));
        
        this.setCancelBilling(dec.getCancelBilling());
        this.setCommentaires(dec.getCommentaires());
        this.setDenomination(dec.getDenomination());
        this.setDepartDate(dec.getDepartDate());
        this.setDueDate(dec.getDueDate());
        this.setEstimation(dec.getEstimation());
        this.setExoneration(dec.getExoneration());
        this.setFiscaleDate(dec.getFiscaleDate());
        this.setLocaliteSuggestion(dec.getLocaliteSuggestion()); // Need cloned
        this.setLocation(dec.getLocation());
        this.setMontant(dec.getMontant());
        this.setNom(dec.getNom());
        this.setNomPreNomLocation(dec.getNomPreNomLocation());
        this.setNotPaid(dec.getNotPaid());
        this.setPaid(dec.getPaid());
        this.setPrenom(dec.getPrenom());
        this.setRefuseBilling(dec.getRefuseBilling());
        this.setStatus(dec.getStatus());
        this.setSubmissionDate(dec.getSubmissionDate());
        this.setTaille(dec.getTaille());
        this.setTaxAmount(dec.getTaxAmount());
        this.setTotalNuits(dec.getTotalNuits());
        this.setTotalResidentiel(dec.getTotalResidentiel());
        this.setUserComment(dec.getUserComment());
        this.setWeeks(dec.getWeeks());
        this.setFixedRate(dec.isFixedRate());
        this.setYear(dec.getYear());
    }
    
    
    @Transient
    public String getMontantString() {
        return ExportDataUtils.encodeNumberValue(getMontant());
    }
    
    @Transient
    public String getTaxAmountString() {
        return ExportDataUtils.encodeNumberValue(getTaxAmount());
    }
    
    @Transient
    public String getTotalNuitsString() {
        return ExportDataUtils.encodeNumberValue(getTotalNuits());
    }

    @Transient
    public String getTotalResidentielString() {
        return ExportDataUtils.encodeNumberValue(getTotalResidentiel());
    }
    
    @Transient
    public String getAjustValueString() {
        return  ExportDataUtils.encodeNumberValue(getAjustValue());
    }
    
    @Transient
    public String getDescription() {
    	description = "";
    	if(!this.bienTaxe.getAssujetti().getPerson().getNom().isEmpty()){
    		description += this.bienTaxe.getAssujetti().getPerson().getNom()+" "+this.bienTaxe.getAssujetti().getPerson().getPrenom()+", ";
    	}
    	
    	if(!this.bienTaxe.getAssujetti().getPerson().getOrganisation().isEmpty()){
    		description += this.bienTaxe.getAssujetti().getPerson().getOrganisation()+", "; 
    	}
    	    	
    	if(!this.bienTaxe.getAdresse().getRue().trim().isEmpty()){
    		description += (this.bienTaxe.getAdresse().getRue()+" "+this.bienTaxe.getAdresse().getNo()).trim()+", ";
    	}
    	
    	description += this.bienTaxe.getAdresse().getNpa()+" "+this.bienTaxe.getAdresse().getLocalite();
    	
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
