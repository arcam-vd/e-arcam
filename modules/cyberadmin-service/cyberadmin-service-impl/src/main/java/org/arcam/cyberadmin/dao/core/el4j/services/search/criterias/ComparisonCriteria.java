/*
 * ComparisonCriteria
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

import org.springframework.util.StringUtils;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;

/**
 * Criteria to compare fields to values.
 *
 * @author Martin Zeltner (MZE)
 */
public class ComparisonCriteria extends AbstractCriteria {
	/**
	 * Is the type prefix.
	 */
	public static final String TYPE_PREFIX = "comparison";
	
	/**
	 * Is the compare operator.
	 */
	private String m_operator;
	
	/**
	 * Is the type of this criteria.
	 */
	private String m_type;
	
	/**
	 * Default constructor for remoting protocols like hessian added.
	 */
	protected ComparisonCriteria() { }
	
	/**
	 * Constructor to create new special comparison Criteria objects.
	 *
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @param operator Is the compare operator.
	 * @param typeSuffix is the type suffix of this criteria.
	 *    (we use the unqualified class name of the basic Java types (
	 *     Boolean,Integer, ...))
	 */
	public ComparisonCriteria(String field, Object value, String operator,
		String typeSuffix) {
		super(field, value);
		Reject.ifEmpty(field);
		m_type = TYPE_PREFIX + StringUtils.capitalize(typeSuffix);
		m_operator = operator;
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, boolean value) {
		return new ComparisonCriteria(
			field, Boolean.valueOf(value), "=", "Boolean");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, int value) {
		return new ComparisonCriteria(
			field, Integer.valueOf(value), "=", "Integer");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, long value) {
		return new ComparisonCriteria(
			field, Long.valueOf(value), "=", "Long");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, short value) {
		return new ComparisonCriteria(
			field, Short.valueOf(value), "=", "Short");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, byte value) {
		return new ComparisonCriteria(
			field, Byte.valueOf(value), "=", "Byte");
	}
	
	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, double value) {
		return new ComparisonCriteria(
			field, new Double(value), "=", "Double");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, Enum<?> value) {
		return new ComparisonCriteria(
			field, value, "=", "Enum");
	}
	
	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, float value) {
		return new ComparisonCriteria(
			field, new Float(value), "=", "Float");
	}

	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equals(String field, String value) {
		return new ComparisonCriteria(
			field, value, "=", "String");
	}
	
	/**
	 * @param field Is the field the criteria is made for.
	 * @param value Is the value of this criteria.
	 * @return Returns an equals comparison criteria.
	 */
	public static ComparisonCriteria equalsObject(String field, Object value) {
		return new ComparisonCriteria(
			field, value, "=", "Object");
	}
	
	/**
	 * @return Returns the compare operator.
	 */
	public String getOperator() {
		return m_operator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return m_type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getSqlWhereCondition();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSqlWhereCondition() {
		return " ( "+getField()+" "+getOperator()+" "+getValue()+ " ) ";
	}
	
}
