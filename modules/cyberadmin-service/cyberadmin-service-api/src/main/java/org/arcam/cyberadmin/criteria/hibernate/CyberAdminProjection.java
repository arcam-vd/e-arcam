/*
 * IrisProjection.java
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.type.Type;

/**
 * 
 * Description about a projection used in querying a list of Hibernate entities.
 * 
 * @author phd
 * @see CyberAdminSearchCriteria
 * 
 */
@SuppressWarnings("serial")
public class CyberAdminProjection implements Serializable {

    /**
     * Enum represents a projection type.
     * 
     * @author ttu
     * 
     */
    public enum ProjectionType {
        /**
         * Distinct projection type.
         */
        DISTINCT,
        /**
         * Property projection type.
         */
        PROPERTY,
        /**
         * Group property projection type.
         */
        GROUP_PROPERTY,
        /**
         * Avg projection type.
         */
        AVG,
        /**
         * Count projection type.
         */
        COUNT,
        /**
         * Count distinct projection type.
         */
        COUNT_DISTINCT,
        /**
         * Min projection type.
         */
        MIN,
        /**
         * Max projection type.
         */
        MAX,
        /**
         * Sum projection type.
         */
        SUM,
        /**
         * Row count projection type.
         */
        ROW_COUNT,
        /**
         * SQL projection type.
         */
        SQL_PROJECTION,
        /**
         * SQL group projection type.
         */
        SQL_GROUP_PROJECTION
    }

    /** Type of the projection, default to PROPERTY **/
    private ProjectionType projectionType = ProjectionType.PROPERTY;

    /** Name of the property and alias used in PROPERTY projection **/
    private String propertyName;
    private String alias = null;

    /** Expression, aliases and types used in SQL projection **/
    private String sql;
    private String[] columnAliases = new String[] {};
    private Type[] types;

    /** Expression used in GROUPBY projection **/
    private String groupBy;

    /**
     * Constructs a projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     */
    public CyberAdminProjection(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Constructs a projection on the specified {@link ProjectionType} (property is trivial).
     * 
     * @param projectionType
     *            Property name from which the projection will be constructed.
     */
    public CyberAdminProjection(ProjectionType projectionType) {
        this.projectionType = projectionType;
    }

    /**
     * Constructs a projection on the specified {@link ProjectionType} on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * @param projectionType
     *            Property name from which the projection will be constructed.
     */
    public CyberAdminProjection(String propertyName, ProjectionType projectionType) {
        this.propertyName = propertyName;
        this.projectionType = projectionType;
    }

    /**
     * Constructs a SQL projection. The projection type can be SQL_PROJECTION or SQL_GROUP_PROJECTION.
     * 
     * @param sql
     *            a select SQL clause
     * @param groupBy
     *            a group by clause, should be null when projection type is SQL_PROJECTION
     * @param columnAliases
     *            the aliases for all columns of this projection
     * @param types
     *            the list of type for this sql projection
     * 
     */
    public CyberAdminProjection(String sql, String groupBy, String[] columnAliases, Type[] types,
            ProjectionType projectionType) {
        this.sql = sql;
        this.groupBy = groupBy;
        this.columnAliases = columnAliases;
        this.types = types;
        this.projectionType = projectionType;
    }

    /**
     * Set alias to this object and return it.
     * 
     * @param anAlias
     *            the alias to set
     */
    public CyberAdminProjection as(String anAlias) {
        this.setAlias(anAlias);
        return this;
    }

    /**
     * Set alias to an {@link CyberAdminProjection}.
     * 
     * @param projection
     *            the projection to be set
     * @param alias
     *            the alias to set
     */
    public static CyberAdminProjection alias(CyberAdminProjection projection, String alias) {
        return projection.as(alias);
    }

    /**
     * Constructs a distinct projection at SQL level.
     * 
     * @return A distinct projection.
     */
    public static CyberAdminProjection distinct() {
        return new CyberAdminProjection(ProjectionType.DISTINCT);
    }

    /**
     * Constructs a rowCount projection.
     * 
     * @return A rowCount projection.
     */
    public static CyberAdminProjection rowCount() {
        return new CyberAdminProjection(ProjectionType.ROW_COUNT);
    }

    /**
     * Constructs a property projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * @return A property projection on the specified property.
     */
    public static CyberAdminProjection property(String propertyName) {
        return new CyberAdminProjection(propertyName);
    }

    /**
     * Constructs a group property projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * @return A group property projection on the specified property.
     */
    public static CyberAdminProjection groupProperty(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.GROUP_PROPERTY);
    }

