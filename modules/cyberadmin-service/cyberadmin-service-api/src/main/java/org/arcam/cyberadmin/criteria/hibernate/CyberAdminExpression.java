/*
 * IrisExpression.java
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
package org.arcam.cyberadmin.criteria.hibernate;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;

/**
 * Description about an expression used for querying Hibernate entities.
 * 
 * @author phd
 * 
 */
@SuppressWarnings("serial")
public class CyberAdminExpression implements Serializable {

     private static final String DOT = ".";
     private static final String[] ACCEPTABLE_DATE_FORMATS = new String[] { "d-M-y H:m", "dd/MM/yyyy" };

     private static final Map<String, String> SUPPORTED_OPERATORS = new HashMap<String, String>();
     private static final Map<String, Class<?>> SUPPORTED_TYPES = new HashMap<String, Class<?>>();
     private static final List<String> PROPERTY_COMPARISON_OPERATORS = new ArrayList<String>();

     // CHECKSTYLE:OFF MultipleStringLiterals
     static {

          /** Basic operators **/
          SUPPORTED_OPERATORS.put("eq", "=");
          SUPPORTED_OPERATORS.put("lt", "<");
          SUPPORTED_OPERATORS.put("le", "<=");
          SUPPORTED_OPERATORS.put("gt", ">");
          SUPPORTED_OPERATORS.put("ge", ">=");
          SUPPORTED_OPERATORS.put("ne", "<>");
          SUPPORTED_OPERATORS.put("isNull", "isNull");
          SUPPORTED_OPERATORS.put("isNotNull", "isNotNull");
          SUPPORTED_OPERATORS.put("isEmpty", "isEmpty");
          SUPPORTED_OPERATORS.put("isNotEmpty", "isNotEmpty");
          SUPPORTED_OPERATORS.put("like", "like");
          SUPPORTED_OPERATORS.put("in", "in");
          SUPPORTED_OPERATORS.put("notIn", "notIn");
          SUPPORTED_OPERATORS.put("between", "between");
          SUPPORTED_OPERATORS.put("notBetween", "notBetween");
          SUPPORTED_OPERATORS.put("startWith", "startWith");
          SUPPORTED_OPERATORS.put("endWith", "endWith");
          SUPPORTED_OPERATORS.put("contain", "contain");
          SUPPORTED_OPERATORS.put("notContain", "notContain");
          SUPPORTED_OPERATORS.put("not", "not");
          SUPPORTED_OPERATORS.put("sqlRestriction", "sqlRestriction");
          
          /** Property comparison operators **/
          SUPPORTED_OPERATORS.put("eqProperty", "eqProperty");
          SUPPORTED_OPERATORS.put("geProperty", "geProperty");
          SUPPORTED_OPERATORS.put("gtProperty", "gtProperty");
          SUPPORTED_OPERATORS.put("leProperty", "leProperty");
          SUPPORTED_OPERATORS.put("ltProperty", "ltProperty");
          SUPPORTED_OPERATORS.put("neProperty", "neProperty");
          
          PROPERTY_COMPARISON_OPERATORS.add("eqProperty");
          PROPERTY_COMPARISON_OPERATORS.add("geProperty");
          PROPERTY_COMPARISON_OPERATORS.add("gtProperty");
          PROPERTY_COMPARISON_OPERATORS.add("leProperty");
          PROPERTY_COMPARISON_OPERATORS.add("ltProperty");
          PROPERTY_COMPARISON_OPERATORS.add("neProperty");

          /** Subquery Operators **/
          SUPPORTED_OPERATORS.put("subqueryEq", "subqueryEq");
          SUPPORTED_OPERATORS.put("subqueryEqAll", "subqueryEqAll");
          SUPPORTED_OPERATORS.put("subqueryExists", "subqueryExists");
          SUPPORTED_OPERATORS.put("subqueryGe", "subqueryGe");
          SUPPORTED_OPERATORS.put("subqueryGeAll", "subqueryGeAll");
          SUPPORTED_OPERATORS.put("subqueryGeSome", "subqueryGeSome");
          SUPPORTED_OPERATORS.put("subqueryGt", "subqueryGt");
          SUPPORTED_OPERATORS.put("subqueryGtAll", "subqueryGtAll");
          SUPPORTED_OPERATORS.put("subqueryGtSome", "subqueryGtSome");
          SUPPORTED_OPERATORS.put("subqueryIn", "subqueryIn");
          SUPPORTED_OPERATORS.put("subqueryLe", "subqueryLe");
          SUPPORTED_OPERATORS.put("subqueryLeAll", "subqueryLeAll");
          SUPPORTED_OPERATORS.put("subqueryLeSome", "subqueryLeSome");
          SUPPORTED_OPERATORS.put("subqueryLt", "subqueryLt");
          SUPPORTED_OPERATORS.put("subqueryLtAll", "subqueryLtAll");
          SUPPORTED_OPERATORS.put("subqueryLtSome", "subqueryLtSome");
          SUPPORTED_OPERATORS.put("subqueryNe", "subqueryNe");
          SUPPORTED_OPERATORS.put("subqueryNotExists", "subqueryNotExists");
          SUPPORTED_OPERATORS.put("subqueryNotIn", "subqueryNotIn");
          SUPPORTED_OPERATORS.put("subqueryPropertyEq", "subqueryPropertyEq");
          SUPPORTED_OPERATORS.put("subqueryPropertyEqAll", "subqueryPropertyEqAll");
          SUPPORTED_OPERATORS.put("subqueryPropertyGe", "subqueryPropertyGe");
          SUPPORTED_OPERATORS.put("subqueryPropertyGeAll", "subqueryPropertyGeAll");
          SUPPORTED_OPERATORS.put("subqueryPropertyGeSome", "subqueryPropertyGeSome");
          SUPPORTED_OPERATORS.put("subqueryPropertyGt", "subqueryPropertyGt");
          SUPPORTED_OPERATORS.put("subqueryPropertyGtAll", "subqueryPropertyGtAll");
          SUPPORTED_OPERATORS.put("subqueryPropertyGtSome", "subqueryPropertyGtSome");
          SUPPORTED_OPERATORS.put("subqueryPropertyIn", "subqueryPropertyIn");
          SUPPORTED_OPERATORS.put("subqueryPropertyLe", "subqueryPropertyLe");
          SUPPORTED_OPERATORS.put("subqueryPropertyLeAll", "subqueryPropertyLeAll");
          SUPPORTED_OPERATORS.put("subqueryPropertyLeSome", "subqueryPropertyLeSome");
          SUPPORTED_OPERATORS.put("subqueryPropertyLt", "subqueryPropertyLt");
          SUPPORTED_OPERATORS.put("subqueryPropertyLtAll", "subqueryPropertyLtAll");
          SUPPORTED_OPERATORS.put("subqueryPropertyLtSome", "subqueryPropertyLtSome");
          SUPPORTED_OPERATORS.put("subqueryPropertyNe", "subqueryPropertyNe");
          SUPPORTED_OPERATORS.put("subqueryPropertyNotIn", "subqueryPropertyNotIn");

          /** Supported Types **/
          SUPPORTED_TYPES.put("String", String.class);
          SUPPORTED_TYPES.put("Integer", Integer.class);
          SUPPORTED_TYPES.put("Long", Long.class);
          SUPPORTED_TYPES.put("Double", Double.class);
          SUPPORTED_TYPES.put("Boolean", Boolean.class);
          SUPPORTED_TYPES.put("Date", Date.class);
     }
     // CHECKSTYLE:ON MultipleStringLiterals

