/*
 * CyberAdminExceptionHandlerBean
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

package org.arcam.cyberadmin.ui.bean.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Contains all messages to be displayed on error page after an exception occurs.
 * Using flash scope does not work when error.xhtml page is in a different directory with other pages.
 * @author dtl
 *
 */
@ManagedBean(name = "cyberAdminExceptionHandlerBean")
@SessionScoped
public class CyberAdminExceptionHandlerBean {
    private Throwable errorException;
    private Class<?> exceptionType;
    private String errorMsg;
    private String requestUri;
    private int errorStatusCode;
    
    // To use this bean on error page or using the default requestScope bean.
    private boolean active = false;
    
    public static CyberAdminExceptionHandlerBean newInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().evaluateExpressionGet(facesContext, "#{cyberAdminExceptionHandlerBean}",
                CyberAdminExceptionHandlerBean.class);
    }
    
    public Throwable getErrorException() {
        return errorException;
    }
    public void setErrorException(Throwable errorException) {
        this.errorException = errorException;
    }

    public Class<?> getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Class<?> exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public int getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(int errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public boolean isActive() {
        boolean referredActive = active;
        // Reset the active value to not use this bean again when go back to error page.
        if (active) {
            active = false;
        }
        return referredActive;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
