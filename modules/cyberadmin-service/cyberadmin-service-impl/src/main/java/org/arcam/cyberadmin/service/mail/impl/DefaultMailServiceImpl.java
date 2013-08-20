/*
 * DefaultMailServiceImpl.java
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

package org.arcam.cyberadmin.service.mail.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.service.mail.exception.InvalidAttachmentException;
import org.arcam.cyberadmin.service.mail.exception.MissingRecipientException;
import org.arcam.cyberadmin.service.mail.type.MailBuilder;
import org.arcam.cyberadmin.service.mail.type.MailMessage;
import org.arcam.cyberadmin.service.mail.type.MailResource;
import org.arcam.cyberadmin.utils.CyberAdminConstants;
import org.arcam.cyberadmin.utils.DeclarationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Base implementation of {@link MailService}.<br>
 * 
 * Provides basic mail sending service with attachments, but without template.<br>
 * Defines mail sending as 2 phases:<br>
 * - mail validation<br>
 * - mail sending <br>
 * 
 * NOTE: If no templating mechanism provided (as the case of {@link DefaultMailServiceImpl}, they will be sent as mail's
 * attachments with unpredictable names. Subtypes fix this by overriding {@link #constructMessageContent(MailMessage)}.
 * 
 * @author mmn
 * 
 */
