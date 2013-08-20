/*
 * AbstractCriteria
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
 package org.arcam.cyberadmin.dao.core.el4j.services.search.criterias;

import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;


/**
 * Abstract parent class for Criteria implementations.
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractCriteria implements Criteria {
	/**
	 * Is the field the criteria is made for.
	 */
	private String m_field;
	
	/**
	 * Is the value of this criteria.
	 */
	private Object m_value;
	
	/**
	 * Default constructor for remoting protocols like hessian added.
	 */
	protected AbstractCriteria() { }
	
	/**
	 * Constructor.
	 *
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 */
	protected AbstractCriteria(String field, Object value) {
		Reject.ifEmpty(field);
		Reject.ifNull(value);
		m_field = field;
		m_value = value;
	}
	
	/**
	 * @return Returns the field.
	 */
	public final String getField() {
		return m_field;
	}

	/**
	 * @return Returns the value.
	 */
	public final Object getValue() {
		return m_value;
	}
	
	/**
	 * @return Returns the string value of this criteria.
	 */
	public final String getStringValue() {
		return (String) getValue();
	}
	
	/**
	 * @return Returns the boolean value of this criteria.
	 */
	public final Boolean getBooleanValue() {
		return (Boolean) getValue();
	}

	/**
	 * @return Returns the integer value of this criteria.
	 */
	public final Integer getIntegerValue() {
		return (Integer) getValue();
	}

	/**
	 * @return Returns the long value of this criteria.
	 */
	public final Long getLongValue() {
		return (Long) getValue();
	}

	/**
	 * @return Returns the short value of this criteria.
	 */
	public final Short getShortValue() {
		return (Short) getValue();
	}

	/**
	 * @return Returns the byte value of this criteria.
	 */
	public final Byte getByteValue() {
		return (Byte) getValue();
	}
	
	/**
	 * @return Returns the double value of this criteria.
	 */
	public final Double getDoubleValue() {
		return (Double) getValue();
	}
	
	/**
	 * @return Returns the float value of this criteria.
	 */
	public final Float getFloatValue() {
		return (Float) getValue();
	}
}