     /** Property name, operator and the value to be compared **/
     private String propertyName;
     private String op;
     private Object value;

     /** Logical expressions associated with this expression **/
     private List<CyberAdminExpression> orExps = new ArrayList<CyberAdminExpression>();
     private List<CyberAdminExpression> andExps = new ArrayList<CyberAdminExpression>();

     /** Indicates whether the String comparison should be done in ignore-case mode **/
     private boolean ignoreCase = false;

     /** Should a negation be applied on the final criteria **/
     private boolean negative = false;

     /**
      * Alias for this expression.
      **/
     private String alias = null;
     
     /**
      * Alias for other side property (used in case of {@link #PROPERTY_COMPARISON_OPERATORS}).
      **/
     private String otherSideAlias = null;
     
     /** Criteria to generate the sub query in case it is used **/
     private CyberAdminSearchCriteria subQueryCriteria;

     /**
      * <p>
      * The default constructor.
      * </p>
      * 
      */
     public CyberAdminExpression() {

     }

     /**
      * <p>
      * Constructs an instance of {@link CyberAdminExpression} from a string. Input <code>deserializedString</code>
      * <strong>must</strong> be in the following format: "propertyName"."op"="value".
      * </p>
      * 
      */
     @SuppressWarnings({ "rawtypes", "unchecked" })
     public CyberAdminExpression(String deserializedString) {
          int operatorEnd = deserializedString.indexOf("=");
          int propertyNameEnd = deserializedString.substring(0, operatorEnd).lastIndexOf(DOT);
          propertyName = deserializedString.substring(0, propertyNameEnd);
          String opWithType = deserializedString.substring(propertyNameEnd + 1, operatorEnd);
          for (String supportedOp : SUPPORTED_OPERATORS.keySet()) {
                if (StringUtils.equals(opWithType, supportedOp)) {
                     op = SUPPORTED_OPERATORS.get(supportedOp);
                     value = deserializedString.substring(operatorEnd + 1);
                     break;
                } else if (StringUtils.startsWith(opWithType, supportedOp)) {
                     op = SUPPORTED_OPERATORS.get(supportedOp);

                     if ("in".equals(op) || "notIn".equals(op)) {
                          Class valueClass = SUPPORTED_TYPES.get(StringUtils.removeStart(opWithType, supportedOp));
                          String[] valueStrings = StringUtils.split(deserializedString.substring(operatorEnd + 1), ",");
                          List valueObjects = new ArrayList();
                          for (String valueString : valueStrings) {
                                valueObjects.add(getTypedValueFromString(valueClass, valueString));
                          }
                          value = valueObjects.toArray(new Object[valueObjects.size()]);
                     } else {
                          Class valueClass = SUPPORTED_TYPES.get(StringUtils.removeStart(opWithType, supportedOp));
                          String valueString = deserializedString.substring(operatorEnd + 1);
                          value = getTypedValueFromString(valueClass, valueString);
                     }
                     break;
                }
          }
     }

     /**
      * <p>
      * Constructs an instance of {@link CyberAdminExpression} from a string. Input <code>deserializedString</code>
      * <strong>must</strong> be in the following format: "propertyName"."op"="value".
      * </p>
      */
     public static CyberAdminExpression forExpression(String deserializedString) {
          return new CyberAdminExpression(deserializedString);
     }

