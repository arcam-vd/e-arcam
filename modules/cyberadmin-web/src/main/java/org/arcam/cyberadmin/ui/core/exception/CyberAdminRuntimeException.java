/*
 * CyberAdminRuntimeException.java
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

package org.arcam.cyberadmin.ui.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author mmn
 *
 */
public class CyberAdminRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CyberAdminRuntimeException() {
        super();
    }

    public CyberAdminRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyberAdminRuntimeException(String message) {
        super(message);
    }

    public CyberAdminRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Get the full stack trace for the exception.
     * 
     * @param ex
     *            Exception to get stack trace.
     * @return
     */
    public static String getStackTrace(Throwable ex) {
        // Create a StringWriter and a PrintWriter both of these object
        // will be used to convert the data in the stack trace to a string.
        //
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        //
        // Instead of writing the stack trace in the console we write it
        // to the PrintWriter, to get the stack trace message we then call
        // the toString() method of StringWriter.
        //
        ex.printStackTrace(pw);

        return sw.toString();
    }

}
