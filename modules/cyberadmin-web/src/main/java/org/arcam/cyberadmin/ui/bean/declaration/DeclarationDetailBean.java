/*
 * DeclarationDetailBean
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Attachment;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.ui.bean.core.AbstractDetailBean;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.type.AddressTypeEnum;
import org.arcam.cyberadmin.ui.type.TaxeCalculDto;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.NumberUtils;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing bean for detail declaration.
 * 
 * @author vtn
 * 
 */

@ManagedBean(name = "declarationDetailBean")
@ViewScoped
public class DeclarationDetailBean extends AbstractDetailBean<Declaration> {

    private static final int PERCENTAGE_UNIT = 100;
    private static final long serialVersionUID = 1L;
    private static final String AMOUNT_TO_LOCATION_KEY = "cyberadmin.declarationDetail.amountToPaidLocation";
    private static final String AMOUNT_TO_RESIDENT_KEY = "cyberadmin.declarationDetail.amountToPaidResidence";
    private static final String EXAMPLE_CAMPING_INSTITUE 
                        = "cyberadmin.declarationDetail.taxCalculation.example.camping";
    
    private static final int PERSON_STAYING_NUMBER_NIGHT_CAMPING = 59;
    private static final int PERSON_STAYING_NUMBER_NIGHT_INSTITUTE = 179;
    
    
    /**
     * OutcoemID page declaration list.
     */
    private static final String DECLARATION_LIST = "DECLARATION_LIST";
    private static final String DECLARATION_FINISH = "DECLARATION_FINISH";
    private static final String DECLARATION_DETAIL = "DECLARATION_DETAIL";

    private AddressTypeEnum adresseType = AddressTypeEnum.CORRESPONDANCE_ADDRESS;
    private String titleType;

    private List<TaxeCalculDto> taxeCalDoms = new ArrayList<TaxeCalculDto>();

    private boolean viewMode;
    private boolean editMode;
    
    /**
     * Using for fiscale date.
     */
    private int month;
    private int year;

    @ManagedProperty(value = "#{declarationService}")
    private DeclarationService declarationService;
    
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    @ManagedProperty(value = "#{commentaireListBean}")
    private CommentaireListBean commentaireListBean;
    
    @ManagedProperty(value = "#{commentaireDetailBean}")
    private CommentaireDetailBean commentaireDetailBean;
    
    private boolean report = false;
    
    private boolean modifiedHouseID = true;
    
    private boolean showConfirmSend = false;
    private boolean showConfirmErrorCresus = false;
    
    private Declaration originalDec = new Declaration();
    
    private boolean addedCommentSuccessfully;
    
    @Override
    protected void loadEntityInSession() {
        entity = (Declaration) FacesUtils.getSessionMapValue(BienTaxeDetailBean.SESSION_DECLARATION_KEY);
        setScreenModeForDeclaration(entity);
        
        setYear(DateHelper.getYear(entity.getFiscaleDate()));
        setMonth(DateHelper.getMonth(entity.getFiscaleDate()));
    }

    @Override
    public Declaration loadEntity(Long id) {
        Declaration declarationObj = declarationService.load(id);
        BienTaxe bienTaxe = declarationObj.getBienTaxe();
        if ((getCurrentUser().isAssujetti() && !bienTaxe.getAssujetti().equals(
                getCurrentUser().getUserInfo().getAssujetti()))
                || (getCurrentUser().isCommune() && !bienTaxe.getCommuneCode().equals(
                        getCurrentUser().getUserInfo().getCommuneCode()))) {
            try {
                FacesUtils
                        .getFacesContext()
                        .getExternalContext()
                        .redirect(
                                FacesUtils.getFacesContext().getExternalContext().getRequestContextPath()
                                        + "/authError.xhtml");
            } catch (IOException e) {
                logger.error("Error when try to load declaration detail page");
            }
        }
        setScreenModeForDeclaration(declarationObj);
        
        setLocaliteSuggestion(declarationObj);
        
        setYear(DateHelper.getYear(declarationObj.getFiscaleDate()));
        setMonth(DateHelper.getMonth(declarationObj.getFiscaleDate()));
        
        // Clone object to restore after cancel action.
        originalDec.copyChangeableProperties(declarationObj);
        return declarationObj;
    }

    private void setLocaliteSuggestion(Declaration declarationObj) {
        Localite localiteSuggestion = new Localite();
        localiteSuggestion.setCode(declarationObj.getAdresse().getNpa());
        localiteSuggestion.setText(declarationObj.getAdresse().getLocalite());
        declarationObj.setLocaliteSuggestion(localiteSuggestion);
    }

