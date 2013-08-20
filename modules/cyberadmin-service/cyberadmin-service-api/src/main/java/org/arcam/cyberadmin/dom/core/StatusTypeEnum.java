/*
 * StatusTypeEnum.java
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
 * @author vtn
 *
 */
public enum StatusTypeEnum {
    /**
     * a saisir.
     */
    TO_FILLED(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * contest.
     */
    REFUSED(JournalMessageTypeEnum.DECLARATION_REJECTION),
    /**
     * valider eGID/eWID.
     */
    EGID_VALIDATED(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * saisie.
     */
    FILLED(JournalMessageTypeEnum.DECLARATION_SUBMISSION),
    /**
     * valide.
     */
    VALIDATED(JournalMessageTypeEnum.DECLARATION_VALIDATION),
    /**
     * facture.
     */
    BILLED(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * non pay.
     */
    NOT_PAID(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * facture contest.
     */
    BILL_REFUSED(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * pay.
     */
    PAID(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * pas assujetti.
     */
    NOT_SUBJECTES(JournalMessageTypeEnum.DECLARATION_MODIFICATION),
    /**
     * annulle.
     */
    CANCELLED(JournalMessageTypeEnum.DECLARATION_CANCEL);
    
    private final JournalMessageTypeEnum modificationMessageType;
    
    private StatusTypeEnum(JournalMessageTypeEnum modificationMessageType) {
        this.modificationMessageType = modificationMessageType;
    }

    public JournalMessageTypeEnum getModificationMessageType() {
        return modificationMessageType;
    }
    
}
