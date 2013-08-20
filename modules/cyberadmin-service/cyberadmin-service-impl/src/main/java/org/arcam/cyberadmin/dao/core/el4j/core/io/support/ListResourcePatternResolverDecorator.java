/*
 * ListResourcePatternResolverDecorator
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
 package org.arcam.cyberadmin.dao.core.el4j.core.io.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.arcam.cyberadmin.dao.core.el4j.services.monitoring.notification.CoreNotificationHelper;


/**
 * This class resolves resources using a list of resource names to preserve
 * a specific order. A request is delegated to another
 * {@link org.springframework.core.io.support.ResourcePatternResolver}, if there
 * are no resources found by this class. Hence the list of resource names does
 * not have to be exhaustive.
 *
 * @author Andreas Bur (ABU)
 */
public class ListResourcePatternResolverDecorator
	implements ResourcePatternResolver {

	/** The static logger. */
	private static Logger s_logger = LoggerFactory.getLogger(
			ListResourcePatternResolverDecorator.class);
	
	/** The (ordered) list of configuration locations. */
	private final String[] m_configLocations;
	
	/** The (ordered) list of config location resources. */
	private final Resource[] m_configLocationResources;
	
	/**
	 * The resource pattern resolver, where unsatisfied requests are delegated
	 * to.
	 */
	private ResourcePatternResolver m_patternResolver;
	
	/** A path matcher providing a specific wildcard notation. */
	private PathMatcher m_pathMatcher;
	
	/**
	 * Whether to merge resources hold in the location list should be merged
	 * with resources looked up in the file system.
	 */
	private boolean m_mergeWithOuterResources = false;
	
	/**
	 * Indicates if the most specific resource should be the last resource
	 * in the fetched resource array. If its value is set to <code>true</code>
	 * and only one resource is requested the least specific resource will be
	 * returned. Default is set to <code>false</code>.
	 */
	private boolean m_mostSpecificResourceLast = false;

	/**
	 * Creates a new instance using a {@link
	 * PathMatchingResourcePatternResolver} to delegate unresolved requests to
	 * and an {@link AntPathMatcher} to interprete wildcard notations.
	 *
	 * @param locationProvider
	 *      The configuration location provider to get the locations from.
	 */
	public ListResourcePatternResolverDecorator(
			ConfigLocationProvider locationProvider) {
		
		this(locationProvider, new PathMatchingResourcePatternResolver(),
				new AntPathMatcher());
	}
	
	/**
	 * Creates a new instance using a {@link AntPathMatcher} to interprete
	 * wildcard notations.
	 *
	 * @param locationProvider
	 *            The configuration location provider to get the locations from.
	 * @param patternResovler
	 *            The pattern resolver to delegate unresolved requests to.
	 */
	public ListResourcePatternResolverDecorator(
		ConfigLocationProvider locationProvider,
		ResourcePatternResolver patternResovler) {
		this(locationProvider, patternResovler, new AntPathMatcher());
	}

	/**
	 * Creates a new instance, that is fully configured through constructor
	 * arguments.
	 *
	 * @param locationProvider
	 *      The configuration location provider to use.
	 *
	 * @param patternResovler
	 *      The pattern resolver to delegate unresolved requests to.
	 *
	 * @param pathMatcher
	 *      The path matcher that interprets a specific wildcard notation.
	 */
	public ListResourcePatternResolverDecorator(
			ConfigLocationProvider locationProvider,
			ResourcePatternResolver patternResovler,
			PathMatcher pathMatcher) {
		
		m_configLocations = locationProvider.getConfigLocations();
		m_configLocationResources
			= locationProvider.getConfigLocationResources();
		
		if (m_configLocations.length != m_configLocationResources.length) {
			CoreNotificationHelper.notifyMisconfiguration(
				"Number of config locations (size = {0}) is not equals the "
				+ "number of config location resources (size = {1})!",
				m_configLocations.length, m_configLocationResources.length);
		}
		
		m_patternResolver = patternResovler;
		m_pathMatcher = pathMatcher;
	}
	
	/**
	 * @return Returns the patternResolver.
	 */
	public ResourcePatternResolver getPatternResolver() {
		return m_patternResolver;
	}

	/**
	 * @param patternResolver Is the patternResolver to set.
	 */
	public void setPatternResolver(ResourcePatternResolver patternResolver) {
		m_patternResolver = patternResolver;
	}

	/**
	 * @return Returns the path matcher that is used to interprete a specific
	 *      wildcard notation.
	 */
	protected PathMatcher getPathMatcher() {
		return m_pathMatcher;
	}

	/**
	 * Sets a specific path matcher that is used to interprete a specific
	 * wildcard notation.
	 *
	 * @param pathMathcher
	 *      The path matcher to set.
	 */
	public void setPathMatcher(PathMatcher pathMathcher) {
		m_pathMatcher = pathMathcher;
	}

	/**
	 * @return Returns whether to merge resources hold in this classe's resource
	 *      list with resources looked up in the file system.
	 */
	public boolean isMergeWithOuterResources() {
		return m_mergeWithOuterResources;
	}

	/**
	 * Sets whether resources that are hold in this classe's resource list have
	 * to be merged with resources looked up in the file system. Resources
	 * that are in the list have precedence.
	 *
	 * @param mergeWithOuterResources
	 *      <code>true</code> to merge file system resources with the ones hold
	 *      in this classe's resource list. <code>false</code> to force
	 *      closed-world assumption.
	 */
	public void setMergeWithOuterResources(boolean mergeWithOuterResources) {
		m_mergeWithOuterResources = mergeWithOuterResources;
	}

	/**
	 * @return Returns the mostSpecificResourceLast.
	 */
	public boolean isMostSpecificResourceLast() {
		return m_mostSpecificResourceLast;
	}

	/**
	 * @param mostSpecificResourceLast Is the mostSpecificResourceLast to set.
	 */
	public void setMostSpecificResourceLast(boolean mostSpecificResourceLast) {
		m_mostSpecificResourceLast = mostSpecificResourceLast;
	}

	/**
	 * {@inheritDoc}
	 */
	public Resource getResource(String location) {
		Resource resource = null;
		if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			resource = findSingleClassPathResource(
				location.substring(CLASSPATH_URL_PREFIX.length()));
		} else if (location.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
			resource = findSingleClassPathResource(
				location.substring(CLASSPATH_ALL_URL_PREFIX.length()));
		}
		
		if (resource == null) {
			resource = delegateResourceLookup(location);
		}
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	public Resource[] getResources(String locationPattern) throws IOException {
		Resource[] resources = null;
		if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
			String pattern = locationPattern.substring(
				CLASSPATH_ALL_URL_PREFIX.length());
			
			if (getPathMatcher().isPattern(pattern)) {
				resources = findAllMatchingResources(pattern);
			} else {
				resources = findAllClassPathResources(pattern);
			}

			if (isMergeWithOuterResources()) {
				try {
					resources = mergeResources(delegateResourcesLookup(locationPattern), resources);
				} catch (IOException ioe) {
					s_logger.error("Couldn't merge configuration locations.",
							ioe);
				}
			}
		} else if (locationPattern.startsWith(CLASSPATH_URL_PREFIX)) {
			String pattern = locationPattern.substring(
				CLASSPATH_URL_PREFIX.length());
			
			Resource singleResource = null;
			if (!getPathMatcher().isPattern(pattern)) {
				// If it is no pattern try to find the resource in
				// in the ordered resource list.
				singleResource = findSingleClassPathResource(pattern);
			}
			
			if (singleResource != null) {
				resources = new Resource[] {singleResource};
			} else {
				s_logger.debug("Ordered resource loading not supported for "
					+ "location pattern '" + locationPattern + "'!");
				resources = delegateResourcesLookup(locationPattern);
			}
		} else {
			resources = delegateResourcesLookup(locationPattern);
		}
		
		// Reverse the array if the most specific resource should be at the
		// end of the array.
		if (isMostSpecificResourceLast()) {
			ArrayUtils.reverse(resources);
		}
		
		return resources;
	}

	/**
	 * Resolves the given location into Resource objects, using delegation.
	 *
	 * @param locationPattern
	 *      The pattern to resolve.
	 *
	 * @return Returns the list with resolved resources.
	 *
	 * @throws IOException
	 *      If an I/O error occurs.
	 */
	protected Resource[] delegateResourcesLookup(String locationPattern)
		throws IOException {
		s_logger.trace("Delegating resources lookup with pattern '"
			+ locationPattern + "'.");
		return m_patternResolver.getResources(locationPattern);
	}
	
	/**
	 * Resolves the given location into one resource, using delegation.
	 *
	 * @param location
	 *      The location to resolve.
	 *
	 * @return Returns the resolved resource.
	 */
	protected Resource delegateResourceLookup(String location) {
		s_logger.trace("Delegating resource lookup for location '"
			+ location + "'.");
		return m_patternResolver.getResource(location);
	}
	
	/**
	 * Finds all resources that match the given location pattern that is
	 * interpreted by the path matcher which this class is configured for.
	 *
	 * @param locationPattern
	 *      The location pattern to match against (contains a particular
	 *      wildcard notation).
	 *
	 * @return Returns a list of resources that matches the given pattern.
	 */
	protected Resource[] findAllMatchingResources(String locationPattern) {
		List<Resource> result = new ArrayList<Resource>();
		for (int i = 0; i < m_configLocations.length; i++) {
			if (getPathMatcher().match(locationPattern, m_configLocations[i])) {
				result.add(m_configLocationResources[i]);
			}
		}
		return (Resource[]) result.toArray(new Resource[result.size()]);
	}
	
	/**
	 * Finds all resources that do not contain any wildcards except the
	 * <code>classpath*</code>.
	 *
	 * @param location
	 *      The location pattern to match resources against (without any
	 *      particular wildcard notation).
	 *
	 * @return Returns a list of resources that match the given pattern.
	 */
	protected Resource[] findAllClassPathResources(String location) {
		List<Resource> result = new ArrayList<Resource>();
		for (int i = 0; i < m_configLocations.length; i++) {
			if (location.equals(m_configLocations[i])) {
				result.add(m_configLocationResources[i]);
			}
		}
		return (Resource[]) result.toArray(new Resource[result.size()]);
	}
	
	/**
	 * @param location Is the looked up resource location.
	 * @return Returns only the first found class path resource.
	 */
	protected Resource findSingleClassPathResource(String location) {
		Resource resource = null;
		try {
			if (isMostSpecificResourceLast()) {
				resource = findLastClassPathResource(location);
			} else {
				resource = findFirstClassPathResource(location);
			}
		} catch (FileNotFoundException e) {
			// Treat a FileNotFoundException differently to avoid stacktraces
			// in setups where the file later gets found by delegation to
			// another resource locator.
			s_logger.debug("Resource not found: " + location, e);
		} catch (IOException e) {
			s_logger.warn("Exception occurred while lookup of single class "
				+ "path resource at location '" + location + "'.", e);
		}
		return resource;
	}
	
	/**
	 * @param location Is the looked up resource location.
	 * @return Returns only the first found class path resource.
	 * @throws IOException On any io problem.
	 */
	protected Resource findFirstClassPathResource(String location) throws IOException {
		Resource resource = null;
		Map<URL, Resource> resourceUrlMap = new LinkedHashMap<URL, Resource>();
		if (isMergeWithOuterResources()) {
			Resource[] resources = delegateResourcesLookup(location);
			for (Resource r : resources) {
				try {
					
					// POS: the next line can throw a FileNotFoundException
					//  when run in a web server (in a servlet context)
					//   But this is no real problem.
					URL url = r.getURL();
					resourceUrlMap.put(url, r);
				} catch (FileNotFoundException fnfe) { }
			}
			
			List<Resource> orderedResources = new ArrayList<Resource>();
			for (int i = 0; i < m_configLocations.length; i++) {
				if (location.equals(m_configLocations[i])) {
					Resource r = m_configLocationResources[i];
					URL url = r.getURL();
					resourceUrlMap.remove(url);
					orderedResources.add(r);
				}
			}
			
			if (resourceUrlMap.size() > 0) {
				resource
					= resourceUrlMap.entrySet().iterator().next().getValue();
			} else if (orderedResources.size() > 0) {
				resource = orderedResources.get(0);
			}
		} else {
			for (int i = 0; i < m_configLocations.length; i++) {
				if (location.equals(m_configLocations[i])) {
					resource = m_configLocationResources[i];
					break;
				}
			}
		}
	
		return resource;
	}
	
	/**
	 * @param location Is the looked up resource location.
	 * @return Returns only the last found class path resource. If flag
	 *         <code>mergeWithOuterResources</code> is set to <code>true</code>
	 *         this method will try to find a resource that is not in the
	 *         config location list.
	 * @throws IOException On any io problem.
	 */
	protected Resource findLastClassPathResource(String location)
		throws IOException {
		Resource resource = null;
		Map<URL, Resource> resourceUrlMap = new LinkedHashMap<URL, Resource>();
		if (isMergeWithOuterResources()) {
			Resource[] resources = delegateResourcesLookup(location);
			for (Resource r : resources) {
				try {
					
					// POS: the next line can throw a FileNotFoundException
					//  when run in a web server (in a servlet context)
					//   But this is no real problem.
					URL url = r.getURL();
					resourceUrlMap.put(url, r);
				} catch (FileNotFoundException fnfe) { }
			}
			
			List<Resource> orderedResources = new ArrayList<Resource>();
			for (int i = m_configLocations.length - 1; i >= 0; i--) {
				if (location.equals(m_configLocations[i])) {
					Resource r = m_configLocationResources[i];
					URL url = r.getURL();
					resourceUrlMap.remove(url);
					orderedResources.add(r);
				}
			}
			
			if (orderedResources.size() > 0) {
				resource = orderedResources.get(0);
			} else if (resourceUrlMap.size() > 0) {
				resource
					= resourceUrlMap.entrySet().iterator().next().getValue();
			}
		} else {
			for (int i = m_configLocations.length - 1;
				i >= 0 && resource == null; i--) {
				if (location.equals(m_configLocations[i])) {
					resource = m_configLocationResources[i];
				}
			}
		}

		return resource;
	}
	
	/**
	 * Merges the two given list of resources with the former having higher
	 * precedence, i.e. they are added to the resulting list's head. Items of
	 * the latter list that are also member of the former are added only once.
	 *
	 * @param former
	 *      A list of resources that has higher precedence.
	 *
	 * @param latter
	 *      A list of resources that has lower precedence. Items that are
	 *      already added by the former list are filtered.
	 *
	 * @return Returns a list with the merged resources, having the former list
	 *      at the resulting list's head. Items of the latter list are ignored,
	 *      if they are already added by the former list.
	 *
	 * @throws IOException
	 *      If an I/O exception occurs.
	 */
	protected Resource[] mergeResources(Resource[] former, Resource[] latter)
		throws IOException {
		
		if (s_logger.isDebugEnabled()) {
			debugArray("former", former);
			debugArray("latter", latter);
		}
		
		List<Resource> result = new ArrayList<Resource>();
		List<URL> urlList = new ArrayList<URL>();
		for (Resource resource : latter) {
			urlList.add(resource.getURL());
		}
		for (Resource resource : former) {
			URL url = resource.getURL();
			if (!urlList.contains(url)) {
				result.add(resource);
			}
		}
		for (Resource resource : latter) {
			result.add(resource);
		}
		return (Resource[]) result.toArray(new Resource[result.size()]);
	}
	
	/**
	 * Debugs the given array.
	 *
	 * @param msg
	 *      An arbitrary message prepended to the array.
	 *
	 * @param array
	 *      The array to debug.
	 */
	protected void debugArray(String msg, Object[] array) {
		String s = StringUtils.arrayToCommaDelimitedString(array);
		s_logger.debug(msg + " [" + s + "]");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ClassLoader getClassLoader() {
		return m_patternResolver.getClassLoader();
	}
}
