/*
 * DeclarationServiceImpl.java
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

package org.arcam.cyberadmin.service.core.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.criteria.business.CommentaireCriteria;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria;
import org.arcam.cyberadmin.criteria.business.DeclarationCriteria.SearchMode;
import org.arcam.cyberadmin.dao.utils.DaoUtils;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.Attachment;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Commentaire;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.core.StatusTypeEnum;
import org.arcam.cyberadmin.dom.core.UserTypeEnum;
import org.arcam.cyberadmin.dom.reference.Tarif;
import org.arcam.cyberadmin.service.core.DeclarationService;
import org.arcam.cyberadmin.service.core.LogService;
import org.arcam.cyberadmin.service.core.ReferenceDataService;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.mail.MailService;
import org.arcam.cyberadmin.utils.DateHelper;
import org.arcam.cyberadmin.utils.NumberUtils;
import org.arcam.cyberadmin.utils.Utility;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * The implementation for {@link DeclarationService}.
 * 
 * @author vtn, mmn
 * 
 */
@Service("declarationService")
@Transactional
public class DeclarationServiceImpl extends GenericDataServiceDefaultImpl<Declaration> implements DeclarationService {

    private static final int DECLARATION_VALIDITY_PERIOD = 10; // in day
    private static final int DAY_OF_DUE_DATE = 9;
    
    @Autowired
    private ReferenceDataService referenceDataService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public List<Declaration> findDeclarationByCriteria(DeclarationCriteria criteria, boolean isExport) {
        if (criteria.getSearchMode() == null) {
            return new ArrayList<Declaration>(0);
        }
        
        DetachedCriteria hCrit = buildSearchDeclarationCriteria(criteria, isExport);
        if (isExport) {
            return daoFor(Declaration.class).findByCriteria(hCrit);
        }
        return daoFor(Declaration.class).findByCriteria(hCrit, criteria.getFirstResult(), criteria.getMaxResults());
    }
    
    @Override
    public int countDeclarationByCriteria(DeclarationCriteria criteria) {
        if (criteria.getSearchMode() == null) {
            return 0;
        }
        
        DetachedCriteria hCrit = buildSearchDeclarationCriteria(criteria, false);
        return daoFor(Declaration.class).findCountByCriteria(hCrit);
    }

