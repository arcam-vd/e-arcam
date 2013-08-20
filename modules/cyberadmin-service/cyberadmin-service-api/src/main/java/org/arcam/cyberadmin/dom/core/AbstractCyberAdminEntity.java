/*
 * AbstractCyberAdminEntity.java
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

package org.arcam.cyberadmin.dom.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.hibernate.proxy.HibernateProxyHelper;

/**
 * Base entity for all entities.
 * 
 * @author vtn
 * 
 */
@MappedSuperclass
public class AbstractCyberAdminEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Id for transient entity (i.e. this entity has not been persisted)
     */
    public static final long TRANSIENT_ENTITY_ID = 0;

    private long id;
    private int version;
    
    /**
     * Has the hashCode value been leaked while being in transient state? e.g. hashcode is asking twice on the same
     * object: one when the object is being in transient state (not saved) and one when it is saved.
     */
    private boolean transientHashCodeLeaked = false;
    
    //Transient fields
    private Localite localiteSuggestion;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CYBER_ADMIN_SEQ")
    @SequenceGenerator(name = "CYBER_ADMIN_SEQ", sequenceName = "CYBER_ADMIN_SEQ")
    @Column(name = "ID", unique = true, nullable = false)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "VERSION", nullable = false)
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Check if this entity has not been persisted yet.
     * 
     * @return true or false
     */
    @Transient
    public boolean isPersisted() {
        return getId() > TRANSIENT_ENTITY_ID;
    }
    
    @Transient
    public Localite getLocaliteSuggestion() {
        return localiteSuggestion;
    }

    public void setLocaliteSuggestion(Localite localiteSuggestion) {
        this.localiteSuggestion = localiteSuggestion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        
        Class<?> thisClass = HibernateProxyHelper.getClassWithoutInitializingProxy(this);
        Class<?> otherClass = HibernateProxyHelper.getClassWithoutInitializingProxy(obj);
        if (thisClass != otherClass) {
            return false;
        }
        
        if (obj instanceof AbstractCyberAdminEntity) {
            final AbstractCyberAdminEntity other = (AbstractCyberAdminEntity) obj;
            if (isPersisted() && other.isPersisted()) {
                return new EqualsBuilder().append(id, other.id).isEquals();
            }
            // if one of entity is new (transient), they are considered not equal.
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (!isPersisted()) { // is new or is in transient state.
            transientHashCodeLeaked = true;
            return -super.hashCode();
        }
        
        // because hashcode has just been asked for when the object is in transient state
        // at that time, super.hashCode(); is returned. Now for consistency, we return the
        // same value.
        if (transientHashCodeLeaked) {
            return -super.hashCode();
        }
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Transient
    protected List<String> getUnAuditedProperties() {
        return Arrays.asList("id", "version");
    }
}
