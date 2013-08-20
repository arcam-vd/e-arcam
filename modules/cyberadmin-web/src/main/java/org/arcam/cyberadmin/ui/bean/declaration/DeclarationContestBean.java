/*
 * DeclarationContestBean
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

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.type.IUploadDownload;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.Utility;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Managing for all actions on screen6: Refuse an application of declaration.
 * 
 * @author dtl
 * 
 */

@ManagedBean(name = "declarationContestBean")
@ViewScoped
public class DeclarationContestBean extends DeclarationDetailBean implements IUploadDownload {
    private static final String TO_DECLARATION_FINISH = "TO_DECLARATION_FINISH";

    private static final long serialVersionUID = 1L;

    private static final int MAX_DISPLAY_FILE_NAME_CHARACTER = 30;
    
    private String communeDisplayText;
    private UploadedFile uploadedFile;
    // Message inputed from Taxpayer to refuse the declaration. 
    private String message;
    private Commentaire commentForDec;
    
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    public String getCommuneDisplayText() {
        if (communeDisplayText == null && entity.getBienTaxe().getCommuneCode() != null) {
            communeDisplayText = entity.getBienTaxe().getCommuneCodeDisplayText();
        }
        return communeDisplayText;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String saveEntity() {
        entity.setLastModificationDate(DateHelper.now());
        entity.setSubmissionDate(DateHelper.now());
        // Change the status of declaration to REFUSED.
        entity.setStatus(StatusTypeEnum.REFUSED);
        if (uploadedFile != null && uploadedFile.getSize() > webConstants.getSizeLimit()) {
            uploadedFile = null;
        } 
        this.doAddCommentForDeclaration(uploadedFile, commentForDec);
        
        // Send email to COM users in the same COMMUNE and "arcam" GES/ADM users.
        mailService.rejectDeclaration(entity, getCurrentUser().getUserInfo().getEmail(),
                userService.getGetCommuneAndArcamUserMailAddress(entity.getBienTaxe().getCommuneCode()),
                commentForDec.getMessage());
        return TO_DECLARATION_FINISH;
    }
    
    @Override
    protected boolean beforeSave() {
        commentForDec = new Commentaire();
        commentForDec.setMessage(message);
        return true;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void handleFileUpload(FileUploadEvent event) {
        uploadedFile = null;
        uploadedFile = event.getFile();
        if (uploadedFile.getSize() > webConstants.getSizeLimit()) {
            FacesUtils.getFacesContext().addMessage(event.getComponent().getClientId(),
                    FacesUtils.getI18nErrorMessage("cyberadmin.common.fileUpload.size", FacesMessage.SEVERITY_ERROR));
        }
    }
    
    public void setCommuneDisplayText(String communeDisplayText) {
        this.communeDisplayText = communeDisplayText;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Commentaire getCommentForDec() {
        return commentForDec;
    }

    public void setCommentForDec(Commentaire commentForDec) {
        this.commentForDec = commentForDec;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    // TODO [DTL] Should move UploadFile handler to a common bean to be reused.
    public String getDisplayUploadFileName() {
        if (uploadedFile != null) {
            return Utility.truncate(uploadedFile.getFileName(), MAX_DISPLAY_FILE_NAME_CHARACTER);
        }
        return StringUtils.EMPTY;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getDisplayFileNameToolTip() {
        if (uploadedFile != null) {
            if (uploadedFile.getFileName().length() > MAX_DISPLAY_FILE_NAME_CHARACTER) {
                return uploadedFile.getFileName();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public StreamedContent getDownloadedFile() throws IOException {
        StreamedContent file = null;
        if (uploadedFile != null) {
            file = new DefaultStreamedContent(uploadedFile.getInputstream(), uploadedFile.getContentType(),
                    FilenameUtils.getName(uploadedFile.getFileName()));
        }
        return file;
    }
}
