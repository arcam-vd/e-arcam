/*
 * MailService.java
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

package org.arcam.cyberadmin.service.mail;

import java.util.Set;

import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.service.mail.type.MailMessage;

/**
 * Common interface for mailing service.
 *  
 * @author mmn
 */
public interface MailService {
    
    /**
     * Mails the specified <code>message</code> based on its self-defined information.
     * @param message The mail message to be sent.
     */
    public void send(MailMessage message);
    
    /**
     * Sends an email to all the COM users registered for the commune of the declaration informing a eGid/eWid request
     * in the given declaration.
     * 
     * @param declaration
     * @param from
     * @param to
     *            a list for recipient's addresses.
     * @return the content of the generated mail.
     */
    public String requestEGridEwid(Declaration declaration, String from, Set<String> to);
    
    /**
     * Sends an email to taxpayer when a COM or GES demand him to fill in a declaration.
     * 
     * @param declaration
     * @param from
     * @return the content of the generated mail.
     */
    public String demandDeclaration(Declaration declaration, String from);
    
    /**
     * Sends to all the COM users registered for the commune of the declaration when a GES user submits a 'Signaler a la
     * commune' form.
     * 
     * @param declaration
     * @param message
     * @param from
     * @param to
     * @return the content of the generated mail.
     */
    public String demandSupport(Declaration declaration, String message, String from, Set<String> to);
    
    /**
     * Sends an email to all the COM users registered for the commune of the declaration and to all 'ARCAM' users when
     * an ASJ (or a GES) puts a declaration in rejected state.
     * 
     * @param declaration
     * @param from
     * @param to
     * @return the content of the generated mail.
     */
    public String rejectDeclaration(Declaration declaration, String from, Set<String> to, String motivation);
    
    /**
     * Sends an email to the concerned ASJ when the batch job 2 finds an overdue declaration.
     * 
     * @param declaration
     * @param from
     * @return the content of the generated mail.
     */
    public String remindOverdueDeclaration(Declaration declaration, String from);
    
    /**
     * Generates and send an email contains new password for the given user.
     * 
     * @param user
     * @param from
     * @param password
     * @return the content of the generated mail.
     */
    public String newPassword(User user, String from, String password);
    
    /**
     * Sends an email to registered address to confirm the taxpayer registration.
     * 
     * @param assujetti
     * @param from
     * @return the content of the generated mail.
     */
    public String confirmRegistration(Assujetti assujetti, String from);
    
    /**
     * Sends an email to taxpayer to inform the success account validation.
     * 
     * @param assujetti
     * @param from
     * @return the content of the generated mail.
     */
    public String informValidatedAccount(Assujetti assujetti, String from);
    
    public String enrolAccount(Assujetti assujetti, String from, String password);
}
