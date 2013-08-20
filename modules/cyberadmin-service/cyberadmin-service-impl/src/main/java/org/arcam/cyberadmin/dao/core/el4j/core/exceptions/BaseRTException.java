/*
 * BaseRTException
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
 package org.arcam.cyberadmin.dao.core.el4j.core.exceptions;

import java.text.MessageFormat;

/**
 * This is the parent runtime exception of all the runtime exceptions in EL4J.
 * It provides a few functionalities, that may or may not be used by child
 * exceptions:
 * <ul>
 * <li>Support to substitute parameters in an exception message.</li>
 * </ul>
 *
 * Internationalizing exception messages can be performed (external to this
 * class) with the help of the MessageSources of the spring framework.
 *
 *
 * This class uses the exception wrapping mechanism of the java.lang.Exception
 * class that was introduced with the JDK 1.4.
 *
 * @author Alain Borlet-Hote (ABH), Philipp Oser (POS), Paul E. Sevinç (PES),
 *         Yves Martin (YMA), Martin Zeltner (MZE)
 *
 * @see java.text.MessageFormat For more information on the format for the
 *      parameter substitution
 */
public class BaseRTException extends RuntimeException {
	/**
	 * Contains either the message of the exception or when message format is
	 * used, the messageFormat. MessageFormat substitution is applied if the
	 * result of getFormatParameters() is not null. The message field in the
	 * Throwable class is not used (rationale: it cannot be set without creating
	 * a new object).
	 */
	protected String m_message;

	/**
	 * The parameters to substitute. In subclasses, you may either use this
	 * Object[] to hold the messageFormatParameters or (typically preferred)
	 * define your own (typed and named) attributes that you need.
	 */
	protected Object[] m_messageFormatParameters;

	/**
	 * The constructor with a message in MessageFormat, with parameters, and
	 * with a wrapped exception (with all the formal parameters).
	 *
	 * @param message
	 *            the message of this exception
	 * @param parameters
	 *            the parameters to substitute in the message
	 * @param wrappedException
	 *            the exception that is wrapped in this exception
	 */
	public BaseRTException(String message, Object[] parameters,
			Throwable wrappedException) {
		super(wrappedException);
		m_message = message;
		m_messageFormatParameters = parameters;
	}

	/**
	 * The constructor with a message in MessageFormat and parameters. No
	 * Throwable or Exception is transfered.
	 *
	 * @param message
	 *            the message of this exception
	 * @param parameters
	 *            the parameters to substitute in the message
	 */
	public BaseRTException(String message, Object[] parameters) {
		this(message, parameters, null);
	}

	/**
	 * Constructor with a message and an exception.
	 *
	 * @param message
	 *            the message of this exception
	 * @param exception
	 *            the exception that is wrapped in this exception
	 */
	public BaseRTException(String message, Throwable exception) {
		this(message, null, exception);
	}

	/**
	 * Constructor with a message.
	 *
	 * @param message
	 *            the message of this exception
	 */
	public BaseRTException(String message) {
		this(message, null, null);
	}

	/**
	 * Constructor with an exception.
	 *
	 * @param exception
	 *            the exception that is wrapped in this exception
	 */
	public BaseRTException(Throwable exception) {
		this("BaseRTException without message", null, exception);
	}

	/**
	 * Returns the message pattern for <code>MessageFormat</code> or the
	 * message of the exception.
	 *
	 * @return the message of this exception (without any parameters substituted
	 *         in it).
	 */
	public String getFormatString() {
		return m_message;
	}

	/**
	 * Sets a new format String. It replaces the default formatString by an
	 * internationalized String.
	 *
	 * @param formatString
	 *            replaces the message
	 */
	public void setFormatString(String formatString) {
		m_message = formatString;
	}

	/**
	 * Gets parameters defined for the message. This should be overridden if you
	 * have defined your own set of attributes.
	 *
	 * @return array of arguments for <code>MessageFormat</code>
	 */
	public Object[] getFormatParameters() {
		return m_messageFormatParameters;
	}

	/**
	 * Get the normal message for the exception.
	 *
	 * Please override the <code>Throwable.getLocalizedMessage()</code> to
	 * automatically get the message particular for your Locale.
	 *
	 * @return the message for the user (if necessary with the substituted
	 *         parameters)
	 */
	public String getMessage() {
		Object[] formatParameters = getFormatParameters();
		if (formatParameters != null) {
			return MessageFormat.format(m_message, formatParameters);
		} else {
			return m_message;
		}
	}
}