    public void showConfirmSend(boolean value) {
        if (!validatedDeclaration()) {
            return;
        }
        showConfirmSend = value;
    }
    
    public void showConfirmErrorCresus(boolean value) {
        if (!validatedDeclaration()) {
            return;
        }
        showConfirmErrorCresus = value;
    }
    
    private void setScreenModeForDeclaration(Declaration dec) {
        // ASJ
        if (getCurrentUser().isAssujetti()) {
            if (dec.getStatus() == StatusTypeEnum.TO_FILLED) {
                setScreenMode(false, true);
            } else {
                setScreenMode(true, false);
            }
        }
        // COM
        if (getCurrentUser().isCommune()) {
            setScreenMode(true, false);
        }
        // GES,ADM
        if (getCurrentUser().isGestionnaire() || getCurrentUser().isAdministrator()) {
            if (dec.getStatus() == StatusTypeEnum.TO_FILLED) {
                setScreenMode(false, true);
            } else {
                setScreenMode(true, false);
            }
        }
    }
    
    public String cancel() {
        setScreenMode(true, false);
        //entity.copyChangeableProperties(originalDec);
        //ARCANCYBERADM-92  browser "back" button issues
        if (!entity.isPersisted()) {
            FacesUtils.removeSessionMapValue(BienTaxeDetailBean.FILL_MODE);
            FacesUtils.removeSessionMapValue(BienTaxeDetailBean.TAXPAYER_ID);
            return "/assujetti/taxpayerList.xhtml?faces-redirect=true";
        }
        return StringUtils.EMPTY;
    }
    
    public void saveOnPage() {
          super.save();
    }
    
    public void updateEGidWid() {
        if (entity.getStatus() == StatusTypeEnum.EGID_VALIDATED) {
            entity.setStatus(StatusTypeEnum.FILLED);
        }
        declarationService.save(entity);
        modifiedHouseID = true;
    }
    
    public void modifiereGidWid() {
        modifiedHouseID = false;
    }
    
    public void modify(ActionEvent actionEvent) {
        setScreenMode(false, true);
    }
    
    @Override
    protected void afterSave() {
        setScreenMode(true, false);
        // Clone after edit entity to restore after cancel action.
        originalDec.copyChangeableProperties(entity);
       //ARCANCYBERADM-92  browser "back" button issues
        FacesUtils.removeSessionMapValue(BienTaxeDetailBean.FILL_MODE);
        FacesUtils.removeSessionMapValue(BienTaxeDetailBean.TAXPAYER_ID);
    }
   
    public void addComment(ActionEvent event) {
        // Using this property for considering to close add-comment dialog after send or not. 
        addedCommentSuccessfully = false;
        UploadedFile uploadedFile = commentaireDetailBean.getUploadedFile();
        if (uploadedFile != null && uploadedFile.getSize() > webConstants.getSizeLimit()) {
            uploadedFile = null;
        }
        Commentaire commentaire = commentaireDetailBean.getEntity();
        doAddCommentForDeclaration(uploadedFile, commentaire);
        if (report) {
            Set<String> mailAddresses = userService
                    .getGetCommuneAndArcamUserMailAddress(entity.getBienTaxe().getCommuneCode());
            if (!mailAddresses.isEmpty()) {
                mailService.demandSupport(entity, commentaireDetailBean.getEntity().getMessage(), getCurrentUser()
                        .getUserInfo().getEmail(), mailAddresses);
            }
        }
        
        // Reset row count to make the next table renderer work.
        commentaireListBean.search(event);
        // Allow add-comment dialog to be closed.
        addedCommentSuccessfully = true;
    }

    public void setFlagReport(boolean value) {
        this.report = value;
    }
    
    protected void doAddCommentForDeclaration(UploadedFile uploadedFile, Commentaire commentaire) {
        if (uploadedFile != null) {
            byte[] contents = uploadedFile.getContents();
            Attachment attachment = new Attachment();
            attachment.setContent(contents);
            String uploadedFileName = FilenameUtils.getName(uploadedFile.getFileName());
            attachment.setFilename(StringUtils.substring(uploadedFileName, 0, 
                                                         Attachment.SAVED_MAXIMUM_FILENAME_LENGTH));
            attachment.setCommentaire(commentaire);
            commentaire.setAttachment(attachment);
        }
        
        commentaire.setTimestamp(DateHelper.now());
        commentaire.setUser(getCurrentUser().getUserInfo());
        commentaire.setDeclaration(entity);
        declarationService.addComment(entity, commentaire);
    }
   
