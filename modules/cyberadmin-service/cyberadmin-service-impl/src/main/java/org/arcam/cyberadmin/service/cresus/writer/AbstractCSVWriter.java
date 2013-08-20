/*
 * AbstractCSVWriter.java
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

package org.arcam.cyberadmin.service.cresus.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.utils.CresusExportConstant;

/**
 * A class for encoding data as CSV.
 * 
 * @param <T>
 *            the type of the object is going to be exported.
 * 
 * @author mmn
 * 
 */
public abstract class AbstractCSVWriter<T> {
    
    private OutputStreamWriter writer;
    
    public AbstractCSVWriter(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            return;
        }
        
        this.writer = new OutputStreamWriter(outputStream, CresusExportConstant.CHARACTER_ENCODING);
        
        // Write header
        writer.write(StringUtils.join(getHeaders(), CresusExportConstant.FIELD_SEPARATOR_CHAR) 
                + CresusExportConstant.LINE_END_CHARACTER + CresusExportConstant.NEW_LINE_CHARACTER);
    }
    
    protected void writeRecord(String[] data) throws IOException {
        if (writer != null) {
            writer.write(StringUtils.join(data, CresusExportConstant.FIELD_SEPARATOR_CHAR)
                    + CresusExportConstant.LINE_END_CHARACTER + CresusExportConstant.NEW_LINE_CHARACTER);
        }
            
    }
    
    public abstract void writeRecord(T object) throws IOException;
    
    protected abstract String[] getHeaders();
    
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