    /**
     * Constructs an avg projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return An average projection on the specified property.
     */
    public static CyberAdminProjection avg(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.AVG);
    }

    /**
     * Constructs a count projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return A count projection on the specified property.
     */
    public static CyberAdminProjection count(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.COUNT);
    }

    /**
     * Constructs a countDistinct projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return A count distinct projection on the specified property.
     */
    public static CyberAdminProjection countDistinct(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.COUNT_DISTINCT);
    }

    /**
     * Constructs a min projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return A min projection on the specified property.
     */
    public static CyberAdminProjection min(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.MIN);
    }

    /**
     * Constructs a max projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return A max projection on the specified property.
     */
    public static CyberAdminProjection max(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.MAX);
    }

    /**
     * Constructs a sum projection on the specified property.
     * 
     * @param propertyName
     *            Property name from which the projection will be constructed.
     * 
     * @return A sum projection on the specified property.
     */
    public static CyberAdminProjection sum(String propertyName) {
        return new CyberAdminProjection(propertyName, ProjectionType.SUM);
    }

    /**
     * Constructs a sql projection with the given sql clause, column aliases and types.
     * 
     * @param sql
     *            a select SQL clause
     * @param columnAliases
     *            the aliases for all columns of this projection
     * @param types
     *            the list of type for this sql projection
     * 
     * @return A sql projection from the specified sql expression.
     * 
     */
    public static CyberAdminProjection sqlProjection(String sql, String[] columnAliases, Type[] types) {
        return new CyberAdminProjection(sql, null, columnAliases, types, ProjectionType.SQL_PROJECTION);
    }

    /**
     * Constructs a sql group projection with the given sql clause, groupBy clause, column aliases and types.
     * 
     * @param sql
     *            a select SQL clause
     * @param groupBy
     *            a group by clause
     * @param columnAliases
     *            the aliases for all columns of this projection
     * @param types
     *            the list of type for this sql projection
     * 
     */
    //CHECKSTYLE:OFF LineLength
    public static CyberAdminProjection sqlGroupProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
        return new CyberAdminProjection(sql, groupBy, columnAliases, types, ProjectionType.SQL_GROUP_PROJECTION);
    }
    //CHECKSTYLE:ON
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public ProjectionType getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(ProjectionType projectionType) {
        this.projectionType = projectionType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF MagicNumber
        return new HashCodeBuilder(17, 37).append(propertyName).append(projectionType).toHashCode();
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
        CyberAdminProjection rhs = (CyberAdminProjection) obj;
        return new EqualsBuilder().append(propertyName, rhs.propertyName).append(projectionType, rhs.projectionType)
                .isEquals();
    }

    /**
     * Creates Hibernate {@link Projection} for the specified property with the specified alias.
     * 
     * @param processedPropertyName
     *            The property bound to this projection, but was processed by {@link HibernateCriteriaBuilder}.
     *            
     * @return Hibernate {@link Projection} for the specified property with the specified alias.
     * 
     */
    public Projection toHibernateProjection(String processedPropertyName) {
        Projection result = null;
        String projectionAlias;
        if (alias != null) {
            projectionAlias = alias;
        } else {
            projectionAlias = StringUtils.replace(propertyName, "-", ".");
        }
        if (projectionType == ProjectionType.PROPERTY) {
            result = Projections.property(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.GROUP_PROPERTY) {
            result = Projections.groupProperty(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.AVG) {
            result = Projections.avg(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.COUNT) {
            result = Projections.count(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.COUNT_DISTINCT) {
            result = Projections.countDistinct(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.MIN) {
            result = Projections.min(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.MAX) {
            result = Projections.max(processedPropertyName).as(projectionAlias);
        } else if (projectionType == ProjectionType.SUM) {
            result = Projections.sum(processedPropertyName).as(projectionAlias);
        } else {
            throw new UnsupportedOperationException("Projection type not supported at the moment");
        }

        return result;
    }

    /**
     * Creates Hibernate {@link Projection} from this instance.
     * 
     * @return Hibernate {@link Projection} corresponds to this instance.
     */
    public Projection toHibernateSQLProjection() {
        Projection result = null;
        if (projectionType == ProjectionType.SQL_PROJECTION) {
            result = Projections.sqlProjection(sql, columnAliases, types);
        } else if (projectionType == ProjectionType.SQL_GROUP_PROJECTION) {
            result = Projections.sqlGroupProjection(sql, groupBy, columnAliases, types);
        } else {
            throw new UnsupportedOperationException("Projection type not supported at the moment");
        }
        return result;
    }
    
    /**
     * Checks whether the property is on association or not.
     * 
     * @return <code>true</code> if the property is on association, <code>false</code> otherwise.
     */
    public boolean onAssociation() {
        return StringUtils.contains(propertyName, ".");
    }
}
