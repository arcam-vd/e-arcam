/*
 * ConvenienceHibernateTemplate
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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;
import org.arcam.cyberadmin.dao.core.el4j.services.monitoring.notification.PersistenceNotificationHelper;
import org.arcam.cyberadmin.dao.core.el4j.services.search.QueryObject;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;

/**
 * This is a convenience class for the Hibernate template.
 *  Features:
 *   <ul>
 *      <li> improved paging support: allows to specify id of 1st element of a
 *            query
 *      <li> methods that signal an error if no element is found
 *            (they use the <em>Strong</em> suffixes)
 *   </ul>
 *
 * @author Alex Mathey (AMA)
 */
public class ConvenienceHibernateTemplate extends HibernateTemplate {
	
	/**
	 * Constructor.
	 * @param sessionFactory SessionFactory to create Sessions
	 */
	public ConvenienceHibernateTemplate(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	/**
	 * Retrieves the persistent instance given by its identifier in a strong
	 * way: does the same as the <code>getById(Class, java.io.Serializable)</code>
	 * method, but throws a <code>DataRetrievalException</code> instead of
	 * <code>null</code> if the persistent instance could not be found.
	 *
	 * @see HibernateTemplate#get(Class, java.io.Serializable)
	 * @param entityClass
	 *            The class of the object which should be returned.
	 * @param id
	 *            An identifier of the persistent instance
	 * @param objectName
	 *            Name of the persistent object type.
	 * @return the persistent instance
	 * @throws org.springframework.dao.DataAccessException
	 *             in case of Hibernate errors
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance is null
	 */
	public Object getByIdStrong(Class<?> entityClass, Serializable id, final String objectName)
		throws DataAccessException, DataRetrievalFailureException {

		Reject.ifNull(id, "The identifier must not be null.");
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		Object result = get(entityClass, id);
		
		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
	}
	
	/**
	 * Retrieves a persistent instance lazily.
	 * @see getByIdStrong
	 */
	public Object getByIdStrongLazy(Class<?> entityClass, Serializable id, final String objectName)
		throws DataAccessException, DataRetrievalFailureException {
		
		Reject.ifNull(id, "The identifier must not be null.");
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		Object result = load(entityClass, id);
		
		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
	}

	
	/**
	 * Retrieves a persistent instance with the help of a parameterized query:
	 * does the same as the
	 * <code>findByNamedParam(String, String, Object)</code> method, but
	 * returns a persistent instance instead of a list of persistent objects and
	 * throws a <code>DataRetrievalException</code> if the returned list does
	 * not contain exactly one element.
	 *
	 * @see HibernateTemplate#findByNamedParam(String, String, Object)
	 * @param queryString
	 *            The string corresponding to HQL query
	 * @param paramName
	 *            The name of the parameter
	 * @param value
	 *            The value of the parameter
	 * @param objectName
	 *            Name of the persistent object type.
	 * @return the persistent instance returned by the query
	 * @throws org.springframework.dao.DataAccessException
	 *             in case of Hibernate errors
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the list of persistent instances is empty, or if it
	 *             contains more than one object
	 */
	public Object findByNamedParamStrong(String queryString, String paramName, Object value, final String objectName)
		throws DataAccessException, DataRetrievalFailureException {
		
		Reject.ifEmpty(paramName);
		Reject.ifNull(value);
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		List<?> result = findByNamedParam(queryString, paramName, value);
		if (result.size() != 1) {
			String message = "";
			if (result.isEmpty()) {
				message = "The desired " + objectName
					+ " does not exist.";
			} else if (result.size() > 1) {
				message = "The query resulted in more than one persistent "
					+ " instance.";
			}
			PersistenceNotificationHelper.notifyDataRetrievalFailure(message,
				objectName);
		}
		return result.get(0);
	}
	
	/**
	 * Saves or updates the given persistent instance in a strong way: does the
	 * same as the <code>saveOrUpdate(Object)</code> method, but throws a more
	 * specific <code>OptimisticLockingFailureException</code> in the case of
	 * an optimistic locking failure.
	 *
	 * @see HibernateTemplate#saveOrUpdate(Object)
	 * @param entity
	 *            the persistent entity to save or update
	 * @param objectName
	 *            Name of the persistent object type.
	 * @throws DataAccessException
	 *             in case of Hibernate errors
	 * @throws OptimisticLockingFailureException
	 *             in case optimistic locking fails
	 */
	public void saveOrUpdateStrong(Object entity, final String objectName)
		throws DataAccessException, OptimisticLockingFailureException {
		
		Reject.ifNull(entity);
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		try {
			saveOrUpdate(entity);
		} catch (HibernateOptimisticLockingFailureException holfe) {
			String message = "The current " + objectName + " was modified or"
				+ " deleted in the meantime.";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
				message, objectName, holfe);
		}
	}
	