    private DetachedCriteria buildSearchDeclarationCriteria(DeclarationCriteria criteria, boolean isExport) {
        DeclarationCriteria.denormalizeFreetextCriteria(criteria);
        DetachedCriteria rootCrit = DetachedCriteria.forClass(Declaration.class);
        rootCrit.createAlias("bienTaxe", "bienTaxe");
        rootCrit.createAlias("bienTaxe.adresse", "adresse");
        rootCrit.createAlias("bienTaxe.assujetti", "assujetti");
        rootCrit.createAlias("assujetti.person", "person");
        
        SearchMode searchMode = criteria.getSearchMode();
        User user = criteria.getCurrentUser();
        UserTypeEnum userType = user.getUserType();
        
        addPeriodRestrictions(criteria, rootCrit);
        
        if (searchMode == SearchMode.QUICK) {
            String freetext = criteria.getFreetext();
            if (StringUtils.isNotBlank(freetext)) {
                Disjunction disjunction = Restrictions.disjunction();
                disjunction.add(createDenominationCriterion(freetext));
                
                if (userType != UserTypeEnum.COM) {
                    // the freetext will not effect on commune field as in this case, 
                    // we only return declaration inside a commune which is the commune of the current town user
                    disjunction
                            .add(Restrictions.ilike("bienTaxe.communeCodeDisplayText", freetext, MatchMode.ANYWHERE));
                }
                
                if (userType != UserTypeEnum.ASJ) {
                    // search on assujetti's person info
                    disjunction.add(Restrictions.ilike("person.nom", freetext, MatchMode.ANYWHERE));
                    disjunction.add(Restrictions.ilike("person.prenom", freetext, MatchMode.ANYWHERE));
                }
                
                if (org.apache.commons.lang3.math.NumberUtils.isDigits(freetext)) {
                    disjunction.add(Restrictions.eq("id", new Long(freetext)));
                }
                
                if (CollectionUtils.isNotEmpty(criteria.getStatusTypes())) {
                    disjunction.add(Restrictions.in("status", criteria.getStatusTypes()));
                }
                
                if (CollectionUtils.isNotEmpty(criteria.getDeclarationTypes())) {
                    disjunction.add(Restrictions.in("bienTaxe.declarationType", criteria.getDeclarationTypes()));
                }
                
                rootCrit.add(disjunction);
            }
            
            // query on declaration's status (used for quick search links)
            if (criteria.getStatus() != null) {
                rootCrit.add(Restrictions.eq("status", criteria.getStatus()));
            }
            
            if (userType == UserTypeEnum.ASJ) {
                // only return declaration which belong to the current assujetti
                rootCrit.add(Restrictions.eq("assujetti.user", user));
            } else if (userType == UserTypeEnum.COM) {
                // only return declaration inside a commune which is the commune of the current town user
                rootCrit.add(Restrictions.eq("bienTaxe.communeCode", user.getCommuneCode()));
            }
        } else if (searchMode == SearchMode.FULL) {
            // query on commune of declaration's bientaxe
            if (StringUtils.isNotBlank(criteria.getCommune())) {
                rootCrit.add(Restrictions.eq("bienTaxe.communeCode", criteria.getCommune()));
            }
            
            // query on declaration's type
            if (criteria.getType() != null) {
                rootCrit.add(Restrictions.eq("bienTaxe.declarationType", criteria.getType()));
            }
            
            // query on declaration's status
            if (criteria.getStatus() != null) {
                rootCrit.add(Restrictions.eq("status", criteria.getStatus()));
            }
            
            String denomination = criteria.getDenomination();
            if (StringUtils.isNotBlank(denomination)) {
                rootCrit.add(createDenominationCriterion(denomination));
            }
            
            if (criteria.getId() != null && criteria.getId() != 0) {
                rootCrit.add(Restrictions.eq("id", criteria.getId()));
            }
            
            // restriction on taxpayer information (name, firstname, organisation)
            addTaxpayerInfoRestriction(criteria, rootCrit);
        }
        
        addOrderingInfo(rootCrit, criteria);
        return rootCrit;
    }

    private void addPeriodRestrictions(DeclarationCriteria criteria, DetachedCriteria rootCrit) {
        // add period from, to restriction
        Date from = criteria.getFrom();
        if (from != null) {
            rootCrit.add(createFromCriterion(from));
        }
        Date to = criteria.getTo();
        if (to != null) {
            rootCrit.add(createToCriterion(to));
        }
    }

    private Criterion createFromCriterion(Date from) {
        return DaoUtils.equalsOrGreaterThanIgnoreTime("calculatedDate", from);
    }
    
    private Criterion createToCriterion(Date to) {
        return DaoUtils.equalsOrLessThanIgnoreTime("calculatedDate", to);
    }
    
    private Criterion createDenominationCriterion(String denomination) {
        return Restrictions.ilike("denomination", denomination, MatchMode.ANYWHERE);
    }

