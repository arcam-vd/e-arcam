/*
 * MailBuilder.java
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

import java.util.List;
import java.util.Map;

import javax.mail.Message.RecipientType;

import org.apache.commons.collections.MapUtils;

/**
 * Mail builder that creates {@link MailMessage} for simple to complex purpose.<br>
 * 
 * @author dvl
 */
public class MailBuilder {

    protected MailBuilder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Simplest template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param recipients
     *            Map Recipient type with addresses (String).
     */
    private static MailMessage createMail(String from, String subject, Map<RecipientType, List<String>> recipients) {
        MailMessage message = new MailMessage();

        message.setFrom(from);
        message.setSubject(subject);

        if (!MapUtils.isEmpty(recipients)) {
            message.setTo(getRecipientsByType(recipients, RecipientType.TO));
            message.setCc(getRecipientsByType(recipients, RecipientType.CC));
            message.setBcc(getRecipientsByType(recipients, RecipientType.BCC));
        }

        return message;
    }

    private static String[] getRecipientsByType(Map<RecipientType, List<String>> recipients, RecipientType type) {
        if (recipients.containsKey(type)) {
            List<String> list = recipients.get(type);
            if (list != null) {
                return list.toArray(new String[list.size()]);
            }
        }
        return new String[0];
    }

    /**
     * Template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param plainTextContent
     *            Mail content as plaintext.
     */
    public static MailMessage createMail(String from, String subject, Map<RecipientType, List<String>> recipients,
            String plainTextContent) {
        MailMessage message = createMail(from, subject, recipients);
        message.setText(plainTextContent);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage} with attachments.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param plainTextContent
     *            Mail content as plaintext.
     * @param attachments
     *            Mail attachments.
     * @return
     */
    public static MailMessage createMail(String from, String subject, Map<RecipientType, List<String>> recipients,
            String plainTextContent, List<MailResource> attachments) {
        MailMessage message = createMail(from, subject, recipients, plainTextContent);
        message.setAttachments(attachments);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param replyTo
     *            Address that receives reply.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param plainTextContent
     *            Mail content as plaintext.
     */
    public static MailMessage createMail(String from, String subject, String replyTo,
            Map<RecipientType, List<String>> recipients, String plainTextContent) {
        MailMessage message = createMail(from, subject, recipients, plainTextContent);
        message.setReplyTo(replyTo);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param replyTo
     *            Address that receives reply.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param plainTextContent
     *            Mail content as plaintext.
     * @param attachments
     *            Mail attachments.
     */
    public static MailMessage createMail(String from, String subject, String replyTo,
            Map<RecipientType, List<String>> recipients, String plainTextContent, List<MailResource> attachments) {
        MailMessage message = createMail(from, subject, replyTo, recipients, plainTextContent);
        message.setAttachments(attachments);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage} with templating.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param classPathTemplateLocation
     *            Classpath location of template file.
     * @param templateModel
     *            Model objects that binded with template file.
     * @param templateInlineResources
     *            Resource that binded with template file.
     */
    public static MailMessage createMail(String from, String subject, Map<RecipientType, List<String>> recipients,
            String classPathTemplateLocation, Map<String, Object> templateModel,
            List<MailResource> templateInlineResources) {
        MailMessage message = createMail(from, subject, recipients);
        message.setTemplateLocation(classPathTemplateLocation);
        message.setTemplateModel(templateModel);
        message.setTemplateInlineResources(templateInlineResources);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage} with templating and attachments.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param classPathTemplateLocation
     *            Classpath location of template file.
     * @param templateModel
     *            Model objects that binded with template file.
     * @param templateInlineResources
     *            Resource that binded with template file.
     * @param attachments
     *            Mail attachments.
     */
    public static MailMessage createMail(String from, String subject, Map<RecipientType, List<String>> recipients,
            String classPathTemplateLocation, Map<String, Object> templateModel,
            List<MailResource> templateInlineResources, List<MailResource> attachments) {
        MailMessage message = createMail(from, subject, recipients, classPathTemplateLocation, templateModel,
                templateInlineResources);
        message.setAttachments(attachments);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param replyTo
     *            Address that receives reply.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param classPathTemplateLocation
     *            Classpath location of template file.
     * @param templateModel
     *            Model objects that binded with template file.
     * @param templateInlineResources
     *            Resource that binded with template file.
     */
    public static MailMessage createMail(String from, String subject, String replyTo,
            Map<RecipientType, List<String>> recipients, String classPathTemplateLocation,
            Map<String, Object> templateModel, List<MailResource> templateInlineResources) {
        MailMessage message = createMail(from, subject, recipients, classPathTemplateLocation, templateModel,
                templateInlineResources);
        message.setReplyTo(replyTo);
        return message;
    }

    /**
     * Template API to create a {@link MailMessage}.
     * 
     * @param from
     *            Sender address.
     * @param subject
     *            Mail subject.
     * @param replyTo
     *            Address that receives reply.
     * @param recipients
     *            Map Recipient type with addresses (String).
     * @param classPathTemplateLocation
     *            Classpath location of template file.
     * @param templateModel
     *            Model objects that binded with template file.
     * @param templateInlineResources
     *            Resource that binded with template file.
     * @param attachments
     *            Mail attachments.
     */
    // CHECKSTYLE:OFF ParameterNumber
    public static MailMessage createMail(String from, String subject, String replyTo,
            Map<RecipientType, List<String>> recipients, String classPathTemplateLocation,
            Map<String, Object> templateModel, List<MailResource> templateInlineResources,
            List<MailResource> attachments) {
        MailMessage message = createMail(from, subject, recipients, classPathTemplateLocation, templateModel,
                templateInlineResources, attachments);
        message.setReplyTo(replyTo);
        return message;
    }
    // CHECKSTYLE:ON ParameterNumber
    
}
