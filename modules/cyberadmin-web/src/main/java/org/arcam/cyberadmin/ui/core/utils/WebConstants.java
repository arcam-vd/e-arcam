/*
 * WebConstants.java
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

import java.util.HashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Contains constants for web presentation.
 * 
 * @author mmn
 *
 */
@Component("webConstants")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class WebConstants extends HashMap<String, Object> implements InitializingBean {

    private static final long serialVersionUID = 1L;
    
    /**
     * Maximum row per page in data table.
     */
    public static final long MAX_ROW = 15;
    
    /**
     * List of page sizes used in data table.
     */
    public static final String PAGE_SIZES = "15,25,50";
    
    /**
     * Format of date shown on GUI.
     */
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    /**
     * Format of date shown on GUI.
     */
    public static final String DATE_PATTERN_EXCEL = "dd.mm.yyyy";

    /**
     * Format of only month and year of date shown on GUI.
     */
    public static final String HOTEL_CAMPING_DATE_PATTERN = "MMM yyyy";
    
    /**
     * Format of only month and year of date shown on GUI.
     */
    public static final String RESIDENCE_DATE_PATTERN = "yyyy";

    /**
     * Format of date used common in application.
     */
    public static final String SHORTENED_DATE_PATTERN = "dd.MM.yy";

    /**
     * Format of date time shown on GUI.
     */
    public static final String DATE_HOUR_PATTERN = "HH:mm dd.MM.yyyy";
    
    /**
     * Format of date time shown on Excel.
     */
    public static final String DATE_HOUR_PATTERN_EXCEL = "hh:mm dd.mm.yyyy";

    /**
     * The name of column index parameter send to server to process sorting.
     */
    public static final String COLUMN_SORT_PARAMETER = "columnSortName";
    
    /**
     * The sort order name of column.
     */
    public static final String COLUMN_SORT_ORDER = "columnSortOrder";
    
    /**
     * HTML space charater.
     */
    public static final String HTML_SPACE = "<span>&nbsp;</span>";
    
    /**
     * French language key.
     */
    public static final String LANGUAGE_FRANCE_KEY = "fr";
    
    /**
     * English language key.
     */
    public static final String LANGUAGE_ENGLISH_KEY = "en";
    
    /**
     * Declaration status i18n key.
     */
    public static final String PREFIX_STATUS_DECLARATION_KEY = "cyberadmin.common.declaration.status";
    /**
     * Declaration type i18n key.
     */
    public static final String PREFIX_DECLARATION_KEY = "cyberadmin.common.declaration.type";
    
    /**
     * Declaration short type i18n key.
     */
    public static final String PREFIX_SHORT_DECLARATION_KEY = "cyberadmin.common.declaration.type.short";
    /**
     * Periodicite type i18n key.
     */
    public static final String PREFIX_PERIODCITE_KEY = "cyberadmin.common.periodicite.type";
    /**
     * Declaration type i18n key.
     */
    public static final String PREFIX_DECLARATION_NEW_FOR_TYPE = "cyberadmin.declarationDetail.title";
    
    /**
     * Exoneration type i18n key.
     */
    public static final String PREFIX_TYPE_EXONERATION = "cyberadmin.exonerations.type";
    
    /**
     * Exoneration type i18n key.
     */
    public static final String PREFIX_SHORT_TYPE_EXONERATION = "cyberadmin.exonerations.typeshort";
    
    /**
     * Adresse type i18n key.
     */
    public static final String PREFIX_TYPE_ADRESSE = "cyberadmin.common.adresse.type";
    
    /**
     * Adresse type i18n key.
     */
    public static final String PREFIX_TYPE_USER = "cyberadmin.common.user.type";
    
    /**
     * Journal type i18n message prefix.
     */
    public static final String PREFIX_JOURNAL_TYPE = "cyberadmin.common.journal.type";
    
    /**
     * I18n Key of submit Total.
     */
    public static final String SUBMIT_TOTAL_KEY = "cyberadmin.declarationDetail.submitTotal";
    /**
     * I18n key of tarif.
     */
    public static final String TARIF_KEY = "cyberadmin.declarationDetail.tarif";
    /**
     * I18n key of subTotal.
     */
    public static final String SUB_TOTAL_KEY = "cyberadmin.declarationDetail.subTotal";
    
    /**
     * Empty search result i18n key.
     */
    public static final String EMPTY_SEARCH_RESULT_INFO_KEY = "cyberadmin.common.table.noResult";
    
    /**
     * Invalid date range error message key.
     */
    public static final String INVALID_DATE_RANGE_ERROR_KEY = "cyberadmin.common.invalidDateRange";
    
    /**
     * Switzerland code.
     */
    public static final String SWITZERLAND_CODE = "CH";
    
    /**
     * Switzerland currency.
     */
    public static final String SWITZERLAND_CURRENCY = "CHF";
    
    /**
     * Number of exonerations.
     */
    
    public static final int NUM_EXONERATION = 8;
    
    /**
     * Pattern to be checked against the input value, make it only contain alphabetic characters and numbers.
     */
    public static final String ALPHANUMERICAL_PATTERN = "^[a-zA-Z0-9]+$";
   
    /**
     * Link to file template declaration.
     */
    public static final String TEMPLATES_DECLARATIONS_EXPORT = "/templates/Declarations_0.3.xls";
    
    /**
     * Export declaration file name.
     * 
     */
    public static final String EXPORTED_DECLARATION_FILENAME = "Declaration.xls";
    
    /**
     * Export declaration to billing file name.
     * 
     */
    public static final String EXPORTED_DECLARATION_TO_BILLING_FILENAME = "Declarations_To_Billing.xls";
    
    /**
     * Mine type for excel.
     */
    public static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";
    
    /**
     * Mime type for PDF.
     */
    public static final String MIME_TYPE_PDF = "application/pdf";
    
    /**
     * Sheetname of exported declaration file.
     */
    public static final String SHEET_NAME_EXPORT_DECLARATION = "D\u00E9clarations";
    /**
     * Export assujetti file name.
     * 
     */
    public static final String EXPORTED_ASSUJETTI_FILENAME = "Assujetti.xls";
    /**
     * Sheetname of exported assujetti file.
     */
    public static final String SHEET_NAME_EXPORT_ASSUJETTI = "Assujettis";
    /**
     * Export assujetti file name.
     * 
     */
    public static final String EXPORTED_USER_FILENAME = "Users.xls";
    /**
     * Sheetname of exported user file.
     */
    public static final String SHEET_NAME_EXPORT_USER = "Utilisateurs";
    /**
     * Prefix key message bundle of type mailing. 
     */
    public static final String PREFIX_TYPE_MAILING = "cyberadmin.generateMailing.type";
    /**
     * Sheetname of exported mailing file.
     */
    public static final String SHEET_NAME_EXPORT_MAILING = "Data";
    /**
     * Export mailing file name.
     * 
     */
    public static final String EXPORTED_MAILING_FILENAME = "Mailing_1.xls";
    /**
     * Export enrolment mailing file name.
     * 
     */
    public static final String EXPORTED_MAILING_ENROLMENT_FILENAME = "Mailing_2.xls";

    /**
     * Sheetname of exported statistic file.
     */
    public static final String SHEET_NAME_EXPORT_STATISTIC = "Data";
    /**
     * Export statistic file name.
     */
    public static final String EXPORTED_STATISTIC_FILENAME = "Statistics.xls";
    /**
     * Export statistic prefix file name.
     */
    public static final String EXPORTED_STATISTIC_FREFIX_FILENAME = "eARCAM_Statistiques_";
    
    /**
     * Excel type.
     */
    public static final String EXCEL_TYPE = ".xls";

    /**
     * Default number format of CyberAdmin.
     */
    public static final String NUMBER_FORMAT = "##,###,###.####";

    /**
     * Month key.
     */
    public static final String PREFIX_MONTH_KEY = "cyberadmin.month.";

    /**
     * Negative number error key.
     */
    public static final String NEGATIVE_NUBMER_ERROR_KEY = "cyberadmin.common.negativeValue";
    
    /**
     * Arrival equals zero.
     */
    public static final String ARRIVAL_EQUAL_ZERO_ERROR_KEY = "cyberadmin.common.arrivalEqualZero";

    /**
     * Arrival greater than night in other.
     */
    public static final String ARRIVAL_GREATER_THAN_NIGHT_RESIENTIEL_ORTHER_ERROR_KEY 
                    = "cyberadmin.common.arrivalGreaterThanNightOther";

    /**
     * Arrival greater than total in Total des personnes.
     */
    public static final String ARRIVAL_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY 
                    = "cyberadmin.common.arrivalGreaterThanCaculated";

    /**
     * Night greater than total in Total des personnes.
     */
    public static final String NIGHT_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY 
                    = "cyberadmin.common.nightGreaterThanCaculated";

    /**
     * Residentiel greater than total in Total des personnes.
     */
    public static final String RESIDENTIEL_GREATER_THAN_NUMBER_TOTAL_CACULATED_ERROR_KEY 
                    = "cyberadmin.common.residentielGreaterThanCaculated";
    /**
     * Arrival greater than night in total.
     */
    public static final String ARRIVAL_GREATER_THAN_NIGHT_RESIDENTIEL_TOTAL_ERROR_KEY 
                            = "cyberadmin.common.arrivalGreaterThanNightTotal";

    /**
     * Generate mailing mandatory mail.
     */
    public static final String GENERATE_MAILING_MANDATORY_MAIL = "cyberadmin.generateMailing.mandatory.mail";

    /**
     * Generate mailing mandatory email and mail.
     */
    public static final String GENERATE_MAILING_MANDATORY_EMAIL_MAIL = "cyberadmin.generateMailing.mandatory.mailEmail";

    /**
     * Npa mandatory.
     */
    public static final String FILLING_WRONG_NPA_LOCALITE_ERROR_KEY = 
                                    "cyberadmin.declarationDetail.mandatory.wrong.Npa";
    /**
     * Exporting journal sheet name.
     */
    public static final String SHEET_NAME_EXPORT_JOURNAL = "Journal";

    /**
     * Prefix type message.
     */
    public static final String PREFIX_TYPE_MESSAGE = "cyberadmin.common.journal.type";

    /**
     * Export journal file name.
     * 
     */
    public static final String EXPORTED_JOURNAL_FILENAME = "Journal.xls";
    
    /**
     * Allow type upload.
     */
    public static final String ALLOW_TYPES = "/(\\.|\\/)(doc|docx|xls|xlsx|jpg|png|pdf)$/";
    
    /**
     * Size limit upload file.
     */
    @Value("${fileUpload.size}")
    private long sizeLimit;
    
    public long getSizeLimit() {
        return sizeLimit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // init map values to use in .xhtml page
        put("COLUMN_SORT_PARAMETER", COLUMN_SORT_PARAMETER);
        put("COLUMN_SORT_ORDER", COLUMN_SORT_ORDER);
        put("DATE_PATTERN", DATE_PATTERN);
        put("HOTEL_CAMPING_DATE_PATTERN", HOTEL_CAMPING_DATE_PATTERN);
        put("RESIDENCE_DATE_PATTERN", RESIDENCE_DATE_PATTERN);
        put("DATE_HOUR_PATTERN", DATE_HOUR_PATTERN);
        put("HTML_SPACE", HTML_SPACE);
        put("MAX_ROW", MAX_ROW);
        put("PAGE_SIZES", PAGE_SIZES);
        put("ALPHANUMERICAL_PATTERN", ALPHANUMERICAL_PATTERN);
        put("PREFIX_SHORT_DECLARATION_KEY", PREFIX_SHORT_DECLARATION_KEY);
        put("NUMBER_FORMAT", NUMBER_FORMAT);
        put("ALLOW_TYPES", ALLOW_TYPES);
        put("SIZE_LIMIT", sizeLimit);
    }
}
