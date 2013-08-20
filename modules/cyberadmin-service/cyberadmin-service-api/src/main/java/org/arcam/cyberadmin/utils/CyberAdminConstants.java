/*
 * CyberAdminConstants.java
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

package org.arcam.cyberadmin.utils;

/**
 * @author mmn
 *
 */
public final class CyberAdminConstants {

    private CyberAdminConstants() {
        // to hide
    }
    
    /**
     * Format of date used common in application.
     */
    public static final String DATE_PATTERN = "dd.MM.yyyy";

    /**
     * Format of date used common in application.
     */
    public static final String DATE_HOUR_PATTERN = "HH:mm dd.MM.yyyy";
    
    /**
     * Date pattern used for yearly fiscale period.
     */
    public static final String FISCALE_YEAR_PATTERN = "yyyy";
    
    /**
     * Date pattern userd for other fiscale period.
     */
    public static final String FISCALE_MONTH_YEAR_PATTERN = "MM.yyyy";
   
    /**
     *  Code of switzerland.
     */
    public static final String SWITZERLAND_CODE = "CH";
    
    /**
     * The length of the auto-generated password.
     */
    public static final int AUTO_GENERATED_PASSWORD_LENGTH = 8;
    
    /**
     * Default sender if can not found any in the DB.
     */
    public static final String DEFAULT_SYSTEM_MAIL = "noreply@arcam-vd.ch";
    
    /**
     * The user name of the system user which is usually used to run the schedule tasks.
     */
    public static final String SYSTEM_USERNAME = "sys";
    
    /**
     * The password of the system user which is usually used to run the schedule tasks. 
     */
    public static final String SYSTEM_PASSWORD = "password";
    
    /**
     * String to be added when a text is truncated.
     */
    public static final String TRUNCATE_MARK = "...";
    
    /**
     * Default number format of CyberAdmin.
     */
    public static final String NUMBER_FORMAT = "###.00";
}
