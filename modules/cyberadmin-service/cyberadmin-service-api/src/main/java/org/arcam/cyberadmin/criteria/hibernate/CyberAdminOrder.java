/*
 * IrisOrder.java
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
package org.arcam.cyberadmin.criteria.hibernate;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * Description of an order used for sorting a list of Hibernate entities when querying 
 * with {@link CyberAdminSearchCriteria}.
 * 
 * @author phd
 * @see CyberAdminSearchCriteria
 * 
 */
@SuppressWarnings("serial")
public class CyberAdminOrder implements Serializable {

    private boolean ascending = true;
    private boolean ignoreCase = false;
    private String propertyName;

    /**
     * Constructs an order with input attributes.
     * 
     * @param ignoreCase
     *            See {@link #ignoreCase}
     * @param propertyName
     *            See {@link #propertyName}
     */
    public CyberAdminOrder(boolean ignoreCase, String propertyName) {
        this.ignoreCase = ignoreCase;
        this.propertyName = propertyName;
    }

    /**
     * Constructs an order with input attributes.
     * 
     * @param ascending
     *            See {@link #ascending}
     * @param ignoreCase
     *            See {@link #ignoreCase}
     * @param propertyName
     *            See {@link #propertyName}
     */
    public CyberAdminOrder(boolean ascending, boolean ignoreCase, String propertyName) {
        this.ascending = ascending;
        this.ignoreCase = ignoreCase;
        this.propertyName = propertyName;
    }

    /**
     * Constructs an ascending order on specified <code>propertyName</code>.
     * 
     * @param propertyName
     *            See {@link #propertyName}
     * @return An ascending order on specified <code>propertyName</code>.
     */
    public static CyberAdminOrder asc(String propertyName) {
        CyberAdminOrder order = new CyberAdminOrder(true, true, propertyName);
        return order;
    }

    /**
     * Constructs an ascending order on specified <code>propertyName</code>.
     * 
     * @param ignoreCase
     *            See {@link #ignoreCase}
     * @param propertyName
     *            See {@link #propertyName}
     * @return An ascending order on specified <code>propertyName</code>.
     */
    public static CyberAdminOrder asc(boolean ignoreCase, String propertyName) {
        CyberAdminOrder order = new CyberAdminOrder(true, ignoreCase, propertyName);
        return order;
    }
    
    /**
     * Constructs a descending order on specified <code>propertyName</code>.
     * 
     * @param propertyName
     *            See {@link #propertyName}
     * @return A descending order on specified <code>propertyName</code>.
     */
    public static CyberAdminOrder desc(String propertyName) {
        CyberAdminOrder order = new CyberAdminOrder(false, true, propertyName);
        return order;
    }

    /**
     * Constructs a descending order on specified <code>propertyName</code>.
     * 
     * @param ignoreCase
     *            See {@link #ignoreCase}
     * @param propertyName
     *            See {@link #propertyName}
     * @return A descending order on specified <code>propertyName</code>.
     */
    public static CyberAdminOrder desc(boolean ignoreCase, String propertyName) {
        CyberAdminOrder order = new CyberAdminOrder(false, ignoreCase, propertyName);
        return order;
    }

    public boolean isAscending() {
        return ascending;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(propertyName).append(ignoreCase).append(ascending).toHashCode();
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
        CyberAdminOrder rhs = (CyberAdminOrder) obj;
        return new EqualsBuilder().append(propertyName, rhs.propertyName).append(ignoreCase, rhs.ignoreCase)
                .append(ascending, rhs.ascending).isEquals();
    }
    
    /**
     * Checks whether the property is on association or not.
     * 
     * @return <code>true</code> if the property is on association, <code>false</code> otherwise.
     */
    public boolean onAssociation() {
        return propertyName.contains(".");
    }
}