	/**
	 * Deletes the persistent instance given by its identifier in a strong way:
	 * first, the persistent instance is retrieved with the help of the
	 * identifier. If it exists, it will be deleted, otherwise a
	 * <code>DataRetrievalFailureException</code> will be thrown.
	 *
	 * @see HibernateTemplate#delete(Object)
	 * @param entityClass
	 *            The class of the object which should be deleted.
	 * @param id
	 *            The identifier of the persistent instance to delete
	 * @param objectName
	 *            Name of the persistent object type.
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance to delete is null
	 */
	public void deleteStrong(Class<?> entityClass, Serializable id, final String objectName)
		throws DataRetrievalFailureException {
		
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		Object toDelete = null;
		try {
			toDelete = getByIdStrong(entityClass, id, objectName);
		} catch (DataRetrievalFailureException e) {
			String message = "The current " + objectName + " was "
				+ "deleted in the meantime.";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
				message, objectName, null);
		}
		delete(toDelete);
	}
	
	//// paging support /////
	
	/**
	 * for paging: what is the id of the first result to return?
	 *  NO_CONSTRAINT means we do not constrain anything
	 */
	int m_firstResult = QueryObject.NO_CONSTRAINT;
	
	/**
	 * Overload parent class to support also a constraint
	 *  of the id of the first result to load.
	 * {@inheritDoc}
	 *
	 *  TODO shall we drop this and instead use the
	 *   findByCriteria(DetachedCriteria,int,int) method?
	 */
	@Override
	protected void prepareQuery(Query queryObject) {
		super.prepareQuery(queryObject);
		
		if (getFirstResult() != QueryObject.NO_CONSTRAINT) {
			queryObject.setFirstResult(getFirstResult());
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareCriteria(Criteria criteria) {
		super.prepareCriteria(criteria);

		if (getFirstResult() != QueryObject.NO_CONSTRAINT) {
			criteria.setFirstResult(getFirstResult());
		}
		
	}

	
	/**
	 * Counts the number of results of a search.
	 * @param criteria The criteria for the query.
	 * @return The number of results of the query.
	 * @throws DataAccessException
	 */
	public int findCountByCriteria(final DetachedCriteria criteria) throws DataAccessException {

		Assert.notNull(criteria, "DetachedCriteria must not be null");
		Object result =  executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				executableCriteria.setProjection(Projections.rowCount());
								
				prepareCriteria(executableCriteria);
				
				return executableCriteria.uniqueResult();
			}
		});
		if (result == null) {
			result = 0;
		}
		
		if (result instanceof Long) {
            result = ((Long) result).intValue();
        }
		
		return (Integer) result;
	}
	
	/**
	 * Gets the id of the first result to return.
	 * @return The id of the first result to return.
	 */
	public int getFirstResult() {
		return m_firstResult;
	}

	/**
	 * Sets the id of the first result to return.
	 * @param firstResult The id of the first result to return.
	 */
	public void setFirstResult(int firstResult) {
		m_firstResult = firstResult;
	}
	
}
