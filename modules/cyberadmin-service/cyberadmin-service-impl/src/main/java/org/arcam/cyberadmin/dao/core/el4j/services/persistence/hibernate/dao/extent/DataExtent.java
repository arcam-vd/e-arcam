/*
 * DataExtent
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

import java.io.Serializable;

/**
 * A DataExtent represents the extent of the graphs of objects to be
 * loaded in a hibernate query.<br>
 * Be careful in not mixing up the notion of an extent:<br>
 * In this context it is referring to the extent of the DOM to be loaded from the underlying
 * persistent store.<br>
 * In other contexts like for example OpenJPA the notion can have a different meaning.<br>
 * <br>
 * <b>Principle:</b><br>
 * An Extent represents a part of the DOM that should be loaded together.<br>
 * It can be used to pool together associated entities in order 
 * to provide performance improvements over standard data fetching.<br>
 * Specifying the extent when loading entities with Hibernate 
 * allows for tuning of lazy loading and eager fetching behavior.<br>
 * For details about how to use the "Fetch Type" in order to control whether a field is fetched eagerly or lazily,
 * see the corresponding reference manual of Java Persistence API 
 * (eg. {@link http://java.sun.com/javaee/5/docs/api/javax/persistence/FetchType.html})<br>
 * 
 * In a DataExtent we internally distinguish between fields, entities and collections:<br>
 * <ul>
 * 	<li> Fields: fields/methods in persistent class which are of a simple java type or of type string or enum.<br>
 * 		These fields are normally stored in the same table as the root entity and cannot have any child entities.<br>
 * 		Note that you cannot lazy-load such fields without byte-code instrumentation, thus normally all fields are
 * 		loaded eagerly and you should not have to mention any fields in your extent.<br>
 * 		Exception: the type {@link java.sql.Blob} and {@link java.sql.Clob} are loaded lazy per default. 
 * 		Since there are different handling policies from different db vendors, be careful when using these types.<br>
 * 		For example oracle takes care of the lazy loading of a blob outside a session, whereas with derby you have 
 * 		to read out the blob during the session into for example a byte array.
 * 	<li> Entities: entities are the complex data types in a persistent class. Normally they get stored in a different 
 * 		entity table and can have child entities and fields themselves. To define an entity lazy loading, specify the 
 * 		fetch property for example in your association annotation:<br>
 * 		<code>@OneToOne(fetch = FetchType.LAZY)</code><br>
 * 	<li> Collections: all data types implementing the {@link java.util.Collection} interface should be
 * 		added to the extent as collection for a proper fetching at runtime.
 * </ul>
 * <b>Remark:</b> When using DataExtent you don't have to have any knowledge 
 * about the db mapping or anything like that. 
 * It suffices to know the interface of the entity you are about to use. For example if you have:<br>
 * <code><pre>
 * public class Employee {
 * 	...
 * 	public Employee getManager() {...
 * </pre></code>
 * To be sure that you can access the manager of an employee after retrieving it from the dao, add the manager to 
 * the extent you pass as an extra argument to the dao: <code>dao.findById(id, extent.with("manager"));</code><br>
 * <br>
 * Note that any part of the extent that is eagerly loaded according to the JPA metadata rules cannot be changed to 
 * a lazy loading behavior with DataExtent. On the other hand, all as lazy loading indicated parts can be forced to
 * be loaded at runtime in each query.<br>
 * Also, parts of the extent that does not get loaded but accessed at runtime will throw a LazyLoadingException as it 
 * would without using DataExtent's.<br>
 * <br> 
 * <b>Features:</b> <br>
 *  <ul>
 *   <li> new DataExtent: provide root class and optionally the name of the root 
 *   <li> with/without: add/remove fields, sub-entities and/or collections to/from the extent.<br>
 *   	If the part to add already exists in the extent, the two corresponding parts are merged.
 *   <li> withSubentities: add sub-entities to the extent, convenient for adding entities you want
 *   		to define in detail.
 *   <li> all: add everything to the graph of objects.
 *   <li> merge: merge two DataExtents to one. The class has to be the same.
 *   <li> freeze: freeze an extent to prevent any changes to it afterwards.<br>
 *   <li> see also features of {@link ExtentEntity} to write your code in a convenient way
 *  </ul>
 *
 *  Remark: Be sure to import the static methods {@link ExtentEntity#entity} and 
 *  	{@link ExtentCollection#collection} to create easily new Entities and Collections.<br> 
 *  <br>
 *  <b>Sample code:</b> <br>
 * 		<code>
 * 			<pre>
 * 	// The Extent Object of type 'Person'
 * 	extent = new DataExtent(Person.class);
 * 	// Construct a complex graph:
 * 	// Person has a List of Teeth, a Tooth has a 'Person' as owner, 
 * 	// the owner has a list of 'Person' as friends, the friends are again
 * 	// the same 'Person'-entity as defined in the beginning.
 * 	extent.withSubentities(
 *		collection("teeth",
 *			entity(Tooth.class)
 *				.with("owner")
 *			),
 *		collection("friends", extent.getRootEntity())
 *	);
 *	
 *	// Extent of a File
 *	extent = new DataExtent(File.class);
 *	// Create a simple, light extent for an overview over the files
 *	extent.with("name", "lastModified", "fileSize", "mimeType");
 *	dao.getAll(extent);
 *	// Note: there will potentially be loaded more than you specify, if it is defined to be fetched eagerly
 *
 *	// Another extent to see also the file content
 *	extent = new DataExtent(File.class);
 *	extent.with("name", "content", "lastModified", "fileSize", "mimeType");
 *	dao.findById(id, extent);
 *
 *	// Extent in a reference DOM, where references are loaded lazily
 *	extent = new DataExtent(Publication.class);
 *	extent.all(3);
 *	dao.findByName(publicationName, extent);
 *	// Now you have loaded all referenced publications, books, papers, ... to a depth of 3
 *	// Eg. using the parent of the parent is now possible.
 *			</pre>
 *		</code>
 *
 * 
 * @author Andreas Rueedlinger (ARR)
 */
