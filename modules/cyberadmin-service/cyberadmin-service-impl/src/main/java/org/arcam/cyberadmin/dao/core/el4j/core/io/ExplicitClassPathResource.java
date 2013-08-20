/*
 * ExplicitClassPathResource
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
 package org.arcam.cyberadmin.dao.core.el4j.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * This is an explicit class path resource. A normal class path resource has
 * only a path that can point to multiple resources. By using the given url too
 * this resource explicitly points to one resource.
 *
 * @author Martin Zeltner (MZE)
 */
public class ExplicitClassPathResource extends ClassPathResource {
	/**
	 * See {@link #getURL()}.
	 */
	private URL m_url;

	/**
	 * Constructor with an explicit url for the given class path resource.
	 *
	 * @param url
	 *            The explicit url for this class path resource.
	 * @param path
	 *            The path to get this resource from class loader. This path can
	 *            return a different resource than the given url points to.
	 * @param classLoader
	 *            The class loader used to load the given url.
	 */
	public ExplicitClassPathResource(URL url, String path,
		ClassLoader classLoader) {
		super(path, classLoader);
		Assert.notNull(url);
		m_url = url;
	}
	
	/**
	 * @return Returns the explicit url for this class path resource.
	 */
	@Override
	public URL getURL() throws IOException {
		return m_url;
	}
	
	/**
	 * @return Returns the input stream of the given url.
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return m_url.openStream();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		// just to make clear that it is intended to use the method of the superclass
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		// just to make clear that it is intended to use the method of the superclass
		return super.hashCode();
	}
	
}
