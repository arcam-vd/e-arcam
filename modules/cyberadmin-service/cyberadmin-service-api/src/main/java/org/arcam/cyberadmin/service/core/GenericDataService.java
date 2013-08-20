/*
 * GenericDataService.java
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

package org.arcam.cyberadmin.service.core;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author mmn
 *
 * @param <T>
 */
public interface GenericDataService<T> {

    /**
     * Saves or updates the given domain object.
     * 
     * @param entity
     *            The domain object to save or update
     * @return The saved or updated domain object
     */
    T saveOrUpdate(T entity);

    /**
     * Deletes the given domain objects. This method executed in a single
     * transaction (by default with the Required semantics).
     * 
     * @param entities
     *            The domain objects to delete.
     */
    void delete(Collection<T> entities);

    /**
     * Retrieves a domain object by identifier. This method gets the object from
     * the hibernate cache. It might be that you don't get the actual version
     * that is in the database. If you want the actual version do a refresh()
     * after this method call.
     * 
     * @param id
     *            The id of the domain object to find
     * @return Returns the found domain object.
     */
    T findById(Long id);

    /**
     * Retrieves all the domain objects of type T.
     * 
     * @return The list containing all the domain objects of type T; if no such
     *         domain objects exist, an empty list will be returned
     */
    List<T> getAll();

    /**
     * Deletes the given domain object.
     * 
     * @param entity
     *            The domain object to delete
     */
    void delete(T entity);

    /**
     * Deletes all available <code>T</code>.
     */
    public void deleteAll();
}
