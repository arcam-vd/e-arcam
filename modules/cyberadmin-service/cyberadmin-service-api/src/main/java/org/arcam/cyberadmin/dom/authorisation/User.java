/*
 * User.java
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

package org.arcam.cyberadmin.dom.authorisation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.core.Auditable;
import org.arcam.cyberadmin.dom.core.JournalMessageTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.utils.EntityComparisonUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "USER")
@AttributeOverride(name = "id", column = @Column(name = "USER_ID"))
public class User extends AbstractCyberAdminEntity implements Auditable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private boolean activated;
    private String email;
    private UserTypeEnum userType;
    private boolean arcam;
    private String telephone;
    private Set<Commentaire> commentaires = new HashSet<Commentaire>();
    private Assujetti assujetti;
    private Person person;
    private boolean validated;
    private Date lastLogOnDate; 
    private String communeCode;
    private boolean adminCreated;

    //Transient fields
    private String decodePassword;
    private String protectedPassword;
    // Using to export communecode
    private String communeCodeDisplayText;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PERSON_ID", nullable = false)
    @ForeignKey(name = "FK_USER_PERSON")
    @NotNull
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    @NotBlank
    @Length(max = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PASSWORD", nullable = false, length = 255)
    @NotBlank
    @Length(max = 255)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public Assujetti getAssujetti() {
        return assujetti;
    }

    public void setAssujetti(Assujetti assujetti) {
        this.assujetti = assujetti;
    }

    @Column(name = "TELEPHONE", nullable = false, length = 30)
    @NotBlank
    @Length(max = 30)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "VALIDATED", nullable = false)
    @Type(type = "true_false")
    @NotNull
    public boolean getValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    public Set<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(Set<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    @Column(name = "USER_TYPE", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @NotNull
    public UserTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    @Column(name = "ARCAM", nullable = false)
    @Type(type = "true_false")
    @NotNull
    public boolean getArcam() {
        return arcam;
    }

    public void setArcam(boolean arcam) {
        this.arcam = arcam;
    }

    @Column(name = "USERNAME", nullable = false, length = 30, unique = true)
    @NotBlank
    @Length(max = 30)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "ACTIVATED", nullable = false)
    @Type(type = "true_false")
    @NotNull
    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Column(name = "LAST_LOG_ON")
    public Date getLastLogOnDate() {
        return lastLogOnDate;
    }

    public void setLastLogOnDate(Date lastLogOnDate) {
        this.lastLogOnDate = lastLogOnDate;
    }
    
    @Column(name = "COMMUNE_CODE")
    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    @Column(name = "ADMIN_CREATED", nullable = false, updatable = false)
    @Type(type = "true_false")
    @NotNull
    public boolean getAdminCreated() {
        return adminCreated;
    }

    public void setAdminCreated(boolean adminCreated) {
        this.adminCreated = adminCreated;
    }

    @Transient
    public String getFullname() {
        if (StringUtils.isNotEmpty(person.getNom())) {
            return person.getNom() + " " + person.getPrenom();
        }
        return person.getPrenom();
    }

    @Override
    @Transient
    public JournalMessageTypeEnum getModificationLogMessageType() {
        return JournalMessageTypeEnum.USER_MODIFICATION;
    }

    @Override
    @Transient
    public JournalMessageTypeEnum getCreationLogMessageType() {
        return JournalMessageTypeEnum.USER_CREATION;
    }

    @Override
    public String getDifference(Auditable another) {
        if (!(another instanceof User)) {
            throw new IllegalArgumentException("Can not retrieve the differences between " + this.getClass() + " and "
                    + another.getClass());
        }
        return EntityComparisonUtils.getDifference(this, (User) another, true);
    }

    @Transient
    public String getDecodePassword() {
        return decodePassword;
    }

    public void setDecodePassword(String decodePassword) {
        this.decodePassword = decodePassword;
    }

    @Transient
    public String getProtectedPassword() {
        return protectedPassword;
    }

    public void setProtectedPassword(String protectedPassword) {
        this.protectedPassword = protectedPassword;
    }

    @Formula(value = "(SELECT cm.NAME FROM COMMUNE cm WHERE cm.CODE = COMMUNE_CODE) ")
    public String getCommuneCodeDisplayText() {
        return communeCodeDisplayText;
    }

    public void setCommuneCodeDisplayText(String communeCodeDisplayText) {
        this.communeCodeDisplayText = communeCodeDisplayText;
    }

}
