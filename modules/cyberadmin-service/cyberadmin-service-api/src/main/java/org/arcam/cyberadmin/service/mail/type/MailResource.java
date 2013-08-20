/*
 * MailResource.java
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

package org.arcam.cyberadmin.service.mail.type;

import java.io.File;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * POJO for mail attachments and inline resources.
 * 
 * @author dvl
 * 
 */
public class MailResource {

    private String name;
    private Resource content;

    /**
     * Basic constructor.
     * 
     * @param name
     * @param resource
     */
    public MailResource(String name, Resource resource) {
        this.name = name;
        this.content = resource;
    }

    /**
     * Create resource from an File.
     * 
     * @param name
     *            This resource name.
     * @param file
     *            File to convert to Resource instance.
     */
    public MailResource(String name, File file) {
        this(name, new FileSystemResource(file));
    }

    /**
     * Create resource by an classpath resource.
     * 
     * @param name
     *            This resource name.
     * @param resourceClasspath
     *            Classpath to a system resource.
     */
    public MailResource(String name, String resourceClasspath) {
        this(name, new ClassPathResource(resourceClasspath));
    }

    /**
     * Create resource given byte array content. Must specify extensionType (e.g. gif, bmp, etc.) to encode the array
     * content.
     * 
     * @param name
     *            This resource name.
     * @param byteArray
     *            Byte-array content.
     * @param extensionType
     *            Extension type to help encoding.
     */
    public MailResource(String name, byte[] byteArray, final String extensionType) {

        this(name, new ByteArrayResource((byte[]) byteArray) {
            @Override
            public String getFilename() throws IllegalStateException {
                return "." + extensionType;
            }
        });
    }

    public String getName() {
        return name;
    }

    public Resource getContent() {
        return content;
    }

}
