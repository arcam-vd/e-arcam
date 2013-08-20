/*
 * EntityDetectorAnnotationSessionFactoryBean
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.entityfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.Entity;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.StringUtils;


/**
 * Extends <a
 * href="http://www.springframework.org/docs/api/org/springframework/orm/hibernate3/annotation/AnnotationSessionFactoryBean.html"
 * target="_new">AnnotationSessionFactoryBean</a>. <br>
 *
 * Detects all classes that have a \@Entity annotation under a given parent package
 * (indicated with the property <code>autoDetectEntityPackage</code>). <a>
 *
 *
 * Source of the idea: secutix project
 *
 * @svnLink $Revision: 3874 $;$Date: 2009-08-04 19:25:40 +0700 (Tue, 04 Aug 2009) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_7/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/entityfinder/EntityDetectorAnnotationSessionFactoryBean.java $
 */
public class EntityDetectorAnnotationSessionFactoryBean extends AnnotationSessionFactoryBean
	implements InitializingBean {

	private static final Logger s_logger =
		LoggerFactory.getLogger(EntityDetectorAnnotationSessionFactoryBean.class);

	/**
	 * Package name for given set of entities.
	 */
	private String autoDetectEntityPackage[];

	/**
	 * To simplify testing (there is not access to parent
	 *  field of same name)
	 */
	private Class<?>[] localAnnotatedClasses;
	
	/**
	 * Is full support for the JPA enabled.
	 */
	private boolean jpaFullSupportEnabled;

	/**
	 * Sets the 1 or n packages in which the entities are defined
	 *  Sample package: "org.hibernate.tests.entities"
	 *
	 * @param pack
	 *            The parent package name that contains the target entities
	 *            	(as strings)
	 */
	public void setAutoDetectEntityPackage(String... pack) {
		autoDetectEntityPackage = pack;
	}

	/**
	 * @return Returns the package prefixes in which we do the auto detection of entities.
	 */
	protected String[] getAutoDetectEntityPackage() {
		return autoDetectEntityPackage;
	}
	
	/**
	 * Specify the names of annotated packages, for which (including all
	 * sub packages) package-level JDK 1.5+ annotation metadata will be read.
	 * 
	 * @param annotatedPackages    a list of annotated packages
	 */
	public void setAutoDetectAnnotatedPackages(String[] annotatedPackages) {
		final Package[] packages = Package.getPackages();
		
		HashSet<String> detectedAnnotatedPackages = new HashSet<String>();
		for (String prefix : annotatedPackages) {
			for (Package p : packages) {
				if (p.getName().startsWith(prefix)) {
					detectedAnnotatedPackages.add(p.getName());
				}
			}
		}
		setAnnotatedPackages(detectedAnnotatedPackages.toArray(new String[0]));
	}

	/**
	 * Explicitly specify annotated entity classes.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setAnnotatedClasses(Class[] annotatedClasses) {
		ArrayList<Class> classes;
		if (!ArrayUtils.isEmpty(annotatedClasses)) {
			classes = new ArrayList<Class>(Arrays.asList(annotatedClasses));
		} else {
			classes = new ArrayList<Class>();
		}
		localAnnotatedClasses = (Class[]) classes.toArray(new Class[classes.size()]);
		super.setAnnotatedClasses(localAnnotatedClasses);
	}

	/**
	 * Just to have them available for tests
	 * @return
	 */
	public Class<?>[] getAnnotatedClasses() {
		return localAnnotatedClasses;
	}
	
	/**
	 * Set the enabled flag for the full JPA support.
	 * @param enabled
	 */
	public void setJpaFullSupportEnabled(boolean enabled) {
		jpaFullSupportEnabled = enabled;
	}
	
	/**
	 * @return if the full Java Persistence API support is enabled.
	 */
	public boolean isJpaFullSupportEnabled() {
		return jpaFullSupportEnabled;
	}

	/**
	 * really do the searching.
	 */
	public void afterPropertiesSet() throws Exception {
		if (getAutoDetectEntityPackage() != null) {
			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();;
			try {
				ClassLocator cl = new ClassLocator(getAutoDetectEntityPackage());
				for (ClassLocation loc : cl.getAllClassLocations()) {
					Class<?> clazz = Class.forName(loc.getClassName());
					Entity isEntity = (Entity) clazz.getAnnotation(Entity.class);
					if (isEntity != null) {
						classes.add(clazz);
						if (s_logger.isDebugEnabled()) {
							s_logger.debug("Adding entity " + clazz);
						}
					}
				}

				s_logger.debug("all detected hibernate entities"+
						StringUtils.arrayToCommaDelimitedString(classes.toArray()));

				// merge existing classes and new classes
				if (localAnnotatedClasses != null) {
					classes.addAll(Arrays.asList(localAnnotatedClasses));
				}

				localAnnotatedClasses = (Class[]) classes.toArray(new Class[classes.size()]);
				s_logger.debug("number of classes detected:"+localAnnotatedClasses.length);
				super.setAnnotatedClasses(localAnnotatedClasses);

			} catch (Exception e) {
				s_logger.error(e.toString());
				throw new RuntimeException(e);
			}


		}
		super.afterPropertiesSet();
	}
	
	/** {@inheritDoc} */
	protected SessionFactory newSessionFactory(Configuration config) throws HibernateException {
		if (jpaFullSupportEnabled) {
            throw new IllegalArgumentException(
                    "jpaFullSupportEnabled is not supported in this lightweight version which was modified for CWS");
		} else {
			return super.newSessionFactory(config);
		}
	}
	
}
