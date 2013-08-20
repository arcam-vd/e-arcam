/*
 * HibernateSessionFactoryInjectorBeanPostProcessor
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

import java.beans.PropertyDescriptor;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.GenericDao;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;

/**
 * Inject the session factory in GenericDaos (or other daos) if needed.
 *  It gets the sessionFactory from the spring context
 *   by using the default name {@link SESSION_FACTORY_BEAN_DEFAULT_NAME} or
 *   via its setter method.
 *
 * @svnLink $Revision: 3875 $;$Date: 2009-08-04 19:35:53 +0700 (Tue, 04 Aug 2009) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_7/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/HibernateSessionFactoryInjectorBeanPostProcessor.java $
 *
 * @author Philipp Oser (POS)
 */
public class HibernateSessionFactoryInjectorBeanPostProcessor
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware {

	private static final Logger s_logger= LoggerFactory.getLogger(HibernateSessionFactoryInjectorBeanPostProcessor.class);

	/**
	 * The default name for the property of the session factory.
	 */
	public static final String SESSION_FACTORY_BEAN_DEFAULT_NAME = "sessionFactory";
	
	private int order = Ordered.LOWEST_PRECEDENCE;
		

	/**
	 * Initiates the real work.
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		s_logger.debug("Treating bean with name:" + beanName);
		if (GenericDao.class.isAssignableFrom(bean.getClass())) {
			s_logger.debug("init dao with name:" + beanName);
			initDao((GenericDao<?>) bean);
		}
		return bean;
	}

	/** {@inheritDoc} */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	/**
	 * Try to init the sessionFactory of the bean.
	 * @param dao
	 */
	protected void initDao(GenericDao<?> dao) {
		if (getSessionFactory() != null) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor(SESSION_FACTORY_BEAN_DEFAULT_NAME,
					dao.getClass());
				Object value = pd.getReadMethod().invoke(dao);
				if (value == null) {
					pd.getWriteMethod().invoke(dao, m_sessionFactory);
				}
				s_logger.debug("value set in dao set");
			} catch (Exception e) {
				// ignore problems
				s_logger.info("problem when auto-setting sessionFactory", e);

			}
		}
	}

	
	
	/**
	 * Gets the session factory (from spring context if needed).
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		if ((m_sessionFactory == null) && (m_applicationContext != null)) {
			// try to locate the session factory
			if (m_applicationContext.containsBean(SESSION_FACTORY_BEAN_DEFAULT_NAME)) {
				m_sessionFactory = (SessionFactory)
					m_applicationContext.getBean(SESSION_FACTORY_BEAN_DEFAULT_NAME);
				Reject.ifNull(m_sessionFactory, "session factory must not be null!");
			}
		}
		return m_sessionFactory;
	}

	/**
	 * You can either set the session factory explicitly or
	 *  have the factory load the session factory implicitly (by name).
	 * @param factory
	 */
	public void setSessionFactory(SessionFactory factory) {
		m_sessionFactory = factory;
	}

	protected SessionFactory m_sessionFactory;

	private ApplicationContext m_applicationContext;

	public void setOrder(int order) {
		this.order = order;
	}

	/** {@inheritDoc} */
	public int getOrder() {
		return this.order;
	}

	/** {@inheritDoc} */
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		
		m_applicationContext = applicationContext;
	}
}
