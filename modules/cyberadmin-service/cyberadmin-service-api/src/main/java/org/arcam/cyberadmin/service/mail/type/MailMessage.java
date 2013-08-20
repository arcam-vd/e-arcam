/*
 * MailMessage.java
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.ExtendedModelMap;

/**
 * Class defines mail messages to be used. This is simply an extension of
 * {@link SimpleMailMessage} with attributes to support JavaMail MIME message.
 * 
 * @author dvl
 * 
 */
public class MailMessage extends SimpleMailMessage {

    private static final long serialVersionUID = 1L;

    /**
     * Classpath location of the template.
     */
    private String templateLocation;

    /**
     * When {@link #templateLocation} is defined, it can be used as a mail template whose model is defined here.
     */
    private Map<String, Object> templateModel = new HashMap<String, Object>();

    /**
     * List needed resources (e.g. image) to be rendered within the mail template defined by 
     * {@link #templateLocation}<br>
     */
    private List<MailResource> templateInlineResources = new LinkedList<MailResource>();

    /**
     * List of attachments to be sent (map from attachment names and the corresponding ({@link MailResource})).<br>
     */
    private List<MailResource> attachments = new LinkedList<MailResource>();

    /**
     * Empty constructor.
     */
    public MailMessage() {
        super();
    }

    /**
     * Constructor from a simple template message.
     * 
     * @param original
     */
    public MailMessage(MailMessage original) {
        super(original);
        this.templateLocation = original.templateLocation;
        this.templateModel = original.templateModel;
        this.attachments = original.attachments;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public Map<String, Object> getTemplateModel() {
        return templateModel;
    }

    public void setTemplateModel(Map<String, Object> templateModel) {
        this.templateModel = new ExtendedModelMap().addAllAttributes(templateModel);
    }

    public List<MailResource> getAttachments() {
        return attachments;
    }

    /**
     * Null-value attachments are converted to an empty list.
     * 
     * @param attachments
     */
    public void setAttachments(List<MailResource> attachments) {
        if (attachments == null) {
            this.attachments = new LinkedList<MailResource>();
        } else {
            this.attachments = attachments;
        }
    }

    public List<MailResource> getTemplateInlineResources() {
        return templateInlineResources;
    }

    /**
     * Null-value templateInlineResources are converted to an empty list.
     * 
     * @param templateInlineResources
     */
    public void setTemplateInlineResources(List<MailResource> templateInlineResources) {
        if (templateInlineResources == null) {
            this.templateInlineResources = new LinkedList<MailResource>();
        } else {
            this.templateInlineResources = templateInlineResources;
        }
    }

    /**
     * Check if any recipients specified in this mail.
     * 
     * @return boolean
     */
    public boolean hasRecipient() {
        String[] to = this.getTo();
        String[] cc = this.getCc();
        String[] bcc = this.getBcc();

        return !(ArrayUtils.isEmpty(to) && ArrayUtils.isEmpty(cc) && ArrayUtils.isEmpty(bcc));
    }

}
