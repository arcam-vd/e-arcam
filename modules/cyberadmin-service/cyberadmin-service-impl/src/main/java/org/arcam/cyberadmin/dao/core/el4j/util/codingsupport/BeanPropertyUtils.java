/*
 * BeanPropertyUtils
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
 package org.arcam.cyberadmin.dao.core.el4j.util.codingsupport;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to get/set properties generically.
 *
 * @author David Bernhard (DBD)
 */
public final class BeanPropertyUtils {
	
	/**
	 * Utility class -> no instances.
	 */
	private BeanPropertyUtils() { }

	/**
	 * Generically get a property.
	 * @param entity The entity to get a property on.
	 * @param propertyName The property name to get.
	 * @return The property value as returned from reading the property.
	 */
	public static Object getProperty(Object entity, String propertyName) {
		PropertyDescriptor descriptor = getDescriptor(entity, propertyName);
		Method reader = descriptor.getReadMethod();
		if (reader == null) {
			throw new IllegalArgumentException("Property " + propertyName
				+ "is not readable.");
		}

		boolean accessible = reader.isAccessible();
		try {
			reader.setAccessible(true);
			return reader.invoke(entity, new Object[0]);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			reader.setAccessible(accessible);
		}
	}
	
	/**
	 * Generically get a property, casting arrays to collections.
	 * @param entity The entity to get a property on.
	 * @param propertyName The property name to get.
	 * @return The property value. If it was an array, it is returned as a collection.
	 */
	public static Object getPropertySimplified(Object entity, String propertyName) {
		Object value = getProperty(entity, propertyName);
		if (value.getClass().isArray()) {
			// Unlike generics, arrays luckily ARE covariant.
			List<Object> list = new ArrayList<Object>(((Object[]) value).length);
			for (Object entry : (Object[]) value) {
				list.add(entry);
			}
			return list;
		} else {
			return value;
		}
	}
	
	/**
	 * Get the property descriptor for a property by name.
	 * @param entity The entity to get the property for.
	 * @param propertyName The property name.
	 * @return The property descriptor.
	 */
	private static PropertyDescriptor getDescriptor(Object entity, String propertyName) {
		Class<?> entityClass = entity.getClass();
		return getDescriptorByClass(entityClass, propertyName);
	}
	
	/**
	 * Get the property descriptor for a property by name and class.
	 * @param entityClass The entity class to get the property for.
	 * @param propertyName The property name.
	 * @return The property descriptor.
	 */
	private static PropertyDescriptor getDescriptorByClass(Class<?> entityClass, String propertyName) {
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(entityClass);
		} catch (IntrospectionException e) {
			throw new RuntimeException("Cannot introspect " + entityClass, e);
		}
		PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
		PropertyDescriptor descriptor = null;
		for (PropertyDescriptor candidate : descriptors) {
			if (propertyName.equals(candidate.getName())) {
				descriptor = candidate;
				break;
			}
		}
		if (descriptor == null) {
			throw new IllegalArgumentException("No property " + propertyName + " in class "
				+ entityClass);
		}
		return descriptor;
	}
	
	/**
	 * Generically set a property.
	 * @param entity The entity to set the property on.
	 * @param propertyName The property name.
	 * @param value The value to set.
	 */
	public static void setProperty(Object entity, String propertyName, Object value) {
		PropertyDescriptor descriptor = getDescriptor(entity, propertyName);
		Method writer = descriptor.getWriteMethod();
		if (writer == null) {
			throw new IllegalArgumentException("Property " + propertyName
				+ "is not writable.");
		}
		
		boolean accessible = writer.isAccessible();
		try {
			writer.setAccessible(true);
			writer.invoke(entity, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			writer.setAccessible(accessible);
		}
	}
	
	/**
	 * Generically set a property. If the setter requires an array, a collection may be
	 * passed too in which case it is put into an array.
	 * @param entity The entity to set the property on.
	 * @param propertyName The property name.
	 * @param value The value to set.
	 */
	public static void setPropertySimplified(Object entity, String propertyName, Object value) {
		PropertyDescriptor descriptor = getDescriptor(entity, propertyName);
		Method writer = descriptor.getWriteMethod();
		
		if (writer.getParameterTypes()[0].isArray() && value instanceof Collection) {
			Class<?> componentClass = writer.getParameterTypes()[0].getComponentType();
			// We need unchecked access to the collection because we have no
			// type information. This is safe as we only use it internally
			// to take objects out.
			// PRE: The caller has only put elements in the collection that 
			// can go into the array.
			@SuppressWarnings("unchecked")
			Collection collection = (Collection) value;
			Object array = Array.newInstance(componentClass, collection.size());
			int i = 0;
			for (Object entry : collection) {
				Array.set(array, i, entry);
				i++;
			}
			setProperty(entity, propertyName, array);
		} else {
			setProperty(entity, propertyName, value);
		}
	}
	
	/**
	 * Get the read method of a property.
	 * @param entityClass     the class of the entity
	 * @param propertyName    the property name
	 * @return                the read method to access the property
	 */
	public static Method getReadMethod(Class<?> entityClass, String propertyName) {
		PropertyDescriptor descriptor = getDescriptorByClass(entityClass, propertyName);
		return descriptor.getReadMethod();
	}
	
	/**
	 * Get the write method of a property.
	 * @param entityClass     the class of the entity
	 * @param propertyName    the property name
	 * @return                the write method to access the property
	 */
	public static Method getWriteMethod(Class<?> entityClass, String propertyName) {
		PropertyDescriptor descriptor = getDescriptorByClass(entityClass, propertyName);
		return descriptor.getWriteMethod();
	}
}