    private void addTaxpayerInfoRestriction(DeclarationCriteria criteria, DetachedCriteria detachedCriteria) {
        if (StringUtils.isNotBlank(criteria.getName())) {
            detachedCriteria.add(Restrictions.ilike("person.nom", criteria.getName(), MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(criteria.getFirstname())) {
            detachedCriteria.add(Restrictions.ilike("person.prenom", criteria.getFirstname(), MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(criteria.getOrganisation())) {
            detachedCriteria.add(Restrictions.ilike("person.organisation", criteria.getOrganisation(),
                    MatchMode.ANYWHERE));
        }
    }
    
    @Override
    public Declaration prepare(Assujetti assujetti) {
        Assert.notNull(assujetti, "The taxpayer is missing.");
        
        Declaration declaration = new Declaration();
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        
        // create bien taxe
        BienTaxe bienTaxe = new BienTaxe();
        bienTaxe.getDeclarations().add(declaration);
        declaration.setBienTaxe(bienTaxe);
        
        // assujetti for the bien taxe
        bienTaxe.setAssujetti(assujetti);
        assujetti.getBienTaxes().add(bienTaxe);
        
        // address for the bien taxe
        Adresse bienAdresse = new Adresse();
        bienTaxe.setAdresse(bienAdresse);
        
        // 'Address de Facturation' is created using the assujetti's address
        Adresse adresse = assujetti.getAdresse().clone();
        declaration.setAdresse(adresse);
        
        return declaration;
    }

    @Override
    public void create(Declaration declaration) {
        // create a list of guest exemptions in case of 5a, 5c & 5d
        createGuestExemptions(declaration);
    }

    private void createGuestExemptions(Declaration declaration) {
        DeclarationTypeEnum declarationType = declaration.getBienTaxe().getDeclarationType();
        
        Set<GuestExemptions> guestExemptions = declaration.getGuestExemptions();
        if (declarationType == DeclarationTypeEnum.HOTEL 
                || declarationType == DeclarationTypeEnum.CAMPING
                || declarationType == DeclarationTypeEnum.CHAMBRE 
                || declarationType == DeclarationTypeEnum.INSTITUT) {
            for (ExonerationTypeEnum exonerationType : ExonerationTypeEnum.values()) {
                GuestExemptions exemption = new GuestExemptions();
                exemption.setDeclaration(declaration);
                exemption.setExemptionType(exonerationType);
                if (exonerationType != ExonerationTypeEnum.AUCUNE) {
                    exemption.setHotes(new Integer(0));
                    exemption.setNuits(new Integer(0));
                    exemption.setResidentiel(new Integer(0));
                }
                guestExemptions.add(exemption);
            }
        } else {
            guestExemptions.clear();
        }
        
        // fill transient fields
        calculateTax(declaration);
    }

    @Override
    public void demand(Declaration declaration, boolean sendReminder) {
        declaration.setStatus(StatusTypeEnum.TO_FILLED);
        
        // calculate due date
        Date dueDate = calculateDueDate(declaration);
        declaration.setDueDate(dueDate);
        declaration.setLastModificationDate(DateHelper.now());
        
        if (declaration.getPeriodiciteCode() == null) {
            // ARCANCYBERADM-93: copy periodicity of bientaxe to declaration
            declaration.setPeriodiciteCode(declaration.getBienTaxe().getPeriodiciteCode());
        }
        
        // create a list of guest exemptions in case of 5a, 5c & 5d
        createGuestExemptions(declaration);
        
        // save the bien taxe and it's declaration into DB
        saveAndLog(declaration);
        
        if (sendReminder) {
            // sent an email informing the taxpayer about the declaration.
            User sysUser = userService.getSystemUser();
            String from = Utility.getEmailFromUserDefaultIfEmpty(sysUser);
            
            String message = mailService.demandDeclaration(declaration, from);
            if (StringUtils.isNotBlank(message)) {
                // log the mail content in the comments for the declaration
                Commentaire comment = new Commentaire();
                comment.setDeclaration(declaration);
                comment.setMessage(message);
                comment.setTimestamp(DateHelper.now());
                comment.setUser(sysUser);
                declaration.getCommentaires().add(comment);
            }
            
            // save the comment
            daoFor(Declaration.class).merge(declaration);
        }
    }

    private Date calculateDueDate(Declaration declaration) {
        Date dueDate = null;
        Date fiscaleDate = declaration.getFiscaleDate();
        Date departDate = declaration.getDepartDate();
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        DeclarationTypeEnum type = bienTaxe.getDeclarationType();
        if (type == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            // DueDate = 30th September of the current year (year of the fiscale date)
            dueDate = DateHelper.getDate(DateHelper.END_DAY_OF_SEPTEMBER, Calendar.SEPTEMBER,
                    DateHelper.getYear(fiscaleDate));
        } else if (type == DeclarationTypeEnum.LOCATION) {
            if (departDate != null) {
                // DueDate = DepartDate + 10 days
                dueDate = DateUtils.addDays(departDate, DECLARATION_VALIDITY_PERIOD);
            } else {
                // DueDate = 10th January of the next year (year fiscale date + 1)
                dueDate = DateHelper.getDate(DECLARATION_VALIDITY_PERIOD, Calendar.JANUARY,
                        DateHelper.getYear(fiscaleDate) + 1);
            }
        } else { // other cases
            PeriodiciteTypeEnum period = bienTaxe.getPeriodiciteCode();
            int numberOfMonth = 0;
            if (period != null) {
                numberOfMonth = period.getMonths();
            }
            
            // DueDate = FiscaleDate + periodicity[months] + 9 days   
            // (10th of the first month after the end of the period)
            dueDate = DateUtils.addDays(DateUtils.addMonths(fiscaleDate, numberOfMonth), DAY_OF_DUE_DATE);
        }
        return dueDate;
    }
    
    @Override
    public void save(Declaration declaration) {
        // only update the new information of the declaration. The status remains 'TO_FILLED'
        
        // calculate due date
        Date dueDate = calculateDueDate(declaration);
        declaration.setDueDate(dueDate);
        declaration.setLastModificationDate(DateHelper.now());
        
        if (declaration.getPeriodiciteCode() == null) {
            // ARCANCYBERADM-93: copy periodicity of bientaxe to declaration
            declaration.setPeriodiciteCode(declaration.getBienTaxe().getPeriodiciteCode());
        }
        
        // save the bien taxe and it's declaration into DB
        saveAndLog(declaration);
    }

    @Override
    public void submit(Declaration declaration) {
        BienTaxe bienTaxe = declaration.getBienTaxe();
        DeclarationTypeEnum type = bienTaxe.getDeclarationType();
        if ((type == DeclarationTypeEnum.RESIDENCE_SECONDAIRE || type == DeclarationTypeEnum.LOCATION)
                && !bienTaxe.hasLocationInfo()) {
            declaration.setStatus(StatusTypeEnum.EGID_VALIDATED);
        } else {
            declaration.setStatus(StatusTypeEnum.FILLED);
        }
        
        // calculate due date
        Date dueDate = calculateDueDate(declaration);
        declaration.setDueDate(dueDate);
        declaration.setLastModificationDate(DateHelper.now());
        declaration.setSubmissionDate(DateHelper.now());
        
        if (declaration.getPeriodiciteCode() == null) {
            // ARCANCYBERADM-93: copy periodicity of bientaxe to declaration
            declaration.setPeriodiciteCode(declaration.getBienTaxe().getPeriodiciteCode());
        }
        
        // save the bien taxe and it's declaration into DB
        saveAndLog(declaration);
        
        if (declaration.getStatus() == StatusTypeEnum.EGID_VALIDATED) {
            Set<String> mailAddresses = userService.getCommuneUserMailAddressesIn(bienTaxe.getCommuneCode());
            if (CollectionUtils.isNotEmpty(mailAddresses)) {
                User sysUser = userService.getSystemUser();
                String from = Utility.getEmailFromUserDefaultIfEmpty(sysUser);
                
                // sent an email to all the COM users registered for the commune of the declaration.
                String message = mailService.requestEGridEwid(declaration, from, mailAddresses);
                
                // log the mail content in the comments for the declaration
                Commentaire comment = new Commentaire();
                comment.setDeclaration(declaration);
                comment.setMessage(message);
                comment.setTimestamp(DateHelper.now());
                comment.setUser(sysUser);
                declaration.getCommentaires().add(comment);
                
                // save the comment
                daoFor(Declaration.class).merge(declaration);
            }
        }
    }
    
    @Override
    public Declaration load(long id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class).add(Restrictions.eq("id", id));
        DaoUtils.fetchEager("guestExemptions", criteria);
        DaoUtils.fetchEager("commentaires", criteria);
        
        List<Declaration> declarations = daoFor(Declaration.class).findByCriteria(criteria);
        if (CollectionUtils.isEmpty(declarations)) {
            throw new DataRetrievalFailureException("Can not find the declaration with id=" + id + " in the DB.");
        }
        
        Declaration declaration = declarations.get(0);
        
        // fill transient fields
        calculateTax(declaration);
        
        return declaration;
    }

    @Override
    public void calculateTax(Declaration declaration) {
    
        final double roundTo5Cts = 20d;
        // load the object contains parameters for the calculation.
        Tarif tarif = referenceDataService.getTarif(declaration.getFiscaleDate());
        declaration.setTarif(tarif);
        DeclarationTypeEnum type = declaration.getBienTaxe().getDeclarationType();
        
        double taxAmount = 0;
        if (type == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
            taxAmount = Math.min(
                    Math.max(
                            NumberUtils.defaultIfNull(declaration.getEstimation(), 0L)
                                    * tarif.getCoefficientResidence(), tarif.getMinResidence()),
                    tarif.getMaxResidence());
            
           taxAmount = ((int) Math.round((taxAmount * roundTo5Cts))) / roundTo5Cts; // round at the nearest 5 cts

            
        } else if (type == DeclarationTypeEnum.LOCATION) {
            double locataire = 0;
            double maxLocataire = 0;
            if (BooleanUtils.isFalse(declaration.getTaille())) {
                locataire = tarif.getLocataire2p();
                maxLocataire = tarif.getMaxLocataire2p();
            } else if (BooleanUtils.isTrue(declaration.getTaille())) {
                locataire = tarif.getLocataire3p();
                maxLocataire = tarif.getMaxLocataire3p();
            }
            
            int weeks = DateHelper.getDifferenceInWeek(declaration.getDepartDate(), declaration.getFiscaleDate());
            declaration.setWeeks(weeks);
            taxAmount = Math.min(weeks * locataire, maxLocataire);
            if (declaration.getExoneration() != ExonerationTypeEnum.AUCUNE) {
                taxAmount = 0;
            }
            declaration.setFixedRate(taxAmount == maxLocataire);
        } else {
            List<GuestExemptions> exemptions = new ArrayList<GuestExemptions>(declaration.getGuestExemptions().size());
            GuestExemptions totalExemption = null;
            
            int totalHotes = 0;
            int totalNuits = 0;
            int totalResidentiel = 0;
            
            for (GuestExemptions guestExemptions : declaration.getGuestExemptions()) {
                if (guestExemptions.getExemptionType() == ExonerationTypeEnum.AUCUNE) {
                    // the total one
                    totalExemption = guestExemptions;
                    continue;
                }
                
                totalHotes += NumberUtils.defaultIfNull(guestExemptions.getHotes(), 0);
                totalNuits += NumberUtils.defaultIfNull(guestExemptions.getNuits(), 0);
                totalResidentiel += NumberUtils.defaultIfNull(guestExemptions.getResidentiel(), 0);
                
                exemptions.add(guestExemptions);
            }
            
            if (totalExemption != null) {
                GuestExemptions calculatedExemption = new GuestExemptions();
                calculatedExemption.setHotes(NumberUtils.defaultIfNull(totalExemption.getHotes(), 0) - totalHotes);
                calculatedExemption.setNuits(NumberUtils.defaultIfNull(totalExemption.getNuits(), 0) - totalNuits);
                calculatedExemption.setResidentiel(NumberUtils.defaultIfNull(totalExemption.getResidentiel(), 0)
                        - totalResidentiel);
                declaration.setCalculatedExemption(calculatedExemption);
                
                declaration.setTotalNuits(new Double(NumberUtils.defaultIfNull(calculatedExemption.getNuits(), 0)
                        * getNuitsTarif(tarif, type)));
                declaration.setTotalResidentiel(new Double(NumberUtils.defaultIfNull(
                        calculatedExemption.getResidentiel(), 0)
                        * getResidentielTarif(tarif, type)));
            }
            
            Collections.sort(exemptions);
            declaration.setExemptions(exemptions);
            declaration.setTotalExemption(totalExemption);
            taxAmount = declaration.getTotalNuits() + declaration.getTotalResidentiel();
        }
        declaration.setTaxAmount(taxAmount);
    }

    @Override
    public double getResidentielTarif(Tarif tarif, DeclarationTypeEnum type) {
        if (type == DeclarationTypeEnum.CAMPING) {
            return tarif.getResidentielCamping();
        } else if (type == DeclarationTypeEnum.HOTEL) {
            return 0;
        } else if (type == DeclarationTypeEnum.CHAMBRE) {
            return 0;
        } else if (type == DeclarationTypeEnum.INSTITUT) {
            return tarif.getResidentielInstitut();
        } else {
            throw new IllegalArgumentException("The application does not support tarification exemption for " + type);
        }
    }
    
    @Override
    public double getNuitsTarif(Tarif tarif, DeclarationTypeEnum type) {
        if (type == DeclarationTypeEnum.CAMPING) {
            return tarif.getNuitcamping();
        } else if (type == DeclarationTypeEnum.HOTEL) {
            return tarif.getNuitHotel();
        } else if (type == DeclarationTypeEnum.CHAMBRE) {
            return tarif.getNuitChambre();
        } else if (type == DeclarationTypeEnum.INSTITUT) {
            return tarif.getNuitInstitut();
        } else {
            throw new IllegalArgumentException("The application does not support tarification exemption for " + type);
        }
    }
    
    @Override
    public List<Commentaire> findCommentaireByCriteria(CommentaireCriteria criteria) {
        DetachedCriteria hCrit = buildSearchCommentaireCriteria(criteria);
        return daoFor(Commentaire.class).findByCriteria(hCrit, criteria.getFirstResult(), criteria.getMaxResults());
    }

    private DetachedCriteria buildSearchCommentaireCriteria(CommentaireCriteria criteria) {
        DetachedCriteria rootCrit = DetachedCriteria.forClass(Commentaire.class);
        rootCrit.createAlias("user", "user");
        rootCrit.createAlias("user.person", "person");
        rootCrit.add(Restrictions.eq("declaration.id", criteria.getDeclarationId()));
        addOrderingInfo(rootCrit, criteria);
        return rootCrit;
    }

    @Override
    public int countCommentaireByCriteria(CommentaireCriteria criteria) {
        DetachedCriteria hCrit = buildSearchCommentaireCriteria(criteria);
        return daoFor(Commentaire.class).findCountByCriteria(hCrit);
    }

    @Override
    public int countFilledDeclarations() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        criteria.add(Restrictions.eq("status", StatusTypeEnum.FILLED));
        return daoFor(Declaration.class).findCountByCriteria(criteria);
    }

    @Override
    public int countCurrentToFilledDeclarations() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        criteria.add(Restrictions.eq("status", StatusTypeEnum.TO_FILLED));
        criteria.add(DaoUtils.equalsOrLessThanIgnoreTime("calculatedDate", DateHelper.today()));
        return daoFor(Declaration.class).findCountByCriteria(criteria);
    }

    @Override
    public int countToFilledDeclarationsFor(User user) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        criteria.createAlias("bienTaxe", "bienTaxe");
        criteria.createAlias("bienTaxe.assujetti", "assujetti");
        
        criteria.add(Restrictions.eq("status", StatusTypeEnum.TO_FILLED));
        
        // only return declaration which belong to the current assujetti
        criteria.add(Restrictions.eq("assujetti.user", user));
        
        return daoFor(Declaration.class).findCountByCriteria(criteria);
    }

