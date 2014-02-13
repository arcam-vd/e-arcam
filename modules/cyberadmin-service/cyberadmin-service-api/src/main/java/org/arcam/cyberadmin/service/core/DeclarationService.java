/*
 * DeclarationService.java
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

package org.arcam.cyberadmin.service.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arcam.cyberadmin.criteria.business.CommentaireCriteria;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Attachment;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.reference.Tarif;

/**
 * Inteface for all declaration services.
 * 
 * @author vtn
 *
 */
public interface DeclarationService extends GenericDataService<Declaration> {

    /**
     * Finds all declaration which match the given criteria.
     * 
     * @param criteria
     *            the declaration criteria.
     *        isExport
     *             flag when get all result   
     * @return list of declarations.
     */
    List<Declaration> findDeclarationByCriteria(DeclarationCriteria criteria, boolean isExport);

    /**
     * Counts the number of declarations which match the given criteria.
     * 
     * @param criteria
     *            the declaration criteria.
     * @return
     */
    int countDeclarationByCriteria(DeclarationCriteria criteria);

    /**
     * Creates an empty {@link Declaration} object and it's {@link BienTaxe}. This method is usually called in the
     * screen 4, 10, 10a, 10b when we want to create a form backing object for the screen.
     * 
     * @param assujetti
     *            the taxpayer who is responsible for this declaration.
     * @return a new {@link Declaration}.
     */
    Declaration prepare(Assujetti assujetti);

    /**
     * Create all related information for the given declaration (bases on the declaration type). E.g: exemtion.
     * 
     * @param declaration
     */
    void create(Declaration declaration);

    /**
     * Demands a declaration for a taxpayer. This method is usually called by a COM, GES or ADM.
     * 
     * @param declaration
     *            the declaration.
     * @param sendReminder
     *            a flag indicates if reminder 'declaration.onDemand' is sent. 
     *            It's usually true for 'Demand' function on GUI and false for auto-creation for batch job.
     */
    void demand(Declaration declaration, boolean sendReminder);

    /**
     * Saves the given declaration into DB. This method is usually call by a ASJ.
     * 
     * @param declaration
     *            the declaration.
     */
    void save(Declaration declaration);

    /**
     * Submits a declaration. This method is usually called by ASJ, GES or ADM when 'Nouvelle declaration'.
     * 
     * @param declaration
     *            the declaration.
     */
    void submit(Declaration declaration);

    /**
     * Loads {@link Declaration} all all related information (exemption, ...).
     * 
     * @param id
     *            the id of the declaration.
     * @return
     */
    Declaration load(long id);

    /**
     * Calculates exemptions for the given {@link Declaration} bases on parameters (tarif).
     * 
     * @param declaration
     *            the {@link Declaration}.
     */
    void calculateTax(Declaration declaration);

    /**
     * Finds all commentaire which match the given criteria.
     * 
     * @param criteria
     * @return
     */
    List<Commentaire> findCommentaireByCriteria(CommentaireCriteria criteria);

    /**
     * Counts the number of commentaire which match the given criteria.
     * 
     * @param criteria
     * @return
     */
    int countCommentaireByCriteria(CommentaireCriteria criteria);

    /**
     * @return the number of FILLED declaration.
     */
    int countFilledDeclarations();
    
    /**
     * @return the number of TO_FILLED declaration which has date <= today
     */
    int countCurrentToFilledDeclarations();
    
    /**
     * Counts the number of TO_FILLED declaration for the given user.
     * 
     * @param user the the Cyber Admin user.
     * @return
     */
    int countToFilledDeclarationsFor(User user);
    
    /**
     * Counts the number of TO_FILLED declarations which has data <= today for the given user.
     * 
     * @param user the the Cyber Admin user.
     * @return
     */
    int countCurrentToFilledDeclarationFor(User user);
    
    
    /**
     * Return the night tarif for the specified declaration type.
     * 
     * @param tarif the tarif matrix.
     * @oaram type the declaration type
     * @return
     */
    double getNuitsTarif(Tarif tarif, DeclarationTypeEnum type);
    
    /**
     * Return the residential tarif for the specified declaration type.
     * 
     * @param tarif the tarif matrix.
     * @oaram type the declaration type
     * @return
     */    
    double getResidentielTarif(Tarif tarif, DeclarationTypeEnum type);
    
    /**
     * Finds all declaration which match the given criteria.
     * 
     * @param criteria
     *            the declaration criteria.
     *        isExport
     *            flag when get all result   
     * @return list of declarations.
     */
    List<Declaration> findTaxpayerDeclarationByCriteria(DeclarationCriteria criteria, Assujetti assujetti,
            boolean isExport);

    /**
     * Counts the number of declarations which match the given criteria.
     * 
     * @param criteria
     *            the declaration criteria.
     * @return
     */
    int countTaxpayerDeclarationByCriteria(DeclarationCriteria criteria, Assujetti assujetti);

    /**
     * Gets the list of <{@link Declaration}'s ids which are in status 'TO_FILLED'.
     * @return
     */
    Set<Long> getToFillDeclarationIds();
    
    /**
     * Saves or updates the given declaration into database and then log the changes into LOG table.
     * 
     * @param declaration
     * @return the up-to-date declaration.
     */
    Declaration saveAndLog(Declaration declaration);
    
    /**
     * Add user comment into declaration.
     * @param commentaire 
     * @return 
     */
    Declaration addComment(Declaration declaration, Commentaire commentaire);
    
    /***
     * Avoid to load attachment by lazy load to improve performance. Using this way
     * will load only the needed attachment.
     * @param commentId
     * @return
     */
    Attachment findAttachmentOfComment(Long commentId);

    /**
     * Search all pairs <id,version> of declarations which is in VALID status.
     * @return
     */
    Map<Integer, Integer> findUnexportedDecForBillingIds();
    
    int updateExportedDeclaration(Integer id, Integer version, StatusTypeEnum status);
    
    /**
     * Update a list of declarations.
     * @param declarations
     */
    void saveAndLogDeclarations(List<Declaration> declarations);
}
