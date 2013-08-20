/*
 * DeepAliasToBeanResultTransformer.java
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.HibernateException;
import org.hibernate.transform.ResultTransformer;

/**
 * 
 * A util that helps transforming a projected entity deeply back to its own class of definition. This aims to reduce the
 * time needed for writting another DTO for re-mapping the result.
 * 
 * @see CyberAdminSearchCriteria
 * @author phd
 * 
 */
public class DeepAliasToBeanResultTransformer implements ResultTransformer {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Association paths used when specifying the {@link CyberAdminSearchCriteria}.
     */
    private Set<String> associationPaths = new HashSet<String>();

    /**
     * Indicates whether a distinct root should be applied on the return result list.
     */
    private boolean distinctRoot;

    /**
     * Class of the result item.
     */
    @SuppressWarnings("rawtypes")
    private final Class resultClass;

    /**
     * Constructs an instance of this transformer from {@link #resultClass}, {@link #associationPaths} and
     * {@link #distinctRoot}.
     * 
     * @param resultClass
     *            Class to be mapped back for each returned tuple.
     * @param associationPaths
     *            Association paths used to query data, will be used in {@link #transformList(List)}.
     * @param distinctRoot
     *            Specifies whether a distinction on root entity should be done on the result list.
     */
    @SuppressWarnings({ "rawtypes" })
    public DeepAliasToBeanResultTransformer(Class resultClass, Set<String> associationPaths, boolean distinctRoot) {

        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.resultClass = resultClass;
        this.distinctRoot = distinctRoot;

        this.associationPaths = associationPaths;
        Set<String> toBeRemovedPaths = new HashSet<String>();
        for (String path : this.associationPaths) {
            for (String anotherPath : this.associationPaths) {
                if (!path.equals(anotherPath) && StringUtils.startsWith(path, anotherPath)) {
                    toBeRemovedPaths.add(anotherPath);
                }
            }
        }
        this.associationPaths.removeAll(toBeRemovedPaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result;

        try {
            result = resultClass.newInstance();

            for (int i = 0; i < aliases.length; i++) {

                // No needs to initializes any object on the path if the return tuple item is NULL.
                if (tuple[i] == null) {
                    continue;
                }

                String alias = aliases[i];
                if (alias != null) {
                    if (alias.indexOf(HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) == -1) {
                        Field field = FieldUtils.getField(resultClass, alias, true);
                        field.set(result, tuple[i]);
                    } else {
                        String fullPath = alias.substring(0,
                                StringUtils.lastIndexOf(alias, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR));
                        String prop = alias.substring(StringUtils.lastIndexOf(alias,
                                HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR) + 1);

                        String[] paths = StringUtils.split(fullPath, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);

                        Class lastClazz = resultClass;
                        Object lastObj = result;
                        for (String path : paths) {
                            Field field = FieldUtils.getField(lastClazz, path, true);
                            lastClazz = field.getType();

                            if (field.getType().isAssignableFrom(Set.class)
                                    || field.getType().isAssignableFrom(SortedSet.class)
                                    || field.getType().isAssignableFrom(List.class)) {
                                Collection collection = (Collection) field.get(lastObj);
                                if (collection.isEmpty()) {
                                    collection.add(newCollectionItem(field));
                                }

                                lastClazz = (Class) ((ParameterizedType) field.getGenericType())
                                        .getActualTypeArguments()[0];
                                lastObj = collection.iterator().next();
                            } else {
                                if (field.get(lastObj) == null) {
                                    field.set(lastObj, lastClazz.newInstance());
                                }
                                lastObj = field.get(lastObj);
                            }
                        }

                        if (lastClazz != null) {
                            FieldUtils.getField(lastClazz, prop, true).set(lastObj, tuple[i]);
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName() + ". Detail: "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName() + ". Detail: "
                    + e.getMessage());
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    private Object newCollectionItem(Field f) throws InstantiationException, IllegalAccessException {
        return ((Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]).newInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List transformList(List list) {
        if (!distinctRoot) {
            return list;
        }
        List distinctResult = new ArrayList();
        for (Object newEntity : list) {
            if (!distinctResult.contains(newEntity)) {
                distinctResult.add(newEntity);
            } else {
                Object existingEntity = distinctResult.get(distinctResult.indexOf(newEntity));
                for (String associationPath : associationPaths) {
                    String[] parts = StringUtils.split(associationPath, HibernateCriteriaBuilder.ASSOCIATION_SEPARATOR);
                    merge(existingEntity, newEntity, parts);
                }
            }
        }
        return distinctResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void merge(Object existingEntity, Object newEntity, String[] parts) {
        Object lastExistingObj = existingEntity;
        Object lastNewObj = newEntity;

        if (lastExistingObj instanceof Collection) {
            Collection lastExistingCollection = (Collection) lastExistingObj;
            Collection lastNewCollection = (Collection) lastNewObj;
            lastExistingCollection.addAll(lastNewCollection);
        }

        if (parts.length > 0 && lastExistingObj != null && lastNewObj != null && lastExistingObj.equals(lastNewObj)) {
            try {
                String part = parts[0];
                lastExistingObj = PropertyUtils.getProperty(lastExistingObj, part);
                lastNewObj = PropertyUtils.getProperty(lastNewObj, part);
                if (lastExistingObj != null && lastNewObj != null) {
                    if (!(lastExistingObj instanceof Collection)) {
                        if (lastExistingObj.equals(lastNewObj)) {
                            merge(lastExistingObj, lastNewObj, (String[]) ArrayUtils.remove(parts, 0));
                        }
                    } else {
                        Collection lastExistingCollection = (Collection) lastExistingObj;
                        Collection lastNewCollection = (Collection) lastNewObj;

                        if (lastExistingCollection instanceof SortedSet) {
                            lastExistingCollection = new TreeSet(lastExistingCollection);
                        } else {
                            lastExistingCollection = new HashSet(lastExistingCollection);
                        }

                        lastExistingCollection.addAll(lastNewCollection);
                        PropertyUtils.setProperty(existingEntity, part, lastExistingCollection);

                        for (Object lastExistingItem : lastExistingCollection) {
                            Object lastNewItem = CollectionUtils.find(lastNewCollection, new Predicate() {
                                @Override
                                public boolean evaluate(Object rhs) {
                                    return PredicateUtils.equalPredicate(rhs).evaluate(rhs);
                                }
                            });
                            if (lastNewItem != null) {
                                merge(lastExistingItem, lastNewItem, (String[]) ArrayUtils.remove(parts, 0));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error during transforming list in Hibernate", e);
            }
        }
    }
}
