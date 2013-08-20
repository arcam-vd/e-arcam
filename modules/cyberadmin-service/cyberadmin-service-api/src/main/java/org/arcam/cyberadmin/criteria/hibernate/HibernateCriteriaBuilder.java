/*
 * HibernateCriteriaBuilder.java
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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.hibernate.CyberAdminProjection.ProjectionType;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.sql.JoinFragment;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

/**
 * 
 * Helper for transforming an input {@link CyberAdminSearchCriteria} into Hibernate {@link DetachedCriteria}.<br/>
 * This supports to build a {@link DetachedCriteria} that allows applying:<br/>
 * - criterions on any association's property of the root entity - orders on any association's property of the root
 * entity<br/>
 * - projects on any association's property of the root entity
 * 
 * @author phd
 * @see CyberAdminSearchCriteria
 * 
 */
public final class HibernateCriteriaBuilder {

    /**
     * Separator for association path.
     */
    protected static final String ASSOCIATION_SEPARATOR = ".";

    /**
     * Special to denote a star projection.
     */
    private static final String STAR_PROJECTION = "*";

    /**
     * Comma separator for instance, sorting on a list of property.
     */
    private static final String COMMA_SEPARATOR = ",";

    private HibernateCriteriaBuilder() {
        // None instantiatable.
    }

    /**
     * Builds Hibernate criteria for getting object from an instance of {@link CyberAdminSearchCriteria}.
     * 
     * @param irisCriteria
     *            An instance of {@link CyberAdminSearchCriteria}.
     * @return Hibernate criteria built from the input <code>oxygenCriteria</code>.
     */
   //CHECKSTYLE:OFF LineLength
    public static DetachedCriteria createSearchCriteria(CyberAdminSearchCriteria irisCriteria, SessionFactory sessionFactory) {

        DetachedCriteria criteria = irisCriteria.createDetachedCriteria();

        List<CyberAdminExpression> allExpressions = irisCriteria.getFlaternedExpressions();
        for (CyberAdminExpression irisExpression : allExpressions) {
            if (!irisExpression.onAssociation()) {
                irisExpression.setAlias(irisCriteria.getAlias());
            }
            if (irisExpression.isPropertyComparison() && !irisExpression.otherSideOnAssociation()
                    && irisExpression.getOtherSideAlias() == null) {
                irisExpression.setOtherSideAlias(irisCriteria.getAlias());
            }
        }

        List<String> ascs = new ArrayList<String>();
        List<String> descs = new ArrayList<String>();
        for (CyberAdminOrder order : irisCriteria.getOrders()) {
            if (order.isAscending()) {
                if (order.onAssociation()) {
                    ascs.add(order.getPropertyName());
                } else {
                    criteria.addOrder(Order.asc(normalize(order.getPropertyName())));
                }
            } else {
                if (order.onAssociation()) {
                    descs.add(order.getPropertyName());
                } else {
                    criteria.addOrder(Order.desc(normalize(order.getPropertyName())));
                }
            }
        }

        Map<String, List<String>> ascPaths = pathToOrders(ascs);
        Map<String, List<String>> descPaths = pathToOrders(descs);
        Map<String, List<CyberAdminExpression>> sePaths = pathToExpressions(allExpressions, false);
        Map<String, List<CyberAdminExpression>> otherSePaths = pathToExpressions(allExpressions, true);

        List<String> paths = new ArrayList<String>();
        for (Entry<String, List<String>> ascPath : ascPaths.entrySet()) {
            if (!paths.contains(ascPath.getKey())) {
                paths.add(ascPath.getKey());
            }
        }
        for (Entry<String, List<String>> descPath : descPaths.entrySet()) {
            if (!paths.contains(descPath.getKey())) {
                paths.add(descPath.getKey());
            }
        }
        for (Entry<String, List<CyberAdminExpression>> sePath : sePaths.entrySet()) {
            if (!paths.contains(sePath.getKey())) {
                paths.add(sePath.getKey());
            }
        }
        for (Entry<String, List<CyberAdminExpression>> otherSePath : otherSePaths.entrySet()) {
            if (!paths.contains(otherSePath.getKey())) {
                paths.add(otherSePath.getKey());
            }
        }

        for (CyberAdminProjection demandedPrj : irisCriteria.getProjections()) {
            if (demandedPrj.getProjectionType() == ProjectionType.DISTINCT
                    || demandedPrj.getProjectionType() == ProjectionType.ROW_COUNT
                    || demandedPrj.getProjectionType() == ProjectionType.SQL_PROJECTION
                    || demandedPrj.getProjectionType() == ProjectionType.SQL_GROUP_PROJECTION) {
                continue;
            }

            String prj = demandedPrj.getPropertyName();
            if (prj.indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) != -1) {
                String path = prj.substring(0,
                        StringUtils.lastIndexOf(prj, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR));
                if (!paths.contains(path)) {
                    paths.add(path);
                }
            }
        }

