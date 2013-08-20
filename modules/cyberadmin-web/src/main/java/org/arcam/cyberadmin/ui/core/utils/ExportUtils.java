/*
 * ExportUtils.java
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

package org.arcam.cyberadmin.ui.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.arcam.cyberadmin.dom.authorisation.Log;
import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.dom.business.BienTaxe;
import org.arcam.cyberadmin.dom.business.Declaration;
import org.arcam.cyberadmin.dom.business.GuestExemptions;
import org.arcam.cyberadmin.dom.core.DeclarationTypeEnum;
import org.arcam.cyberadmin.service.poi.type.CellDataDto;
import org.arcam.cyberadmin.service.poi.type.RowDataDto;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.service.poi.utils.CellFormatter;
import org.arcam.cyberadmin.service.poi.utils.ExcelExportingUtil;

/**
 * Utils for exporting excel.
 * 
 * @author vtn
 * 
 */
public final class ExportUtils {

    private ExportUtils() {

    }

    private static boolean isLocationDeclaration(Declaration dec) {
        return dec.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.LOCATION;
    }

    private static boolean isResidenceDeclaration(Declaration dec) {
        return dec.getBienTaxe().getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE;
    }
    
    public static SheetDataDto prepareDataDeclarations(List<Declaration> declarations) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Declaration dec : declarations) {
            List<Object> row = new ArrayList<Object>();
            // Declaration
            BienTaxe bienTaxe = dec.getBienTaxe();
            row.add(dec.getId());
            row.add(dec.getBienTaxe().getAssujetti().getId());
            row.add(bienTaxe.getCommuneCodeDisplayText());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_SHORT_DECLARATION_KEY,
                    bienTaxe.getDeclarationType()));
            row.add(dec.getMontant());
            row.add(dec.getSubmissionDate());
            row.add(dec.getFiscaleDate());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_STATUS_DECLARATION_KEY, dec.getStatus()));
            // Facturation
            Assujetti assujetti2 = bienTaxe.getAssujetti();
            Person person = assujetti2.getPerson();
            Adresse adresseFacturation = dec.getAdresse();
            row.add(person.getNom());
            row.add(person.getPrenom());
            row.add(person.getOrganisation());
            row.add(adresseFacturation.getAdresse());
            row.add(adresseFacturation.getRue());
            row.add(adresseFacturation.getNo());
            row.add(adresseFacturation.getNpa());
            row.add(adresseFacturation.getLocalite());
            row.add(adresseFacturation.getCountryExport());
            row.add(adresseFacturation.getEmail());
            row.add(adresseFacturation.getTelephone());
            // Identifiant du bien
            Adresse adresseDeBien = bienTaxe.getAdresse();
            row.add(bienTaxe.getEtablissement());
            row.add(bienTaxe.geteGid());
            row.add(bienTaxe.geteWid());
            row.add(adresseDeBien.getAdresse());
            row.add(adresseDeBien.getRue());
            row.add(adresseDeBien.getNo());
            row.add(adresseDeBien.getNpa());
            row.add(adresseDeBien.getLocalite());
            // Residence secondaire
            if (isResidenceDeclaration(dec)) {
                row.add(dec.getEstimation());
                row.add(dec.getLocation());
            } else {
                row.add(null);
                row.add(null);
            }
            // Location
            if (isLocationDeclaration(dec)) {
                row.add(dec.getNomPreNomLocation());
                row.add(dec.getFiscaleDate());
                row.add(dec.getDepartDate());
                if (BooleanUtils.isFalse(dec.getTaille())) {
                    row.add(FacesUtils.getMessage("cyberadmin.declarationDetail.taxCalculation.twoBeds"));
                } else {
                    row.add(FacesUtils.getMessage("cyberadmin.declarationDetail.taxCalculation.threeBeds"));
                }

                row.add(dec.getWeeks());
            } else {
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
            }
            // Sejours
            GuestExemptions guestExemptionsTotal = new GuestExemptions();
            GuestExemptions calculatedExemption = new GuestExemptions();
            if (!isResidenceDeclaration(dec) && !isLocationDeclaration(dec)) {
                guestExemptionsTotal = dec.getTotalExemption();
                calculatedExemption = dec.getCalculatedExemption();
            }

            row.add(guestExemptionsTotal.getHotes());
            row.add(guestExemptionsTotal.getNuits());
            row.add(calculatedExemption.getNuits());
            row.add(guestExemptionsTotal.getResidentiel());
            row.add(calculatedExemption.getResidentiel());
            // Reduction
            row.add(dec.getAjustComment());

            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_DECLARATION);
        sheetDeclaration.setOverriden(true);

        for (int i = 2; i < 2 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 2);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        //CHECKSTYLE:OFF MagicNumber
        for (int i = 2; i < 2 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 2);
            for (int j = 0; j < row.getCells().size(); j++) {
                switch (j) {
                case 5:
                case 6:
                case 30:
                case 31:
                    CellFormatter formatter = new CellFormatter(WebConstants.DATE_PATTERN_EXCEL);
                    sheetDeclaration.getFormatters().add(formatter);
                    break;
                default:
                    sheetDeclaration.getFormatters().add(null);
                    break;
                }
            }
        }
        //CHECKSTYLE:ON
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataAssujettis(List<Assujetti> assujettis) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Assujetti as : assujettis) {
            List<Object> row = new ArrayList<Object>();
            Person person = as.getPerson();
            Adresse adresse = as.getAdresse();
            row.add(as.getId());
            row.add(person.getNom());
            row.add(person.getPrenom());
            row.add(person.getOrganisation());
            row.add(adresse.getAdresse());
            row.add(adresse.getRue());
            row.add(adresse.getNo());
            row.add(adresse.getNpa());
            row.add(adresse.getLocalite());
            row.add(adresse.getCountryExport());
            row.add(as.getEmail());
            row.add(as.getTelephone());
            if (as.getUser() != null) {
                row.add(as.getUser().getUsername());
            } else {
                row.add(null);
            }
            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_ASSUJETTI);
        sheetDeclaration.setOverriden(true);

        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataUsers(List<User> users) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (User user : users) {
            List<Object> row = new ArrayList<Object>();
            Person person = user.getPerson();
            row.add(user.getId());
            row.add(user.getUsername());
            row.add(person.getNom());
            row.add(person.getPrenom());
            row.add(person.getOrganisation());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_TYPE_USER, user.getUserType()));
            row.add(user.getArcam());
            row.add(user.getCommuneCodeDisplayText());
            row.add(user.getEmail());
            row.add(user.getTelephone());
            row.add(user.getActivated());
            row.add(user.getValidated());
            row.add(user.getLastLogOnDate());

            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_USER);
        sheetDeclaration.setOverriden(true);

        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        
        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            for (int j = 0; j < row.getCells().size(); j++) {
                switch (j) {
                case 12:
                    CellFormatter formatter = new CellFormatter(WebConstants.DATE_PATTERN_EXCEL);
                    sheetDeclaration.getFormatters().add(formatter);
                    break;
                default:
                    sheetDeclaration.getFormatters().add(null);
                    break;
                }
            }
        }
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataMailingsInvitationReminder(List<Assujetti> assujettis) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Assujetti as : assujettis) {
            List<Object> row = new ArrayList<Object>();
            Person person = as.getPerson();
            Adresse adresse = as.getAdresse();
            row.add(person.getNom());
            row.add(person.getPrenom());
            row.add(person.getOrganisation());
            row.add(adresse.getAdresse());
            row.add(adresse.getRue());
            row.add(adresse.getNo());
            row.add(adresse.getNpa());
            row.add(adresse.getLocalite());
            row.add(adresse.getPays());
            row.add(as.getEmail());
            row.add(as.getDecTypeResient());
            row.add(as.getDecTypeLocation());
            row.add(as.getDecTypeHotel());
            row.add(as.getDecTypeInsitut());
            row.add(as.getDecTypeCamping());
            row.add(as.getDecTypeChambre());
            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_MAILING);
        sheetDeclaration.setOverriden(true);

        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataMailingsEnrolmentLetter(List<Assujetti> assujettis) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Assujetti as : assujettis) {
            List<Object> row = new ArrayList<Object>();
            Person person = as.getPerson();
            Adresse adresse = as.getAdresse();
            row.add(person.getNom());
            row.add(person.getPrenom());
            row.add(person.getOrganisation());
            row.add(adresse.getAdresse());
            row.add(adresse.getRue());
            row.add(adresse.getNo());
            row.add(adresse.getNpa());
            row.add(adresse.getLocalite());
            row.add(adresse.getPays());
            row.add(as.getEmail());
            row.add(as.getUser().getUsername());
            row.add(as.getUser().getDecodePassword());
            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_MAILING);
        sheetDeclaration.setOverriden(true);

        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataGenerateStatistic(List<Declaration> declarations) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Declaration dec : declarations) {
            List<Object> row = new ArrayList<Object>();

            BienTaxe bienTaxe = dec.getBienTaxe();
            Assujetti assujetti = bienTaxe.getAssujetti();
            Person person = assujetti.getPerson();
 
            row.add(dec.getId());
            row.add(person.getNom());
            row.add(bienTaxe.getCommuneCodeDisplayText());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_SHORT_DECLARATION_KEY,
                    bienTaxe.getDeclarationType()));
            
            row.add(dec.getFiscaleDate());
            row.add(dec.getSubmissionDate());
            
            if (bienTaxe.getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE) {
                row.add(dec.getEstimation());
                row.add(dec.getLocation());
            } else {
                row.add(null);
                row.add(null);
            }
            
            if (bienTaxe.getDeclarationType() == DeclarationTypeEnum.LOCATION) {
                row.add(dec.getArrivalDate());
                row.add(dec.getDepartDate());
                if (BooleanUtils.isFalse(dec.getTaille())) {
                    row.add(FacesUtils.getMessage("cyberadmin.declarationDetail.taxCalculation.twoBeds"));
                } else {
                    row.add(FacesUtils.getMessage("cyberadmin.declarationDetail.taxCalculation.threeBeds"));
                }
                row.add(dec.getWeeks());
            } else {
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
            }

            //CHECKSTYLE:OFF MagicNumber
            if (bienTaxe.getDeclarationType() == DeclarationTypeEnum.RESIDENCE_SECONDAIRE
                        || bienTaxe.getDeclarationType() == DeclarationTypeEnum.LOCATION) {
                for (int i = 0; i < 19; i++) {
                    row.add(null);
                }
            } else {
                List<GuestExemptions> exemptions = dec.getExemptions();
                Collections.sort(exemptions);
                for (GuestExemptions ex : exemptions) {
                    row.add(ex.getNuits());
                }
                for (GuestExemptions ex : exemptions) {
                    row.add(ex.getResidentiel());
                }
                GuestExemptions totalExemption = dec.getTotalExemption();
                GuestExemptions calculatedExemption = dec.getCalculatedExemption();
                row.add(totalExemption.getHotes());
                row.add(totalExemption.getNuits());
                row.add(calculatedExemption.getNuits());
                row.add(totalExemption.getResidentiel());
                row.add(calculatedExemption.getResidentiel());
            }
            row.add(dec.getAjustValue());
            row.add(dec.getMontant());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_STATUS_DECLARATION_KEY, dec.getStatus()));
            rows.add(row);
            //CHECKSTYLE:ON
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_STATISTIC);
        sheetDeclaration.setOverriden(true);

        for (int i = 2; i < 2 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 2);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        
        for (int i = 2; i < 2 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 2);
            for (int j = 0; j < row.getCells().size(); j++) {
                switch (j) {
                case 4:
                case 5:
                case 8:
                case 9:
                    CellFormatter formatter = new CellFormatter(WebConstants.DATE_PATTERN_EXCEL);
                    sheetDeclaration.getFormatters().add(formatter);
                    break;
                default:
                    sheetDeclaration.getFormatters().add(null);
                    break;
                } 
            }
        }
        
        return sheetDeclaration;
    }

    public static SheetDataDto prepareDataJournals(List<Log> journals) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (Log journal : journals) {
            List<Object> row = new ArrayList<Object>();
            row.add(journal.getTimestamp());
            row.add(journal.getUser().getUsername());
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_TYPE_USER, journal.getUser().getUserType()));
            row.add(FacesUtils.getI18nEnumLabel(WebConstants.PREFIX_TYPE_MESSAGE, journal.getMessageType()));
            row.add(journal.getMessage());
            rows.add(row);
        }

        SheetDataDto sheetDeclaration = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheetDeclaration.setName(WebConstants.SHEET_NAME_EXPORT_JOURNAL);
        sheetDeclaration.setOverriden(true);

        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }
        
        for (int i = 1; i < 1 + sheetDeclaration.getRows().size(); i++) {
            RowDataDto row = sheetDeclaration.getRows().get(i - 1);
            for (int j = 0; j < row.getCells().size(); j++) {
                switch (j) {
                case 0:
                    CellFormatter formatter = new CellFormatter(WebConstants.DATE_HOUR_PATTERN_EXCEL);
                    sheetDeclaration.getFormatters().add(formatter);
                    break;
                default:
                    sheetDeclaration.getFormatters().add(null);
                    break;
                } 
            }
        }
        
        return sheetDeclaration;
    }
}
