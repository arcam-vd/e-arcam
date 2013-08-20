/*
 * DeclarationUtils.java
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

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.dom.core.ExonerationTypeEnum;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;
import org.arcam.cyberadmin.dom.reference.Tarif;
import org.arcam.cyberadmin.i18n.UTF8Control;
import org.arcam.cyberadmin.service.cresus.type.BillingDTO;

/**
 * <p>
 * Utility class for declaration data.
 * </p>
 * 
 * 
 * @author mmn
 * 
 */
public final class DeclarationUtils {

    private DeclarationUtils() {
        // to hide
    }

    public static GuestExemptions get(Set<GuestExemptions> guestExemptions, ExonerationTypeEnum type) {
        if (CollectionUtils.isEmpty(guestExemptions) || type == null) {
            return null;
        }
        
        for (GuestExemptions exemption : guestExemptions) {
            if (exemption.getExemptionType() == type) {
                return exemption;
            }
        }
        return null;
    }

    /**
     * <ul>
     * Gets the displayed fiscale period (fiscale date).
     * <li>Fiscal year (when the periodicity is 12 months)</li>
     * <li>Fiscal Month year in the other cases.</li>
     * </ul>
     * 
     * @param declaration
     * @return
     */
    public static String getDisplayedFiscalePeriod(Declaration declaration) {
        if (declaration == null || declaration.getBienTaxe() == null || declaration.getFiscaleDate() == null) {
            return "";
        }
        
        DateFormat df = null;
        if (declaration.getBienTaxe().getPeriodiciteCode() == PeriodiciteTypeEnum.YEAR) {
            df = new SimpleDateFormat(CyberAdminConstants.FISCALE_YEAR_PATTERN);
        } else {
            df = new SimpleDateFormat(CyberAdminConstants.FISCALE_MONTH_YEAR_PATTERN);
        }
        return df.format(declaration.getFiscaleDate());
    }
    
    public static void enrichBillingDTO(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        DeclarationTypeEnum type = declaration.getBienTaxe().getDeclarationType();
        switch (type) {
        case RESIDENCE_SECONDAIRE:
            enrichResidenceSecondaireBilling(declaration, billingDTO);
            break;
        case LOCATION:
            enrichLocationBilling(declaration, billingDTO, tarif);
            break;
        case HOTEL:
            enrichHotelBilling(declaration, billingDTO, tarif);
            break;
        case CHAMBRE:
            enrichChambreBilling(declaration, billingDTO, tarif);
            break;
        case CAMPING:
            enrichCampingBilling(declaration, billingDTO, tarif);
            break;
        case INSTITUT:
            enrichInstituteBilling(declaration, billingDTO, tarif);
            break;
        default:
            throw new IllegalArgumentException("Unsupported declaration type " + type);
        }
    }
    
    private static String getType(String typeName){
    	
    	ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", new UTF8Control());
        return messages.getString("cyberadmin.common.declaration.type."+typeName+".export");
    }
    
