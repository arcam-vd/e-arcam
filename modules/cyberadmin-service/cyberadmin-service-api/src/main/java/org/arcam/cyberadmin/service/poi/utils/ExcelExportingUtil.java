/*
 * ExcelExportingUtil.java
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

import java.util.Date;
import java.util.List;

import org.arcam.cyberadmin.service.poi.type.CellDataDto;
import org.arcam.cyberadmin.service.poi.type.CellDataTypeEnum;
import org.arcam.cyberadmin.service.poi.type.RowDataDto;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;

/**
 * util class for exporting data to excel file.
 * 
 * @author vlp
 * 
 */
public final class ExcelExportingUtil {

    private ExcelExportingUtil() {
    }

    /**
     * converts value to Double. In case an error happens then the exception is swallowed and this method returns null
     * value.
     * 
     * @param value
     *            the value to convert
     * @return converted value
     */
    public static final Double convertToDoubleOrNull(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        try {
            return new Double(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * casts the value to Date if possible. Otherwise, returns null.
     * 
     * @param value
     *            value to cast
     * @return cast result
     */
    public static final Date castToDateOrNull(Object value) {
        if (value == null || !(value instanceof Date)) {
            return null;
        }

        return (Date) value;
    }

    // CHECKSTYLE:OFF LineLength
    /**
     * converts a 2D list to SheetDataDto entity using the default converter. The null element in the list means that
     * the cell at that position is ignored. Some notes about the conversion: <br>
     * <li>the first element in the list is used as the header row.</li> <li>only following types (and their primitive
     * types) are supported: String, Date, Boolean, Long, Float, Integer, Double, BigDecimal, Short</li> <li>If the
     * value at [m,n] position in the 2D list is null then a blank cell is created at that position.</li> <li>For
     * String, the method toString() is used to convert value to String</li> <li>For Date, the value is casted directly
     * to java.util.Date</li> <li>For Boolean, true value is converted to "yes" and false value is converted to "no"</li>
     * <li>For Number, the value is tested with NumberUtils.isNumber() to make sure it is a valid number. Then, it is
     * converted to Double value to fill in the cell. Note: since the value is always converted to Double we must care
     * about losing precision when converting from BigDecimal to Double, e.g the value "299792.457999999984" when being
     * converted from BigDecimal to Double the result will be "299792.458".</li> <li>for the other data types, an
     * IllegalArgumentException is thrown. In case you need to convert data in your own way, please use the
     * convert2DListToSheetDto(List<List<Object>>, )</li> <li><b>This util has a limitation that it can't create formula
     * cells. You must set the style of the corresponding cell to FORMULA yourself</b></li>
     * 
     * @param rawData
     *            the data to convert.
     * @param headerIncluded
     *            the flag to determine if the rawData contains the header or not.
     * @return
     */
    // CHECKSTYLE:ON
    public static final SheetDataDto convert2DListToSheetDto(List<List<Object>> rawData, boolean headerIncluded) {
        return convert2DListToSheetDto(rawData, new DefaultCellValueConverter(), headerIncluded);
    }

    /**
     * converts a 2D list to SheetDataDto entity using the user-defined converter. Some notes about the conversion: <br>
     * <li>the first element in the list is used as the header row.</li> <li>only following generic types are supported:
     * String, Date and Number. You can define your own way to convert other data types by implementing the method in
     * the converter. However, the format of the converted values will be restricted to String, Date and Number only.
     * For example, if you define to convert a number to string then the cell value will be String, no calculation can
     * be applied on that cell unless you open that excel file and modify the format of that cell manually.</li> <li>
     * <b>This util has a limitation that it can't create formula cells. You must set the style of the corresponding
     * cell to FORMULA yourself</b></li>
     * 
     * @param rawData
     *            the data to convert
     * @param converter
     *            the user-defined converter
     * @param headerIncluded
     *            the flag to determine if the rawData contains the header or not.
     * @return
     */
    public static final SheetDataDto convert2DListToSheetDto(List<List<Object>> rawData, CellValueConverter converter,
            boolean headerIncluded) {
        SheetDataDto result = new SheetDataDto();

        int rowIdx = 0;
        for (List<Object> row : rawData) {
            RowDataDto rowDto = new RowDataDto();

            int colIdx = 0;
            for (Object value : row) {
                CellDataDto cellDto = new CellDataDto(null, CellDataTypeEnum.TEXT);
                if (!converter.isNull(value)) {
                    if (converter.isString(value)) {
                        cellDto.setDataType(CellDataTypeEnum.TEXT);
                        cellDto.setValue(converter.convertToString(value));

                    } else if (converter.isDate(value)) {
                        cellDto.setDataType(CellDataTypeEnum.DATE);
                        cellDto.setValue(converter.convertToDate(value));

                    } else if (converter.isNumber(value)) {
                        cellDto.setDataType(CellDataTypeEnum.NUMBER);
                        cellDto.setValue(converter.convertToNumber(value));

                    } else if (converter.isBoolean(value)) {
                        cellDto.setDataType(CellDataTypeEnum.TEXT);
                        cellDto.setValue(converter.convertToBoolean(value));

                    } else {
                        throw new IllegalArgumentException(String.format(
                                "Can't convert the value in the cell[%d,%d] to supported types", rowIdx, colIdx));
                    }
                } else {
                    //cellDto.setValue(converter.convertToNull(value));
                    cellDto.setValue(converter.convertToEmpty(value));
                }

                rowDto.getCells().add(cellDto);
                colIdx++;
            }

            if (rowIdx == 0 && headerIncluded) {
                result.setHeaderRow(rowDto);
            } else {
                result.getRows().add(rowDto);
            }
            rowIdx++;
        }

        return result;
    }
}
