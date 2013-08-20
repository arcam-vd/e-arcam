/*
 * FallbackDaoRegistry
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

import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.GenericDao;

/**
 * This class extends the {@link DefaultDaoRegistry} with a fallback function:
 * If no DAO can be found for an entityType, a generic DAO is created.
 *
 * @author Stefan Wismer (SWI)
 */
public class FallbackDaoRegistry extends DefaultDaoRegistry {
	/**
	 * The bean name of the fallback DAO prototype.
	 */
	protected String m_daoPrototypeBeanName;
	
	/** {@inheritDoc} */
	@Override
	public <T> GenericDao<T> getFor(Class<T> entityType) {
		GenericDao<T> dao = super.getFor(entityType);
		
		/* if no dao was found, try to create one automatically! */
		if (dao == null) {
			dao = createFor(entityType);
			m_daos.put(entityType, dao);
		}
		
		return dao;
	}
	
	/**
	 * @param <T>   The class of entityType
	 * @param entityType
	 *              The domain class for which a generic DAO should be returned.
	 * @return      A new generic DAO for this entityType
	 */
	@SuppressWarnings("unchecked")
	protected <T> GenericDao<T> createFor(Class<T> entityType) {
		GenericDao<T> dao = null;
		if (m_daoPrototypeBeanName != null) {
			dao = (GenericDao<T>) m_applicationContext.getBean(
				m_daoPrototypeBeanName);
			if (dao != null) {
				dao.setPersistentClass(entityType);
			}
		}
		
		return dao;
	}

	/**
	 * @return    The bean name of the fallback DAO prototype.
	 */
	public String getDaoPrototypeBeanName() {
		return m_daoPrototypeBeanName;
	}

	/**
	 * @param daoPrototypeBeanName
	 *                             The bean name of the fallback DAO prototype.
	 */
	public void setDaoPrototypeBeanName(String daoPrototypeBeanName) {
		m_daoPrototypeBeanName = daoPrototypeBeanName;
	}
}
