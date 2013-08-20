/*
 * CyberadminExceptionHandler.java
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

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.arcam.cyberadmin.ui.bean.common.CyberAdminExceptionHandlerBean;
import org.arcam.cyberadmin.ui.core.utils.AjaxUtils;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized place for hanlding exceptions in IRIS.
 * 
 * @author phd
 * 
 */
public class CyberadminExceptionHandler extends ExceptionHandlerWrapper {

    private static final String ERROR_XHTML = "/error.xhtml";

    private static final Logger LOGGER = LoggerFactory.getLogger(CyberadminExceptionHandler.class);

    private ExceptionHandler wrapped;

    public CyberadminExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        FacesContext facesContext = FacesUtils.getFacesContext();
        if (facesContext.getPartialViewContext().isAjaxRequest()) {
            Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents()
                    .iterator();
            if (unhandledExceptionQueuedEvents.hasNext()) {

                // Unwraps the first exception.
                Throwable error = unhandledExceptionQueuedEvents.next().getContext().getException();
                Throwable rootCause = ExceptionUtils.getRootCause(error);
                if (rootCause == null) {
                    rootCause = error;
                }

                unhandledExceptionQueuedEvents.remove();

                // Logs the exception to server log like as in a normal synchronous HTTP 500 error page response.
                facesContext.getExternalContext().log("Exception occured during JSF request", rootCause);

                // Sets the necessary servlet request attributes that can be used later for error displaying.
                final HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
                LOGGER.error("Exception occured during JSF request", rootCause);

                if (rootCause instanceof ViewExpiredException || rootCause instanceof RuntimeException) {
                    // Initialize the instance of exception handler bean to manage all the message to be shown to user
                    // on error page.
                    CyberAdminExceptionHandlerBean exceptionBean = CyberAdminExceptionHandlerBean.newInstance();
                    
                    // Add error message values.
                    exceptionBean.setActive(true);
                    exceptionBean.setErrorException(rootCause);
                    exceptionBean.setExceptionType(rootCause.getClass());
                    exceptionBean.setErrorMsg(rootCause.getMessage());
                    exceptionBean.setRequestUri(request.getRequestURI());
                    exceptionBean.setErrorStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    
                    AjaxUtils.redirect(request, (HttpServletResponse) facesContext.getExternalContext().getResponse(),
                            ERROR_XHTML);
                    facesContext.responseComplete();
                } else {
                    FacesUtils.addError(rootCause);
                }

                while (unhandledExceptionQueuedEvents.hasNext()) {
                    // Any remaining unhandled exceptions will be swallowed. Only the first one is relevant.
                    unhandledExceptionQueuedEvents.next();
                    unhandledExceptionQueuedEvents.remove();
                }
            }
        }
        getWrapped().handle();
    }
}
