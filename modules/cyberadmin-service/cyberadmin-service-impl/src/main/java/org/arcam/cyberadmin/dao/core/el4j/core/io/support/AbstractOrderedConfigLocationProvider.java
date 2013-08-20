/*
 * AbstractOrderedConfigLocationProvider
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.Resource;

/**
 * This class simplifies writing ordered configuration location providers.
 *
 * @author Andreas Bur (ABU)
 */
public abstract class AbstractOrderedConfigLocationProvider
	implements ConfigLocationProvider {

	/**
	 * The module sorter that computes the list of modules preserving their
	 * hierarchical constraints.
	 */
	private ModuleSorter m_moduleSorter = new DefaultModuleSorter();

	/**
	 * The sorted list of modules.
	 */
	private Module[] m_sortedModules;

	/**
	 * Sets the module sorter used to compute the list of modules while
	 * preserving their hierarchical constraints. Default sorter is a
	 * {@link DefaultModuleSorter}.
	 *
	 * @param moduleSorter
	 *      The module sorter to use.
	 *
	 * @see ModuleSorter
	 */
	public void setModuleSorter(ModuleSorter moduleSorter) {
		m_moduleSorter = moduleSorter;
	}

	/**
	 * @return Returns the sorted list of modules.
	 * @throws IOException On any io problem while working with modules.
	 */
	protected Module[] getSortedModules() throws IOException {
		if (m_sortedModules == null) {
			Module[] modules = createModules();
			m_sortedModules = sortModules(modules);
		}
		return m_sortedModules;
	}
	
	/**
	 * @return Returns the created list of modules.
	 * @throws IOException On any io problem while module creation.
	 */
	protected abstract Module[] createModules() throws IOException;
	
	/**
	 * Sorts an unordered list of modules using the hierarchical constraints
	 * defined across the modules.
	 *
	 * @param modules
	 *      The list of modules to sort.
	 *
	 * @return Returns an ordered list of modules that fulfill the partial
	 *      order defined by the module's hierarchical constraints.
	 */
	protected Module[] sortModules(Module[] modules) {
		return m_moduleSorter.sortModules(modules);
	}
	
	/**
	 * Merges the configuration locations of the provided list of modules,
	 * preserving the module's order.
	 *
	 * @param modules
	 *      The ordered list of modules which configuration locations has to be
	 *      merged.
	 *
	 * @return Returns the ordered list of configuration locations declared
	 *      by the modules. Most specific location comes first.
	 */
	protected String[] mergeConfigLocations(Module[] modules) {
		List<String> configLocations = new ArrayList<String>();
		for (int i = 0; i < modules.length; i++) {
			configLocations.addAll(modules[i].getConfigFilesAsList());
		}
		
		String[] result = (String[]) configLocations.toArray(
				new String[configLocations.size()]);
		ArrayUtils.reverse(result);
		return result;
	}
	
	/**
	 * Merges the configuration location resources of the provided list of
	 * modules, preserving the module's order.
	 *
	 * @param modules
	 *      The ordered list of modules which configuration location resources
	 *      has to be merged.
	 *
	 * @return Returns the ordered list of configuration location resources
	 *      declared by the modules. Most specific location comes first.
	 */
	protected Resource[] mergeConfigLocationResources(Module[] modules) {
		List<Resource> configLocationResources = new ArrayList<Resource>();
		for (Module module : modules) {
			configLocationResources.addAll(
				module.getConfigFileResourcesAsList());
		}
		Resource[] result = (Resource[]) configLocationResources.toArray(
				new Resource[configLocationResources.size()]);
		ArrayUtils.reverse(result);
		return result;
	}
}
