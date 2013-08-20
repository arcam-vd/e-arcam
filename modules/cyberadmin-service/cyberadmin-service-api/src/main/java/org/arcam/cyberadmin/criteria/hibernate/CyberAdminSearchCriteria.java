/*
 * CyberAdminSearchCriteria.java
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.transform.ResultTransformer;

/**
 * 
 * Criteria to be used for searching a list of Hibernate entities. It consists of:
 *  - {@link #clazz}:       Class of the entity.
 *  - {@link #expressions}: Criteria for the search, many operators like '=', '>', '<', 'like', ... are supported.
 *  - {@link #orders}:      Orders, can be multiple.
 *  - {@link #projections}: Projections to be used on the search, can be freely extended to any depth level.
 * 
 * @author phd
 * @see CyberAdminExpression
 * @see CyberAdminOrder
 * @see CyberAdminProjection
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class CyberAdminSearchCriteria implements Serializable {

    /** Target class, expressions, orders and projections **/
    private Class clazz;
    private List<CyberAdminExpression> expressions = new ArrayList<CyberAdminExpression>();
    private List<CyberAdminOrder> orders = new ArrayList<CyberAdminOrder>();
    private List<CyberAdminProjection> projections = new ArrayList<CyberAdminProjection>();

    /** Pagination criteria **/
    private int maxResults = -1;
    private int firstResult = -1;

    /** Result transformers **/
    private boolean distinctRoot = false;
    private ResultTransformer resultTransformer = null;
    private Class aliasToBeanResultTransformerClass = null;

    /** Alias of the root criteria **/
    private String alias = CriteriaSpecification.ROOT_ALIAS;

    /**
     * Constructs an instance by type of expected return entity.
     * 
     * @param clazz
     *            Type of the expected return entity.
     */
    public CyberAdminSearchCriteria(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Constructs an instance by type of expected return entity.
     * 
     * @param clazz
     *            Type of the expected return entity.
     * @param alias
     *            The alias for this entity.
     */
    public CyberAdminSearchCriteria(Class clazz, String alias) {
        this.clazz = clazz;
        this.alias = alias;
    }

    /**
     * Constructs an instance from input attributes.
     * 
     * @param clazz
     *            Type of return entity.
     * @param expressions
     *            List of criteria.
     * @param orders
     *            List of orders.
     * @param projections
     *            List of projections.
     */
    public CyberAdminSearchCriteria(Class clazz, List<CyberAdminExpression> expressions, List<CyberAdminOrder> orders,
            List<CyberAdminProjection> projections) {
        this.clazz = clazz;
        this.expressions = expressions;
        this.orders = orders;
        this.projections = projections;
    }

    /**
     * Constructs an instance from input attributes.
     * 
     * @param clazz
     *            Type of return entity.
     * @param alias
     *            The alias for this entity.
     * @param expressions
     *            List of criteria.
     * @param orders
     *            List of orders.
     * @param projections
     *            List of projections.
     */
    public CyberAdminSearchCriteria(Class clazz, String alias, List<CyberAdminExpression> expressions,
            List<CyberAdminOrder> orders, List<CyberAdminProjection> projections) {
        this.clazz = clazz;
        this.alias = alias;
        this.expressions = expressions;
        this.orders = orders;
        this.projections = projections;
    }

    /**
     * Constructs an instance from expected entity type.
     * 
     * @param clazz
     *            Type of the return entity.
     * @return An instance of {@link CyberAdminSearchCriteria} from <code>clazz</code>.
     */
    public static CyberAdminSearchCriteria forClass(Class clazz) {
        return new CyberAdminSearchCriteria(clazz);
    }

    /**
     * Constructs an instance from expected entity type with the specified alias.
     * 
     * @param clazz
     *            Type of the return entity.
     * @return An instance of {@link CyberAdminSearchCriteria} from <code>clazz</code>.
     */
    public static CyberAdminSearchCriteria forClass(Class clazz, String alias) {
        return new CyberAdminSearchCriteria(clazz, alias);
    }

    /**
     * Adds input <code>expression</code> into this instance.
     * 
     * @param expression
     *            Expression to be added.
     * @return This instance with the added <code>expression</code>.
     */
    public CyberAdminSearchCriteria with(CyberAdminExpression expression) {
        expressions.add(expression);
        return this;
    }

    /**
     * Adds input <code>expressionString</code> into this instance.
     * 
     * @param expressionString
     *            Expression to be added.
     * @return This instance with the added <code>expressionString</code>.
     */
    public CyberAdminSearchCriteria with(String expressionString) {
        expressions.add(CyberAdminExpression.forExpression(expressionString));
        return this;
    }

    /**
     * Adds input <code>expressions</code> into this instance.
     * 
     * @param newExpressions
     *            Expressions to be added.
     * @return This instance with the added <code>expressions</code>.
     */
    public CyberAdminSearchCriteria with(List<CyberAdminExpression> newExpressions) {
        this.expressions.addAll(newExpressions);
        return this;
    }

    /**
     * Adds disctinct project to this instance. NOTE: This will result in a DISTINCT at the SQL level.
     * 
     * @return This instance with the added distinct projection.
     */
    public CyberAdminSearchCriteria distinct() {
        project(CyberAdminProjection.distinct());
        return this;
    }

    /**
     * Activates the distinct root at result transformer level.
     * 
     * @return This instance of criteria.
     */
    public CyberAdminSearchCriteria distinctRoot() {
        this.distinctRoot = true;
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyName</code> to this instance.
     * 
     * @param propertyName
     *            Ascending property to be added.
     * @return This instance with added ascending <code>propertyName</code>.
     */
    public CyberAdminSearchCriteria asc(String propertyName) {
        orders.add(CyberAdminOrder.asc(propertyName));
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyName</code> to this instance.
     * 
     * @param ignoreCase
     *            Ignore case when sorting.
     * @param propertyName
     *            Ascending property to be added.
     * @return This instance with added ascending <code>propertyName</code>.
     */
    public CyberAdminSearchCriteria asc(boolean ignoreCase, String propertyName) {
        orders.add(CyberAdminOrder.asc(ignoreCase, propertyName));
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyNames</code> to this instance.
     * 
     * @param propertyNames
     *            List of ascending properties.
     * @return This instance with added ascending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria asc(String[] propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.asc(propertyName));
        }
        return this;
    }
    
    /**
     * Adds the input ascending on <code>propertyNames</code> to this instance.
     * 
     * @param propertyNames
     *            List of ascending properties.
     * @return This instance with added ascending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria asc(List<String> propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.asc(propertyName));
        }
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyNames</code> to this instance.
     * 
     * @param ignoreCase
     *            Ignore case when sorting.
     * @param propertyNames
     *            List of ascending properties.
     * @return This instance with added ascending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria asc(boolean ignoreCase, String[] propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.asc(ignoreCase, propertyName));
        }
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyName</code> to this instance.
     * 
     * @param propertyName
     *            Ascending property to be added.
     * @return This instance with added ascending <code>propertyName</code>.
     */
    public CyberAdminSearchCriteria desc(String propertyName) {
        orders.add(CyberAdminOrder.desc(propertyName));
        return this;
    }

    /**
     * Adds the input ascending on <code>propertyName</code> to this instance.
     * 
     * @param ignoreCase
     *            Ignore case when sorting.
     * @param propertyName
     *            Ascending property to be added.
     * @return This instance with added ascending <code>propertyName</code>.
     */
    public CyberAdminSearchCriteria desc(boolean ignoreCase, String propertyName) {
        orders.add(CyberAdminOrder.desc(ignoreCase, propertyName));
        return this;
    }

    /**
     * Adds the input descending on <code>propertyNames</code> to this instance.
     * 
     * @param propertyNames
     *            List of descending properties.
     * @return This instance with added descending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria desc(String[] propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.desc(propertyName));
        }
        return this;
    }
    
    /**
     * Adds the input descending on <code>propertyNames</code> to this instance.
     * 
     * @param propertyNames
     *            List of descending properties.
     * @return This instance with added descending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria desc(List<String> propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.desc(propertyName));
        }
        return this;
    }

    /**
     * Adds the input descending on <code>propertyNames</code> to this instance.
     * 
     * @param ignoreCase
     *            Ignore case when sorting.
     * @param propertyNames
     *            List of descending properties.
     * @return This instance with added descending <code>propertyNames</code>.
     */
    public CyberAdminSearchCriteria desc(boolean ignoreCase, String[] propertyNames) {
        for (String propertyName : propertyNames) {
            orders.add(CyberAdminOrder.desc(ignoreCase, propertyName));
        }
        return this;
    }

    /**
     * Adds input projection into this instance.
     * 
     * @param projection
     *            Projection to be added.
     * @return This instance with added <code>projection</code>.
     */
    public CyberAdminSearchCriteria project(CyberAdminProjection projection) {
        projections.add(projection);
        return this;
    }

    /**
     * Adds a projection on the input property to this instance.
     * 
     * @param propertyName
     *            Property name of the projection to be added.
     * @return This instance with added projection.
     */
    public CyberAdminSearchCriteria project(String propertyName) {
        projections.add(CyberAdminProjection.property(propertyName));
        return this;
    }

    /**
     * Adds a list of the input properties to this instance.
     * 
     * @param propertyNames
     *            An array of property names of the projection to be added.
     * @return This instance with added projection.
     */
    public CyberAdminSearchCriteria project(String[] propertyNames) {
        for (String propertyName : propertyNames) {
            projections.add(CyberAdminProjection.property(propertyName));
        }
        return this;
    }

    /**
     * Adds a projection on the input property to this instance.
     * 
     * @param propertyName
     *            Property name of the projection to be added.
     * @param anAlias
     *            The alias for the property name.
     * @return This instance with added projection.
     */
    public CyberAdminSearchCriteria project(String propertyName, String anAlias) {
        projections.add(CyberAdminProjection.alias(CyberAdminProjection.property(propertyName), anAlias));
        return this;
    }

    /**
     * Adds the list of input <code>projections</code> to this instance.
     * 
     * @param newProjections
     *            Projection list to be added.
     * @return This instance with added <code>projections</code>.
     */
    public CyberAdminSearchCriteria project(List<CyberAdminProjection> newProjections) {
        this.projections.addAll(newProjections);
        return this;
    }

    /**
     * Applies max result limitation on this criteria.
     * 
     * @param max
     *            The expected max result.
     * @return This instance with max result limitation applied.
     */
    public CyberAdminSearchCriteria max(int max) {
        maxResults = max;
        return this;
    }

    /**
     * Applies first result limitation on this criteria.
     * 
     * @param first
     *            The expected first result.
     * @return This instance with first result limitation applied.
     */
    public CyberAdminSearchCriteria first(int first) {
        firstResult = first;
        return this;
    }

    /**
     * Set alias to the entity of this criteria.
     * 
     * @param anAlias
     *            The alias to be set.
     * @return This instance with alias set.
     */
    public CyberAdminSearchCriteria as(String anAlias) {
        this.alias = anAlias;
        return this;
    }

    public List<CyberAdminExpression> getExpressions() {
        return expressions;
    }

    public List<CyberAdminOrder> getOrders() {
        return orders;
    }

    public List<CyberAdminProjection> getProjections() {
        return projections;
    }

    /**
     * Converts this instance to a Hibernate {@link DetachedCriteria}.
     * 
     * @return Hibernate {@link DetachedCriteria} corresponds to this instance.
     */
    public DetachedCriteria createDetachedCriteria() {
        return DetachedCriteria.forClass(clazz, alias);
    }

    public Class getClazz() {
        return clazz;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public ResultTransformer getResultTransformer() {
        return resultTransformer;
    }

    public Class getAliasToBeanResultTransformerClass() {
        return aliasToBeanResultTransformerClass;
    }

    public String getAlias() {
        return alias;
    }

    /**
     * Sets the {@link ResultTransformer} to be used to transform the database records to Java objects.
     * 
     * @param theResultTransformer
     *            The {@link ResultTransformer} to be used.
     * @return This instance of the criteria.
     */
    public CyberAdminSearchCriteria setResultTransformer(ResultTransformer theResultTransformer) {
        this.resultTransformer = theResultTransformer;
        return this;
    }

    /**
     * Sets the class to be used with the {@link AliasToBeanResultTransformer} to transform the result. NOTE: This
     * should be used instead of the {@link #setResultTransformer(ResultTransformer)} in case of remoting call to avoid
     * the un-serializable of the {@link AliasToBeanResultTransformer}.
     * 
     * @param aClazz
     *            The class to be used with the {@link AliasToBeanResultTransformer} to transform the result.
     * 
     */
    public CyberAdminSearchCriteria setAliasToBeanResultTransformerClass(Class aClazz) {
        this.aliasToBeanResultTransformerClass = aClazz;
        return this;
    }

    /**
     * Is the search paginated?
     * 
     * @return <code>true</code> if the search must be paginated, <code>false</code> otherwise.
     */
    public boolean needPaginated() {
        return maxResults != -1 || firstResult != -1;
    }

    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF MagicNumber
        return new HashCodeBuilder(17, 37).append(expressions).append(projections).append(orders).toHashCode();
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
        CyberAdminSearchCriteria rhs = (CyberAdminSearchCriteria) obj;
        return new EqualsBuilder().appendSuper(CollectionUtils.isEqualCollection(expressions, rhs.expressions))
                .appendSuper(CollectionUtils.isEqualCollection(projections, rhs.projections))
                .appendSuper(CollectionUtils.isEqualCollection(orders, rhs.orders)).isEquals();
    }

    /**
     * Flaternizes all expresions under this criteria.
     * 
     * @return All expression under this criteria, flaterned.
     */
    public List<CyberAdminExpression> getFlaternedExpressions() {
        List<CyberAdminExpression> result = new ArrayList<CyberAdminExpression>();
        for (CyberAdminExpression exp : expressions) {
            result.add(exp);
            result.addAll(exp.allChildren());
        }
        return result;
    }

    /**
     * Should a distinct root be applied at result transforming level?
     * 
     * @return <code>true</code> if a distinct root should be applied at result transforming level, <code>false</code>
     *         otherwise.
     */
    public boolean isDistinctRoot() {
        return distinctRoot;
    }
}
