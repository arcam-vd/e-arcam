/*
 * GenericDao
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import org.arcam.cyberadmin.dao.core.el4j.services.search.QueryObject;


/**
 *
 * Interface for generic DAOs. It is the interface that implements
 * the DDD-Book's (http://www.domaindrivendesign.org/) Repository pattern.
 * This interface is implemented generically and it can be extended in case
 * you need more specific methods. Based on an idea from the Hibernate website.
 *
 * This is the canonical form of this interface. We recommend it when a generic
 * DAO is used in tools (to make the contract minimal).
 * For direct programmer-usage we recommend to use the convenience subclasses
 *  (@link ConvenienceGenericDao).
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Adrian Moos (AMS)
 * @author Martin Zeltner (MZE)
 */
public interface GenericDao<T> {
	/**
	 *  Needed because the Java generics throw away this type
	 *  information.
	 * @return Returns the domain class this DAO is responsible for.
	 */
	public Class<T> getPersistentClass();
	
	/**
	 * New: this callback is in general no longer required (the constructor
	 *  should figure the type out itself).
	 *
	 * @param c    Mandatory. The domain class this DAO is responsible for.
	 */
	public void setPersistentClass(Class<T> c);
	
	/**
	 * Executes a query based on a given query object.
	 *  This method may also support paging (see javadoc
	 *   of implementing class).
	 *
	 * @param q The search query object
	 * @throws  DataAccessException
	 *             If general data access problem occurred
	 * @return A list containing 0 or more domain objects
	 */
	List<T> findByQuery(QueryObject q) throws DataAccessException;

	
	/**
	 * Count number of results of a search.
	 *
	 * @param query    The search query object
	 * @return the number of results that this query could at most
	 *   return.
	 * @throws DataAccessException
	 */
	int findCountByQuery(final QueryObject query) throws DataAccessException;
	
	
	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity) throws DataAccessException,
		DataRetrievalFailureException;
	
	/**
	 * Re-reads the state of the given domain object from the undermost
	 * store (eg. the database).
	 *
	 * @param entity
	 *            The domain object to re-load the state of
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-loaded
	 * @return The reloaded entity
	 */
	T reload(T entity) throws DataAccessException,
		DataRetrievalFailureException;

	/**
	 * Saves or updates the given domain object.
	 *
	 * @param entity
	 *            The domain object to save or update
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataIntegrityViolationException
	 *             If domain object could not be inserted due to a data
	 *             integrity violation
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @return The saved or updated domain object
	 */
	@ReturnsUnchangedParameter
	T saveOrUpdate(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * Deletes the given domain objects. This method executed in a single
	 * transaction (by default with the Required semantics).
	 *
	 * @param entities
	 *             The domain objects to delete.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 */
	void delete(Collection<T> entities)
		throws OptimisticLockingFailureException, DataAccessException;
}
