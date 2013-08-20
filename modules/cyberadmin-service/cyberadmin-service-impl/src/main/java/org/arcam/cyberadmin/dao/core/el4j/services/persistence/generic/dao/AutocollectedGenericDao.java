/*
 * AutocollectedGenericDao
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;


/**
 * Indicates that a GenericDao implementation should be added as singleton bean to spring
 *  (and in a second step (by the @link {@link DefaultDaoRegistry}) to the DAO Registry). <a>
 *
 *  You can indicate as parameter the desired <code>id</code> for the spring bean. <a>
 *
 *  Requires config that looks something like the following (see sample for more details): <a>
 *
 *  <pre>
 *  1) collect GenericDao implementations with this annotation <br>
 *     &lt;!--  This section scans for DAOs annotated with @AutocollectedGenericDao that should be
 *     added to the spring application context (as beans). Later, the DAO Registry
 *     automatically collects these DAOs. --&gt;
 *     &lt;!-- 	The attribute base-packages indicates the packages where we look for DAOs  --&gt;
 *     &lt;context:component-scan use-default-filters="false"
 *     annotation-config="false"
 *     base-package="ch.vd.seven.semis.dao.el4j.apps.keyword.dao"&gt;
 *     &lt;context:include-filter type="annotation"
 *     expression="ch.vd.seven.semis.dao.el4j.services.persistence.generic.dao.AutocollectedGenericDao" /&gt;
 *     &lt;/context:component-scan&gt;
 *
 *  2) set up dao registry <br>
 *     &lt;!-- Allows to register DAOs. Automatically collects all GenericDaos from the application context --&gt;
 *     &lt;bean id="daoRegistry"
 *     class="ch.vd.seven.semis.dao.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry"/&gt;
 *
 *
 *  3) init the session factory on the DAOs <br>
 *     &lt;!-- Inits the session factory in all the GenericDaos registered in the spring application context--&gt;
 *     &lt;bean id="injectionPostProcessor"
 *     class="ch.vd.seven.semis.dao.el4j.services.persistence.hibernate.dao.HibernateSessionFactoryInjectorBeanPostProcessor" /&gt;
 * </pre>
 *
 * @author Philipp Oser (POS)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AutocollectedGenericDao {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any
	 */
	String value() default "";
	
}
