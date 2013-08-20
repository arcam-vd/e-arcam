/*
 * IdentityFixerCollectionField
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

import java.lang.reflect.Field;

/**
 * This class is container for a collection containing object, holding both the instance and the field
 * giving access to the collection.
 * This container is used in the {@link AbstractIdentityFixer} to remember which collections has to be
 * replaced in the 2-way merging.
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class IdentityFixerCollectionField {
	/** See corresponding getter for information. */
	private Object m_instance;
	
	/** See corresponding getter for information. */
	private Field m_field;
	
	/**
	 * Constructs a new collection field.
	 * @param instance the instance containing the collection.
	 * @param field the field containing the collection.
	 */
	public IdentityFixerCollectionField(Object instance, Field field) {
		assert instance != null;
		assert field != null;
		m_instance = instance;
		m_field = field;
	}
	
	/**
	 * @return the contained instance.
	 */
	public Object getInstance() {
		return m_instance;
	}
	
	/**
	 * @return the contained field.
	 */
	public Field getField() {
		return m_field;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return m_instance.getClass().hashCode() * 31 + m_field.hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof IdentityFixerCollectionField) {
			IdentityFixerCollectionField idcf = (IdentityFixerCollectionField) o;
			return m_instance == idcf.m_instance && m_field.equals(idcf.m_field);
		} else {
			return false;
		}
	}
}
