/*
 * DefaultDaoRegistry
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
package org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.impl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.PatternMatchUtils;
import org.arcam.cyberadmin.dao.core.el4j.core.context.ModuleApplicationListener;
import org.arcam.cyberadmin.dao.core.el4j.core.context.RefreshableModuleApplicationContext;
import org.arcam.cyberadmin.dao.core.el4j.services.monitoring.notification.CoreNotificationHelper;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoRegistry;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.GenericDao;

/**
 * A DaoRegistry where DAOs can be registered either explicitly (via its map configuration) or
 * implicitly (by collecting all beans that have the GenericDao interface). <br>
 * 
 * This can be used together with
 * <ul>
 * <li>context:component-scan configuration setting and the
 * 
 * @AutocollectedGenericDao annotation to load all DAOs with this annotation into the spring
 *                          application context. <li>
 *                          {@link HibernateSessionFactoryInjectorPostProcessor} or
 *                          {@link IbatisSqlMapClientTemplateInjectorBeanPostProcessor} to
 *                          automatically set the session factory/ sql map client template.
 *                          </ul>
 * 
 *                          Important: This class has to be created inside a
 *                          ModuleApplicationContext, because it waits for this context to be fully
 *                          initialized. If another context is taken it will wait forever.
 * 
 * @author Adrian Moos (AMS)
 * @author Alex Mathey (AMA)
 */
