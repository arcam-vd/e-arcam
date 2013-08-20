/*
 * CoreNotificationHelper
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
 
package org.arcam.cyberadmin.dao.core.el4j.services.monitoring.notification;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.arcam.cyberadmin.dao.core.el4j.core.exceptions.MisconfigurationRTException;

/**
 * This class is used to notify on events which are core based.
 *
 * @author Martin Zeltner (MZE)
 */
public final class CoreNotificationHelper {
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(CoreNotificationHelper.class);
	
	/**
	 * Hide default constructor.
	 */
	private CoreNotificationHelper() {
	}

	/**
	 * Method to notify that an essential property of a bean is lacking. In
	 * every case a <code>MissconfigurationRTException</code> will be thrown.
	 *
	 * @param propertyName
	 *            Is the name of the lacking property.
	 * @param concernedBean
	 *            Is the bean where the property is missing.
	 * @throws MisconfigurationRTException Will be thrown everytime.
	 */
	public static void notifyLackingEssentialProperty(String propertyName,
			Object concernedBean) throws MisconfigurationRTException {
		assert propertyName != null && propertyName.length() > 0;
		assert concernedBean != null;

		Class<?> beanClass = concernedBean.getClass();
		String beanClassName = beanClass.getName();
		String beanName;
		try {
			Method m = beanClass.getMethod("getBeanName", (Class[]) null);
			beanName = (String) m.invoke(concernedBean, (Object[]) null);
		} catch (Exception e) {
			// Bean name could not be extracted.
			beanName = null;
		}

		String message = "The property '" + propertyName + "' is required! ";
		if (StringUtils.hasText(beanName)) {
			message += "Concerned bean is of class '" + beanClassName
					+ "' and has name '" + beanName + "'.";
		} else {
			message += "Concerned bean is of class '" + beanClassName + "'.";
		}

		notifyMisconfiguration(message);
	}
	
	/**
	 * Method to check if an essential property is not <code>null</code>.
	 *
	 * @param essentialProperty
	 *            Is the property which should not be null.
	 * @param propertyName
	 *            Is the name of the lacking property.
	 * @param concernedBean
	 *            Is the bean where the property is missing.
	 * @throws MisconfigurationRTException
	 *             If essential property is misconfigured.
	 */
	public static void notifyIfEssentialPropertyIsEmpty(
		Object essentialProperty, String propertyName, Object concernedBean)
		throws MisconfigurationRTException {
		if (essentialProperty == null) {
			notifyLackingEssentialProperty(propertyName, concernedBean);
		}
	}
	
	/**
	 * Method to check if an essential property string is has text.
	 *
	 * @param essentialStringProperty
	 *            Is the property which should have text.
	 * @param propertyName
	 *            Is the name of the lacking property.
	 * @param concernedBean
	 *            Is the bean where the property is missing.
	 * @throws MisconfigurationRTException
	 *             If essential property is misconfigured.
	 * @see StringUtils#hasText(java.lang.String)
	 */
	public static void notifyIfEssentialPropertyIsEmpty(
		String essentialStringProperty, String propertyName,
		Object concernedBean) throws MisconfigurationRTException {
		if (!StringUtils.hasText(essentialStringProperty)) {
			notifyLackingEssentialProperty(propertyName, concernedBean);
		}
	}
	
	/**
	 * Method to log that a misconfiguration has occurred.
	 * This method will always throw an exception.
	 *
	 * @param message
	 *            Is the message which explains the misconfiguration.
	 * @throws MisconfigurationRTException
	 *            Will be thrown in every case.
	 */
	public static void notifyMisconfiguration(String message)
		throws MisconfigurationRTException {
		s_logger.error(message);
		throw new MisconfigurationRTException(message);
	}
	
	/**
	 * Logs the message and the Exception cause and translates it to a
	 * {@link MisconfigurationRTException}.
	 *
	 * @param message
	 *            describes the misconfiguration.
	 * @param cause
	 *            the cause exception.
	 * @throws MisconfigurationRTException
	 *            Will be thrown in every case.
	 */
	public static void notifyMisconfiguration(String message, Throwable cause)
		throws MisconfigurationRTException {
		s_logger.error(message, cause);
		throw new MisconfigurationRTException(message, cause);
	}

	/**
	 * Replaces place holders in the message with the values provided as
	 * parameters.
	 *
	 * @param message
	 *            message that contains place holders.
	 * @param parameters
	 *            list of parameters that replace the place holders.
	 * @throws MisconfigurationRTException
	 *            Will be thorwn in every case.
	 * @see MessageFormat
	 */
	public static void notifyMisconfiguration(String message,
			Object... parameters) throws MisconfigurationRTException {
		String msg = MessageFormat.format(message, parameters);
		notifyMisconfiguration(msg);
	}
}