    @Override
    public String saveEntity() {
        entity.getAdresse().setNpa(entity.getLocaliteSuggestion().getCode());
        setFiscaleDate();
        declarationService.save(entity);
        return DECLARATION_LIST;
    }

    public String send() {
        if (!validatedDeclaration()) {
            return null;
        }
        setFiscaleDate();
        declarationService.submit(entity);
        if (getCurrentUser().isAssujetti()) {
            return DECLARATION_FINISH;
        } else {
            // GES,ADM
            //ARCANCYBERADM-92  browser "back" button issues
            FacesUtils.removeSessionMapValue(BienTaxeDetailBean.FILL_MODE);
            FacesUtils.removeSessionMapValue(BienTaxeDetailBean.TAXPAYER_ID);
            return DECLARATION_DETAIL;
        }
    }
    
    private void setFiscaleDate() {
        Date newFiscaleDate = entity.getFiscaleDate();
        if (entity.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            newFiscaleDate = DateHelper.getDate(1, Calendar.JANUARY, year);
        } else if (entity.getBienTaxe().getDeclarationType() != DeclarationTypeEnum.LOCATION) {
            newFiscaleDate = DateHelper.getDate(1, month, year);
        }
        entity.setFiscaleDate(newFiscaleDate);
    }

    public String changeStatus(StatusTypeEnum status) {
        entity.setStatus(status);
        if (status == StatusTypeEnum.NOT_SUBJECTES) {
            entity.getBienTaxe().setPeriodiciteCode(PeriodiciteTypeEnum.NONE);
        }
        declarationService.save(entity);
        return DECLARATION_LIST;
    }
    
    public void reloadAdresseDeclaration(ActionEvent actionEvent) {
        if (adresseType == AddressTypeEnum.BIEN_ADDRESS) {
            Adresse adresse = entity.getBienTaxe().getAdresse();
            entity.setAdresse(adresse.clone());
        } else {
            Adresse adresse = entity.getBienTaxe().getAssujetti().getAdresse();
            entity.setAdresse(adresse.clone());
        }
        setLocaliteSuggestion(entity);
    }

