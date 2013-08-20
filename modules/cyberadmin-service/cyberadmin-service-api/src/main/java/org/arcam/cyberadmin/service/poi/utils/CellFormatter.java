/*
 * CellFormatter.java
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

package org.arcam.cyberadmin.service.poi.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * The formatter for the column of the sheet. This formatter should be used in case you don't want to use the default
 * format for the column. This formatter doesn't support hidden style or locked style for cell because these styles can
 * have effect only when the sheet is protected.
 * 
 * @author vlp
 * 
 */
public class CellFormatter {
    private String dataFormatString;
    private Short alignment;
    private Boolean wrapText;

    /**
     * Default constructor creates an empty formatter.
     */
    public CellFormatter() {
    }

    /**
     * constructs a formatter to format a cell with a specific format string.
     * 
     * @param format
     *            the format string of the cell
     */
    public CellFormatter(String format) {
        dataFormatString = format;
    }

    /**
     * gets the format string for the formatter.
     * 
     * @return
     */
    public String getDataFormatString() {
        return dataFormatString;
    }

    /**
     * sets the format string for the formatter. When a blank format string is set for this property it means that the
     * formatter will use the available format of the column if possible.<b>Note: for Date's format string, the format
     * character for month is OK for both "M" and "m". This means that "dd-MM-yy" and "dd-mm-yy" are treated as the same
     * format</b>
     * 
     * @param dataFormatString
     */
    public void setDataFormatString(String dataFormatString) {
        this.dataFormatString = dataFormatString;
    }

    /**
     * gets the alignment for the cell.
     * 
     * @return
     */
    public Short getAlignment() {
        return alignment;
    }

    /**
     * sets the alignment for the cell. When a null value is set for this property it means that the formatter will use
     * the available alignment of the column if possible.
     * 
     * @param alignment
     *            the alignment constant defined in {@link org.apache.poi.ss.usermodel.CellStyle}. When a null value is
     *            set to this formatter it means that this formatter will use the
     */
    public void setAlignment(Short alignment) {
        this.alignment = alignment;
    }

    /**
     * gets the flag shows if the text in this cell is wrapped or not.
     * 
     * @return
     */
    public Boolean getWrapText() {
        return wrapText;
    }

    /**
     * sets the flag shows if the text in this cell is wrapped or not. When a null value is set for this property it
     * means that the formatter will use the available "wrap text" style of the column if possible.
     * 
     * @param wrapText
     */
    public void setWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
    }

    /**
     * checks if the formatter is not empty. An empty formatter will be ignored when filling in the data to the cell.
     * 
     * @return
     */
    public boolean isNotEmptyFormatter() {
        return !(StringUtils.isBlank(dataFormatString) && alignment == null && wrapText == null);
    }
}
