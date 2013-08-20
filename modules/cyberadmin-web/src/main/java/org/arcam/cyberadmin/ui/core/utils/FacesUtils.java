/*
 * FacesUtils.java
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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.arcam.cyberadmin.ui.i18n.ErrorsBundle;
import org.arcam.cyberadmin.ui.i18n.HelpBundle;
import org.arcam.cyberadmin.ui.i18n.MessagesBundle;
import org.springframework.util.Assert;

/**
 * 
 * Utilities for working with {@link FacesContext}.
 * 
 * @author phd
 * 
 */
public abstract class FacesUtils {

    /**
     * Adds global error messages to current {@link FacesContext} for the input <code>error</code>.<br/>
     * The implementation will look for the all messages from the error message bundle that were:<br>
     * <ul>
     * <li>either keyed by this <code>error</code>'s qualified name to use as the global one</li>
     * <li>or keyed by this <code>error</code>'s qualified name plus a ".detail" suffix to use as the detail one</li>
     * </ul>
     * 
     * @param error
     * @see #getMessage(String, Object...)
     */
    public static void addError(Throwable error) {
        FacesUtils.getFacesContext().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, getErrorMessage(error.getClass().getName()),
                        getErrorMessage(error.getClass().getName() + ".detail")));
    }

    /**
     * <p>
     * Looks up the {@link FacesMessage} according to the input <code>key</code>,
     * <code>params</code> and <code>severity</code>.
     * </p>
     * 
     * @param key I18N key of the message.
     * @param params Parameters for formating the message.
     * @param severity Severity of the message.
     * @return The {@link FacesMessage} according to the input <code>key</code>, <code>params</code>
     *         and <code>severity</code>.
     */
    public static FacesMessage getI18nMessage(String key, FacesMessage.Severity severity, Object... params) {
        String message = getMessage(key, params);
        FacesMessage result = new FacesMessage(severity, message, null);
        return result;
    }
    
    public static FacesMessage getI18nErrorMessage(String key, FacesMessage.Severity severity, Object... params) {
        String message = getErrorMessage(key, params);
        FacesMessage result = new FacesMessage(severity, message, null);
        return result;
    }
    
    /**
     * Gets i18n message associated with the input <code>key</code> and formats it with the input <code>params</code>.
     * 
     * @param key
     *            The key to be used for looking to the message.
     * @param params
     *            The list of params to be used for formatting the message.
     * @return The i18n message associated with the input <code>key</code> and formatted with the input
     *         <code>params</code>.
     */
    public static String getMessage(String key, Object... params) {
    	return enrichMessage((new MessagesBundle().getString(key)), params);
        //return getMessage(FacesUtils.getLocale(), "/i18n/messages", key, params);
    }

    /**
     * Gets i18n error message associated with the input <code>key</code> and formats it with the input
     * <code>params</code>.
     * 
     * @param key
     *            The key to be used for looking to the message.
     * @param params
     *            The list of params to be used for formatting the message.
     * @return The i18n message associated with the input <code>key</code> and formatted with the input
     *         <code>params</code>.
     */
    public static String getErrorMessage(String key, Object... params) {
    	return enrichMessage((new ErrorsBundle().getString(key)), params);
        //return getMessage(FacesUtils.getLocale(), "/i18n/errors", key, params);
    }
    
    /**
     * Gets i18n helper message associated with the input <code>key</code> and formats it with the input
     * <code>params</code>.
     * 
     * @param key
     *            The key to be used for looking to the message.
     * @param params
     *            The list of params to be used for formatting the message.
     * @return The i18n helper message associated with the input <code>key</code> and formatted with the input
     *         <code>params</code>.
     */
    public static String getHelperMessage(String key, Object... params) {
    	return enrichMessage((new HelpBundle().getString(key)), params);
        //return getMessage(FacesUtils.getLocale(), "/i18n/help", key, params);
    }
    
    
    private static String enrichMessage(String message, final Object... params) {
    	String finalMessage = null;
    	if (params != null) {
            final MessageFormat mf = new MessageFormat(message, FacesUtils.getLocale());
            finalMessage = mf.format(params, new StringBuffer(), null).toString();
        }
        return finalMessage;    	
    }
    

    private static String getMessage(Locale locale, String baseName, String key, final Object... params) {
        String message = null;
        final ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        //final ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, new UTF8Control());
        try {
            message = bundle.getString(key);
        } catch (final MissingResourceException e) {
            message = "?? key " + key + " is not found ??";
        }
        if (params != null) {
            final MessageFormat mf = new MessageFormat(message, locale);
            message = mf.format(params, new StringBuffer(), null).toString();
        }
        return message;
    }

    /**
     * Gets default locale of the current {@link UIViewRoot}.
     * 
     * @return Default locale of the current {@link UIViewRoot}.
     */
    public static Locale getLocale() {
        UIViewRoot viewRoot = FacesUtils.getFacesContext().getViewRoot();
        return viewRoot.getLocale();
    }

    /**
     * Convenient access to the current instance of {@link FacesContext}.
     * 
     * @return The current instance of {@link FacesContext}.
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    /**
     * Get i18n display text for the given enum.
     * 
     * @param enumValue
     * @return
     */
    public static String getI18nEnumLabel(Object enumValue) {
        Assert.isTrue(enumValue.getClass().isEnum(), "The input value must be an enum");
        String key = enumValue.getClass().getSimpleName() + "." + ((Enum<?>) enumValue).name();
        return getMessage(key);
    }

    /**
     * Get i18n display text for the given enum.
     * 
     * @param prefixKey
     * @param enumValue
     * @return
     */
    public static String getI18nEnumLabel(String prefixKey, Object enumValue) {
        Assert.isTrue(enumValue.getClass().isEnum(), "The input value must be an enum");
        String key = prefixKey + "." + ((Enum<?>) enumValue).name();
        return getMessage(key);
    }

    /**
     * <p>
     * Gets a key value during one user session.
     * </p>
     * 
     * @param key
     *            The Key to be get value from user session
     * @return
     */
    public static Object getSessionMapValue(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
    }

    /**
     * <p>
     * Removes the value associated to the <code>key</code> from user's session.
     * </p>
     * 
     * @param key
     *            Key of the value from which the value will be got.
     * @return Value associated with the <code>key</code> from user's session.
     */
    public static Object removeSessionMapValue(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(key);
    }

    /**
     * <p>
     * Sets a key value during one user session.
     * </p>
     * 
     * @param key
     *            The Key to be stored in user session
     * @param value
     *            The Value of the Key to be stored in user session
     */
    public static void setSessionMapValue(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
    }

    /**
     * <p>
     * Gets a specific param value from external context.
     * </p>
     * 
     * @return A parameter value from external context or NULL if no parameter in the context path.
     */
    public static String getRequestParam(final String paramName) {
        return FacesUtils.getFacesContext().getExternalContext().getRequestParameterMap().get(paramName);
    }
    
    /**
     * <p>
     * Check the request is AJAX.
     * </p>
     * @return
     */
    public static boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }
}
