/*
 * CresusExportConstant.java
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

/**
 * The class that holds all common constant used in data exportation to Cresus.
 * 
 * @author mmn
 *
 */
public final class CresusExportConstant {

    private CresusExportConstant() {
        // to hide
    }
    
    /**
     * The tab character used in exported CSV file.
     */
    public static final String TAB_CHARACTER = "\t";
    
    /**
     * The new line character used in exported CSV file.
     */
    public static final String NEW_LINE_CHARACTER = "\n";
    
    /**
     * The line end character.
     */
    public static final String LINE_END_CHARACTER = "\r";
    
    /**
     * Character encoding used for the exported CSV file.
     */
    public static final String CHARACTER_ENCODING = "ISO8859_1";
    
    /**
     * Separator for each field in the exported CSV file.
     */
    public static final String FIELD_SEPARATOR_CHAR = "\t";
    
    /**
     * Separator used for line separator in a field.
     */
    public static final String IN_FIELD_LINE_SEPARATOR_CHAR = "|";
    
    /**
     * Entry separator in a multiple entry field.
     */
    public static final String ENTRY_SEPARATOR_CHAR = "\\";
    
    /**
     * The character to use for quoted elements in the exported CSV file..
     */
    public static final String QUOTE_CHAR = "\"";
    
    /**
     * The character to use for escaping quote char in the exported CSV file.
     */
    public static final String ESCAPED_QUOTE_CHAR = "\"\"";
    
    /**
     * The space character used in exported CSV file.
     */
    public static final String SPACE_CHAR = " ";
    
    /**
     * The name of the file which is used to test the accessing to the exported folder.
     */
    public static final String TEST_FILE = "cresus.test";
    
    /**
     * The name of the lock file.
     */
    public static final String LOCK_FILE = "cresus.lock";

    /**
     * The pattern of the export filename.
     */
    public static final String EXPORT_FILE = "{0}_{1}.txt";
    
    /**
     * The file separator.
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    /**
     * The separator of the values in configuration file.
     */
    public static final String CONFIGURATION_PARAM_SEPARATOR = ",";
    
    /**
     * Designation for residence secondary declaration.
     */
    public static final String DESIGNATION_RESIDENCE_SECONDAIRE = "Taxe de résidence sécondaire";
    
    /**
     * Weekly designation for location declaration.
     */
    public static final String DESIGNATION_LOCATION_WEEK = "Taxe par semaine";
    
    /**
     * Flat designation for location declaration. 
     */
    public static final String DESIGNATION_LOCATION_FLAT = "Taxe forfaitaire";
    
    /**
     * Designation for hotel declaration.
     */
    public static final String DESIGNATION_HOTEL = "Taxe par nuitée";
    
    /**
     * Designation for chambre declaration.
     */
    public static final String DESIGNATION_CHAMBRE = "Taxe par nuitée";
    
    /**
     * Night designation for camping declaration.
     */
    public static final String DESIGNATION_CAMPING_NIGHT = "Taxe par nuitée";
    
    /**
     * Extended stay designation for camping declaration.
     */
    public static final String DESIGNATION_CAMPING_EXTENDED_STAY = "Taxe séjour prolongé";
    
    /**
     * Night designation for institute declaration.
     */
    public static final String DESIGNATION_INSTITUTE_NIGHT = "Taxe par nuitée";
    
    /**
     * Extended stay designation for institute declaration.
     */
    public static final String DESIGNATION_INSTITUTE_EXTENDED_STAY = "Taxe séjour prolongé";
    
    /**
     * Weekly unit for location declaration. 
     */
    public static final String UNIT_LOCATION_WEEK = "semaines";
    
    /**
     * Flat unit for location declaration. 
     */
    public static final String UNIT_LOCATION_FLAT = "forfait";
    
    /**
     * Night unit for hotel declaration.
     */
    public static final String UNIT_HOTEL_NIGHT = "nuitée";
    
    /**
     * Night unit for chambre declaration.
     */
    public static final String UNIT_CHAMBRE_NIGHT = "nuitée";
    
    /**
     * Night unit for camping declaration.
     */
    public static final String UNIT_CAMPING_NIGHT = "nuitée";
    
    /**
     * Personal unit for camping declaration.
     */
    public static final String UNIT_CAMPING_PERSONAL = "personne";
    
    /**
     * Night unit for institute declaration.
     */
    public static final String UNIT_INSTITUTE_NIGHT = "nuitée";
    
    /**
     * Personal unit for institute declaration.
     */
    public static final String UNIT_INSTITUTE_PERSONAL = "personne";
    
    /**
     * Facture label for residence secondaire declaration.
     */
    public static final String DESC_RESIDENCE_SECONDAIRE =     
		    "Taxe annuelle de residence secondaire intercommunale relative à l''année {0}|"+
		    "Propriété : {1} {2}, {3} {4}|EGID: {5}, EWID: {6}|Catégorie : {7}";

    
    /**
     * Facture label for location declaration.
     */
    public static final String DESC_LOCATION =    
    "Taxe de résidence secondaire intercommunale relative à la période du {0} à {1}|" +
    "Locataire: {2} {3}|Propriété : {4} {5}, {6} {7}|EGID: {8}, EWID: {9}|Catégorie : {10}";

    
    /**
     * Facture label for other declarations.
     */
    public static final String DESC_OTHER = "Taxe de séjour intercommunale relative "
            + "à la période du {0} à {1}|Catégorie: {2}";
}
