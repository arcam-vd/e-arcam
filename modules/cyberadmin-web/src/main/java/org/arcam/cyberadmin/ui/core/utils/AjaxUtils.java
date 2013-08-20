/*
 * AjaxUtils.java
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

package org.arcam.cyberadmin.ui.core.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Utilities for working with Ajax request.
 * 
 * @author phd
 * 
 */
public abstract class AjaxUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AjaxUtils.class);

    /**
     * Checks whether the incoming <code>request</code> is Ajax or not.
     * 
     * @param request
     *            The request in question.
     * @return <code>true</code> if the incoming request is Ajax, <code>false</code> otherwise.
     */
    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * 
     * Sends redirect to Ajax clients via an Ajaxified message instead of responding with a normal 302 status code
     * (which doesn't work with such clients).
     * 
     * @param request
     *            The incoming Ajax request.
     * @param response
     *            The corresponding response.
     * @param targetUrl
     *            The target url.
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String targetUrl) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version='1.0' encoding='UTF-8'?>");
            sb.append("<partial-response><redirect url=\"").append(request.getContextPath()).append(targetUrl)
                    .append("\"/></partial-response>");
            response.getWriter().print(sb.toString());
            response.flushBuffer();
        } catch (IOException e) {
            LOGGER.error("IOException occured while redirecting Ajax client", e);
            throw new RuntimeException("IOException occured while redirecting Ajax client", e);
        }
    }
}
