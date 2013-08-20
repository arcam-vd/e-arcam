/*
 * CyberAdminBeanHelper.java
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

package org.arcam.cyberadmin.ui.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import org.arcam.cyberadmin.ui.core.exception.CyberAdminRuntimeException;
import org.springframework.util.Assert;
import java.lang.reflect.ParameterizedType;

/**
 * 
 * @author vtn
 * 
 */
public final class CyberAdminBeanHelper {

    private CyberAdminBeanHelper() {
        // to hide
    }

    /**
     * Try to Instantiate for specified class <code>clazzType</code> by default constructor.
     * 
     * @param <T>
     *            the entity type.
     * @param clazzType
     *            the calling type and it MUST have default constructor (regardless its modifier).
     * @return new instance of T.
     */
    public static <T> T newInstance(Class<T> clazzType) {
        Assert.notNull(clazzType, "The class type must be given!!!");
        if (clazzType.isInterface()) {
            throw new CyberAdminRuntimeException(
                    MessageFormat.format("Specified class {0} is an interface", clazzType));
        }
        try {
            Constructor<T> defaultConstructor = clazzType.getDeclaredConstructor();

            boolean accessible = defaultConstructor.isAccessible();
            if ((!Modifier.isPublic(defaultConstructor.getModifiers()) || !Modifier.isPublic(defaultConstructor
                    .getDeclaringClass().getModifiers())) && !defaultConstructor.isAccessible()) {
                defaultConstructor.setAccessible(true);
            }
            T instance = defaultConstructor.newInstance();
            // back to original state.
            defaultConstructor.setAccessible(accessible);

            return instance;
        } catch (NoSuchMethodException e) {
            throw new CyberAdminRuntimeException(MessageFormat.format(
                    "The specified class {0} has not default constructor.{1}", clazzType,
                    CyberAdminRuntimeException.getStackTrace(e)));

        } catch (Exception e) {
            throw new CyberAdminRuntimeException(MessageFormat.format(
                    "Cannot invoke default constructor of type {0}.{1}", clazzType,
                    CyberAdminRuntimeException.getStackTrace(e)));
        }
    }

    /**
     * Try to Instantiate for specified class <code>clazzType</code> by given constructor <code>constructor</code>.
     * 
     * @param <T>
     *            the entity type.
     * @param clazzType
     *            the class of entity.
     * @param constructor
     *            the constructor of <T>
     * @param params
     *            parameter type for constructor.
     * @return new instance of T.
     */
    public static <T> T newInstance(Class<T> clazzType, Constructor<T> constructor, Object... params) {
        Assert.notNull(clazzType, "The class type must be given!!!");
        Assert.notNull(constructor, "The constructor must be given!!!");
        T newObject;
        try {
            boolean accessible = constructor.isAccessible();
            if ((!Modifier.isPublic(constructor.getModifiers()) || !Modifier.isPublic(constructor.getDeclaringClass()
                    .getModifiers())) && !accessible) {
                constructor.setAccessible(true);
            }
            newObject = constructor.newInstance(params);
            // back to original state.
            constructor.setAccessible(accessible);
        } catch (Throwable e) {
            throw new CyberAdminRuntimeException(e);
        }
        return newObject;
    }

    /**
     * Instantiate a class that is type argument at the position <code>pos</code> in a generic class.
     * 
     * @param clazz
     * @param pos
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T newInstance(final Class<?> clazz, final int pos) {
        try {
            return (T) ((Class) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[pos])
                    .newInstance();
        } catch (final Exception ex) {
            throw new RuntimeException("Cannot instantiate the class " + clazz + " " + pos, ex);
        }
    }

    /**
     * Get the class of type argument at position <code> pos </code> in a generic class.
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Class getClass(final Class<?> clazz, final int pos) {
        Assert.notNull(clazz, "The entity class must be given!!!");
        Type genericSuperclass = clazz.getGenericSuperclass();
        // Also check in the whole inheritance tree of clazz.
        if (!(genericSuperclass instanceof ParameterizedType)) {
            if (clazz.getSuperclass() == null) {
                return null;
            }
            return CyberAdminBeanHelper.getClass(clazz.getSuperclass(), pos);
        }
        return ((Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[pos]);
    }
}
