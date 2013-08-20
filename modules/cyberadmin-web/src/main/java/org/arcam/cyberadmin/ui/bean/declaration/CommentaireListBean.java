/*
 * CommentaireListBean.java
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

package org.arcam.cyberadmin.ui.bean.declaration;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.criteria.business.CommentaireCriteria;
import org.arcam.cyberadmin.dom.business.Attachment;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.ui.bean.core.AbstractFilterListBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Backing bean for comment search.
 * 
 * @author vtn
 * 
 */
@ManagedBean(name = "commentaireListBean")
@ViewScoped
public class CommentaireListBean extends AbstractFilterListBean<Commentaire, CommentaireCriteria> {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;

    @Override
    public List<ColumnModel> getColumns() {
        // The data table on this screen is built manually (not use the <arcam:dataTable> tag).
        // Thus There is no need to defined column model.
        return new ArrayList<ColumnModel>(0);
    }

    @Override
    protected CommentaireCriteria instantiateCriteria() {
        CommentaireCriteria criteria = new CommentaireCriteria();
        String declarationId = FacesUtils.getRequestParam("id");
        if (declarationId != null && !declarationId.isEmpty()) {
            criteria.setDeclarationId(Long.valueOf(declarationId).longValue());
        }
        return criteria;
    }
    
    /**
     * Download an attachment of a comment object.
     * @param commentId
     * @return
     */
    public StreamedContent downloadAttachment(Long commentId) {
        DefaultStreamedContent file = null;
        Attachment attachment = declarationService.findAttachmentOfComment(commentId);
        if (attachment != null) {
            // Create content for downloaded file.
            file = new DefaultStreamedContent(new ByteArrayInputStream(attachment.getContent()));
            file.setName(attachment.getFilename());
        }
        return file;
    }

    @Override
    protected List<Commentaire> loadData(int startRow, int endRow) {
        return declarationService.findCommentaireByCriteria(criteria);
    }

    @Override
    protected int countData() {
        return declarationService.countCommentaireByCriteria(criteria);
    }

    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

}
