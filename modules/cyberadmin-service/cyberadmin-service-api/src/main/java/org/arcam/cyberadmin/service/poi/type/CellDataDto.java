/*
 * CellDataDto.java
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

package org.arcam.cyberadmin.service.poi.type;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * DTO represents a cell in the sheet.
 * 
 * @author vlp
 * 
 */
public class CellDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private CellDataTypeEnum dataType;
    private Object value;
    private Integer cellNum;

    /**
     * constructs a CellDataDto with value and data type. If the value or dataType is null then a blank cell is created.
     * For the cell which contains date value, the format of the output will be dd-MM-yyyy by default. If you want to
     * change these format, please specify a CellFormatter in the sheet. The formatter will be applied for all the cells
     * in the same column.
     * 
     * @param value
     *            value of the cell
     * @param dataType
     *            data type of the cell
     */
    public CellDataDto(Object value, CellDataTypeEnum dataType) {
        this.setValue(value);
        this.setDataType(dataType);
    }

    /**
     * gets the data type of the cell.
     * 
     * @return
     */
    public CellDataTypeEnum getDataType() {
        return dataType;
    }

    /**
     * sets the data type of the cell. There are only 4 types: TEXT, NUMBER, DATE and FORMULA. <br>
     * A note when setting the data type: When you want to insert an empty cell you create new CellDataDto object in any
     * of these types and set the value to null. For the cell which contains date value, the format of the output will
     * be dd-MM-yyyy by default. If you want to change these format, please specify a CellFormatter in the sheet. The
     * formatter will be applied for all the cells in the same column. value is null.
     * 
     * @param dataType
     */
    public void setDataType(CellDataTypeEnum dataType) {
        this.dataType = dataType;
    }

    /**
     * gets the value of the cell. This value must be suitable with the data type of the cell.
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * sets the value of the cell. Some notes when setting the value for a formula cell:<br>
     * <li>Your formula must not start with "=" like when you type in the excel.</li> <li>Your formula must be the ones
     * that are predefined in the Excel. For the user-defined formulas we need to turn on the auto-recalculation formula
     * feature in Excel so that the formula can be applied. Otherwise, what you can see in this cell is the formula
     * string instead of the result value.</li> <li>For the cell which contains date value, the format of the output
     * will be dd-MM-yyyy by default. If you want to change these format, please specify a CellFormatter in the sheet.
     * The formatter will be applied for all the cells in the same column.</li>
     * 
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * gets the flag indicates if the cell is new or not. When the cellNum is not null then this cell is considered an
     * available cell in the row.
     * 
     * @return
     */
    public boolean isNewCell() {
        return cellNum == null;
    }

    /**
     * gets the cell number in 0-base in the current row.
     * 
     * @return
     */
    public Integer getCellNum() {
        return cellNum;
    }

    /**
     * sets the cell number in 0-base in the current row. This value should be set when we are working with an available
     * cell in the template.
     * 
     * @param cellNum
     */
    public void setCellNum(Integer cellNum) {
        this.cellNum = cellNum;
    }

    /**
     * gets the value of the cell as string. This getter just calls the toString() of the cell's value. When the value
     * is null an empty string is returned.
     * 
     * @return
     */
    // CHECKSTYLE:OFF AvoidInlineConditionals
    public String getValueAsString() {
        return value == null ? StringUtils.EMPTY : value.toString();
    }
    // CHECKSTYLE:ON
}
