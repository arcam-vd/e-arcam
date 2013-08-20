/*
 * ConvenienceGenericDao
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.GenericDao;

/**
 * Extends the GenericDao with a few convenience methods.
 * As EL4J only supports Hibernate as persistence framework it's more convenient to use
 * ConvenienceGenericHibernateDao directly.
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public interface ConvenienceGenericDao<T, ID extends Serializable>
	extends GenericDao<T> {
	
	/**
	 * Retrieves a domain object by identifier. This method gets the object from
	 * the hibernate cache. It might be that you don't get the actual version
	 * that is in the database. If you want the actual version do a refresh()
	 * after this method call.
	 *
	 * @param id
	 *            The id of the domain object to find
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	T findById(ID id) throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier lazily. It uses Hibernate lazy loading, namely
	 * the method load() instead of get(). For detailed information about fetching strategies see
	 * http://www.hibernate.org/hib_docs/v3/reference/en/html/performance.html.
	 * 
	 * @see findById
	 */
	T findByIdLazy(ID id) throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Deletes the domain object with the given id, disregarding any
	 * concurrent modifications that may have occurred.
	 *
	 * @param id
	 *             The id of the domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @deprecated Renamed to deleteById as of el4j 1.6
	 */
	@Deprecated
	void delete(ID id)
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Deletes the domain object with the given id, disregarding any
	 * concurrent modifications that may have occurred.
	 *
	 * @param id
	 *             The id of the domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	void deleteById(ID id)
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 *
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	List<T> getAll() throws DataAccessException;
	
	/**
	 * Deletes the given domain object.
	 *
	 * @param entity
	 *             The domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	void delete(T entity)
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Deletes all available <code>T</code>.
	 *
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public void deleteAll()
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Sometimes, the way Hibernate handles all the actions in a session is
	 * very unbelievable. For example, we call
	 * <code>
	 *  delete(project);
	 *  project.setId(null) <= to insert new one
	 *  insert(project);
	 * </code>
	 *
	 * It could cause java.sql.BatchUpdateException:
	 * ORA-00001: unique constraint BECAUSE Hibernate doesn't flush
	 * the previous action first.
	 *
	 * This method provides a way to flush manually some action.
	 * Note that this method is only used in an extremely rare case.
	 */
	void flush();
}