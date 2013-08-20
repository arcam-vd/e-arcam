/*
 * ManifestOrderedConfigLocationProvider
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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.arcam.cyberadmin.dao.core.el4j.core.exceptions.BaseRTException;

/**
 * This configuration location provider extracts module dependency information
 * from manifest files.
 *
 * @author Andreas Bur (ABU)
 */
public class ManifestOrderedConfigLocationProvider
	extends AbstractOrderedConfigLocationProvider {
	
	/** A manifest file's path. */
	public static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
	
	/** The name of the configuration section in the manifest file. */
	public static final String CONFIG_SECTION = "el4j-config";
	
	/** The name of the module name attribute. */
	public static final String CONFIG_MODULE = "Module";
	
	/** The name of the configuration files attribute. */
	public static final String CONFIG_FILES = "Files";
	
	/** The name of the module dependencies attribute. */
	public static final String CONFIG_DEPENDENCIES = "Dependencies";
	
	/** Private logger. */
	private static Logger s_logger
		= LoggerFactory.getLogger(ManifestOrderedConfigLocationProvider.class);
	
	/**
	 * The sorted list of configuration locations.
	 */
	protected String[] m_configLocations;
	
	/**
	 * The sorted list of resolved config locations.
	 */
	protected Resource[] m_configLocationResources;
	
	/**
	 * {@inheritDoc}
	 */
	public String[] getConfigLocations() {
		if (m_configLocations == null) {
			m_configLocations = assembleConfigLocations();
		}
		return m_configLocations;
	}
	
	
	/**
	 * @return Returns the list of sorted configuration locations, which are
	 *      extracted from manifest files.
	 */
	protected String[] assembleConfigLocations() {
		String[] configLocations = new String[0];
		try {
			Module[] sortedModules = getSortedModules();
			configLocations = mergeConfigLocations(sortedModules);
			
		} catch (IOException ioe) {
			s_logger.error("Error while loading module structure from manifest "
					+ "files. Fall back to the default resource pattern "
					+ "resolver strategy. Correct order of configuration files "
					+ "is no longer guaranteed!", ioe);
		}
		return configLocations;
	}
	
	/**
	 * @return Returns a list of all manifest files which are on the classpath.
	 *
	 * @throws IOException
	 *      If an I/O error occurs.
	 */
	protected URL[] getManifestFiles() throws IOException {
		List<URL> result = new ArrayList<URL>();
		ClassLoader cl = ClassUtils.getDefaultClassLoader();
		Enumeration<URL> urlEnum = cl.getResources(MANIFEST_FILE);
		
		while (urlEnum.hasMoreElements()) {
			result.add(urlEnum.nextElement());
		}
		
		return (URL[]) result.toArray(new URL[result.size()]);
	}
	
	/**
	 * @return Returns an unordered list of modules that are specified in the
	 *      manifest files that are on the classpath.
	 *
	 * @throws IOException
	 *      If an I/O error occurs.
	 */
	protected Module[] createModules() throws IOException {
		List<Module> modules = new ArrayList<Module>();
		URL[] urls = getManifestFiles();
		for (int i = 0; i < urls.length; i++) {
			Module m = createModuleFromManifest(urls[i]);
			if (m != null) {
				modules.add(m);
			}
		}
		return (Module[]) modules.toArray(new Module[modules.size()]);
	}
	
	/**
	 * Creates a new module instance, getting the needed information from a
	 * manifest file.
	 *
	 * @param url
	 *      The manifest file's URL.
	 *
	 * @return Returns a freshly created module containing the information
	 *      specified in the given manifest file.
	 *
	 * @throws IOException
	 *      If an I/O error occurs.
	 */
	protected Module createModuleFromManifest(URL url) throws IOException {
		InputStream is = null;
		Module m = null;
		try {
			is = url.openStream();
			Manifest manifest = new Manifest(is);
			
			Attributes config = manifest.getAttributes(CONFIG_SECTION);
			if (config != null) {
				String module = (String) config.getValue(CONFIG_MODULE);
				String files = (String) config.getValue(CONFIG_FILES);
				String dependencies = (String) config.getValue(
						CONFIG_DEPENDENCIES);
				
				if (!StringUtils.hasText(module)) {
					throw new BaseRTException(
							"Malformed manifest file [" + url.getFile() + "]. "
							+ "Missing or wrongly used attribute '"
							+ CONFIG_MODULE + "' in section '" + CONFIG_SECTION
							+ "'");
				}
				if (!StringUtils.hasText(files)) {
					s_logger.info("Attribute '" + CONFIG_FILES + "' not set ["
							+ url.getFile() + "]");
					files = "";
				}
				if (!StringUtils.hasText(dependencies)) {
					s_logger.info("Attribute '" + CONFIG_DEPENDENCIES
							+ "' not set [" + url.getFile() + "]");
					dependencies = "";
				}
				
				// skip modules that are not properly decorated
				if (!module.equals("null")) {
					String moduleLocation = url.getFile();
					moduleLocation = moduleLocation.substring(
						0, moduleLocation.length() - MANIFEST_FILE.length());
					
					m = new Module(module, moduleLocation);
					m.addAllConfigFiles(files);
					m.addAllDependencies(dependencies);
				}
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return m;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Resource[] getConfigLocationResources() {
		if (m_configLocationResources == null) {
			m_configLocationResources = assembleConfigLocationResources();
		}
		return m_configLocationResources;
	}

	/**
	 * @return Returns the assembled configuration location resources.
	 */
	protected Resource[] assembleConfigLocationResources() {
		Resource[] configLocationResources = new Resource[0];
		try {
			Module[] sortedModules = getSortedModules();
			configLocationResources
				= mergeConfigLocationResources(sortedModules);
			
		} catch (IOException ioe) {
			s_logger.error("Error while loading module structure from manifest "
					+ "files. Fall back to the default resource pattern "
					+ "resolver strategy. Correct order of configuration file "
					+ "resources is no longer guaranteed!", ioe);
		}
		return configLocationResources;
	}
}
