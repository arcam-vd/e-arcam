/*
 * Adresse.java
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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "ADRESSE")
@AttributeOverride(name = "id", column = @Column(name = "ADRESSE_ID"))
public class Adresse extends AbstractCyberAdminEntity {

    private static final long serialVersionUID = 1L;

    private String adresse;
    private String rue;
    private String no;
    private String localite;
    private String npa;
    private String pays;
    private String telephone;
    private String email;

    //Transient fields
    private String country;
    private String countryExport;
    
    @Column(name = "ADRESSE")
    @Length(max = 255)
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Column(name = "RUE")
    @Length(max = 255)
    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    @Column(name = "NO", length = 20)
    @Length(max = 20)
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Column(name = "LOCALITE", nullable = false)
    @NotBlank
    @Length(max = 255)
    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    @Column(name = "NPA", nullable = false, length = 15)
    @NotBlank
    @Length(max = 15)
    public String getNpa() {
        return npa;
    }

    public void setNpa(String npa) {
        this.npa = npa;
    }

    @Column(name = "TELEPHONE", length = 30)
    @Length(max = 30)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "EMAIL")
    @Length(max = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PAYS_CODE", nullable = false, length = 2)
    @NotBlank
    @Length(max = 2)
    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Adresse clone() {
        Adresse result = new Adresse();
        result.setAdresse(adresse);
        result.setRue(rue);
        result.setNo(no);
        result.setLocalite(localite);
        result.setNpa(npa);
        result.setPays(pays);
        result.setTelephone(telephone);
        result.setEmail(email);
        return result;
    }

    @Transient
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Set value for npa and localite fields from a selected Localite object.
     * @param localiteObj
     */
    public void fetchValuesFromLocalite(Localite localiteObj) {
        setNpa(localiteObj.getCode());
        setLocalite(localiteObj.getText());
    }
    
    @Formula(value = 
            "(SELECT CO.COUNTRY FROM COUNTRY CO WHERE "
           + " PAYS_CODE = CO.COUNTRY_ISO_CODE LIMIT 1)")
    public String getCountryExport() {
        return countryExport;
    }

    public void setCountryExport(String countryExport) {
        this.countryExport = countryExport;
    }

}
