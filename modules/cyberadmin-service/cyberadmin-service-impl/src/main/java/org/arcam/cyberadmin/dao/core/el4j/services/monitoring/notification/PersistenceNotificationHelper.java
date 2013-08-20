/*
 * PersistenceNotificationHelper
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.StringUtils;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.exceptions.InsertionFailureException;
/**
 * This class is used to notify on events which are persistence based.
 *
 * @author Martin Zeltner (MZE)
 */
public final class PersistenceNotificationHelper {
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(PersistenceNotificationHelper.class);
	
	/**
	 * Hide default constructor.
	 */
	private PersistenceNotificationHelper() {
	}
	
	/**
	 * Method to log that an object does not exist. This method will always
	 * throw an exception.
	 *
	 * @param detailedMessage
	 *            Is the detailed message.
	 * @param objectName
	 *            Is the name of the object.
	 * @throws DataRetrievalFailureException
	 *             Will be thrown in every case.
	 */
	public static void notifyDataRetrievalFailure(
		String detailedMessage, String objectName)
		throws DataRetrievalFailureException {
		String message = StringUtils.hasText(detailedMessage)
			? detailedMessage
				: "The desired " + objectName + " does not exist.";
		s_logger.info(message);
		throw new DataRetrievalFailureException(message);
	}

	/**
	 * Method to log that an object made problems while insertion. This method
	 * will always throw an exception.
	 *
	 * @param detailedMessage
	 *            Is the detailed message.
	 * @param objectName
	 *            Is the name of the object.
	 * @throws InsertionFailureException
	 *             Will be thrown in every case.
	 */
	public static void notifyInsertionFailure(
		String detailedMessage, String objectName)
		throws InsertionFailureException {
		String message = StringUtils.hasText(detailedMessage)
			? detailedMessage
				: "Insertion of " + objectName + " failed.";
		s_logger.info(message);
		throw new InsertionFailureException(message);
	}
	
	/**
	 * Notify violation of data integrity of an object.
	 * Will always throw an exception.
	 *
	 * @param detailedMessage
	 *            Is the detailed message.
	 * @param objectName
	 *            Is the name of the object.
	 * @throws DataIntegrityViolationException
	 *             Will be thrown in every case.
	 */
	public static void notifyDataIntegrityViolationFailure(
		String detailedMessage, String objectName)
		throws DataIntegrityViolationException {
		String message = StringUtils.hasText(detailedMessage)
			? detailedMessage
				: "Integrity of " + objectName + " violated.";
		s_logger.info(message);
		throw new DataIntegrityViolationException(message);
	}


	/**
	 * Method to log that the object has already been modified. This method
	 * will always throw an exception.
	 *
	 * @param detailedMessage
	 *            Is the detailed message.
	 * @param objectName
	 *            Is the name of the object.
	 * @param optionalException optionally returns the original {@link OptimisticLockingFailureException}
	 *         (in order not to loose information)
	 * @throws OptimisticLockingFailureException
	 *             Will be thrown in every case.
	 */
	public static void notifyOptimisticLockingFailure(
		String detailedMessage, String objectName, OptimisticLockingFailureException optionalException)
		throws OptimisticLockingFailureException {
		String message = StringUtils.hasText(detailedMessage)
			? detailedMessage
				: objectName + " was modified or deleted in the meantime.";
		s_logger.info(message);
		
		// in order not to loose information
		if (optionalException != null) {
			throw optionalException;
		} else {
			throw new OptimisticLockingFailureException(message);
		}
	}

	/**
	 * Method to log that an object could not be retrieved. This method will
	 * always throw an exception.
	 * 
	 * @param entityClass
	 *            The persistent class.
	 * @param identifier
	 *            The ID of the object that should have been retrieved.
	 * @param objectName
	 *            The name of the object.
	 */
	public static void notifyObjectRetrievalFailure(Class<?> entityClass,
		Object identifier, String objectName)
		throws ObjectRetrievalFailureException {
		notifyObjectRetrievalFailure(entityClass, identifier, objectName,
			null, null);
	}
	