    @Override
    public int countCurrentToFilledDeclarationFor(User user) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        criteria.createAlias("bienTaxe", "bienTaxe");
        criteria.createAlias("bienTaxe.assujetti", "assujetti");
        
        criteria.add(Restrictions.eq("status", StatusTypeEnum.TO_FILLED));
        criteria.add(DaoUtils.equalsOrLessThanIgnoreTime("calculatedDate", DateHelper.today()));
        
        // only return declaration which belong to the current assujetti
        criteria.add(Restrictions.eq("assujetti.user", user));
        
        return daoFor(Declaration.class).findCountByCriteria(criteria);
    }

    @Override
    public List<Declaration> findTaxpayerDeclarationByCriteria(DeclarationCriteria criteria, Assujetti assujetti,
            boolean isExport) {
        DetachedCriteria hCrit = buildSearchTaxpayerDeclarationCriteria(criteria, assujetti, isExport);
        if (isExport) {
            return daoFor(Declaration.class).findByCriteria(hCrit);
        }
        return daoFor(Declaration.class).findByCriteria(hCrit, criteria.getFirstResult(), criteria.getMaxResults());
    }

    private DetachedCriteria buildSearchTaxpayerDeclarationCriteria(DeclarationCriteria criteria, Assujetti assujetti,
            boolean isExport) {
        DeclarationCriteria.denormalizeFreetextCriteria(criteria);
        DetachedCriteria rootCrit = DetachedCriteria.forClass(Declaration.class);
        rootCrit.createAlias("bienTaxe", "bienTaxe");
        rootCrit.createAlias("bienTaxe.assujetti", "assujetti");

        addPeriodRestrictions(criteria, rootCrit);

        String freetext = criteria.getFreetext();
        if (StringUtils.isNotBlank(freetext)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(createDenominationCriterion(freetext));
            disjunction.add(Restrictions.ilike("bienTaxe.communeCode", freetext, MatchMode.ANYWHERE));
            
            if (org.apache.commons.lang3.math.NumberUtils.isDigits(freetext)) {
                disjunction.add(Restrictions.eq("id", new Long(freetext)));
            }
            
            if (CollectionUtils.isNotEmpty(criteria.getStatusTypes())) {
                disjunction.add(Restrictions.in("status", criteria.getStatusTypes()));
            }
            
            if (CollectionUtils.isNotEmpty(criteria.getDeclarationTypes())) {
                disjunction.add(Restrictions.in("bienTaxe.declarationType", criteria.getDeclarationTypes()));
            }
            
            rootCrit.add(disjunction);
        }

        User user = criteria.getCurrentUser();
        UserTypeEnum userType = user.getUserType();
        if (userType == UserTypeEnum.COM) {
            rootCrit.add(Restrictions.eq("bienTaxe.communeCode", user.getCommuneCode()));
        }

        rootCrit.add(Restrictions.eq("assujetti.id", assujetti.getId()));

        //sorting follow the declaration id when exporting
        if (isExport) {
            criteria.getAscs().add("id");
        }
        addOrderingInfo(rootCrit, criteria);
        return rootCrit;

    }

    @Override
    public int countTaxpayerDeclarationByCriteria(DeclarationCriteria criteria, Assujetti assujetti) {
        DetachedCriteria hCrit = buildSearchTaxpayerDeclarationCriteria(criteria, assujetti, false);
        return daoFor(Declaration.class).findCountByCriteria(hCrit);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Long> getToFillDeclarationIds() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        
        criteria.add(Restrictions.eq("status", StatusTypeEnum.TO_FILLED));
        ProjectionList proList = Projections.projectionList().add(Projections.property("id"));
        criteria.setProjection(proList);
        
        List ids = daoFor(Declaration.class).findByCriteria(criteria);
        return new HashSet<Long>(ids);
    }

    
    @Override
    public Declaration saveAndLog(Declaration declaration) {
        // load the old one from database
        Declaration oldDec = null;
        if (declaration.isPersisted()) {
            oldDec = load(declaration.getId());
            // Remove oldDec from session to avoid hibernate exception while saving user.
            sessionFactory.getCurrentSession().evict(oldDec);
        }
        
        // Begin to save.
        daoFor(Declaration.class).saveOrUpdate(declaration);
        logService.logModification(declaration, oldDec);
        return declaration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Declaration addComment(Declaration dec, Commentaire comment) {
        dec.getCommentaires().add(comment);
        return getDefaultDao().saveOrUpdate(dec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attachment findAttachmentOfComment(Long commentId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Attachment.class);
        criteria.createAlias("commentaire", "commentaire");
        criteria.add(Restrictions.eq("commentaire.id", commentId));
        List<Attachment> attachments = daoFor(Attachment.class).findByCriteria(criteria);
        if (CollectionUtils.isNotEmpty(attachments)) {
            // Only one attachment for one commentaire object.
            return attachments.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map<Integer, Integer> findUnexportedDecForBillingIds() {
        // Build projection expression.
        DetachedCriteria criteria = DetachedCriteria.forClass(Declaration.class);
        ProjectionList proList = Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("version"));
        criteria.setProjection(proList);
        criteria.add(Restrictions.eq("status", StatusTypeEnum.VALIDATED));
        
        // Query and build the result map.
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        List ids = daoFor(Declaration.class).findByCriteria(criteria);
        for (Object item : ids) {
            Object[] row = (Object[]) item;
            result.put(((Long) row[0]).intValue(), (Integer) row[1]);
        }
        
        return result;
    }

    @Override
    public int updateExportedDeclaration(Integer id, Integer version, StatusTypeEnum status) {
        if (id == null || version == null) {
            // No row is updated
            return 0;
        }
        
        Session session = sessionFactory.getCurrentSession();
        Query query = session
                .createSQLQuery(
                        "update DECLARATION set STATUS = :status, VERSION = :version + 1 "
                        + "where DEC_ID = :id and VERSION = :version")
                    .setParameter("status", status.name())
                    .setParameter("version", version)
                    .setParameter("id", id);
        return query.executeUpdate();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAndLogDeclarations(List<Declaration> declarations) {
        if (CollectionUtils.isEmpty(declarations)) {
            return;
        }
        for (Declaration dec : declarations) {
            saveAndLog(dec);
        }
    }

}