        Collections.sort(paths);
        String subPath = null;
        String prePath = null;
        DetachedCriteria subCriteria = null;
        DetachedCriteria existedSubCriteria = null;
        Map<String, DetachedCriteria> pathToCriteria = new HashMap<String, DetachedCriteria>();
        int randomNr = 0;
        for (String path : paths) {
            String[] associations = null;

            int currentIndex = paths.indexOf(path);
            if (currentIndex > 0
                    && path.startsWith(paths.get(currentIndex - 1) + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR)) {
                associations = StringUtils.split(path.substring(paths.get(currentIndex - 1).length()),
                        HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                prePath = paths.get(currentIndex - 1) + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR;
            } else {
                associations = StringUtils.split(path, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                subCriteria = criteria;
                prePath = "";
            }

            for (int i = 0; i < associations.length; i++) {
                subPath = prePath
                        + StringUtils.join(Arrays.asList(associations).subList(0, i + 1),
                                HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                existedSubCriteria = pathToCriteria.get(subPath);
                if (existedSubCriteria != null) {
                    subCriteria = existedSubCriteria;
                } else {
                    subCriteria = subCriteria.createCriteria(associations[i], associations[i] + randomNr++,
                            JoinFragment.LEFT_OUTER_JOIN);
                    pathToCriteria.put(subPath, subCriteria);
                }
            }

            String lastAlias = associations[associations.length - 1] + (randomNr - 1);
            if (ascPaths.get(path) != null) {
                for (String orderArg : ascPaths.get(path)) {
                    if (orderArg.contains(COMMA_SEPARATOR)) {
                        String[] orderArgs = StringUtils.split(orderArg, COMMA_SEPARATOR);
                        for (String arg : orderArgs) {
                            criteria.addOrder(Order.asc(lastAlias + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR
                                    + normalize(arg)));
                        }
                    } else {
                        criteria.addOrder(Order.asc(lastAlias + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR
                                + normalize(orderArg)));
                    }
                }
            }
            if (descPaths.get(path) != null) {
                for (String orderArg : descPaths.get(path)) {
                    if (orderArg.contains(COMMA_SEPARATOR)) {
                        String[] orderArgs = StringUtils.split(orderArg, COMMA_SEPARATOR);
                        for (String arg : orderArgs) {
                            criteria.addOrder(Order.desc(lastAlias + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR
                                    + normalize(arg)));
                        }
                    } else {
                        criteria.addOrder(Order.desc(lastAlias + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR
                                + normalize(orderArg)));
                    }
                }
            }
            if (sePaths.get(path) != null) {
                for (CyberAdminExpression restriction : sePaths.get(path)) {
                    if (restriction.getAlias() == null) {
                        restriction.setAlias(lastAlias);
                    }
                }
            }
            if (otherSePaths.get(path) != null) {
                for (CyberAdminExpression restriction : otherSePaths.get(path)) {
                    if (restriction.getOtherSideAlias() == null) {
                        restriction.setOtherSideAlias(lastAlias);
                    }
                }
            }
        }

        for (CyberAdminExpression exp : irisCriteria.getExpressions()) {
            Criterion criterion = exp.toHibernateCriterion(sessionFactory);
            if (criterion != null) {
                criteria.add(criterion);
            }
        }

        buildProjections(irisCriteria.getClazz(), irisCriteria.getProjections(), criteria, pathToCriteria, randomNr,
                irisCriteria.isDistinctRoot(), irisCriteria.getResultTransformer(),
                irisCriteria.getAliasToBeanResultTransformerClass(), sessionFactory);

        return criteria;
    }
    
    //CHECKSTYLE:ON
    //CHECKSTYLE:OFF ParameterNumber
    @SuppressWarnings("rawtypes")
    private static void buildProjections(Class clazz, List<CyberAdminProjection> projections, DetachedCriteria criteria,
            Map<String, DetachedCriteria> pathToCriteria, int randomNr, boolean distinctRoot,
            ResultTransformer resultTransformer, Class aliasToBeanClass, SessionFactory sessionFactory) {

        if (!projections.isEmpty()) {
            ProjectionList prjs = Projections.projectionList();
            Map<String, String> pathToAlias = new HashMap<String, String>();
            boolean distinct = false;
            for (CyberAdminProjection demandedPrj : projections) {

                if (demandedPrj.getProjectionType() == ProjectionType.DISTINCT) {
                    distinct = true;
                    continue;
                }

                if (demandedPrj.getProjectionType() == ProjectionType.ROW_COUNT) {
                    prjs.add(Projections.rowCount());
                    continue;
                }

                if (demandedPrj.getProjectionType() == ProjectionType.SQL_PROJECTION
                        || demandedPrj.getProjectionType() == ProjectionType.SQL_GROUP_PROJECTION) {
                    prjs.add(demandedPrj.toHibernateSQLProjection());
                    continue;
                }

                String prj = demandedPrj.getPropertyName();

                if (prj.indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) == -1) {
                    if (prj.indexOf(STAR_PROJECTION) != -1) {
                        List<String> allProps = extractRootProperties(sessionFactory, clazz);
                        for (String prop : allProps) {
                            prjs.add(Projections.property(prop).as(prop));
                        }
                    } else {
                        prjs.add(demandedPrj.toHibernateProjection(normalize(prj)));
                    }
                    continue;
                }

                String demandedPath = prj.substring(0,
                        StringUtils.lastIndexOf(prj, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR));
                String demandedProp = prj.substring(StringUtils.lastIndexOf(prj,
                        HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) + 1);

                if (pathToCriteria.keySet().contains(demandedPath)) {
                    if (STAR_PROJECTION.equals(demandedProp)) {
                        List<String> allProps = extractRootProperties(sessionFactory,
                                getNestedType(clazz, demandedPath));
                        for (String prop : allProps) {
                            prjs.add(Projections.property(
                                    pathToCriteria.get(demandedPath).getAlias()
                                            + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + prop).as(
                                    demandedPath + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + prop));
                        }
                    } else {
                        prjs.add(demandedPrj.toHibernateProjection(pathToCriteria.get(demandedPath).getAlias()
                                + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + normalize(demandedProp)));
                    }
                } else if (pathToAlias.containsKey(demandedPath)) {
                    if (STAR_PROJECTION.equals(demandedProp)) {
                        List<String> allProps = extractRootProperties(sessionFactory,
                                getNestedType(clazz, demandedPath));
                        for (String prop : allProps) {
                            prjs.add(Projections.property(
                                    pathToAlias.get(demandedPath) + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR
                                            + prop).as(
                                    demandedPath + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + prop));
                        }
                    } else {
                        prjs.add(demandedPrj.toHibernateProjection(pathToAlias.get(demandedPath)
                                + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + normalize(demandedProp)));
                    }
                } else {
                    String newAlias = "alias" + randomNr;
                    pathToAlias.put(demandedPath, newAlias);
                    criteria.createAlias(demandedPath, newAlias, JoinFragment.LEFT_OUTER_JOIN);

                    if (STAR_PROJECTION.equals(demandedProp)) {
                        List<String> allProps = extractRootProperties(sessionFactory,
                                getNestedType(clazz, demandedPath));
                        for (String prop : allProps) {
                            prjs.add(Projections.property(
                                    newAlias + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + prop).as(
                                    demandedPath + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + prop));
                        }
                    } else {
                        prjs.add(demandedPrj.toHibernateProjection(newAlias
                                + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + normalize(demandedProp)));
                    }
                    randomNr++;
                }
            }

            if (distinct) {
                criteria.setProjection(Projections.distinct(prjs));
            } else {
                criteria.setProjection(prjs);
            }

            if (resultTransformer != null) {
                criteria.setResultTransformer(resultTransformer);
            } else if (aliasToBeanClass != null) {
                criteria.setResultTransformer(Transformers.aliasToBean(aliasToBeanClass));
            } else {
                criteria.setResultTransformer(new DeepAliasToBeanResultTransformer(clazz, pathToCriteria.keySet(),
                        distinctRoot));
            }
        }
    }

    // CHECKSTYLE:ON
    @SuppressWarnings("rawtypes")
    private static Class getNestedType(Class clazz, String nestedPath) {
        try {
            String firstPath = null;
            if (nestedPath.indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) == -1) {
                firstPath = nestedPath;
                Field f = clazz.getDeclaredField(firstPath);
                if (f.getType().isAssignableFrom(Set.class) || f.getType().isAssignableFrom(SortedSet.class)
                        || f.getType().isAssignableFrom(List.class)) {
                    return (Class) ParameterizedType.class.cast(f.getGenericType()).getActualTypeArguments()[0];
                } else {
                    return f.getType();
                }
            } else {
                firstPath = nestedPath.substring(0, nestedPath.indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR));
                Field f = clazz.getDeclaredField(firstPath);
                if (f.getType().isAssignableFrom(Set.class) || f.getType().isAssignableFrom(SortedSet.class)
                        || f.getType().isAssignableFrom(List.class)) {
                    return getNestedType((Class) ParameterizedType.class.cast(f.getGenericType())
                            .getActualTypeArguments()[0], nestedPath.substring(nestedPath
                            .indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) + 1));
                } else {
                    return getNestedType(f.getType(), nestedPath.substring(nestedPath
                            .indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) + 1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * Creates association paths from input order arguments.
     * </p>
     * 
     * @param orderArgs
     *            List of order arguments.
     * @return A map from association paths to the corresponding list of order properties.
     */
    private static Map<String, List<String>> pathToOrders(List<String> orderArgs) {

        String currentPath;
        List<String> temp;

        Map<String, List<String>> ascPaths = new HashMap<String, List<String>>();
        for (String asc : orderArgs) {

            currentPath = "";
            if (asc.contains(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR)) {
                String[] associations = StringUtils.split(asc, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                for (int i = 0; i < associations.length; i++) {
                    String association = associations[i];

                    if (i < associations.length - 1) {
                        currentPath += association + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR;
                    } else {
                        currentPath = StringUtils.left(currentPath, currentPath.length() - 1);
                        temp = ascPaths.get(currentPath);
                        if (temp == null) {
                            temp = new ArrayList<String>();
                        }
                        temp.add(association);
                        ascPaths.put(currentPath, temp);
                    }
                }
            }
        }
        return ascPaths;
    }

    //CHECKSTYLE:OFF LineLength
    private static Map<String, List<CyberAdminExpression>> pathToExpressions(List<CyberAdminExpression> exps, boolean otherside) {

        String currentPath;
        List<CyberAdminExpression> temp;

        Map<String, List<CyberAdminExpression>> ascPaths = new HashMap<String, List<CyberAdminExpression>>();
        for (CyberAdminExpression exp : exps) {
            currentPath = "";
            if (!otherside && exp.getPropertyName() != null && exp.onAssociation()) {
                String[] associations = StringUtils.split(exp.getPropertyName(),
                        HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                for (int i = 0; i < associations.length; i++) {
                    String association = associations[i];

                    if (i < associations.length - 1) {
                        currentPath += association + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR;
                    } else {
                        currentPath = StringUtils.left(currentPath, currentPath.length() - 1);
                        temp = ascPaths.get(currentPath);
                        if (temp == null) {
                            temp = new ArrayList<CyberAdminExpression>();
                        }
                        temp.add(exp);
                        ascPaths.put(currentPath, temp);
                    }
                }
            } else if (otherside && exp.isPropertyComparison() && exp.otherSideOnAssociation()) {
                String[] associations = StringUtils.split((String) exp.getValue(),
                        HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                for (int i = 0; i < associations.length; i++) {
                    String association = associations[i];

                    if (i < associations.length - 1) {
                        currentPath += association + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR;
                    } else {
                        currentPath = StringUtils.left(currentPath, currentPath.length() - 1);
                        temp = ascPaths.get(currentPath);
                        if (temp == null) {
                            temp = new ArrayList<CyberAdminExpression>();
                        }
                        temp.add(exp);
                        ascPaths.put(currentPath, temp);
                    }
                }
            }
        }
        return ascPaths;
    }
    //CHECKSTYLE:ON
    /**
     * Extracts all properties under root level from specified <code>entityClass</code>.
     * 
     * @param sessionFactory
     *            The active {@link SessionFactory}.
     * @param entityClass
     *            Class of the entity from which the properties will be extracted from.
     */
    @SuppressWarnings("rawtypes")
    private static List<String> extractRootProperties(SessionFactory sessionFactory, Class entityClass) {
        List<String> result = new ArrayList<String>();
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityClass);
        String[] propertyNames = classMetadata.getPropertyNames();
        for (String property : propertyNames) {
            Type type = classMetadata.getPropertyType(property);

            if (type.isCollectionType() || type.isAssociationType() || type.isEntityType()) {
                // Does not load CollectionType, EntityType and AssociationType.
            } else if (type.isComponentType()) {
                // For component type, iterates through sub properties and adds projection.
                ComponentType componentType = (ComponentType) type;
                for (String child : componentType.getPropertyNames()) {
                    String childProperty = property + HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR + child;
                    result.add(childProperty);
                }
            } else {
                result.add(property);
            }
        }

        // Always addes identifier property.
        result.add(classMetadata.getIdentifierPropertyName());

        // Always addes identifier property.
        result.add(classMetadata.getIdentifierPropertyName());

        return result;
    }

    /**
     * Normalizes the property name by: - removing embedded field mark up
     * 
     * @param propertyName
     *            Original property name.
     * @return The normalized property.
     */
    private static final String normalize(String propertyName) {
        return propertyName.replace('-', '.');
    }
}