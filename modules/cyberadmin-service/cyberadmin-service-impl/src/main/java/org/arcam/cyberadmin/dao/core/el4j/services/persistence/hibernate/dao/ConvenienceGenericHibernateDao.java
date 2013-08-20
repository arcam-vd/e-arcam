/*
 * ConvenienceGenericHibernateDao
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
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import org.arcam.cyberadmin.dao.core.el4j.services.search.QueryObject;

/**
 * This interface extends {@link ConvenienceGenericDao} with query methods using
 * {@link DetachedCriteria}s.
 *
 * @param <T>     the domain object type
 * @param <ID>    the id of the domain object to find
 *
 * @author Stefan Wismer (SWI)
 */
public interface ConvenienceGenericHibernateDao<T, ID extends Serializable>
	extends ConvenienceGenericDao<T, ID> {
	
	/**
	 * Convenience method: Executes saveOrUpdate() and flush() on that entity.
	 * 
	 * @param entity    The domain object to save or update
	 * @return          The saved or updated object
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;
	
	/** 
	 * Deletes all available <code>T</code> using a HQL query.
	 * 
	 * This has the benefit of a significant performance improvement
	 * in comparison to {@link deleteAll}. The tradeoff is that this
	 * method does no cascade deletion. 
	 *
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * */
	public void deleteAllNoCascade()
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Deletes the given domain objects using a HQL query. 
	 * 
	 * This has the benefit of a significant performance improvement
	 * in comparison to {@link delete}. The tradeoff is that this
	 * method does no cascade deletion. 
	 * 
	 * @param entities The domain objects to delete.
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public void deleteNoCascade(Collection<T> entities) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * Retrieves all the domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 *                             <b>Note: Do not reuse criteria objects! They need to recreated
	 *                             (or cloned e.g. using <tt>SerializationUtils.clone()</tt>) per execution,
	 *                             due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	/**
	 * Retrieves all the domain objects matching the Hibernate criteria.
	 * Loads at least the given extent.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 *                             <b>Note: Do not reuse criteria objects! They need to recreated
	 *                             (or cloned e.g. using <tt>SerializationUtils.clone()</tt>) per execution,
	 *                             due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @param extent               the extent in which objects get loaded.
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 *                             <b>Note: Do not reuse criteria objects! They need to recreated
	 *                             (or cloned e.g. using <tt>SerializationUtils.clone()</tt>) per execution,
	 *                             due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		int firstResult, int maxResults) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the Hibernate criteria.
	 * Loads at least the given extent.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 *                             <b>Note: Do not reuse criteria objects! They need to recreated
	 *                             (or cloned e.g. using <tt>SerializationUtils.clone()</tt>) per execution,
	 *                             due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @param extent               the extent in which objects get loaded.
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult,
		int maxResults, DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves the number of domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 *                             <b>Note: Do not reuse criteria objects! They need to recreated
	 *                             (or cloned e.g. using <tt>SerializationUtils.clone()</tt>) per execution,
	 *                             due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @return                     the number of objects that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findCountByCriteria(DetachedCriteria)
	 */
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier. This method gets the object from
	 * the hibernate cache. It might be that you don't get the actual version
	 * that is in the database. If you want the actual version do a refresh()
	 * after this method call.
	 * Loads at least the given extent.
	 *
	 * @param id        The id of the domain object to find
	 * @param extent    the extent in which objects get loaded.
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public T findById(ID id, DataExtent extent) 
		throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier, optionally obtaining a database
	 * lock for this operation.  <br>
	 *
	 * (For hibernate specialists: we do a "get()"
	 * in this method. In case you require only a "load()" (e.g. for lazy
	 * loading to work) we recommend that you write your own find method in the
	 * interface's subclass.)
	 * Loads at least the given extent.
	 *
	 * @param id
	 *            The id of a domain object
	 * @param lock
	 *            Indicates whether a database lock should be obtained for this
	 *            operation
	 * @param extent
	 *            the extent in which objects get loaded.
	 * 
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be retrieved
	 * @return The desired domain object
	 */
	public T findById(ID id, boolean lock, DataExtent extent)
		throws DataAccessException, DataRetrievalFailureException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 * Loads at least the given extent.
	 *
	 * @param extent    the extent in which objects get loaded.
	 * 
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	List<T> getAll(DataExtent extent) throws DataAccessException;
	
	/**
	 * Executes a query based on a given query object.
	 *  This method may also support paging (see javadoc
	 *   of implementing class).
	 * Loads at least the given extent.
	 *
	 * @param q         The search query object
	 * @param extent    the extent in which objects get loaded.
	 * @throws  DataAccessException
	 *             If general data access problem occurred
	 * @return A list containing 0 or more domain objects
	 */
	List<T> findByQuery(QueryObject q, DataExtent extent) throws DataAccessException;

	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent
	 *            the extent in which objects get loaded.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException;
	
	/**
	 * Re-reads the state of the given domain object from the undermost
	 * store (eg. the database).
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent
	 *            the extent in which objects get loaded.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T reload(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException;
	
	T merge(T entity) throws DataAccessException;
	
	/**
	 * @return    the default {@link Order} to order the results
	 */
	public Order[] getDefaultOrder();

	/**
	 * Set default order of results returned by getAll and findByQuery (not findByCriteria!).
	 * If defaultOrder is <code>null</code> then default ordering is deactivated.
	 * 
	 * @param defaultOrder    the default {@link Order} to order the results
	 */
	public void setDefaultOrder(Order... defaultOrder);
	
	/**
	 * Create a {@link DetachedCriteria} what contains default ordering and distinct constraints.
	 * 
	 * @return    a {@link DetachedCriteria}
	 */
	public DetachedCriteria getOrderedCriteria();
	
}
