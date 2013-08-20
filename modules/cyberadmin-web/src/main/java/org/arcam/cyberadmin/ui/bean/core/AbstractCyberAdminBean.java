/*
 * AbstractCyberAdminBean.java
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

package org.arcam.cyberadmin.ui.bean.core;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.ResponseStateManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.arcam.cyberadmin.dom.core.CyberAdminUserDetail;
import org.arcam.cyberadmin.service.core.ReferenceDataService;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sun.faces.renderkit.RenderKitUtils;

/**
 * Base bean for all managed bean in Cyber Admin application.
 * 
 * @author mmn
 *
 */
public class AbstractCyberAdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @ManagedProperty(value = "#{webConstants}")
    protected WebConstants webConstants;
    
    @ManagedProperty(value = "#{referenceDataService}")
    protected ReferenceDataService referenceDataService;
    
    /**
     * List of global messages that will be rendered on the page. It is stored here to decide if we
     * put a placeholder on top of each page for the messages or not.
     */
    private List<FacesMessage> messages = new ArrayList<FacesMessage>();

    protected String screenUUID;
    
    /**
     * The constructor.
     * 
     */
    public AbstractCyberAdminBean() {
        super();
    }
    
    /**
     * Returns the pageId of this bean. Subclass will override.
     */
    public String getPageId() {
        return "";
    }
    
    /**
     * Checks if this page is a posting back to itself.
     * 
     * @return <code>true</code> if this page is a posting back to itself, <code>false</code> otherwise.
     */
    protected boolean isPostBack() {
        ResponseStateManager mgr = RenderKitUtils.getResponseStateManager(FacesContext.getCurrentInstance(),
                FacesContext.getCurrentInstance().getViewRoot().getRenderKitId());

        return mgr.isPostback(FacesContext.getCurrentInstance());
    }
    
    /**
     * Retrieves the current instance of {@link FacesContext}.
     * 
     * @return The current instance of {@link FacesContext}.
     */
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    /**
     * Getter method for <code> messages </code>.
     * @return Returns the messages.
     */
    public List<FacesMessage> getMessages() {
        return messages;
    }

    /**
     * Setter for attribute <code> messages </code>.
     * @param messages The messages to set.
     */
    public void setMessages(List<FacesMessage> messages) {
        this.messages = messages;
    }

    /**
     * Notify the message to user on client side.
     * 
     * @param severity the severity of this message.
     * @param message the message want to notify.
     * @param params the parameter used to format the message.
     */
    protected void notifyMessage(Severity severity, String message, Object... params) {
        String msg = message;
        if (!ArrayUtils.isEmpty(params) && !StringUtils.isBlank(message)) {
            message = message.replace("'", "''");
            msg = MessageFormat.format(message, params);
        }
        FacesUtils.getFacesContext().addMessage(null, new FacesMessage(severity, msg, msg));
    }
    
    /**
     * Retrieves the default {@link Lifecycle}.
     * 
     * @return The default {@link Lifecycle}.
     */
    protected Lifecycle getLifecycle() {
        LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        return factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    }
    
    public CyberAdminUserDetail getCurrentUser() {
        return (CyberAdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void setWebConstants(WebConstants webConstants) {
        this.webConstants = webConstants;
    }

    public void setReferenceDataService(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }
    
    public TimeZone getCurrentTimezone() {
        return TimeZone.getDefault();
    }

    public String getScreenUUID() {
        if (screenUUID == null) {
            screenUUID = UUID.randomUUID().toString();
        }
        return screenUUID;
    }

    public void setScreenUUID(String screenUUID) {
        this.screenUUID = screenUUID;
    }
    
}
