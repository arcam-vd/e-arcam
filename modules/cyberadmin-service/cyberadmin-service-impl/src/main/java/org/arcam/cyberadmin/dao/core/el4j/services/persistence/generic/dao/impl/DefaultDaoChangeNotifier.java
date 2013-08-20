/*
 * DefaultDaoChangeNotifier
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

import java.util.ArrayList;
import java.util.List;

import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoChangeListener;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoChangeNotifier;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.DaoChangeNotifier.Change;

/**
 * A default implementation with no notable features.
 *
 * @author Adrian Moos (AMS)
 */
public class DefaultDaoChangeNotifier
	implements DaoChangeNotifier {

	/**
	 * The presently subscribed listeners.
	 */
	protected List<DaoChangeListener> m_listeners
		= new ArrayList<DaoChangeListener>();
	
	/**
	 * {@inheritDoc}
	 */
	public void subscribe(DaoChangeListener cl) {
		m_listeners.add(cl);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void unsubscribe(DaoChangeListener cl) {
		m_listeners.remove(cl);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void announce(Change change) {
		List<DaoChangeListener> snapshot
			= new ArrayList<DaoChangeListener>(m_listeners);
		for (DaoChangeListener cl : snapshot) {
			cl.changed(change);
		}
	}
}
