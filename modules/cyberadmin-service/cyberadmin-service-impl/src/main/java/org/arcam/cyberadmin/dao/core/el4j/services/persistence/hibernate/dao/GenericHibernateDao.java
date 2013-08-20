/*
 * GenericHibernateDao
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.map.ReferenceMap;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import org.arcam.cyberadmin.dao.core.el4j.services.search.QueryObject;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * This class is a Hibernate-specific implementation of the ConvenienceGenericDao interface.
 * 
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Jonas Hauenstein (JHN)
 */
public class GenericHibernateDao<T, ID extends Serializable> extends ConvenienceHibernateDaoSupport
                implements ConvenienceGenericHibernateDao<T, ID>, InitializingBean {

    /**
     * Maximal number of entities which are deleted with a single HQL statement.
     */
    private static final int MAX_BULK_DELETE = 100;
    /**
     * The logger.
     */
    private static Logger s_logger = LoggerFactory.getLogger(GenericHibernateDao.class);
    /**
     * The domain class this DAO is responsible for.
     */
    private Class<T> m_persistentClass;
    /**
     * The default hibernate {@link Order} to order results.
     */
    private Order[] m_defaultOrder = null;

    /**
     * Set up the Generic Dao. Auto-derive the parametrized type.
     */
    @SuppressWarnings("unchecked")
    public GenericHibernateDao() {
        try {
            this.m_persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (final Exception e) {
            // ignore issues (e.g. when the subclass is not a parametrized type)
            // in that case, one needs to set the persistencClass otherwise.
        }
    }

    /**
     * New: this callback is in general no longer required (the constructor figures the type out
     * itself).
     * 
     * @param c
     *            Mandatory. The domain class this DAO is responsible for.
     */
    @Override
    public void setPersistentClass(final Class<T> c) {
        Reject.ifNull(c);
        this.m_persistentClass = c;
    }

    /**
     * @return Returns the domain class this DAO is responsible for.
     */
    @Override
    public Class<T> getPersistentClass() {
        assert this.m_persistentClass != null;
        return this.m_persistentClass;
    }

    /** {@inheritDoc} */
    @Override
    public Order[] getDefaultOrder() {
        return this.m_defaultOrder;
    }

    /** {@inheritDoc} */
    @Override
    public void setDefaultOrder(final Order... defaultOrder) {
        this.m_defaultOrder = defaultOrder;
    }

    /**
     * Retrieves a domain object by identifier, optionally obtaining a database lock for this
     * operation. <br>
     * 
     * (For hibernate specialists: we do a "get()" in this method. In case you require only a
     * "load()" (e.g. for lazy loading to work) we recommend that you write your own find method in
     * the interface's subclass.)
     * 
     * @param id
     *            The id of a domain object
     * @param lock
     *            Indicates whether a database lock should be obtained for this operation
     * @throws DataAccessException
     *             If general data access problem occurred
     * @throws DataRetrievalFailureException
     *             If domain object could not be retrieved
     * @return The desired domain object
     */
    @SuppressWarnings("unchecked")
    
    public T findById(final ID id, final boolean lock) throws DataAccessException,
                    DataRetrievalFailureException {
        T entity;
        if (lock) {
            entity = this.getConvenienceHibernateTemplate().get(this.getPersistentClass(), id,
                            LockMode.UPGRADE);
        } else {
            entity = this.getConvenienceHibernateTemplate().get(this.getPersistentClass(), id);
        }
        if (entity == null) {
            throw new DataRetrievalFailureException(
                            "The desired domain object could not be retrieved.");
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T findById(final ID id, final boolean lock, final DataExtent extent)
                    throws DataAccessException, DataRetrievalFailureException {
        T entity;
        if (lock) {
            entity = this.getConvenienceHibernateTemplate().get(this.getPersistentClass(), id,
                            LockMode.UPGRADE);
        } else {
            entity = this.getConvenienceHibernateTemplate().get(this.getPersistentClass(), id);
        }
        if (entity == null) {
            throw new DataRetrievalFailureException(
                            "The desired domain object could not be retrieved.");
        }
        return this.fetchExtent(entity, extent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    
    public T findById(final ID id) throws DataAccessException, DataRetrievalFailureException {
        return (T) this.getConvenienceHibernateTemplate().getByIdStrong(this.getPersistentClass(),
                        id, this.getPersistentClassName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T findById(final ID id, final DataExtent extent) throws DataAccessException,
                    DataRetrievalFailureException {
        return this.fetchExtent(
                        (T) this.getConvenienceHibernateTemplate().getByIdStrong(
                                        this.getPersistentClass(), id,
                                        this.getPersistentClassName()), extent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    
    public T findByIdLazy(final ID id) throws DataAccessException, DataRetrievalFailureException {
        return (T) this.getConvenienceHibernateTemplate().getByIdStrongLazy(
                        this.getPersistentClass(), id, this.getPersistentClassName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    
    public List<T> getAll() throws DataAccessException {
        return this.getConvenienceHibernateTemplate().findByCriteria(this.getOrderedCriteria());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAll(final DataExtent extent) throws DataAccessException {
        return this.fetchExtent(
                        this.getConvenienceHibernateTemplate().findByCriteria(
                                        this.getOrderedCriteria()), extent);
    }

    /**
     * {@inheritDoc}
     * 
     * This method supports paging (see QueryObject for info on how to use this).
     * 
     */
    @Override
    @SuppressWarnings("unchecked")
    
    public List<T> findByQuery(final QueryObject q) throws DataAccessException {
        final DetachedCriteria hibernateCriteria = this.getCriteria(q);
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return template.findByCriteria(hibernateCriteria, q.getFirstResult(), q.getMaxResults());
    }

    /**
     * {@inheritDoc}
     * 
     * This method supports paging (see QueryObject for info on how to use this).
     * 
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByQuery(final QueryObject q, final DataExtent extent)
                    throws DataAccessException {
        final DetachedCriteria hibernateCriteria = this.getCriteria(q);
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return this.fetchExtent(
                        template.findByCriteria(hibernateCriteria, q.getFirstResult(),
                                        q.getMaxResults()), extent);
    }

    /**
     * {@inheritDoc}
     * 
     * This method supports paging (see QueryObject for info on how to use this).
     * 
     * @return how many elements do we find with the given query
     */
    @Override
    
    public int findCountByQuery(final QueryObject q) throws DataAccessException {
        final DetachedCriteria hibernateCriteria = this.getCriteria(q);
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return template.findCountByCriteria(hibernateCriteria);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    
    public List<T> findByCriteria(final DetachedCriteria hibernateCriteria)
                    throws DataAccessException {
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return template.findByCriteria(hibernateCriteria);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final DetachedCriteria hibernateCriteria, final DataExtent extent)
                    throws DataAccessException {
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return this.fetchExtent(template.findByCriteria(hibernateCriteria), extent);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    
    public List<T> findByCriteria(final DetachedCriteria hibernateCriteria, final int firstResult,
                    final int maxResults) throws DataAccessException {
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return template.findByCriteria(hibernateCriteria, firstResult, maxResults);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final DetachedCriteria hibernateCriteria, final int firstResult,
                    final int maxResults, final DataExtent extent) throws DataAccessException {
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return this.fetchExtent(
                        template.findByCriteria(hibernateCriteria, firstResult, maxResults), extent);
    }

    /** {@inheritDoc} */
    @Override
    
    public int findCountByCriteria(final DetachedCriteria hibernateCriteria)
                    throws DataAccessException {
        final ConvenienceHibernateTemplate template = this.getConvenienceHibernateTemplate();
        return template.findCountByCriteria(hibernateCriteria);
    }

    /** {@inheritDoc} */
    @Override
    @ReturnsUnchangedParameter
    public T saveOrUpdate(final T entity) throws DataAccessException,
                    DataIntegrityViolationException, OptimisticLockingFailureException {
        this.getConvenienceHibernateTemplate().saveOrUpdateStrong(entity,
                        this.getPersistentClassName());
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    @ReturnsUnchangedParameter
    public T saveOrUpdateAndFlush(final T entity) throws DataAccessException,
                    DataIntegrityViolationException, OptimisticLockingFailureException {
        final T tmp = this.saveOrUpdate(entity);
        this.flush();
        return tmp;
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final T entity) throws DataAccessException {
        this.getConvenienceHibernateTemplate().delete(entity);
    }

    /** {@inheritDoc} */
    @Override
    
    public T refresh(final T entity) throws DataAccessException, DataRetrievalFailureException {
        this.getConvenienceHibernateTemplate().refresh(entity);
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    public T refresh(final T entity, final DataExtent extent) throws DataAccessException,
                    DataRetrievalFailureException {
        this.getConvenienceHibernateTemplate().refresh(entity);
        return this.fetchExtent(entity, extent);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    
    public T reload(final T entity) throws DataAccessException, DataRetrievalFailureException {
        final ID id = (ID) this.getSessionFactory().getClassMetadata(entity.getClass())
                        .getIdentifier(entity, EntityMode.POJO);
        return this.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public T reload(final T entity, final DataExtent extent) throws DataAccessException,
                    DataRetrievalFailureException {
        final ID id = (ID) this.getSessionFactory().getClassMetadata(entity.getClass())
                        .getIdentifier(entity, EntityMode.POJO);
        final T refresh = this.findById(id, extent);
        return refresh;
    }

    @Override
    public T merge(T entity) throws DataAccessException {
        this.getConvenienceHibernateTemplate().merge(entity);
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    public void delete(final ID id) throws DataAccessException {
        this.getConvenienceHibernateTemplate().deleteStrong(this.getPersistentClass(), id,
                        this.getPersistentClassName());
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(final ID id) throws DataAccessException {
        this.getConvenienceHibernateTemplate().deleteStrong(this.getPersistentClass(), id,
                        this.getPersistentClassName());
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Collection<T> entities) throws DataAccessException,
                    DataIntegrityViolationException, OptimisticLockingFailureException {
        this.getConvenienceHibernateTemplate().deleteAll(entities);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteNoCascade(final Collection<T> entities) throws DataAccessException,
                    DataIntegrityViolationException, OptimisticLockingFailureException {
        // search for method with @Id annotation
        final Method[] methods = this.getPersistentClass().getMethods();
        Method idMethod = null;
        for (final Method m : methods) {
            if (m.isAnnotationPresent(javax.persistence.Id.class)) {
                idMethod = m;
            }
        }
        if (idMethod != null) {
            final ArrayList<Object> hqlParameter = new ArrayList<Object>();
            StringBuilder hqlQuery = new StringBuilder("delete ");
            hqlQuery.append(this.getPersistentClassName());
            hqlQuery.append(" where ");
            final Iterator<T> it = entities.iterator();
            T entity;
            boolean fallback;
            int querycount = 0;
            // creating hql bulk delete statement for all entities
            // by calling the annotated method to get @Id value
            while (it.hasNext()) {
                fallback = false;
                entity = it.next();
                Object o = null;
                try {
                    o = idMethod.invoke(entity);
                } catch (final IllegalArgumentException e) {
                    fallback = true;
                } catch (final IllegalAccessException e) {
                    fallback = true;
                } catch (final InvocationTargetException e) {
                    fallback = true;
                }
                if (o == null) {
                    fallback = true;
                }
                // if we encountered an error, use the given delete method for this entity object
                if (!fallback) {
                    // check if maximal query length is reached
                    if (querycount >= GenericHibernateDao.MAX_BULK_DELETE) {
                        // execute present query
                        this.getHibernateTemplate().bulkUpdate(hqlQuery.toString(),
                                        hqlParameter.toArray());
                        // reinitialize new vars
                        hqlParameter.clear();
                        hqlQuery = new StringBuilder("delete ");
                        hqlQuery.append(this.getPersistentClassName());
                        hqlQuery.append(" where ");
                        querycount = 0;
                    }
                    if (querycount > 0) {
                        hqlQuery.append("or ");
                    }
                    hqlQuery.append("id = ? ");
                    hqlParameter.add(o);
                    querycount++;
                } else {
                    // something went wrong (in the reflective method call)
                    // use the given delete method to delete this entity
                    GenericHibernateDao.s_logger.warn(idMethod.getName()
                                    + "could not be called in " + this.getPersistentClassName()
                                    + ". Not using HQL bulk delete for this entity.");
                    this.getConvenienceHibernateTemplate().delete(entity);
                }
            }
            // check if there is (still) something to do
            if (querycount > 0) {
                this.getHibernateTemplate().bulkUpdate(hqlQuery.toString(), hqlParameter.toArray());
            }
        } else {
            // if no method with @Id annotation found, delete
            // all entities with the given delete method
            GenericHibernateDao.s_logger.warn("No @Id annotation was found in "
                            + this.getPersistentClassName()
                            + ". Not using HQL bulk delete for all entities.");
            this.getConvenienceHibernateTemplate().deleteAll(entities);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() throws OptimisticLockingFailureException, DataAccessException {
        final List<T> list = this.getAll();
        if (list.size() > 0) {
            this.delete(list);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAllNoCascade() throws OptimisticLockingFailureException, DataAccessException {
        final String hqlQuery = "delete " + this.getPersistentClassName();
        this.getHibernateTemplate().bulkUpdate(hqlQuery);
    }

    /** {@inheritDoc} */
    @Override
    public void flush() {
        this.getConvenienceHibernateTemplate().flush();
    }

    /** {@inheritDoc} */
    @Override
    public DetachedCriteria getOrderedCriteria() {
        final DetachedCriteria criteria = DetachedCriteria.forClass(this.getPersistentClass());
        return this.addOrder(this.makeDistinct(criteria));
    }

    /**
     * Prototype of Extent-based fetching, steps through all the retrieved objects and calls the
     * methods of the extent to ensure loading from db.
     * 
     * @param objects
     *            list of objects to load in given extent
     * @param extent
     *            the fetch-extent
     * @return returns the new list of objects.
     * 
     * @throws DataAccessException
     */
    protected List<T> fetchExtent(final List<T> objects, final DataExtent extent)
                    throws DataAccessException {
        if (extent != null) {
            final ReferenceMap fetchedObjects = new ReferenceMap();
            for (final Object obj : objects) {
                this.fetchExtentObject(obj, extent.getRootEntity(), fetchedObjects);
            }
        }
        return objects;
    }

    /**
     * Prototype of Extent-based fetching, steps through all the retrieved objects and calls the
     * methods of the extent to ensure loading from db.
     * 
     * @param object
     *            object to load in given extent
     * @param extent
     *            the fetch-extent
     * @return returns the new object.
     * 
     * @throws DataAccessException
     */
    protected T fetchExtent(final T object, final DataExtent extent) throws DataAccessException {
        if (extent != null) {
            final ReferenceMap fetchedObjects = new ReferenceMap();
            this.fetchExtentObject(object, extent.getRootEntity(), fetchedObjects);
        }
        return object;
    }

    /**
     * Sub-method of the extent-based fetching, steps through the entities and calls the required
     * methods.
     * 
     * @param object
     *            the object to load in given extent
     * @param entity
     *            the extent entity
     * @param fetchedObjects
     *            the HashMap with all the already fetched objects
     * 
     * @throws DataAccessException
     */
    private void fetchExtentObject(final Object object, final ExtentEntity entity,
                    final ReferenceMap fetchedObjects) throws DataAccessException {
        final Object[] nullArg = null;
        if ((object == null) || (entity == null) || (fetchedObjects == null)) {
            return;
        }
        fetchedObjects.put(object, entity);
        try {
            // Fetch the child entities
            for (final ExtentEntity ent : entity.getChildEntities()) {
                final Object obj = ent.getMethod().invoke(object, nullArg);
                // Initialize the object if it is a proxy
                if ((obj instanceof HibernateProxy) && !Hibernate.isInitialized(obj)) {
                    Hibernate.initialize(obj);
                }
                if (!fetchedObjects.containsKey(obj) || !fetchedObjects.get(obj).equals(ent)) {
                    this.fetchExtentObject(obj, ent, fetchedObjects);
                }
            }
            // Fetch the collections
            for (final ExtentCollection c : entity.getCollections()) {
                final Collection<?> coll = (Collection<?>) c.getMethod().invoke(object, nullArg);
                if (coll != null) {
                    for (final Object o : coll) {
                        // Initialize the object if it is a proxy
                        if ((o instanceof HibernateProxy) && !Hibernate.isInitialized(o)) {
                            Hibernate.initialize(o);
                        }
                        if (!fetchedObjects.containsKey(o)
                                        || !fetchedObjects.get(o).equals(c.getContainedEntity())) {
                            this.fetchExtentObject(o, c.getContainedEntity(), fetchedObjects);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the simple name of the persistent class this DAO is responsible for.
     * 
     * @return The simple name of the persistent class this DAO is responsible for.
     */
    protected String getPersistentClassName() {
        return this.getPersistentClass().getSimpleName();
    }

    /**
     * @param queryObject
     *            an EL4J {@link QueryObject} that should be converted to a {@link DetachedCriteria}
     * @return a suitable {@link DetachedCriteria}
     */
    protected DetachedCriteria getCriteria(final QueryObject queryObject) {
        DetachedCriteria criteria = CriteriaTransformer.transform(queryObject,
                        this.getPersistentClass());
        if (queryObject.getOrderConstraints().size() == 0) {
            criteria = this.addOrder(criteria);
        }
        return this.makeDistinct(criteria);
    }

    /**
     * @param criteria
     *            the criteria to modify
     * @return the criteria enhanced with order constraints (if set using setDefaultOrder)
     */
    protected DetachedCriteria addOrder(final DetachedCriteria criteria) {
        if (this.m_defaultOrder != null) {
            for (final Order order : this.m_defaultOrder) {
                criteria.addOrder(order);
            }
        }
        return criteria;
    }

    /**
     * @param criteria
     *            the criteria to modify
     * @return the criteria enhanced with distinct restrictions
     */
    protected DetachedCriteria makeDistinct(final DetachedCriteria criteria) {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria;
    }
}