     /**
      * Create an expression with the specified <code>operatorName</code>, <code>propertyName</code>,
      * <code>valueClassName</code> and <code>valueString</code>.
      * 
      * @param operatorName
      *                the operator name
      * @param propertyName
      *                the property name
      * @param valueClassName
      *                the class name of the value
      * @param valueString
      *                the string representation of the value
      */
     //CHECKSTYLE:OFF LineLength
     private static CyberAdminExpression createExpression(String operatorName, String propertyName, String valueClassName,
                String valueString) {
          CyberAdminExpression expression = new CyberAdminExpression();
          expression.setPropertyName(propertyName);
          expression.setOp(SUPPORTED_OPERATORS.get(operatorName));
          if (valueClassName != null && valueString != null) { // some operator will ignore the value
                if ("in".equals(operatorName) || "notIn".equals(operatorName)) {
                     String[] valueStrings = StringUtils.split(valueString, ",");
                     List<Object> valueObjects = new ArrayList<Object>();
                     for (String s : valueStrings) {
                          valueObjects.add(getTypedValueFromString(SUPPORTED_TYPES.get(valueClassName), s));
                     }
                     expression.setValue(valueObjects.toArray(new Object[valueObjects.size()]));
                } else {
                     expression.setValue(getTypedValueFromString(SUPPORTED_TYPES.get(valueClassName), valueString));
                }
          }
          return expression;
     }
     //CHECKSTYLE:ON
     /**
      * Create a subquery with the specified <code>operatorName</code>, <code>value</code>, <code>searchCriteria</code>.
      * 
      * @param operatorName
      *                the operator name
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      * @return New instance of the {@link CyberAdminExpression} from input params.
      */
    //CHECKSTYLE:OFF LineLength
     private static CyberAdminExpression createSubqueryExpression(String operatorName, Object value,
                CyberAdminSearchCriteria searchCriteria) {
          return new CyberAdminExpression().setSubQueryCriteria(searchCriteria).setOp(SUPPORTED_OPERATORS.get(operatorName))
                     .setValue(value);
     }
     //CHECKSTYLE:ON
     /**
      * Create a subquery for the specified <code>propertyName</code> with the specified <code>operatorName</code>,
      * <code>searchCriteria</code>.
      * 
      * @param operatorName
      *                the operator name
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return New instance of the {@link CyberAdminExpression} from input params.
      */
     private static CyberAdminExpression createSubqueryPropertyExpression(String operatorName, String propertyName,
                CyberAdminSearchCriteria searchCriteria) {
          return new CyberAdminExpression().setPropertyName(propertyName).setOp(SUPPORTED_OPERATORS.get(operatorName))
                     .setSubQueryCriteria(searchCriteria);
     }