@Service("mailService")
@Transactional(readOnly = true)
public class DefaultMailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMailServiceImpl.class);

    private static final String RESOURCE_BUNDLE_MAIL_TEMPLATE = "mail/mailtemplate";
    protected static final String DEFAULT_ENCODING = "utf-8";

    protected String encoding = DEFAULT_ENCODING;

    @Autowired
    protected JavaMailSender mailSender;
    
    @Value("${mail.url.declarationDetail}")
    private String decDetailUrl;
    @Value("${mail.url.accountConfirmation}")
    private String confirmationUrl;
    @Value("${mail.url.earcam}")
    private String eArcamUrl;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Defines mail sending as 2 phases: mail validation and mail sending.
     * 
     * @param message
     *            The mail message to be sent.
     */
    public void send(MailMessage message) {
        try {

            // Do basic mail validation: check mail's recipients and resources existence.
            if (!message.hasRecipient()) {
                throw new MissingRecipientException();
            }
            checkResourceExistence(message.getAttachments());
            for (MailResource resource : message.getAttachments()) {
                if (!resource.getContent().exists()) {
                    throw new InvalidAttachmentException(resource.getContent().getFilename());
                }
            }
            for (MailResource resource : message.getTemplateInlineResources()) {
                if (!resource.getContent().exists()) {
                    throw new InvalidAttachmentException(resource.getContent().getFilename());
                }
            }

            // send mail
            this.mailSender.send(createMimeMessage(message));

        } catch (MailException e) {
            LOGGER.error("Failed to send mail.", e);
            throw e;
        }
    }

    private void checkResourceExistence(List<MailResource> resources) {
        for (MailResource resource : resources) {
            if (!resource.getContent().exists()) {
                throw new InvalidAttachmentException(resource.getContent().getFilename());
            }
        }
    }

    /**
     * Create a new JavaMail MimeMessage instance and copy information from <b>message</b>.
     * 
     * @param message
     *            The mail message to be sent.
     * @return a new MimeMessage instance
     * @throws MailException
     */
    protected final MimeMessage createMimeMessage(MailMessage message) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, getEncoding());

            String text = message.getText();

            if (StringUtils.isNotEmpty(message.getTemplateLocation())) {
                LOGGER.info("Call template engine to set mail content");
                text = constructMessageContent(message);
                helper.setText(text, true);

                // NOTE: MimeMessageHelper requires addInline(...) to be called after setText(...); else, mail readers
                // might not be able to resolve inline references
                // correctly.
                for (MailResource inlineResource : message.getTemplateInlineResources()) {
                        helper.addInline(inlineResource.getName(), inlineResource.getContent());
                    }

            } else {
                helper.setText(text);
            }

            if (message.getSentDate() != null) {
                helper.setSentDate(message.getSentDate());
            }

            if (message.getSubject() != null) {
                helper.setSubject(message.getSubject());
            }

            if (message.getFrom() != null) {
                helper.setFrom(message.getFrom());
            }

            if (message.getTo() != null) {
                helper.setTo(message.getTo());
            }

            if (message.getBcc() != null) {
                helper.setBcc(message.getBcc());
            }

            if (message.getCc() != null) {
                helper.setCc(message.getCc());
            }

            if (message.getReplyTo() != null) {
                helper.setReplyTo(message.getReplyTo());
            }

            for (MailResource attachment : message.getAttachments()) {
                helper.addAttachment(attachment.getName(), attachment.getContent());
            }

        } catch (MessagingException e) {
            LOGGER.error("Error during creating MimeMessage", e);
            throw new MailPreparationException(e);
        }

        return msg;
    }

    /**
     * Override this to return message content (String) produced by mail templating.
     * 
     * @param message
     * @throws MessagingException
     */
    protected String constructMessageContent(MailMessage message) {
        return message.getText();
    }

    @Override
    public String requestEGridEwid(Declaration declaration, String from, Set<String> to) {
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("declaration.locationRequest.subject");
        String bodyTemplate = bundle.getString("declaration.locationRequest.body");
        
        // fill data for the template
        List<String> data = new ArrayList<String>();
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        data.add(MessageFormat.format(decDetailUrl, declaration.getId() + ""));
        addTaxpayerAndBienTaxeInfo(declaration, data);
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, new ArrayList<String>(to));
        
        String message = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String demandDeclaration(Declaration declaration, String from) {
        String to = declaration.getBienTaxe().getAssujetti().getEmailAddress();
        if (StringUtils.isBlank(to)) {
            return null;
        }
        
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("declaration.onDemand.subject");
        String bodyTemplate = bundle.getString("declaration.onDemand.body");

        // fill data for the template
        List<String> data = new ArrayList<String>();
        
        DateFormat df = new SimpleDateFormat(CyberAdminConstants.DATE_PATTERN);
        data.add(DeclarationUtils.getDisplayedFiscalePeriod(declaration));
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        data.add(MessageFormat.format(decDetailUrl, declaration.getId() + ""));
        data.add(df.format(declaration.getDueDate()));
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(to));
        
        String message = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String demandSupport(Declaration declaration, String message, String from, Set<String> to) {
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("declaration.communeSignaler.subject");
        String bodyTemplate = bundle.getString("declaration.communeSignaler.body");
        
        // fill data for the template
        List<String> data = new ArrayList<String>();
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        data.add(MessageFormat.format(decDetailUrl, declaration.getId() + ""));
        
        data.add(StringUtils.defaultString(message));
        data.add(StringUtils.defaultString(declaration.getDenomination()));
        data.add(DeclarationUtils.getDisplayedFiscalePeriod(declaration));
        addTaxpayerAndBienTaxeInfo(declaration, data);
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, new ArrayList<String>(to));
        
        String content = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, content));
        return content;
    }

    @Override
    public String rejectDeclaration(Declaration declaration, String from, Set<String> to, String motivation) {
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("declaration.rejection.subject");
        String bodyTemplate = bundle.getString("declaration.rejection.body");
        
        // fill data for the template
        List<String> data = new ArrayList<String>();
        
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        data.add(MessageFormat.format(decDetailUrl, declaration.getId() + ""));
        
        data.add(StringUtils.defaultString(motivation));
        data.add(StringUtils.defaultString(declaration.getDenomination()));
        data.add(DeclarationUtils.getDisplayedFiscalePeriod(declaration));
        addTaxpayerAndBienTaxeInfo(declaration, data);
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, new ArrayList<String>(to));
        
        String message = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    private void addTaxpayerAndBienTaxeInfo(Declaration declaration, List<String> data) {
        // taxpayer info
        Assujetti assujetti = declaration.getBienTaxe().getAssujetti();
        data.add(StringUtils.defaultString(assujetti.getPerson().getNom()));
        data.add(StringUtils.defaultString(assujetti.getPerson().getPrenom()));
        data.add(StringUtils.defaultString(assujetti.getPerson().getOrganisation()));
        data.add(StringUtils.defaultString(assujetti.getAdresse().getAdresse()));
        data.add(StringUtils.defaultString(assujetti.getAdresse().getRue()));
        data.add(StringUtils.defaultString(assujetti.getAdresse().getNo()));
        data.add(StringUtils.defaultString(assujetti.getAdresse().getNpa()));
        data.add(StringUtils.defaultString(assujetti.getAdresse().getLocalite()));
        
        // bien taxe info
        BienTaxe bienTaxe = declaration.getBienTaxe();
        data.add(StringUtils.defaultString(bienTaxe.getEtablissement()));
        data.add(StringUtils.defaultString(bienTaxe.getAdresse().getAdresse()));
        data.add(StringUtils.defaultString(bienTaxe.getAdresse().getRue()));
        data.add(StringUtils.defaultString(bienTaxe.getAdresse().getNo()));
        data.add(StringUtils.defaultString(bienTaxe.getAdresse().getNpa()));
        data.add(StringUtils.defaultString(bienTaxe.getAdresse().getLocalite()));
    }

    @Override
    public String remindOverdueDeclaration(Declaration declaration, String from) {
        String to = declaration.getBienTaxe().getAssujetti().getEmailAddress();
        if (StringUtils.isBlank(to)) {
            return null;
        }
        
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("declaration.onRemind.subject");
        String bodyTemplate = bundle.getString("declaration.onRemind.body");

        // fill data for the template
        List<String> data = new ArrayList<String>();
        
        DateFormat df = new SimpleDateFormat(CyberAdminConstants.DATE_PATTERN);
        data.add(DeclarationUtils.getDisplayedFiscalePeriod(declaration));
        data.add(df.format(declaration.getDueDate()));
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        data.add(MessageFormat.format(decDetailUrl, declaration.getId() + ""));
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(to));
        
        String message = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String newPassword(User user, String from, String password) {
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("user.forgotPassword.subject");
        String bodyTemplate = bundle.getString("user.forgotPassword.body");
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(user.getEmail()));
        
        String message = MessageFormat.format(bodyTemplate, password);
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String confirmRegistration(Assujetti assujetti, String from) {
        String to = assujetti.getEmailAddress();
        if (StringUtils.isBlank(to)) {
            return null;
        }
        
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("user.accountConfirmation.subject");
        String bodyTemplate = bundle.getString("user.accountConfirmation.body");
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(to));
        // Format confirmationUrl with parameters before send.
        // We should convert the id to string before formatting to prevent it automatically puts the grouping separator.
        String url = MessageFormat.format(confirmationUrl, assujetti.getUser().getId() + "");
        String message = MessageFormat.format(bodyTemplate, url);
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String informValidatedAccount(Assujetti assujetti, String from) {
        String to = assujetti.getEmailAddress();
        if (StringUtils.isBlank(to)) {
            return null;
        }
        
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("user.validationNotification.subject");
        String bodyTemplate = bundle.getString("user.validationNotification.body");
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(to));
        
        String message = MessageFormat.format(bodyTemplate, eArcamUrl);
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

    @Override
    public String enrolAccount(Assujetti assujetti, String from, String password) {
        String to = assujetti.getEmailAddress();
        if (StringUtils.isBlank(to)) {
            return null;
        }
        
        ResourceBundle bundle = ResourceBundle
                .getBundle(RESOURCE_BUNDLE_MAIL_TEMPLATE, LocaleContextHolder.getLocale());
        String subject = bundle.getString("user.enrolment.subject");
        String bodyTemplate = bundle.getString("user.enrolment.body");
        
        // fill data for the template
        List<String> data = new ArrayList<String>();
        data.add(eArcamUrl);
        data.add(assujetti.getUser().getUsername());
        data.add(password);
        
        Map<RecipientType, List<String>> recipients = new HashMap<RecipientType, List<String>>();
        recipients.put(RecipientType.TO, Arrays.asList(to));
        
        String message = MessageFormat.format(bodyTemplate, (Object[]) data.toArray(new String[data.size()]));
        send(MailBuilder.createMail(from, subject, recipients, message));
        return message;
    }

}
