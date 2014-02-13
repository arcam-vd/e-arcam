/*
 * ExportDataUtils.java
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

import org.apache.commons.lang3.StringUtils;


/**
 * Utility class processes on exported data.
 * 
 * @author mmn
 *
 */
public final class ExportDataUtils {

    private ExportDataUtils() {
        // to hide
    }
    
    /**
     * Enclose string values by "; escape " characters by doubling them.<br/>
     * Eg: William "Bill" Gates -> "William ""Bill"" Gates"
     * 
     * @param value
     *            the original string value.
     * @return the encoded string value.
     */
    public static String encodeStringValue(String value) {
        if (value == null) {
            return value;
        }
    
        String temp = value.trim();
        
//        if (StringUtils.isBlank(value)) {
//            return value.trim(); //return CresusExportConstant.QUOTE_CHAR + CresusExportConstant.QUOTE_CHAR;
//        }

        
        //StringBuilder result = new StringBuilder();
        
        // escape the double quotes by doubling them    -> REMOVED EPI
        //temp = temp.replace(CresusExportConstant.QUOTE_CHAR, CresusExportConstant.ESCAPED_QUOTE_CHAR);
        
        // replace new lines inside string by a space character
        temp = temp.replace(CresusExportConstant.TAB_CHARACTER, CresusExportConstant.SPACE_CHAR);
        temp = temp.replace(CresusExportConstant.NEW_LINE_CHARACTER, CresusExportConstant.SPACE_CHAR);
        temp = temp.replace(CresusExportConstant.LINE_END_CHARACTER, StringUtils.EMPTY);
        
        // enclose the value by double quote     -> REMOVED EPI
        //result.append(CresusExportConstant.QUOTE_CHAR).append(temp.trim()).append(CresusExportConstant.QUOTE_CHAR);
        return temp.trim();
    }
    
    /**
     * Formats the given number value.
     * 
     * @param value the original double value.
     * @return the formatted value.
     */
    public static String encodeNumberValue(Double value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }
        
        if (value.doubleValue() == 0d) {
            return "0";
        }
        
        return NumberUtils.formatNumber(value, CyberAdminConstants.NUMBER_FORMAT);
    }
    
    /**
     * Encodes the multi entries field.
     * 
     * @param values
     *            list of entries for the field.
     * @return
     */
    public static String encodeMultiEntryValue(Object...values) {
        return StringUtils.join(values, CresusExportConstant.ENTRY_SEPARATOR_CHAR);
    }
    
}
