/*
 * ReturnsUnchangedParameter
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated method returns a parameter without changing it. The
 * annotations' value contains the index of the parameter returned; it defaults
 * to 0, i.e. the first parameter.
 * <p>
 * More precisely, this annotation states that the method's return value is
 * <i>logically and transitively identical</i> to the value passed.
 * <i>Logically identical</i> means that the
 * returned object has the same logical identity, <i>transitive</i> means that
 * this must hold for all references reachable through it as
 * well. Note that objects without logical identity are trivially logically
 * identical, ending the recursion. Logical identity is defined by the subclass
 * of AbstractIdentityFixer in use.
 *
 *<p>For instance, <pre>
 *    &#64;ReturnsUnchangedParameter
 *    T saveOrUpdate(T entity);
 *</pre>
 *means that saving an object returns the saved object, which is logically
 *identical to its former version.
 *
 *<p> As of JDK 1.5, it is impossible to inherit method annotations when
 *overriding/implementing a method; such annotations must therefore be provided
 *manually.
 *
 *
 * @author Adrian Moos (AMS)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnsUnchangedParameter {
	/** The index of the parameter returned. The first parameter has index 0. */
	int value() default 0;
}