public class DefaultDaoRegistry implements DaoRegistry, ApplicationContextAware,
                ModuleApplicationListener, ApplicationListener<ContextRefreshedEvent> {

    /**
     * Private logger of this class.
     */
    private static Logger s_logger = LoggerFactory.getLogger(DefaultDaoRegistry.class);
    /**
     * Whether to collect DAOs automatically.
     */
    protected boolean m_collectDaos = true;
    /**
     * The map containing the registered DAOs.
     */
    protected Map<Class<?>, GenericDao<?>> m_daos = new ConcurrentHashMap<Class<?>, GenericDao<?>>();
    /**
     * The application context.
     */
    protected ApplicationContext m_applicationContext;
    /**
     * The dao matching pattern. All GenericDaos whose names match this pattern are collected.
     */
    protected String m_daoNamePattern = "*";
    /**
     * Was {@link initDaosFromSpringBeans} already called?
     */
    protected boolean m_initialized = false;
    /**
     * The thread ID of the thread that set the application context (in general the thread that
     * created this object).
     */
    protected long m_creatorThreadId;
    /**
     * Is Spring context completely initialized?
     */
    protected volatile boolean m_applicationContextIsReady = false;

    /** {@inheritDoc} */
    @Override
    public synchronized void onContextRefreshed() {
        this.m_applicationContextIsReady = true;
        this.notify();
    }

    /**
     * Check if Spring context is completely initialized and wait if we are in multi-threaded
     * environment.
     */
    public synchronized void waitUntilApplicationContextIsReady() {
        // if we are in the same thread, waiting probably doesn't make sense, so we have to check
        // this.
        if (Thread.currentThread().getId() != this.m_creatorThreadId) {
            // access from another thread -> wait
            while (!this.m_applicationContextIsReady) {
                try {
                    DefaultDaoRegistry.s_logger
                                    .debug("Waiting for Spring context to be fully initialized.");
                    this.wait();
                } catch (final InterruptedException e) {
                    DefaultDaoRegistry.s_logger
                                    .debug("Interrupted: Application context might be ready now.");
                }
            }
        }
        // context might be ready but caller got the contextRefreshed-event earlier than we did.
        if (!this.m_applicationContextIsReady) {
            if (this.m_applicationContext instanceof RefreshableModuleApplicationContext) {
                final RefreshableModuleApplicationContext context = (RefreshableModuleApplicationContext) this.m_applicationContext;
                this.m_applicationContextIsReady = context.isRefreshed();
            }
        }
        if (!this.m_applicationContextIsReady) {
            CoreNotificationHelper
                            .notifyMisconfiguration("Trying to get DAOs before Spring context is "
                                            + "fully initialized. Some DAOs might not be found. "
                                            + "Implement ch.vd.seven.semis.dao.el4j.core.context.ModuleApplicationListener to get notified as soon "
                                            + "Spring context is fully initialized");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> GenericDao<T> getFor(final Class<T> entityType) {
        if ((!this.m_initialized) && this.m_collectDaos) {
            this.m_initialized = true;
            this.initDaosFromSpringBeans();
        }
        Class<T> actualEntityType = entityType;
        // ensure this works when the entityType is proxied by an cglib proxy:
        // Thanks Ky (QKP) for the hint!
        if (Enhancer.isEnhanced(entityType)) {
            // "undo" cglib proxying:
            actualEntityType = (Class<T>) entityType.getSuperclass();
        }
        GenericDao<T> candidateReturn = (GenericDao<T>) this.m_daos.get(actualEntityType);
        if (candidateReturn != null) {
            return candidateReturn;
        } else if (Proxy.isProxyClass(actualEntityType)) {
            // if a jdk proxy and candidateReturn is null, try improving
            final Class[] otherPossibilities = AopProxyUtils
                            .proxiedUserInterfaces(actualEntityType);
            if (otherPossibilities != null) {
                DefaultDaoRegistry.s_logger.info("Trying to unwrap JDK proxy to get DAO for type");
                for (final Class c : otherPossibilities) {
                    candidateReturn = (GenericDao<T>) this.m_daos.get(c);
                    if (candidateReturn != null) {
                        return candidateReturn;
                    }
                }
            }
        }
        // we give up
        return null;
    }

    /**
     * Set a new DAO name pattern. Only DAOs whose bean names match this pattern are collected.
     * Allowed wildcards are '*' which match any characters, the default is {@code "*"} which
     * matches all DAOs.
     * 
     * @param namePattern
     *            The name pattern to set.
     */
    public void setNamePattern(final String namePattern) {
        this.m_daoNamePattern = namePattern;
    }

    /**
     * Load all GenericDaos from this spring bean's bean factory.
     */
    protected void initDaosFromSpringBeans() {
        this.waitUntilApplicationContextIsReady();
        final String[] beanNamesToLoad = this.m_applicationContext
                        .getBeanNamesForType(GenericDao.class);
        for (final String name : beanNamesToLoad) {
            if (!PatternMatchUtils.simpleMatch(this.m_daoNamePattern, name)) {
                // Doesn't match - so skip it.
                continue;
            }
            final GenericDao<?> dao = (GenericDao<?>) this.m_applicationContext.getBean(name);
            // avoid adding a DAO again
            if (!this.m_daos.values().contains(dao)) {
                this.initDao(dao);
                this.m_daos.put(dao.getPersistentClass(), dao);
            }
        }
    }

    /**
     * This method can be overridden by child classes to initialize all DAOs even further.
     * 
     * @param dao
     *            The dao to initialize.
     */
    protected void initDao(final GenericDao<?> dao) {
    }

    /** {@inheritDoc} */
    @Override
    public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
        if ((!this.m_initialized) && this.m_collectDaos) {
            this.m_initialized = true;
            this.initDaosFromSpringBeans();
        }
        return this.m_daos;
    }

    /**
     * @param daos
     *            Registers the DAOs.
     */
    public void setDaos(final Map<Class<?>, GenericDao<?>> daos) {
        this.m_daos = daos;
        for (final GenericDao<?> dao : daos.values()) {
            this.initDao(dao);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
                    throws BeansException {
        this.m_applicationContext = applicationContext;
        this.m_creatorThreadId = Thread.currentThread().getId();
    }

    /**
     * See {@link setCollectDaos}.
     * 
     * @return Whether to collect DAOs automatically.
     */
    public boolean isCollectDaos() {
        return this.m_collectDaos;
    }

    /**
     * By default we automatically collect here all generic DAOs from the spring application context
     * (all DAOs that implement the GenericDao interface). This setter method allows to change this
     * default.
     * 
     * @param collectDaos
     *            The new value for collecting daos.
     */
    public void setCollectDaos(final boolean collectDaos) {
        this.m_collectDaos = collectDaos;
    }

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		onContextRefreshed();
	}
}
