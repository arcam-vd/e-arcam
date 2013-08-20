/*
 * Assujetti.java
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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Auditable;
import org.arcam.cyberadmin.dom.core.JournalMessageTypeEnum;
import org.arcam.cyberadmin.utils.EntityComparisonUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "ASSUJETTI")
@AttributeOverride(name = "id", column = @Column(name = "ASJ_ID"))
public class Assujetti extends AbstractCyberAdminEntity implements Auditable {

    private static final long serialVersionUID = 1L;

    private Person person;
    private User user;
    private Adresse adresse;
    private boolean updated = true;
    private Set<BienTaxe> bienTaxes = new HashSet<BienTaxe>();
    
    private String address;
    
    // Transient fields
    private Integer decTypeResient;
    private Integer decTypeLocation;
    private Integer decTypeInsitut;
    private Integer decTypeHotel;
    private Integer decTypeChambre;
    private Integer decTypeCamping;
    private String email;
    private String telephone;
    private String description;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PERSON_ID", nullable = false, unique = true)
    @ForeignKey(name = "FK_ASSUJETTI_PERSON")
    @NotNull
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", unique = true)
    @ForeignKey(name = "FK_ASSUJETTI_USER")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "ADRESSE_DE_CORRESPONDENCE_ID", nullable = false, unique = true)
    @ForeignKey(name = "FK_ASSUJETTI_ADRESSE")
    @NotNull
    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @Column(name = "UPDATED", nullable = false)
    @Type(type = "true_false")
    @NotNull
    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assujetti")
    public Set<BienTaxe> getBienTaxes() {
        return bienTaxes;
    }

    public void setBienTaxes(Set<BienTaxe> bienTaxes) {
        this.bienTaxes = bienTaxes;
    }

    @Formula(value = 
            "(SELECT CONCAT(CONCAT(address.RUE, ' '), address.NO) "
            + "FROM ADRESSE address "
            + "WHERE ADRESSE_DE_CORRESPONDENCE_ID = address.ADRESSE_ID)")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Transient
    public Integer getDecTypeResient() {
        return decTypeResient;
    }

    public void setDecTypeResient(Integer decTypeResient) {
        this.decTypeResient = decTypeResient;
    }

    @Transient
    public Integer getDecTypeLocation() {
        return decTypeLocation;
    }

    public void setDecTypeLocation(Integer decTypeLocation) {
        this.decTypeLocation = decTypeLocation;
    }

    @Transient
    public Integer getDecTypeInsitut() {
        return decTypeInsitut;
    }

    public void setDecTypeInsitut(Integer decTypeInsitut) {
        this.decTypeInsitut = decTypeInsitut;
    }

    @Transient
    public Integer getDecTypeHotel() {
        return decTypeHotel;
    }

    public void setDecTypeHotel(Integer decTypeHotel) {
        this.decTypeHotel = decTypeHotel;
    }

    @Transient
    public Integer getDecTypeCamping() {
        return decTypeCamping;
    }

    public void setDecTypeCamping(Integer decTypeCamping) {
        this.decTypeCamping = decTypeCamping;
    }

    @Transient
    public Integer getDecTypeChambre() {
        return decTypeChambre;
    }

    public void setDecTypeChambre(Integer decTypeChambre) {
        this.decTypeChambre = decTypeChambre;
    }

    @Formula(value = "(SELECT IF(USER_ID is not null, user.EMAIL, address.EMAIL) "
            + "FROM USER user, ADRESSE address "
            + "WHERE USER_ID = user.USER_ID AND ADRESSE_DE_CORRESPONDENCE_ID = address.ADRESSE_ID)")
    public String getEmail() {
        return email;
    }
    
    @Transient
    public String getDescription() {
    	description = "";
    	if(!this.person.getNom().isEmpty()){
    		description += this.person.getNom()+" "+this.person.getPrenom()+", ";
    	}
    	
    	if(!this.person.getOrganisation().isEmpty()){
    		description += this.person.getOrganisation()+", "; 
    	}
    	
    	if(!this.getAddress().trim().isEmpty()){
    		description += this.getAddress().trim()+", ";
    	}
    	
    	description += this.getAdresse().getNpa()+" "+this.getAdresse().getLocalite();
    	
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public void setEmail(String email) {
        this.email = email;
    }

    @Formula(value = "(SELECT IF(USER_ID is not null, user.TELEPHONE, address.TELEPHONE) "
            + "FROM USER user, ADRESSE address "
            + "WHERE USER_ID = user.USER_ID AND ADRESSE_DE_CORRESPONDENCE_ID = address.ADRESSE_ID)")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the email address of the current taxpayer. This method is used when we need the email but the Assujetti
     * is not load from the database. Thus the method getEmail() returns nothing as the formula was not executed.
     */
    @Transient
    public String getEmailAddress() {
        if (user == null) {
            return adresse.getEmail();
        }
        return user.getEmail();
    }
    
    @Override
    @Transient
    public JournalMessageTypeEnum getModificationLogMessageType() {
        return JournalMessageTypeEnum.TAXPAYER_MODIFICATION;
    }

    @Override
    @Transient
    public JournalMessageTypeEnum getCreationLogMessageType() {
        return JournalMessageTypeEnum.TAXPAYER_CREATION;
    }

    @Override
    public String getDifference(Auditable another) {
        if (!(another instanceof Assujetti)) {
            throw new IllegalArgumentException("Can not retrieve the differences between " + this.getClass() + " and "
                    + another.getClass());
        }
        return EntityComparisonUtils.getDifference(this, (Assujetti) another);
    }

}
