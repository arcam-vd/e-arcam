/*
 * ExtentEntity
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao.extent;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;
import org.arcam.cyberadmin.dao.core.el4j.core.metadata.ContainedClass;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.BeanPropertyUtils;


/**
 * A ExtentEntity represents a complex Data Type in an Extent.
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> static method entity: create a new entity 
 *   <li> with/without: add/remove fields, sub-entities and collections to/from the extent
 *   <li> withSubentities: add sub-entities to the extent, convenient for adding entities you want
 *   		to define in detail.
 *   <li> all: the whole entity with all fields, entities and collections.
 *  </ul>
 *
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class ExtentEntity extends AbstractExtentPart {
	
	/** The class of the entity. */
	private Class<?> m_entityClass;
	
	/** The field-methods of the entity, always stays sorted. */
	private List<String> m_fields;
	
	/** The child-entities of the entity, always stays sorted by name. */
	private List<ExtentEntity> m_childEntities;
	
	/** The collections of the entity, always stays sorted by name. */
	private List<ExtentCollection> m_collections;
	
	/** The id of the entity. */
	private String m_entityId;
	
	/** Is the entity a root entity. */
	private boolean m_root = false;
	
	/** Is the ExtentEntity frozen, eg. must not be changed anymore. */
	private boolean m_frozen;
	
	/**
	 * Default Creator, hidden.
	 * @param c		the class of the entity
	 */
	public ExtentEntity(Class<?> c) {
		m_name = firstCharLower(c.getSimpleName());
		m_entityClass = c;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_entityClass.getName());
	}
	
	/**
	 * Default Creator, hidden.
	 * @param name	the name of the entity
	 * @param c		the class of the entity
	 */
	public ExtentEntity(String name, Class<?> c) {
		m_name = name;
		m_entityClass = c;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_name);
	}
	
	/**
	 * Default Creator, hidden.
	 * @param c 		the class of the entity
	 * @param method	the method to get the entity
	 */
	public ExtentEntity(Class<?> c, Method method) {
		m_name = toFieldName(method);
		m_entityClass = c;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_name);
	}
	
	/** {@inheritDoc} */
	public String getId() {
		return m_entityId;
	}
	
	/** {@inheritDoc} */
	protected void updateId() {
		rebuildId();
	}
	
	/**
	 * @return if the entity is a root entity.
	 */
	public boolean isRoot() {
		return m_root;
	}
	
	/**
	 * Class of the entity.
	 * @return the class of the entity.
	 */
	public Class<?> getEntityClass() {
		return m_entityClass;
	}
	
	/**
	 * Field-methods of the entity.
	 * @return the field-methods of the entity.
	 */
	public List<String> getFields() {
		return new LinkedList<String>(m_fields);
	}
	
	/**
	 * Child entities.
	 * @return the child entities of the entity.
	 */
	public List<ExtentEntity> getChildEntities() {
		return new LinkedList<ExtentEntity>(m_childEntities);
	}
	
	/**
	 * Collections.
	 * @return the collections of the entity.
	 */
	public List<ExtentCollection> getCollections() {
		return new LinkedList<ExtentCollection>(m_collections);
	}
	
	/**
	 * Rebuild the id string.
	 * Go recursive through all children and get their id.
	 * If entity is root and has a parent, infinite loops are prevented
	 * by not updating the parent and outputting the hashCode when to toString is called.
	 */
	private void rebuildId() {
		// Rebuild the id string
		String id = "|";
		
		if (isRoot()) {
			id += m_entityClass.getName();
		} else {
			id += m_name;
		}
		id += m_fields.toString();
		id += m_childEntities.toString();
		id += m_collections.toString();
		id += "|";
		
		// Inform the parent if id changed
		if (!m_entityId.equals(id)) {
			m_entityId = id;
			if (m_parent != null && !isRoot()) {
				m_parent.updateId();
			}
		}
	}
	/**
	 * Add a field-method to the fields of the entity.
	 * @param field	 the field-method to add.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	private void addField(String field) {
		if (!m_fields.contains(field)) {
			m_fields.add(field);
			Collections.sort(m_fields);
			rebuildId();
		}
	}
	
	/**
	 * Remove a field from the fields of the entity as name.
	 * @param name	 the field as name to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeField(String name) {
		if (m_fields.remove(name)) {
			rebuildId();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Add a child-entity to the entity. Parent of child entity is set and consistency
	 * to parent class is checked.
	 * @param child	 the child to add.
	 * @throws NoSuchMethodException 
	 */
	private void addChildEntity(ExtentEntity child) throws NoSuchMethodException {
		if (!m_childEntities.contains(child)) {
			// Merge entity with already existing one when same name
			for (ExtentEntity ent : m_childEntities) {
				if (ent.m_name.equals(child.m_name)) {
					try {
						BeanPropertyUtils.getReadMethod(m_entityClass, child.getName());
						ent.merge(child);
						return;
					} catch (IllegalArgumentException e) {
						throw new NoSuchMethodException(e.getMessage());
					}
				}
			}
			try {
				Method m  = BeanPropertyUtils.getReadMethod(m_entityClass, child.getName());
				if (m != null) {
					child.setParent(this);
					m_childEntities.add(child);
					Collections.sort(m_childEntities);
					rebuildId();
				}
			} catch (IllegalArgumentException e) {
				throw new NoSuchMethodException(e.getMessage());
			}
		}
	}
	
	/**
	 * Remove an entity from the children of the entity.
	 * @param name	 name of the entity to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeEntity(String name) {
		for (ExtentEntity e : m_childEntities) {
			if (e.getName().equals(name)) {
				m_childEntities.remove(e);
				rebuildId();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a collection to the entity.
	 * @param collection	 the collection to add.
	 * @throws NoSuchMethodException 
	 */
	private void addCollection(ExtentCollection collection) throws NoSuchMethodException {
		if (!m_collections.contains(collection)) {
			// Merge collection with already existing one when same name
			for (ExtentCollection c : m_collections) {
				if (c.m_name.equals(collection.m_name)) {
					try {
						BeanPropertyUtils.getReadMethod(m_entityClass, collection.getName());
						c.merge(collection);
						return;
					} catch (IllegalArgumentException e) {
						throw new NoSuchMethodException(e.getMessage());
					}
				}
			}
			try {
				Method m = BeanPropertyUtils.getReadMethod(m_entityClass, collection.getName());
				collection.setParent(this);
				boolean consistent = false;
				// Check if the class of the contained entity is consistent
				Type rawType = m.getGenericReturnType();
				if (rawType instanceof ParameterizedType) {
					Type[] pt = ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments();
					if (pt.length > 0 && pt[0] instanceof Class<?>) {
						if (((Class<?>) pt[0]).isAssignableFrom(collection.getContainedEntity().getEntityClass())) {

							consistent = true;
						}
					}
				}
				if (consistent) {
					m_collections.add(collection);
					Collections.sort(m_collections);
					rebuildId();
				} else {
					throw new NoSuchMethodException("Collection type ["
						+ collection.getContainedEntity().getEntityClass().getSimpleName()
						+ "] doesnt conform with class definition.");
				}
			} catch (IllegalArgumentException e) {
				throw new NoSuchMethodException(e.getMessage());
			}
		}
	}
	
	/**
	 * Remove a collection from the collections of the entity.
	 * @param name	 name of the collection to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeCollection(String name) {
		for (ExtentCollection e : m_collections) {
			if (e.getName().equals(name)) {
				m_collections.remove(e);
				rebuildId();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a method to the extent (given as name).
	 * Automatically checks if a property, entity or a collection.
	 * @param name	 the field to add.
	 * @throws NoSuchMethodException 
	 */
	private void addMethodAsName(String name) throws NoSuchMethodException {
		try {
			Method m = BeanPropertyUtils.getReadMethod(m_entityClass, name);
			if (m != null) {
				fetchMethod(m, DataExtent.DEFAULT_LOADING_DEPTH);
			} else {
				throw new NoSuchMethodException("Method doesn't exist.");
			}
		} catch (IllegalArgumentException e) {
			throw new NoSuchMethodException(e.getMessage());
		}
	}
	
	
	//*************** Fluent API ******************//
	
	
	/**
	 * Returns a new Entity object, based on the given class.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity rootEntity(Class<?> c) {
		ExtentEntity tmp = new ExtentEntity(c);
		tmp.m_root = true;
		return tmp;
		
	}
	
	/**
	 * Returns a new Entity object, based on the given class.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(Class<?> c) {
		return new ExtentEntity(c);
	}
	
	/**
	 * Returns a new Entity object, based on the given name and class.
	 * @param name	the name of the entity.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(String name, Class<?> c) {
		return new ExtentEntity(name, c);
	}
	
	/**
	 * Returns a new Entity object, based on the given class and method.
	 * @param c		the class of the entity.
	 * @param m		the method to get the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(Class<?> c, Method m) {
		return new ExtentEntity(c, m);
	}
	
	/**
	 * Extend the entity by the given fields.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be added.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity with(String... fields) throws NoSuchMethodException {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		for (String s : fields) {
			addMethodAsName(s);
		}
		return this;
	}
	
	/**
	 * Extend the entity by the given sub-entities.
	 * @param entities	entities to be added.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity withSubentities(AbstractExtentPart... entities) throws NoSuchMethodException {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		for (AbstractExtentPart entity : entities) {
			if (entity instanceof ExtentEntity) {
				addChildEntity((ExtentEntity) entity);
			} else {
				addCollection((ExtentCollection) entity);
			}
		}
		return this;
	}
	
	/**
	 * Exclude fields from the entity.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity without(String...fields) {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		for (String s : fields) {
			if (!removeField(s)) {
				if (!removeEntity(s)) {
					removeCollection(s);
				}
			}
		}
		return this;
	}
	
	/**
	 * Include all fields, entities and collections of the class-entity.
	 * @param depth		Exploration depth.
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity all(int depth) {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		if (depth > 0) {
			for (Method m : m_entityClass.getMethods()) {
				try {
					fetchMethod(m, depth);
				} catch (NoSuchMethodException e) {
					// not possible since we found the method!
				}
			}
		}
		return this;
	}
	
	/**
	 * Merge two ExtentEntities. Returns the union of the entities.
	 * The class of the two entities should be the same, the name and the parent is taken from 
	 * this object.
	 * @param other	the extent to be merged with.
	 * @return	the merged entity.
	 */
	public ExtentEntity merge(ExtentEntity other) {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		if (m_entityClass.equals(other.m_entityClass) && !this.equals(other)) {
			mergeFields(other.m_fields);
			mergeEntities(other.m_childEntities);
			mergeCollections(other.m_collections);
			rebuildId();
		}
		return this;
	}
	
	/**
	 * Freeze the ExtentEntity, meaning that no further changes to it are possible.
	 * @return the frozen ExtentEntity.
	 */
	public ExtentEntity freeze() {
		if (!m_frozen) {
			m_frozen = true;
			for (ExtentEntity ent : m_childEntities) {
				ent.freeze();
			}
			for (ExtentCollection c : m_collections) {
				c.freeze();
			}
		}
		return this;
	}
	
	/**
	 * Merge the List of fields into the current  field list.
	 * @param otherFields the fields to merge with.
	 */
	private void mergeFields(List<String> otherFields) {
		// Merge fields
		if (m_fields.isEmpty()) {
			m_fields.addAll(otherFields);
		} else {
			Iterator<String> i = otherFields.iterator();
			String f = null;
			if (i.hasNext()) {
				f = i.next();
			}
			for (int k = 0; k < m_fields.size() && f != null; k++) {
				int result = m_fields.get(k).compareTo(f);
				if (result > 0) {
					// add element if it is before the current element
					m_fields.add(k, f);
				}
				if (result >= 0) {
					// Iterate to next element
					if (i.hasNext()) {
						f = i.next();
					} else {
						f = null;
					}
				}
			}
		}
	}
	
	/**
	 * Merge the List of entities into the current entity list.
	 * @param otherEntities the entities to merge with.
	 */
	private void mergeEntities(List<ExtentEntity> otherEntities) {
		// Merge entities
		if (m_childEntities.isEmpty()) {
			m_childEntities.addAll(otherEntities);
		} else {
			Iterator<ExtentEntity> i = otherEntities.iterator();
			ExtentEntity e = null;
			if (i.hasNext()) {
				e = i.next();
			}
			for (int k = 0; k < m_childEntities.size() && e != null; k++) {
				int result = m_childEntities.get(k).compareTo(e);
				if (result > 0) {
					// add element if it is before the current element
					m_childEntities.add(k, e);
				} else if (result == 0 && !e.equals(m_childEntities.get(k))) {
					// Merge the two child entities
					// TODO infinite loops??
					m_childEntities.get(k).merge(e);
				}
				if (result >= 0) {
					// Iterate to next element
					if (i.hasNext()) {
						e = i.next();
					} else {
						e = null;
					}
				}
			}
		}
	}
	
	/**
	 * Merge the List of entities into the current entity list.
	 * @param otherCollections the collections to merge with.
	 */
	private void mergeCollections(List<ExtentCollection> otherCollections) {
		// Merge collections
		if (m_collections.isEmpty()) {
			m_collections.addAll(otherCollections);
		} else {
			Iterator<ExtentCollection> i = otherCollections.iterator();
			ExtentCollection c = null;
			if (i.hasNext()) {
				c = i.next();
			}
			for (int k = 0; k < m_collections.size() && c != null; k++) {
				int result = m_collections.get(k).compareTo(c);
				if (result > 0) {
					// add element if it is before the current element
					m_collections.add(k, c);
				} else if (result == 0 
					&& !c.getContainedEntity().equals(m_collections.get(k).getContainedEntity())) {
					// Merge the two contained entities
					// TODO infinite loops??
					m_collections.get(k).merge(c);
				}
				if (result >= 0) {
					// Iterate to next element
					if (i.hasNext()) {
						c = i.next();
					} else {
						c = null;
					}
				}
			}
		}
	}
	
	/**
	 * Add the method to the entity in the appropriate manner,
	 * as field, entity or collection.
	 * Ensure when calling the function that method exists in entity class.
	 * @param m			the method to be added.
	 * @param depth		Exploration depth.
	 * @throws NoSuchMethodException 
	 */
	private void fetchMethod(Method m, int depth) throws NoSuchMethodException {
		// Fetch only the methods with getter and no arguments
		// Exclude getClass()
		boolean isGetter = m.getName().startsWith("get") && m.getParameterTypes().length == 0;
		if (isGetter && !m.getName().equals("getClass") && !m.getName().equals("get")) {

			if (m.getReturnType().isPrimitive() || m.getReturnType().isEnum()
				/*|| m.getReturnType().equals(String.class)*/) {
				
				addField(toFieldName(m));
			} else if (Collection.class.isAssignableFrom(m.getReturnType())) {
				fetchCollection(m, depth);
			} else {
				ExtentEntity tmp = entity(m.getReturnType(), m);
				if (depth > 1) {
					tmp.all(depth - 1);
				}
				addChildEntity(tmp);
			}
		}
	}
	
	/**
	 * Add the collection method to the entity,
	 * with all its special treatment.
	 * @param m			the method to be added.
	 * @param depth		Exploration depth.
	 * @throws NoSuchMethodException 
	 */
	private void fetchCollection(Method m, int depth) throws NoSuchMethodException {
		// Special treatment if collection type
		Class<?> t = null;
		// First try to read out the contained type of the annotation
		ContainedClass containedAnnotation = m.getAnnotation(ContainedClass.class);
		if (containedAnnotation == null) {
			Type rawType = m.getGenericReturnType();
			if (rawType instanceof ParameterizedType) {
				Type[] pt = ((ParameterizedType) m.getGenericReturnType())
				.getActualTypeArguments();
				if (pt.length > 0 && pt[0] instanceof Class<?>) {
					t = (Class<?>) ((ParameterizedType) m.getGenericReturnType())
						.getActualTypeArguments()[0];
				}
			}
		} else {
			t = containedAnnotation.value();
		}
		if (t != null) {
			ExtentCollection tmp = new ExtentCollection(t, m);
			if (depth > 1) {
				tmp.getContainedEntity().all(depth - 1);
			}
			addCollection(tmp);
		}
		
	}
	

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (m_parent != null && isRoot()) {
			return super.nativeToString();
		} else {
			return m_entityId;
		}
	}
	
}