	/**
	 * Method to log that an object could not be retrieved. This method will
	 * always throw an exception.
	 * 
	 * @param entityClass
	 *            The persistent class.
	 * @param identifier
	 *            The ID of the object that should have been retrieved.
	 * @param objectName
	 *            The name of the object.
	 * @param detailedMessage
	 *            A detailed message.
	 * @param cause
	 *            The source of the exception.
	 */
	public static void notifyObjectRetrievalFailure(Class<?> entityClass,
		Object identifier, String objectName, String detailedMessage,
		Throwable cause) throws ObjectRetrievalFailureException {
		String message = StringUtils.hasText(detailedMessage) ? detailedMessage
			: "The desired " + objectName + " with ID " + identifier
				+ " could not be" + " retrieved.";
		s_logger.info(message);

		throw new ObjectRetrievalFailureException(entityClass, identifier,
			message, cause);
	}
	
	
	/**
	 * Method to log that the object could not be updated correctly. This method
	 * will always throw an exception.
	 *
	 * @param detailedMessage
	 *            Is the detailed message.
	 * @param objectName
	 *            Is the name of the object.
	 * @param sql
	 *            Is the SQL we tried to execute.
	 * @param expected
	 *            Is the expected number of rows affected.
	 * @param actual
	 *            Is the actual number of rows affected.
	 * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
	 *             Will be thrown in every case.
	 */
	public static void notifyJdbcUpdateAffectedIncorrectNumberOfRows(
		String detailedMessage, String objectName,
		String sql, int expected, int actual)
		throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
		String message = StringUtils.hasText(detailedMessage)
			? detailedMessage
				: objectName + " could not be updated correctly.";
		s_logger.info(message);
		throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
			sql, expected, actual);
	}
	
	/**
	 * Same behaviour as call of method
	 * <code>notifyDataRetrievalFailure(null, String)</code>.
	 *
	 * @see #notifyDataRetrievalFailure(String, String)
	 */
	public static void notifyDataRetrievalFailure(String objectName)
		throws DataRetrievalFailureException {
		notifyDataRetrievalFailure(null, objectName);
	}

	/**
	 * Same behaviour as call of method
	 * <code>notifyInsertionFailure(null, String)</code>.
	 *
	 * @see #notifyInsertionFailure(String, String)
	 */
	public static void notifyInsertionFailure(String objectName)
		throws InsertionFailureException {
		notifyInsertionFailure(null, objectName);
	}

	/**
	 * Same behaviour as call of method
	 * <code>notifyDataIntegrityViolationFailure(null, String)</code>.
	 *
	 * @see #notifyDataIntegrityViolationFailure(String, String)
	 */
	public static void notifyDataIntegrityViolationFailure(String objectName)
		throws DataIntegrityViolationException {
		notifyDataIntegrityViolationFailure(null, objectName);
	}

	
	/**
	 * Same behaviour as call of method
	 * <code>notifyOptimisticLockingFailure(null, String)</code>.
	 *
	 * @see #notifyOptimisticLockingFailure(String, String, OptimisticLockingFailureException)
	 */
	public static void notifyOptimisticLockingFailure(String objectName)
		throws OptimisticLockingFailureException {
		notifyOptimisticLockingFailure(null, objectName, null);
	}
	
	/**
	 * Same behaviour as call of method
	 * <code>notifyOptimisticLockingFailure(null, String, String, int, int)
	 * </code>.
	 *
	 * @see #notifyJdbcUpdateAffectedIncorrectNumberOfRows(String, String,
	 *      String, int, int)
	 */
	public static void notifyJdbcUpdateAffectedIncorrectNumberOfRows(
		String objectName, String sql, int expected, int actual)
		throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
		notifyJdbcUpdateAffectedIncorrectNumberOfRows(
			null, objectName, sql, expected, actual);
	}
}
