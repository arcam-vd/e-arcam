/*
 * JournalMessageTypeEnum.java
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

package org.arcam.cyberadmin.dom.core;

/**
 * Enum for journal message type.
 * 
 * @author mmn
 *
 */
public enum JournalMessageTypeEnum {

    /**
     * Message type used when a taxpayer creates his own declaration.
     */
    DECLARATION_CREATION,
    /**
     * Message type used when a COM or GES demands a declaration for a taxpayer.
     */
    DECLARATION_DEMAND,
    /**
     * Message type used when a taxpayer modifies a declaration.
     */
    DECLARATION_MODIFICATION,
    /**
     * Message type used when a taxpayer submits his declaration to the COM or GES.
     */
    DECLARATION_SUBMISSION,
    /**
     * Message type used when a GES validates a declaration.
     */
    DECLARATION_VALIDATION,
    /**
     * Message type used when a taxpayer rejects a declaration.
     */
    DECLARATION_REJECTION,
    /**
     * Message type used when a declaration is cancelled.
     */
    DECLARATION_CANCEL,
    /**
     * Message type used when a user is created in the DB.
     */
    USER_CREATION,
    /**
     * Message type used when a user information is modified.
     */
    USER_MODIFICATION,
    /**
     * Message type used when a taxpayer is created in the DB.
     */
    TAXPAYER_CREATION,
    /**
     * Message type used when a taxpayer information is modified.
     */
    TAXPAYER_MODIFICATION;
}
