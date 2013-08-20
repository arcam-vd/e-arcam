/*
 * DaoChangeNotifier
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

/**
 * Notifies registered observers of DAO changes.
 *
 * The notifications sent are consistent.
 *
 * @author Adrian Moos (AMS)
 */
public interface DaoChangeNotifier {
	/** A fuzzy change object so callers do not have to construct their own. */
	public static final Change FUZZY_CHANGE = new Change();

	/**
	 * Sent if something in this DAO view may have changed.
	 **/
	public static class Change { }
	
	/**
	 * Something about {@link #m_changee} may have changed.
	 */
	public static class EntityChange extends Change {
		/** See class documentation. */
		private Object m_changee;

		/**
		 * @return Returns the changee.
		 */
		public Object getChangee() {
			return m_changee;
		}

		/**
		 * @param changee Is the changee to set.
		 */
		public void setChangee(Object changee) {
			m_changee = changee;
		}
	}
	
	/** The {@link #m_changee} has new state. */
	public static class NewEntityState extends EntityChange { }
	
	/** The {@link #m_changee}'s state has changed. */
	public static class EntityStateChanged extends NewEntityState { }
	
	/**
	 * The {@link #m_changee} has been inserted.
	 */
	public static class EntityInserted extends NewEntityState { }
	
	/**
	 * The {@link #m_changee} has been deleted.
	 */
	public static class EntityDeleted extends EntityChange { }

	
	/**
	 * Causes {@code cl} to receive future change notifications.
	 * @param cl The DaoChangeListener to subscribe
	 */
	public void subscribe(DaoChangeListener cl);
	
	/**
	 * Causes {@code cl} to no longer receive future change notifications.
	 * @param cl The DaoChangeListener to unsubscribe
	 */
	public void unsubscribe(DaoChangeListener cl);
	
	/**
	 * Announces {@code change} to all subscribed observers.
	 * @param change The change to announce
	 */
	public void announce(Change change);
}