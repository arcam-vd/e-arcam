/*
 * CyberAdminCriteria
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

package org.arcam.cyberadmin.criteria.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base criteria for all search screen in Cyber Admin.
 * 
 * @author mmn
 *
 */
public abstract class CyberAdminCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    /**
     * Indicates the first result to be returned when using the DTO as an example for the searching.
     */
    protected int firstResult = 0;

    /**
     * Indicates the maximum rows to be returned when using the DTO as an example for the searching.
     */
    protected int maxResults = -1;

    /**
     * List of properties by which the result must be sorted in ascending order.
     */
    protected List<String> ascs = new ArrayList<String>();

    /**
     * List of properties by which the result must be sorted in descending order.
     */
    protected List<String> descs = new ArrayList<String>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter method for <code> firstResult </code>.
     * 
     * @return Returns the firstResult.
     */
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * Setter for attribute <code> firstResult </code>.
     * 
     * @param firstResult The firstResult to set.
     */
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * Getter method for <code> maxResults </code>.
     * 
     * @return Returns the maxResults.
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Setter for attribute <code> maxResults </code>.
     * 
     * @param maxResults The maxResults to set.
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * <p>
     * Checks if the result needs to be paginated or not (just in case this DTO is used as an
     * example for the searching).
     * </p>
     * 
     * @return <code>true</code> if {@link #maxResults} is different from the initial value.
     */
    public boolean paginated() {
        return maxResults != -1;
    }

    /**
     * Getter method for <code> ascs </code>.
     * 
     * @return Returns the ascs.
     */
    public List<String> getAscs() {
        return ascs;
    }

    /**
     * Setter for attribute <code> ascs </code>.
     * 
     * @param ascs The ascs to set.
     */
    public void setAscs(List<String> ascs) {
        this.ascs = ascs;
    }

    /**
     * Getter method for <code> descs </code>.
     * 
     * @return Returns the descs.
     */
    public List<String> getDescs() {
        return descs;
    }

    /**
     * Setter for attribute <code> descs </code>.
     * 
     * @param descs The descs to set.
     */
    public void setDescs(List<String> descs) {
        this.descs = descs;
    }
    
    public void clear() {
        ascs.clear();
        descs.clear();
    }
}
