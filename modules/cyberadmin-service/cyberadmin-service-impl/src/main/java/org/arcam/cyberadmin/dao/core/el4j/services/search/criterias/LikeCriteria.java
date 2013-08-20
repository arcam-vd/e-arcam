/*
 * LikeCriteria
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

/**
 * Criteria for the like pattern.
 *
 * @author Martin Zeltner (MZE)
 */
public class LikeCriteria extends AbstractCriteria {
	/**
	 * Marks whether the pattern is case sensitive.
	 */
	private Boolean m_caseSensitive;
	
	/**
	 * Default constructor for remoting protocols like hessian added.
	 */
	protected LikeCriteria() { }
	
	/**
	 * Hidden constructor.
	 *
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @param caseSensitive Marks if the pattern is case sensitive.
	 */
	protected LikeCriteria(String field, String value,
		boolean caseSensitive) {
		super(field, value);
		m_caseSensitive = Boolean.valueOf(caseSensitive);
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns a case insensitive pattern criteria.
	 */
	public static LikeCriteria caseInsensitive(String field, String value) {
		return new LikeCriteria(field, value, false);
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns a case sensitive pattern criteria.
	 */
	public static LikeCriteria caseSensitive(String field, String value) {
		return new LikeCriteria(field, value, true);
	}

	/**
	 * @return Returns <code>true</code> if it is case sensitive.
	 */
	public final Boolean isCaseSensitive() {
		return m_caseSensitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return "like";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSqlWhereCondition() {
		// TODO: consider also case sensitivity
		return " ( "+getField()+" LIKE "+getValue()+ " ) ";
	}
	
}