public class DataExtent implements Serializable {
	/** Defines the default loading depth. */
	public static final int DEFAULT_LOADING_DEPTH = 1;
	
	/** The root entity. */
	private ExtentEntity m_rootEntity;
	
	/**
	 * Default Creator.
	 * @param c		the class of the root entity.
	 */
	public DataExtent(Class<?> c) {
		m_rootEntity = ExtentEntity.rootEntity(c);
	}
	
	/**
	 * Root entity of the extent.
	 * @return 		the root entity of the extent.
	 */
	public ExtentEntity getRootEntity() {
		return m_rootEntity;
	}
	
	/**
	 * The id of the data extent.
	 * If two extents contain the same subparts,
	 * this id should be equal.
	 * @return the id of the extent.
	 */
	public String getExtentId() {
		return m_rootEntity.getId();
	}
	
	//****************** Fluent API **********************//
	
	/**
	 * Extend the extent by the given fields.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be added.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent with(String... fields) throws NoSuchMethodException {
		m_rootEntity.with(fields);
		return this;
	}
	
	/**
	 * Extend the extent by the given sub-entities.
	 * @param entities	entities to be added.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent withSubentities(AbstractExtentPart... entities) throws NoSuchMethodException {
		m_rootEntity.withSubentities(entities);
		return this;
	}
	
	/**
	 * Exclude fields from the extent.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent without(String...fields) {
		m_rootEntity.without(fields);
		return this;
	}

	/**
	 * Include all fields, entities and collections of the class.
	 * Exploration depth is DEFAULT_LOADING_DEPTH.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all() {
		m_rootEntity.all(DEFAULT_LOADING_DEPTH);
		return this;
	}
	
	/**
	 * Include all fields, entities and collections of the class.
	 * @param depth		Exploration depth
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all(int depth) {
		m_rootEntity.all(depth);
		return this;
	}
	
	/**
	 * Merge two DataExtents. Returns
	 * The class of the two rootEntities should be the same.
	 * @param other	the extent to be merged with.
	 * @return	 the union of the extents.
	 */
	public DataExtent merge(DataExtent other) {
		m_rootEntity.merge(other.m_rootEntity);
		return this;
	}
	
	/**
	 * Freeze the extent, meaning that no further changes to it are possible.
	 * @return the frozen extent.
	 */
	public DataExtent freeze() {
		m_rootEntity.freeze();
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return getExtentId().hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			return true;
		} else if (object instanceof DataExtent) {
			return getExtentId().equals(((DataExtent) object).getExtentId());
		} else {
			return false;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getExtentId();
	}

	
}
