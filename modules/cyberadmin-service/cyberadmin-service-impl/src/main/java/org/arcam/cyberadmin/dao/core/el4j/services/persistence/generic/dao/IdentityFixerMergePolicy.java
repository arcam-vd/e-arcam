/*
 * IdentityFixerMergePolicy
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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * This class represents a policy on how to merge object graphs in the identity fixer.
 *
 * @see AbstractIdentityFixer
 * 
 * @author Andreas Rueedlinger (ARR)
 */
public class IdentityFixerMergePolicy {

	/** The update policy. */
	private UpdatePolicy m_updatePolicy;
	
	/** The objects to update, only set when <code>m_updatePolicy == UpdatePolicy.UPDATE_CHOSEN</code>. */
	private List<Object> m_objectsToUpdate;
	
	/** Should Preparation be performed. */
	private boolean m_performPreparation;
	
	/**
	 *	A map of [updated -> anchor] used to correctly merge collections.
	 */
	private IdentityHashMap<Object, Object> m_collectionEntryMapping;
	
	/**
	 * Default constructor.
	 * Sets update policy to all and preparation will be performed.
	 */
	protected IdentityFixerMergePolicy() {
		m_updatePolicy = UpdatePolicy.UPDATE_ALL;
		m_performPreparation = true;
		m_collectionEntryMapping = new IdentityHashMap<Object, Object>();
	}
	
	/**
	 * Constructor to customize the policy.
	 * @param updatePolicy            the update policy to use.
	 * @param objectsToUpdate         the objectsToUpdate if UDPATE_CHOSEN policy is chosen above.
	 * @param performPreparation      should preparation be performed (eg. for unwrapping proxies).
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *               used to correctly merge collections.
	 */
	protected IdentityFixerMergePolicy(UpdatePolicy updatePolicy, List<Object> objectsToUpdate,
		boolean performPreparation, IdentityHashMap<Object, Object> collectionEntryMapping) {
		m_updatePolicy = updatePolicy;
		m_objectsToUpdate = objectsToUpdate;
		m_performPreparation = performPreparation;
		m_collectionEntryMapping = collectionEntryMapping;
	}
	
	/**
	 * @return the update policy of this id fixer merge policy.
	 */
	public UpdatePolicy getUpdatePolicy() {
		return m_updatePolicy;
	}
	
	/**
	 * Set the objects to update explicitly.
	 * @param objectsToUpdate the list of objects to update.
	 */
	public void setObjectsToUpdate(List<Object> objectsToUpdate) {
		m_objectsToUpdate = objectsToUpdate;
	}
	
	/**
	 * @return  The objects to update, 
	 *     only set when <code>getUpdatePolicy == UpdatePolicy.UPDATE_CHOSEN</code>.
	 */
	public List<Object> getObjectsToUpdate() {
		return m_objectsToUpdate;
	}
	
	/**
	 * @return if preparation is needed.
	 */
	public boolean needsPreparation() {
		return m_performPreparation;
	}
	
	/**
	 * @return A map of [updated -> anchor] used to correctly merge collections.
	 */
	public IdentityHashMap<Object, Object> getCollectionEntryMapping() {
		return m_collectionEntryMapping;
	}
	
	/**
	 * This enumeration describes how the identity fixer should handle object updates.
	 */
	public enum UpdatePolicy {
		/**
		 * Only complement the identity fixer representatives, 
		 * no update of the old objects. 
		 */
		NO_UPDATE,
		
		/**
		 * Update the specified objects only, do not touch the others.
		 * Add new objects. 
		 */
		UPDATE_CHOSEN,
		
		/**
		 * Update all the objects.
		 */
		UPDATE_ALL
		
	}
	
	/**
	 * @return a policy forcing all objects to be updated.
	 */
	public static IdentityFixerMergePolicy reloadAllPolicy() {
		return new IdentityFixerMergePolicy();
	}
	
	/**
	 * @param objectsToUpdate the objects to be updated.
	 * @return a policy forcing only the specified objects to be updated,
	 *     leaving the rest untouched.
	 */
	public static IdentityFixerMergePolicy reloadObjectsPolicy(List<Object> objectsToUpdate) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_CHOSEN, 
			new ArrayList<Object>(objectsToUpdate), true, new IdentityHashMap<Object, Object>());
		return obj;
	}
	
	/**
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy() {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.NO_UPDATE, null, true, 
			new IdentityHashMap<Object, Object>());
		return obj;
	}
	
	/**
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy forcing all objects to be updated.
	 */
	public static IdentityFixerMergePolicy reloadAllPolicy(IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_ALL, null, 
			true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param objectsToUpdate         the objects to be updated.
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy forcing only the specified objects to be updated,
	 *     leaving the rest untouched.
	 */
	public static IdentityFixerMergePolicy reloadObjectsPolicy(List<Object> objectsToUpdate,
		IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_CHOSEN, 
			new ArrayList<Object>(objectsToUpdate), true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy(IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.NO_UPDATE, null, 
			true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param updatePolicy           the update policy to use.
	 * @param objectsToUpdate        the objectsToUpdate if UDPATE_CHOSEN policy is chosen above.
	 * @param performPreparation     should preparation be performed (eg. for unwrapping proxies).
	 * @param collectionEntryMapping the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return the custom policy specified by the arguments.
	 */
	public static IdentityFixerMergePolicy customPolicy(UpdatePolicy updatePolicy, List<Object> objectsToUpdate,
		boolean performPreparation,	IdentityHashMap<Object, Object> collectionEntryMapping) {
		
		List<Object> objs = null;
		if (updatePolicy == UpdatePolicy.UPDATE_CHOSEN) {
			objs = new ArrayList<Object>(objectsToUpdate);
		}
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(updatePolicy, objs, 
			performPreparation, collectionEntryMapping);
		return obj;
	}
		
	
	
}

