/*
 * OpenSessionInServiceInterceptor
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This interceptor is similar to {@link OpenSessionInViewInterceptor}, but can
 * also be used e.g. for batch job processing where no HTTP requests are used.
 * Another use case is to take this class as a replacement for
 * {@link AbstractIRISHibernateInterceptor}, now having more control over the flush mode and
 * a single session feature.
 *
 *
 * @author Pham Quoc Ky (QKP)
 */
public class OpenSessionInServiceInterceptor implements MethodInterceptor,
	Serializable {
	
	/**
	 * The logger.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(OpenSessionInServiceInterceptor.class);
	
	/**
	 * Is single session mode used?
	 */
	private boolean m_singleSession = true;
	
	/**
	 * The Hibernate session factory.
	 */
	private SessionFactory m_sessionFactory;
	
	/**
	 * The flushing stragtegy to use.
	 */
	private FlushMode m_flushMode = FlushMode.COMMIT;

	
	/**
	 * @return the singleSession
	 */
	public boolean isSingleSession() {
		return m_singleSession;
	}

	/**
	 * @param singleSession
	 *            the singleSession to set
	 */
	public void setSingleSession(boolean singleSession) {
		this.m_singleSession = singleSession;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return m_sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.m_sessionFactory = sessionFactory;
	}

	/**
	 * @return the flushMode
	 */
	public FlushMode getFlushMode() {
		return m_flushMode;
	}

	/**
	 * @param flushMode
	 *            the flushMode to set
	 */
	public void setFlushMode(FlushMode flushMode) {
		this.m_flushMode = flushMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		boolean participate = false;

		if (isSingleSession()) {
			// single session mode
			if (TransactionSynchronizationManager
				.hasResource(m_sessionFactory)) {
				// Do not modify the Session: just set the participate flag.
				participate = true;
			} else {
				s_logger.debug("Opening single Hibernate Session in "
						+ "OpenSessionInServiceInterceptor");
				Session session = getSession(m_sessionFactory);
				TransactionSynchronizationManager.bindResource(
					m_sessionFactory, new SessionHolder(session));
			}
		} else {
			// deferred close mode
			if (SessionFactoryUtils.isDeferredCloseActive(m_sessionFactory)) {
				// Do not modify deferred close: just set the participate flag.
				participate = true;
			} else {
				SessionFactoryUtils.initDeferredClose(m_sessionFactory);
			}
		}

		try {
			return invocation.proceed();
		} finally {
			if (!participate) {
				if (isSingleSession()) {
					// single session mode
					SessionHolder sessionHolder = (SessionHolder)
						TransactionSynchronizationManager
							.unbindResource(m_sessionFactory);
					s_logger.debug("Closing single Hibernate Session in "
							+ "OpenSessionInServiceInterceptor");
					closeSession(sessionHolder.getSession());
				} else {
					// deferred close mode
					SessionFactoryUtils.processDeferredClose(m_sessionFactory);
				}
			}
		}
	}

	/**
	 * Get a Session for the SessionFactory that this filter uses.
	 * Note that this just applies in single session mode!
	 * <p>
	 * The default implementation delegates to the
	 * <code>SessionFactoryUtils.getSession</code> method and sets the
	 * <code>Session</code>'s flush mode to "NEVER".
	 * <p>
	 * Can be overridden in subclasses for creating a Session with a custom
	 * entity interceptor or JDBC exception translator.
	 *
	 * @param sessionFactory
	 *            the SessionFactory that this filter uses
	 * @return the Session to use
	 * @throws DataAccessResourceFailureException
	 *             if the Session could not be created
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession(SessionFactory,
	 *      boolean)
	 * @see org.hibernate.FlushMode#COMMIT
	 */
	protected Session getSession(SessionFactory sessionFactory)
		throws DataAccessResourceFailureException {
		
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		FlushMode flushMode = getFlushMode();
		if (flushMode != null) {
			session.setFlushMode(flushMode);
		}

		return session;
	}

	/**
	 * Close the given Session. Note that this just applies in single
	 * session mode!
	 * <p>
	 * Can be overridden in subclasses, e.g. for flushing the Session before
	 * closing it. See class-level javadoc for a discussion of flush handling.
	 * Note that you should also override getSession accordingly, to set the
	 * flush mode to something else than NEVER.
	 *
	 * @param session
	 *            the Session used for filtering
	 */
	protected void closeSession(Session session) {
		SessionFactoryUtils.closeSession(session);
	}
}