     /**
      * Constructs an 'eq' (equal) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return An 'eq' (equal) expression from the specified property name and value.
      */
     public static CyberAdminExpression eq(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, "=");
     }

     /**
      * Constructs a 'ne' (not equal) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return A 'ne' (not equal) expression from the specified property name and value.
      */
     public static CyberAdminExpression ne(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, "<>");
     }

     /**
      * Constructs a 'lt' (less than) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return A 'lt' (less than) expression from the specified property name and value.
      */
     public static CyberAdminExpression lt(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, "<");
     }

     /**
      * Constructs a 'le' (less than or equal) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return A 'le' (less than or equal) expression from the specified property name and value.
      * 
      */
     public static CyberAdminExpression le(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, "<=");
     }

     /**
      * Constructs a 'gt' (greater than) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return a 'gt' (greater than) expression from the specified property name and value.
      */
     public static CyberAdminExpression gt(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, ">");
     }

     /**
      * Constructs an 'ge' (greater than or equal) expression from the specified property name and value.
      * 
      * @param propertyName
      *                Name of the property to be compared.
      * @param value
      *                Value to be compared.
      * @return a 'ge' (greater than or equal) expression from the specified property name and value.
      * 
      */
     public static CyberAdminExpression ge(String propertyName, Object value) {
          return new CyberAdminExpression(propertyName, value, ">=");
     }

     /**
      * Constructs an 'isNull' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * 
      * @return an 'isNull' expression from the specified property name and value.
      */
     public static CyberAdminExpression isNull(String propertyName) {
          return new CyberAdminExpression(propertyName, "isNull");
     }

     /**
      * Constructs an 'isNotNull' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * 
      * @return an 'isNotNull' expression from the specified property name and value.
      */
     public static CyberAdminExpression isNotNull(String propertyName) {
          return new CyberAdminExpression(propertyName, "isNotNull");
     }

     /**
      * Constructs an 'isEmpty' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * 
      * @return an 'isEmpty' expression from the specified property name and value.
      */
     public static CyberAdminExpression isEmpty(String propertyName) {
          return new CyberAdminExpression(propertyName, "isEmpty");
     }

     /**
      * Constructs an 'isNotEmpty' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * 
      * @return an 'isNotEmpty' expression from the specified property name and value.
      */
     public static CyberAdminExpression isNotEmpty(String propertyName) {
          return new CyberAdminExpression(propertyName, "isNotEmpty");
     }

     /**
      * Constructs a 'like' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * @param valueString
      *                the string representation of the value
      * @return a 'like' expression from the specified property name and value.
      */
     public static CyberAdminExpression like(String propertyName, String valueString) {
          return new CyberAdminExpression(propertyName, valueString, "like");
     }

     /**
      * Constructs an 'in' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * @param values
      *                an array of values for checking
      * @return instance of {@link CyberAdminExpression}
      */
     public static CyberAdminExpression in(String propertyName, Object[] values) {
          return new CyberAdminExpression(propertyName, values, "in");
     }
     
     /**
      * Constructs an 'in' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * @param values
      *                a list of values for checking
      * @return instance of {@link CyberAdminExpression}
      */
     public static CyberAdminExpression in(String propertyName, List<?> values) {
          return new CyberAdminExpression(propertyName, values.toArray(new Object[values.size()]), "in");
     }

     /**
      * Constructs an 'notIn' expression from the specified property name and values.
      * 
      * @param propertyName
      *                the property name
      * @param values
      *                an array of values for checking
      * @return instance of {@link CyberAdminExpression}
      */
     public static CyberAdminExpression notIn(String propertyName, Object[] values) {
          return new CyberAdminExpression(propertyName, values, "notIn");
     }

     /**
      * Constructs an 'between' expression from the specified property name and values.
      * 
      * @param propertyName
      *                the property name
      * @param lo
      *                the lower bound
      * @param hi
      *                the upper bound
      * @return instance of {@link CyberAdminExpression}
      */
     public static CyberAdminExpression between(String propertyName, Object lo, Object hi) {
          return new CyberAdminExpression(propertyName, new Object[] { lo, hi }, "between");
     }

     /**
      * Constructs an 'notBetween' expression from the specified property name and values.
      * 
      * @param propertyName
      *                the property name
      * @param lo
      *                the lower bound
      * @return instance of {@link CyberAdminExpression}
      */
     public static CyberAdminExpression notBetween(String propertyName, Object lo, Object hi) {
          return new CyberAdminExpression(propertyName, new Object[] { lo, hi }, "notBetween");
     }

     /**
      * Constructs a 'startWith' expression from the specified property name and value.
      * 
      * @param propertyName
      *                the property name
      * @param valueString
      *                the string representation of the value
      * @param ignoreCase
      *                the boolean value indicates if case will be ignored during comparison
      *                
      * @return a 'startWith' expression from the specified property name and value.
      */
     public static CyberAdminExpression startWith(String propertyName, String valueString, boolean ignoreCase) {
          return new CyberAdminExpression(propertyName, valueString, "startWith", ignoreCase);
     }

     /**
      * Constructs an 'endWith' expression from the specified property name and value. 
      * 
      * @param propertyName
      *                the property name
      * @param valueString
      *                the string representation of the value
      * @param ignoreCase
      *                the boolean value indicates if case will be ignored during comparison
      *                
      * @return an 'endWith' expression from the specified property name and value.
      */
     public static CyberAdminExpression endWith(String propertyName, String valueString, boolean ignoreCase) {
          return new CyberAdminExpression(propertyName, valueString, "endWith", ignoreCase);
     }

     /**
      * Constructs an 'isTrue' expression from the specified property name. 
      * 
      * @param propertyName
      *                the property name
      *                
      * @return an 'isTrue' expression from the specified property name.
      */
     public static CyberAdminExpression isTrue(String propertyName) {
          return eq(propertyName, Boolean.TRUE);
     }

     /**
      * Constructs an 'isFalse' expression from the specified property name.
      * 
      * @param propertyName
      *                the property name
      *                
      * @return an 'isFalse' expression from the specified property name.
      */
     public static CyberAdminExpression isFalse(String propertyName) {
          return eq(propertyName, Boolean.FALSE);
     }

     /**
      * Constructs an 'yesterday' expression from the specified property name and DateTime value. This method is reserved
      * for the Joda DateTime type only and it completely ignores the time.
      * 
      * @param propertyName
      *                the property name
      *                
      * @return a 'yesterday' expression from the specified property name and DateTime value.
      */
     public static CyberAdminExpression yesterday(String propertyName) {
          return new CyberAdminExpression(propertyName, "yesterday");
     }

     /**
      * Constructs an 'today' expression from the specified property name and DateTime value. This method is reserved for
      * the Joda DateTime type only and it completely ignores the time.
      * 
      * @param propertyName
      *                the property name
      *                
      * @return a 'today' expression from the specified property name and DateTime value.
      */
     public static CyberAdminExpression today(String propertyName) {
          return new CyberAdminExpression(propertyName, "today");
     }

     /**
      * Constructs an 'tomorrow' expression from the specified property name and DateTime value. This method is reserved
      * for the Joda DateTime type only and it completely ignores the time.
      * 
      * @param propertyName
      *                the property name
      *                
      * @return a 'tomorrow' expression from the specified property name and DateTime value.
      */
     public static CyberAdminExpression tomorrow(String propertyName) {
          return new CyberAdminExpression(propertyName, "tomorrow");
     }

     /**
      * Constructs an 'inLastSevenDays' expression from the specified property name and DateTime value. This method is
      * reserved for the Joda DateTime type only and it completely ignores the time.
      * 
      * @param propertyName
      *                the property name
      *                
      * @return an 'inLastSevenDays' expression from the specified property name and DateTime value.
      */
     public static CyberAdminExpression inLastSevenDays(String propertyName) {
          return new CyberAdminExpression(propertyName, "inLast7Days");
     }

     /**
      * Constructs an 'eq' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryEq(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryEq", value, searchCriteria);
     }

     /**
      * Constructs an 'eqAll' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryEqAll(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryEqAll", value, searchCriteria);
     }

     /**
      * Constructs an 'exists' subquery from the specified <code>searchCriteria</code>.
      * 
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryExists(CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryExists", null, searchCriteria);
     }

     /**
      * Constructs an 'ge' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGe(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGe", value, searchCriteria);
     }

     /**
      * Constructs an 'geAll' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGeAll(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGeAll", value, searchCriteria);
     }

     /**
      * Constructs an 'geSome' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGeSome(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGeSome", value, searchCriteria);
     }

     /**
      * Constructs an 'gt' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGt(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGt", value, searchCriteria);
     }

     /**
      * Constructs an 'gtAll' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGtAll(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGtAll", value, searchCriteria);
     }

     /**
      * Constructs an 'gtSome' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryGtSome(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryGtSome", value, searchCriteria);
     }

     /**
      * Constructs an 'in' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryIn(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryIn", value, searchCriteria);
     }

     /**
      * Constructs an 'le' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLe(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLe", value, searchCriteria);
     }

     /**
      * Constructs an 'leAll' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLeAll(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLeAll", value, searchCriteria);
     }

     /**
      * Constructs an 'leSome' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLeSome(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLeSome", value, searchCriteria);
     }

     /**
      * Constructs an 'lt' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLt(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLt", value, searchCriteria);
     }

     /**
      * Constructs an 'ltAll' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLtAll(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLtAll", value, searchCriteria);
     }

     /**
      * Constructs an 'ltSome' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryLtSome(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryLtSome", value, searchCriteria);
     }

     /**
      * Constructs an 'ne' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryNe(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryNe", value, searchCriteria);
     }

     /**
      * Constructs an 'notExists' subquery from the specified <code>searchCriteria</code>.
      * 
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryNotExists(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryNotExists", null, searchCriteria);
     }

     /**
      * Constructs an 'notIn' value subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param value
      *                the comparison target of this expression
      * @param searchCriteria
      *                the subquery
      */
     public static CyberAdminExpression subqueryNotIn(Object value, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryExpression("subqueryNotIn", value, searchCriteria);
     }

     /**
      * Constructs an 'eq' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyEq(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyEq", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'eqAll' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyEqAll(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyEqAll", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'ge' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGe(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGe", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'geAll' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGeAll(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGeAll", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'geSome' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGeSome(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGeSome", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'gt' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGt(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGt", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'gtAll' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGtAll(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGtAll", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'gtSome' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyGtSome(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGtSome", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'in' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyIn(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyIn", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'le' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLe(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyLe", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'leAll' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLeAll(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyLeAll", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'leSome' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLeSome(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGeSome", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'lt' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLt(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGt", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'ltAll' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLtAll(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGtAll", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'ltSome' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyLtSome(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGtSome", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'ne' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyNe(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyGtSome", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'notIn' property subquery from the specified <code>value</code> and <code>searchCriteria</code>.
      * 
      * @param propertyName
      *                the property name
      * @param searchCriteria
      *                the subquery
      * @return
      */
     public static CyberAdminExpression subqueryPropertyNotIn(String propertyName, CyberAdminSearchCriteria searchCriteria) {
          return createSubqueryPropertyExpression("subqueryPropertyNotIn", propertyName, searchCriteria);
     }

     /**
      * Constructs an 'sqlRestriction' expression for the specified <code>sql</code>.
      * 
      * @param sql
      *                the sql of the expression
      */
     public static CyberAdminExpression sqlRestriction(String sql) {
          return createExpression("sqlRestriction", null, "String", sql);
     }

     /**
      * Constructs an 'eqProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return an 'eqProperty' expression for the two specified property.
      */
     public static CyberAdminExpression eqProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "eqProperty");
     }

     /**
      * Constructs an 'geProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return an 'geProperty' expression for the two specified property.
      */
     public static CyberAdminExpression geProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "geProperty");
     }

     /**
      * Constructs a 'gtProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return a 'gtProperty' expression for the two specified property.
      */
     public static CyberAdminExpression gtProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "gtProperty");
     }

     /**
      * Constructs an 'leProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return an 'leProperty' expression for the two specified property.
      */
     public static CyberAdminExpression leProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "leProperty");
     }

     /**
      * Constructs an 'ltProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return an 'ltProperty' expression for the two specified property.
      */
     public static CyberAdminExpression ltProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "ltProperty");
     }

     /**
      * Constructs an 'neProperty' expression for the two specified property.
      * 
      * @param propertyName
      *                the property name
      * @param otherPropertyName
      *                the other property name to be compared with
      *                
      * @return an 'neProperty' expression for the two specified property.
      */
     public static CyberAdminExpression neProperty(String propertyName, String otherPropertyName) {
          return new CyberAdminExpression(propertyName, otherPropertyName, "neProperty");
     }

     @SuppressWarnings({ "unchecked", "rawtypes" })
     private static Object getTypedValueFromString(Class valueClass, String valueString) {
          Object result = valueString;
          if (valueClass.isAssignableFrom(Integer.class)) {
                result = NumberUtils.createInteger(valueString);
          } else if (valueClass.isAssignableFrom(Long.class)) {
                result = NumberUtils.createLong(valueString);
          } else if (valueClass.isAssignableFrom(Double.class)) {
                result = NumberUtils.createDouble(valueString);
          } else if (valueClass.isAssignableFrom(Date.class)) {
                try {
                     result = DateUtils.parseDate(valueString, ACCEPTABLE_DATE_FORMATS);
                } catch (ParseException e) {
                     throw new IllegalArgumentException("Illegal string for Date type");
                }
          } else if (valueClass.isAssignableFrom(Boolean.class)) {
                result = Boolean.valueOf(valueString);
          } 
          return result;
     }

     /**
      * <p>
      * The constructor for unary operator.
      * </p>
      * 
      * @param propertyName
      *                Name of the property takes part in the expression.
      * @param op
      *                Operator to be applied on the <code>propertyName</code>.
      */
     public CyberAdminExpression(String propertyName, String op) {
          this.op = op;
          this.propertyName = propertyName;
     }

     /**
      * <p>
      * The constructor for binary operator.
      * </p>
      * 
      * @param propertyName
      * @param value
      * @param op
      */
     public CyberAdminExpression(String propertyName, Object value, String op) {
          this.value = value;
          this.op = op;
          this.propertyName = propertyName;
     }

     /**
      * <p>
      * The constructor.
      * </p>
      * 
      * @param propertyName
      * @param value
      * @param op
      * @param ignoreCase
      */
     public CyberAdminExpression(String propertyName, Object value, String op, boolean ignoreCase) {
          this.propertyName = propertyName;
          this.value = value;
          this.op = op;
          this.ignoreCase = ignoreCase;
     }

     /**
      * Getter method for <code> propertyName </code>.
      * 
      * @return Returns the propertyName.
      */
     public String getPropertyName() {
          return propertyName;
     }

     /**
      * Setter for attribute <code> propertyName </code>.
      * 
      * @param name
      *                The propertyName to set.
      *                
      * @return This instance of expression.
      */
     public CyberAdminExpression setPropertyName(String name) {
          this.propertyName = name;
          return this;
     }

     /**
      * Getter method for <code> value </code>.
      * 
      * @return Returns the value.
      */
     public Object getValue() {
          return value;
     }

     /**
      * Setter for attribute <code> value </code>.
      * 
      * @param val
      *                The value to set.
      *                
      * @return This instance of expression.
      */
     public CyberAdminExpression setValue(Object val) {
          this.value = val;
          return this;
     }

     /**
      * Getter method for <code> op </code>.
      * 
      * @return Returns the op.
      */
     public String getOp() {
          return op;
     }

     /**
      * Setter for attribute <code> op </code>.
      * 
      * @param o
      *                The op to set.
      */
     public CyberAdminExpression setOp(String o) {
          this.op = o;
          return this;
     }

     /**
      * Creates Hibernate {@link Criterion} from this instance.
      * 
      * @return Hibernate {@link Criterion} that usable in a search by Hibernate criteria.
      */
     public Criterion toHibernateCriterion(SessionFactory sessionFactory) {
          Criterion result = null;

          if ("=".equals(op)) {
                SimpleExpression expression = Restrictions.eq(alias + DOT + removeAssociationPath(), value);
                result = expression;

                if (ignoreCase) {
                     result = expression.ignoreCase();
                }
          } else if ("<".equals(op)) {
                result = Restrictions.lt(alias + DOT + removeAssociationPath(), value);
          } else if ("<=".equals(op)) {
                result = Restrictions.le(alias + DOT + removeAssociationPath(), value);
          } else if (">".equals(op)) {
                result = Restrictions.gt(alias + DOT + removeAssociationPath(), value);
          } else if (">=".equals(op)) {
                result = Restrictions.ge(alias + DOT + removeAssociationPath(), value);
          } else if ("<>".equals(op)) {
                result = Restrictions.ne(alias + DOT + removeAssociationPath(), value);
          } else if ("isNull".equals(op)) {
                result = Restrictions.isNull(alias + DOT + removeAssociationPath());
          } else if ("isNotNull".equals(op)) {
                result = Restrictions.isNotNull(alias + DOT + removeAssociationPath());
          } else if ("isEmpty".equals(op)) {
                result = Restrictions.isEmpty(alias + DOT + removeAssociationPath());
          } else if ("isNotEmpty".equals(op)) {
                result = Restrictions.isNotEmpty(alias + DOT + removeAssociationPath());
          } else if ("like".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.ANYWHERE).ignoreCase();
                }
          } else if ("in".equals(op)) {
                if (value instanceof Object[]) {
                     Object[] list = (Object[]) value;
                     if (list.length > 0) {
                          result = Restrictions.in(alias + DOT + removeAssociationPath(), list);
                     }
                }
          } else if ("notIn".equals(op)) {
                if (value instanceof Object[]) {
                     Object[] list = (Object[]) value;
                     if (list.length > 0) {
                          result = Restrictions.not(Restrictions.in(alias + DOT + removeAssociationPath(), list));
                     }
                }
          } else if ("between".equals(op)) {
                Object[] range = (Object[]) value;
                result = Restrictions.between(alias + DOT + removeAssociationPath(), range[0], range[1]);
          } else if ("notBetween".equals(op)) {
                Object[] range = (Object[]) value;
                result = Restrictions.not(Restrictions.between(alias + DOT + removeAssociationPath(), range[0], range[1]));
          } else if ("startWith".equals(op)) {
                if (ignoreCase) {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.START).ignoreCase();
                } else {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.START);
                }
          } else if ("endWith".equals(op)) {
                if (ignoreCase) {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.END).ignoreCase();
                } else {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.END);
                }
          } else if ("contain".equals(op)) {
                if (ignoreCase) {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.ANYWHERE).ignoreCase();
                } else {
                     result = Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.ANYWHERE);
                }
          } else if ("notContain".equals(op)) {
                if (ignoreCase) {
                     result = Restrictions.not(Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.ANYWHERE).ignoreCase());
                } else {
                     result = Restrictions.not(Restrictions.like(alias + DOT + removeAssociationPath(),
                                StringUtils.lowerCase((String) value), MatchMode.ANYWHERE));
                }
          } else if ("sqlRestriction".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.sqlRestriction((String) value);
                } else {
                     throw new UnsupportedOperationException("'SqlRestriction' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("eqProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.eqProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'EqProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("geProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.geProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'GeProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("gtProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.gtProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'GtProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("leProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.leProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'LeProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("ltProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.ltProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'LtProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("neProperty".equals(op)) {
                if (value instanceof String) {
                     result = Restrictions.neProperty(alias + DOT + removeAssociationPath(), otherSideAlias + DOT
                                + removeAssociationPath((String) value));
                } else {
                     throw new UnsupportedOperationException("'NeProperty' operator does not support type "
                                + value.getClass().getCanonicalName());
                }
          } else if ("subqueryEq".equals(op)) {
                result = Subqueries
                          .eq(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryEqAll".equals(op)) {
                result = Subqueries.eqAll(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryExists".equals(op)) {
                result = Subqueries.exists(HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGe".equals(op)) {
                result = Subqueries
                          .ge(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGeAll".equals(op)) {
                result = Subqueries.geAll(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGeSome".equals(op)) {
                result = Subqueries.geSome(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGt".equals(op)) {
                result = Subqueries
                          .gt(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGtAll".equals(op)) {
                result = Subqueries.gtAll(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryGtSome".equals(op)) {
                result = Subqueries.gtSome(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryIn".equals(op)) {
                result = Subqueries
                          .in(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLe".equals(op)) {
                result = Subqueries
                          .le(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLeAll".equals(op)) {
                result = Subqueries.leAll(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLeSome".equals(op)) {
                result = Subqueries.leSome(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLt".equals(op)) {
                result = Subqueries
                          .lt(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLtAll".equals(op)) {
                result = Subqueries.ltAll(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryLtSome".equals(op)) {
                result = Subqueries.ltSome(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryNe".equals(op)) {
                result = Subqueries
                          .ne(value, HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryNotExists".equals(op)) {
                result = Subqueries
                          .notExists(HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryNotIn".equals(op)) {
                result = Subqueries.notIn(value,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyEq".equals(op)) {
                result = Subqueries.propertyEq(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyEqAll".equals(op)) {
                result = Subqueries.propertyEqAll(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGe".equals(op)) {
                result = Subqueries.propertyGe(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGeAll".equals(op)) {
                result = Subqueries.propertyGeAll(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGeSome".equals(op)) {
                result = Subqueries.propertyGeSome(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGt".equals(op)) {
                result = Subqueries.propertyGt(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGtAll".equals(op)) {
                result = Subqueries.propertyGtAll(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyGtSome".equals(op)) {
                result = Subqueries.propertyGtSome(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyIn".equals(op)) {
                result = Subqueries.propertyIn(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLe".equals(op)) {
                result = Subqueries.propertyLe(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLeAll".equals(op)) {
                result = Subqueries.propertyLeAll(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLeSome".equals(op)) {
                result = Subqueries.propertyLeSome(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLt".equals(op)) {
                result = Subqueries.propertyLt(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLtAll".equals(op)) {
                result = Subqueries.propertyLtAll(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyLtSome".equals(op)) {
                result = Subqueries.propertyLtSome(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyNe".equals(op)) {
                result = Subqueries.propertyNe(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else if ("subqueryPropertyNotIn".equals(op)) {
                result = Subqueries.propertyNotIn(propertyName,
                          HibernateCriteriaBuilder.createSearchCriteria(subQueryCriteria, sessionFactory));
          } else {
                throw new UnsupportedOperationException("Operation '" + op + "' is not supported yet at the moment.");
          }

          if (!andExps.isEmpty()) {
                Junction junction = Restrictions.conjunction().add(result);
                for (CyberAdminExpression andExp : andExps) {
                     Criterion criterion = andExp.toHibernateCriterion(sessionFactory);
                     if (criterion != null) {
                          junction.add(criterion);
                     }
                }
                result = junction;
          }

          if (!orExps.isEmpty()) {
                Junction disjunction = Restrictions.disjunction().add(result);
                for (CyberAdminExpression orExp : orExps) {
                     Criterion criterion = orExp.toHibernateCriterion(sessionFactory);
                     if (criterion != null) {
                          disjunction.add(criterion);
                     }
                }
                result = disjunction;
          }

          if (result != null && negative) {
                result = Restrictions.not(result);
          }

          return result;
     }

     private String removeAssociationPath() {
          return removeAssociationPath(propertyName);
     }
     
     private String removeAssociationPath(String input) {
          return StringUtils.substring(input, StringUtils.lastIndexOf(input, DOT) + 1).replace('-', '.');
     }

     /**
      * <p>
      * ORs this with a new expression.
      * </p>
      * 
      * @param orExp
      *                The new expression.
      */
     public CyberAdminExpression or(CyberAdminExpression orExp) {
          orExps.add(orExp);
          return this;
     }

     /**
      * ORs this instance with the input expression.
      * 
      * @param orStringExp
      *                Expression to be ORed to this (in deserialized format).
      * @return This instance after ORed with the input expression.
      */
     public CyberAdminExpression or(String orStringExp) {
          orExps.add(CyberAdminExpression.forExpression(orStringExp));
          return this;
     }

     /**
      * ANDs this instance with the input expression.
      * 
      * @param andExp
      *                Expression to be ANDed to this.
      * @return This instance after ANDed with the input expression.
      */
     public CyberAdminExpression and(CyberAdminExpression andExp) {
          andExps.add(andExp);
          return this;
     }

     /**
      * ANDs this instance with the input expression.
      * 
      * @param andStringExp
      *                Expression to be ANDed to this (in deserialized format).
      * @return This instance after ANDed with the input expression.
      */
     public CyberAdminExpression and(String andStringExp) {
          andExps.add(CyberAdminExpression.forExpression(andStringExp));
          return this;
     }

     /**
      * Getter method for <code> orExps </code>.
      * 
      * @return Returns the orExps.
      */
     public List<CyberAdminExpression> getOrExps() {
          return orExps;
     }

     /**
      * Getter method for <code> andExps </code>.
      * 
      * @return Returns the andExps.
      */
     public List<CyberAdminExpression> getAndExps() {
          return andExps;
     }

     /**
      * Getter method for <code> alias </code>.
      * 
      * @return Returns the alias.
      */
     public String getAlias() {
          return alias;
     }

     /**
      * Setter for attribute <code> alias </code>.
      * 
      * @param a
      *                The alias to set.
      * @return This instance of expression.
      */
     public CyberAdminExpression setAlias(String a) {
          this.alias = a;
          return this;
     }

     /**
      * Getter method for <code> searchCriteria </code>.
      * 
      * @return Returns the searchCriteria.
      */
     public CyberAdminSearchCriteria getSubQueryCriteria() {
          return subQueryCriteria;
     }

     /**
      * Setter method for attribute <code> searchCriteria </code>.
      * 
      * @param searchCriteria
      *                The searchCriteria to set.
      *                
      * @return This instance of expression.
      */
     public CyberAdminExpression setSubQueryCriteria(CyberAdminSearchCriteria searchCriteria) {
          this.subQueryCriteria = searchCriteria;
          return this;
     }

     @Override
     public int hashCode() {
          // CHECKSTYLE:OFF MagicNumber
          return new HashCodeBuilder(17, 37).append(propertyName).append(op).append(value).append(alias)
                     .append(ignoreCase).toHashCode();
          // CHECKSTYLE:ON MagicNumber
     }

     @Override
     public boolean equals(Object obj) {
          if (obj == null) {
                return false;
          }
          if (obj == this) {
                return true;
          }
          if (obj.getClass() != getClass()) {
                return false;
          }
          CyberAdminExpression rhs = (CyberAdminExpression) obj;
          return new EqualsBuilder().append(propertyName, rhs.propertyName).append(op, rhs.op).append(value, rhs.value)
                     .append(alias, rhs.alias).append(ignoreCase, rhs.ignoreCase).isEquals();
     }

     /**
      * Returns all sub-expressions of this.
      * 
      * @return All sub-expressions (from ORs and ANDs) of this expression.
      */
     public List<CyberAdminExpression> allChildren() {
          List<CyberAdminExpression> result = new ArrayList<CyberAdminExpression>();
          result.addAll(andExps);
          result.addAll(orExps);
          for (CyberAdminExpression andExp : andExps) {
                result.addAll(andExp.allChildren());
          }
          for (CyberAdminExpression orExp : orExps) {

                result.addAll(orExp.allChildren());
          }
          return result;
     }

     /**
      * Ignoring case in String comparison.
      * 
      * @return This instance of the expression.
      */
     public CyberAdminExpression ignoreCase() {
          ignoreCase = true;
          return this;
     }

     /**
      * Negating this expression.
      * 
      * @return This instance of the expression.
      */
     public CyberAdminExpression negate() {
          negative = true;
          return this;
     }
     
     /**
      * Checks whether the property is on association or not.
      * 
      * @return <code>true</code> if the property is on association, <code>false</code> otherwise.
      */
     public boolean onAssociation() {
          return StringUtils.contains(propertyName, DOT);
     }
     
     /**
      * Checks whether the other property (used in case of {@link #PROPERTY_COMPARISON_OPERATORS}) is on association or
      * not.
      * 
      * @return <code>true</code> if the other property (used in case of {@link #PROPERTY_COMPARISON_OPERATORS}) is on
      *            association, <code>false</code> otherwise.
      */
     public boolean otherSideOnAssociation() {
          return isPropertyComparison() && StringUtils.contains(String.class.cast(value), DOT);
     }

     public String getOtherSideAlias() {
          return otherSideAlias;
     }

     /**
      * Sets the other side alias in case comparison between 2 properties is used. It is important to note that if this
      * is left as NULL by default, the API will lately try to interpolate an alias for the other side property.
      * Thus, specify this once some specific alias must be used (i.e: in subquery).
      * 
      * @param otherAlias
      *                The alias to set.
      * @return This instance of expression.
      */
     public CyberAdminExpression setOtherSideAlias(String otherAlias) {
          this.otherSideAlias = otherAlias;
          return this;
     }

     /**
      * Checks whether this expression is a comparison between properties.
      * 
      * @return <code>true</code> if this expression is a comparison between properties, <code>false</code> otherwise.
      */
     public boolean isPropertyComparison() {
          return PROPERTY_COMPARISON_OPERATORS.contains(op);
     }
}