    public String getStatus() {
        return FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_STATUS_DECLARATION_KEY, entity.getStatus());
    }

    public String getTitleType() {
        titleType = FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_DECLARATION_NEW_FOR_TYPE, 
                                                getEntity().getBienTaxe().getDeclarationType());
        return titleType;
    }

    private boolean validateExemptions() {
        reCalcuTaxeCalDoms(null);
        boolean total = validatedTotalExemption();
        boolean exemptions = validatedExemptions();
        boolean calculatedEx = validatedCalculatedExemption();
        return total && exemptions && calculatedEx;
    }

    private boolean validatedCalculatedExemption() {
        boolean result = true;
        GuestExemptions calculatedExemption = entity.getCalculatedExemption();
        if (NumberUtils.defaultIfNull(calculatedExemption.getHotes(), 0) < 0) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(
                            WebConstants.ARRIVAL_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            result =  false;
        }
        
        if (NumberUtils.defaultIfNull(calculatedExemption.getNuits(), 0) < 0) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(
                            WebConstants.NIGHT_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            result = false;
        }
        
        if (NumberUtils.defaultIfNull(calculatedExemption.getResidentiel(), 0) < 0) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(
                            WebConstants.RESIDENTIEL_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            result =  false;
        }
        
        return result;
    }

    private boolean validatedExemptions() {
        boolean result = true;
        List<GuestExemptions> exemptions = entity.getExemptions();
        for (GuestExemptions guest : exemptions) {
            if (guest.validatedValues()) {
                int arrivees = NumberUtils.defaultIfNull(guest.getHotes(), 0);
                int nights = NumberUtils.defaultIfNull(guest.getNuits(), 0);
                int residentiels = NumberUtils.defaultIfNull(guest.getResidentiel(), 0);
                if (arrivees > (nights + residentiels)) {
                    
                    String exemptionType = FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_SHORT_TYPE_EXONERATION,
                            guest.getExemptionType());
                    
                    FacesUtils.getFacesContext().addMessage(
                            null,
                            FacesUtils.getI18nErrorMessage(
                                    WebConstants.ARRIVAL_GREATER_THAN_NIGHT_RESIENTIEL_ORTHER_ERROR_KEY,
                                    FacesMessage.SEVERITY_ERROR, exemptionType));
                    result = false;
                }
            }
            
        }
        return result;
    }

    private boolean validatedTotalExemption() {
        boolean result = true;
        GuestExemptions total = entity.getTotalExemption();
        int totalArrivee = NumberUtils.defaultIfNull(total.getHotes(), 0);
        int totalNight = NumberUtils.defaultIfNull(total.getNuits(), 0);
        int totalResidentiel = NumberUtils.defaultIfNull(total.getResidentiel(), 0);

        if ((totalArrivee == 0 && (totalNight != 0 || totalResidentiel != 0))) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(WebConstants.ARRIVAL_EQUAL_ZERO_ERROR_KEY,
                    FacesMessage.SEVERITY_ERROR));
           result =  false;
        }
        
        if (totalArrivee > 0 && totalArrivee > (totalNight + totalResidentiel)) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(WebConstants.ARRIVAL_GREATER_THAN_NIGHT_RESIDENTIEL_TOTAL_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            result = false;
        }
        return result;
    }

    @Override
    protected boolean beforeSave() {
        if (!checkNpaSwitzerland()) {
            return false;
        }
        return validatedDeclaration();
    }
    
    private boolean checkNpaSwitzerland() {
        Localite localite = localiteService.findLocaliteByNpa(entity.getLocaliteSuggestion().getCode());
        if (localite == null) {
            FacesUtils.getFacesContext().addMessage(
                    null,
                    FacesUtils.getI18nErrorMessage(WebConstants.FILLING_WRONG_NPA_LOCALITE_ERROR_KEY,
                            FacesMessage.SEVERITY_ERROR));
            return false;
        }
        return true;
    }

    private boolean validatedDeclaration() {
        if (entity.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.LOCATION
                || entity.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            return true;
        }
        return validateExemptions();
    }
    
    public void reCalcuTaxeCalDoms(AjaxBehaviorEvent e) {
        declarationService.calculateTax(entity);
    }
    
    public void handleSelect(SelectEvent event) {
        Localite localite = (Localite) event.getObject();
        entity.getAdresse().fetchValuesFromLocalite(localite);
    }
    
    public void handleFromDateSelect(DateSelectEvent event) {
        Date date = event.getDate();
        entity.setFiscaleDate(date);
        reCalcuTaxeCalDoms(null);
    }
    
    public void handleToDateSelect(DateSelectEvent event) {
        Date date = event.getDate();
        entity.setDepartDate(date);
        reCalcuTaxeCalDoms(null);
    }

    public List<TaxeCalculDto> getTaxeCalDoms() {
        taxeCalDoms.clear();
        GuestExemptions calculatedExemption = entity.getCalculatedExemption();
        
        taxeCalDoms.add(new TaxeCalculDto(FacesUtils.getMessage(WebConstants.SUBMIT_TOTAL_KEY), 
                calculatedExemption.getHotes(), calculatedExemption.getNuits(), calculatedExemption.getResidentiel()));
        
        taxeCalDoms.add(new TaxeCalculDto(FacesUtils.getMessage(WebConstants.TARIF_KEY),
                WebConstants.SWITZERLAND_CURRENCY, declarationService.getNuitsTarif(entity.getTarif(), getEntity()
                        .getBienTaxe().getDeclarationType()), declarationService.getResidentielTarif(entity.getTarif(),
                        getEntity().getBienTaxe().getDeclarationType())));
        
        taxeCalDoms.add(new TaxeCalculDto(FacesUtils.getMessage(WebConstants.SUB_TOTAL_KEY),
                WebConstants.SWITZERLAND_CURRENCY, entity.getTotalNuits(), entity.getTotalResidentiel()));

        return taxeCalDoms;
    }

    public String getHelpText() {
        if (getEntity().isPersisted()) {
            return FacesUtils.getHelperMessage("cyberadmin.declarationDetail.help.view");
        }
        return FacesUtils.getHelperMessage("cyberadmin.declarationDetail.help.new");
    }
    
    public List<SelectItem> getAdresseTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>(AddressTypeEnum.values().length);
        for (AddressTypeEnum item : AddressTypeEnum.values()) {
            items.add(new SelectItem(item, FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_TYPE_ADRESSE, item)));
        }
        return items;
    }
    
    public String getAmountToPaidResidenceMsg() {
        return FacesUtils.getMessage(AMOUNT_TO_RESIDENT_KEY, 
                NumberUtils.formatNumber(entity.getTarif().getCoefficientResidence() * PERCENTAGE_UNIT, 
                        WebConstants.NUMBER_FORMAT), 
                NumberUtils.formatNumber(entity.getTarif().getMinResidence(), WebConstants.NUMBER_FORMAT), 
                NumberUtils.formatNumber(entity.getTarif().getMaxResidence(), WebConstants.NUMBER_FORMAT));
    }
    
    public String getAmountToPaidLocationMsg() {
        if (entity.getTaille()) {
            // << 3 pieces et moins >> is selected.
            return FacesUtils.getMessage(AMOUNT_TO_LOCATION_KEY, 
                    NumberUtils.formatNumber(entity.getTarif().getLocataire3p(), WebConstants.NUMBER_FORMAT), 
                    NumberUtils.formatNumber(entity.getTarif().getMaxLocataire3p(), WebConstants.NUMBER_FORMAT));
        }

        // << 2 pieces et plus >> is selected.
        return FacesUtils.getMessage(AMOUNT_TO_LOCATION_KEY, 
                NumberUtils.formatNumber(entity.getTarif().getLocataire2p(), WebConstants.NUMBER_FORMAT), 
                NumberUtils.formatNumber(entity.getTarif().getMaxLocataire2p(), WebConstants.NUMBER_FORMAT));
    }
    
    public String getExampleCampingInstitut() {
        if (entity.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.INSTITUT) {
            return FacesUtils.getMessage(EXAMPLE_CAMPING_INSTITUE,
                    NumberUtils.formatNumber(PERSON_STAYING_NUMBER_NIGHT_INSTITUTE, WebConstants.NUMBER_FORMAT),
                    NumberUtils.formatNumber(PERSON_STAYING_NUMBER_NIGHT_INSTITUTE + 1, WebConstants.NUMBER_FORMAT));
        } else if (entity.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.CAMPING) {
            return FacesUtils.getMessage(EXAMPLE_CAMPING_INSTITUE, 
                    NumberUtils.formatNumber(PERSON_STAYING_NUMBER_NIGHT_CAMPING, WebConstants.NUMBER_FORMAT), 
                    NumberUtils.formatNumber(PERSON_STAYING_NUMBER_NIGHT_CAMPING + 1, WebConstants.NUMBER_FORMAT));
        } else {
            return "";
        }
    }    
    
    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public void setTaxeCalDoms(List<TaxeCalculDto> taxeCalDoms) {
        this.taxeCalDoms = taxeCalDoms;
    }

    public AddressTypeEnum getAdresseType() {
        return adresseType;
    }

    public void setAdresseType(AddressTypeEnum adresseType) {
        this.adresseType = adresseType;
    }

    public void setScreenMode(boolean view, boolean edit) {
        this.viewMode = view;
        this.editMode = edit;
    }
    
    public boolean isEditMode() {
        return editMode;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public CommentaireListBean getCommentaireListBean() {
        return commentaireListBean;
    }

    public void setCommentaireListBean(CommentaireListBean commentaireListBean) {
        this.commentaireListBean = commentaireListBean;
    }
    
    public void setDeclarationService(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    public CommentaireDetailBean getCommentaireDetailBean() {
        return commentaireDetailBean;
    }

    public void setCommentaireDetailBean(CommentaireDetailBean commentaireDetailBean) {
        this.commentaireDetailBean = commentaireDetailBean;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public boolean isModifiedHouseID() {
        return modifiedHouseID;
    }

    public void setModifiedHouseID(boolean modifiedHouseID) {
        this.modifiedHouseID = modifiedHouseID;
    }

    public boolean isShowConfirmSend() {
        return showConfirmSend;
    }

    public void setShowConfirmSend(boolean showConfirmSend) {
        this.showConfirmSend = showConfirmSend;
    }

    public boolean isAddedCommentSuccessfully() {
        return addedCommentSuccessfully;
    }

    public void setAddedCommentSuccessfully(boolean addedCommentSuccessfully) {
        this.addedCommentSuccessfully = addedCommentSuccessfully;
    }

    public boolean isShowConfirmErrorCresus() {
        return showConfirmErrorCresus;
    }

    public void setShowConfirmErrorCresus(boolean showConfirmErrorCresus) {
        this.showConfirmErrorCresus = showConfirmErrorCresus;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