    private static void enrichResidenceSecondaireBilling(Declaration declaration, BillingDTO billingDTO) {
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration,
                CresusExportConstant.DESIGNATION_RESIDENCE_SECONDAIRE, declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, "", ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, "", ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, "", declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTaxAmountString(),
                declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
             
        Adresse address = bienTaxe.getAdresse();        
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_RESIDENCE_SECONDAIRE,
                DateHelper.getFormattedYear(declaration.getFiscaleDate()), 
                address.getRue(), address.getNo(),
                address.getNpa(), address.getLocalite(), bienTaxe.geteGid(), bienTaxe.geteWid(), getType(bienTaxe.getDeclarationType().name())));
    }
    
    private static void enrichLocationBilling(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        String designation = "";
        String semaines = "";
        String unit = "";
        double priceUnit;
        if (declaration.isFixedRate()) {
            designation = CresusExportConstant.DESIGNATION_LOCATION_FLAT;
            unit = CresusExportConstant.UNIT_LOCATION_FLAT;
            if (BooleanUtils.isFalse(declaration.getTaille())) {
                priceUnit = tarif.getMaxLocataire2p();
            } else {
                priceUnit = tarif.getMaxLocataire3p();
            }
            
        } else {
            designation = CresusExportConstant.DESIGNATION_LOCATION_WEEK;
            semaines = declaration.getWeeks() + "";
            unit = CresusExportConstant.UNIT_LOCATION_WEEK;
            if (BooleanUtils.isFalse(declaration.getTaille())) {
                priceUnit = tarif.getLocataire2p();
            } else {
                priceUnit = tarif.getLocataire3p();
            }
        }
        
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration, designation, declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, semaines, ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, unit, ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, priceUnit, declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTaxAmountString(),
                declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        Adresse address = bienTaxe.getAdresse();
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_LOCATION,
                DateHelper.getFormattedDate(declaration.getFiscaleDate()),
                DateHelper.getFormattedDate(declaration.getDepartDate()), declaration.getNom(),
                declaration.getPrenom(), bienTaxe.geteGid(), bienTaxe.geteWid(), address.getRue(), address.getNo(),
                address.getNpa(), address.getLocalite(), getType(bienTaxe.getDeclarationType().name())));
    }

    private static void enrichHotelBilling(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration, CresusExportConstant.DESIGNATION_HOTEL,
                declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, declaration.getCalculatedExemption()
                .getNuits(), ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, CresusExportConstant.UNIT_HOTEL_NIGHT, ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, tarif.getNuitHotel(),
                declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTotalNuitsString(),
                declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        Adresse address = bienTaxe.getAdresse(); 
        Date fiscaleDate = declaration.getFiscaleDate();        
        Date endDate = DateHelper.getPeridEnd(fiscaleDate, bienTaxe.getPeriodiciteCode());        
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_OTHER,
                DateHelper.getFormattedDate(fiscaleDate), DateHelper.getFormattedDate(endDate),
                getType(bienTaxe.getDeclarationType().name())));
    }

    private static void enrichChambreBilling(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration, CresusExportConstant.DESIGNATION_CHAMBRE,
                declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, declaration.getCalculatedExemption()
                .getNuits(), ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, CresusExportConstant.UNIT_CHAMBRE_NIGHT, ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, tarif.getNuitChambre(),
                declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTotalNuitsString(),
                declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        Adresse address = bienTaxe.getAdresse(); 
        Date fiscaleDate = declaration.getFiscaleDate();
        Date endDate = DateHelper.getPeridEnd(fiscaleDate, bienTaxe.getPeriodiciteCode());
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_OTHER,
                DateHelper.getFormattedDate(fiscaleDate), DateHelper.getFormattedDate(endDate),
                getType(bienTaxe.getDeclarationType().name())));
    }
    
    private static void enrichCampingBilling(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration, CresusExportConstant.DESIGNATION_CAMPING_NIGHT,
                CresusExportConstant.DESIGNATION_CAMPING_EXTENDED_STAY, declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, declaration.getCalculatedExemption()
                .getNuits(), declaration.getCalculatedExemption().getResidentiel(), ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, CresusExportConstant.UNIT_CAMPING_NIGHT,
                CresusExportConstant.UNIT_CAMPING_PERSONAL, ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, tarif.getNuitcamping(),
                tarif.getResidentielCamping(), declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTotalNuitsString(),
                declaration.getTotalResidentielString(), declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        Adresse address = bienTaxe.getAdresse(); 
        Date fiscaleDate = declaration.getFiscaleDate();
        Date endDate = DateHelper.getPeridEnd(fiscaleDate, bienTaxe.getPeriodiciteCode());
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_OTHER,
                DateHelper.getFormattedDate(fiscaleDate), DateHelper.getFormattedDate(endDate),
                getType(bienTaxe.getDeclarationType().name())));
    }
    
    private static void enrichInstituteBilling(Declaration declaration, BillingDTO billingDTO, Tarif tarif) {
        billingDTO.setDesignation(multiEntryHandleAdjust(declaration,
                CresusExportConstant.DESIGNATION_INSTITUTE_NIGHT,
                CresusExportConstant.DESIGNATION_INSTITUTE_EXTENDED_STAY, declaration.getAjustComment()));
        billingDTO.setQuantity(multiEntryHandleAdjust(declaration, declaration.getCalculatedExemption()
                .getNuits(), declaration.getCalculatedExemption().getResidentiel(), ""));
        billingDTO.setUnit(multiEntryHandleAdjust(declaration, CresusExportConstant.UNIT_INSTITUTE_NIGHT,
                CresusExportConstant.UNIT_INSTITUTE_PERSONAL, ""));
        billingDTO.setPriceUnit(multiEntryHandleAdjust(declaration, tarif.getNuitInstitut(),
                tarif.getResidentielInstitut(), declaration.getAjustValueString()));
        billingDTO.setPositionAmount(multiEntryHandleAdjust(declaration, declaration.getTotalNuitsString(),
                declaration.getTotalResidentielString(), declaration.getAjustValueString()));
        
        BienTaxe bienTaxe = declaration.getBienTaxe();
        Adresse address = bienTaxe.getAdresse(); 
        Date fiscaleDate = declaration.getFiscaleDate();
        Date endDate = DateHelper.getPeridEnd(fiscaleDate, bienTaxe.getPeriodiciteCode());
        billingDTO.setBillingDescription(MessageFormat.format(CresusExportConstant.DESC_OTHER,
                DateHelper.getFormattedDate(fiscaleDate), DateHelper.getFormattedDate(endDate),
                getType(bienTaxe.getDeclarationType().name())));
    }
    
    
    private static String multiEntryHandleAdjust(Declaration d, Object...values) {
        if ((d.getAjustComment() == null || d.getAjustComment().isEmpty()) && d.getAjustValue() == 0) {
            values[values.length - 1] = "";    
        }
        return ExportDataUtils.encodeMultiEntryValue(values);
    }
}
