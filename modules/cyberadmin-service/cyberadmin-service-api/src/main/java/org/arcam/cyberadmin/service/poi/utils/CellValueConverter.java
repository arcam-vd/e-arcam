/*
 * CellValueConverter.java
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

/**
 * the interface to support user-defined converter. This converter will be used in the
 * ExcelExportingUtil.convert2DListToSheetDto(List<List<Object>>, CellValueConverter) method when we need a custom
 * converter.
 * 
 * @author vlp
 * 
 */
public interface CellValueConverter {
    /**
     * checks if the input value is null.
     * 
     * @param value
     *            the value to check
     * @return check result
     */
    boolean isNull(Object value);

    /**
     * checks if the input value is of the type String.
     * 
     * @param value
     *            the value to check
     * @return check result
     */
    boolean isString(Object value);

    /**
     * checks if the input value is of the type java.util.Date.
     * 
     * @param value
     *            the value to check
     * @return check result
     */
    boolean isDate(Object value);

    /**
     * checks if the input value is one of the following types: Long, Float, Integer, Double, BigDecimal and Short.
     * 
     * @param value
     *            the value to check
     * @return check result
     */
    boolean isNumber(Object value);

    /**
     * checks if the input value is of the type Boolean or boolean.
     * 
     * @param value
     *            the value to check
     * @return check result
     */
    boolean isBoolean(Object value);

    /**
     * converts the input value to String. Note that if the value is null then the method convertToNull() is used
     * instead.
     * 
     * @param value
     *            the value to convert
     * @return a string value corresponding to the input value
     */
    String convertToString(Object value);

    /**
     * converts the input value to Date. Note that if the value is null then the method convertToNull() is used instead.
     * 
     * @param value
     *            the value to convert
     * @return a java.util.Date value corresponding to the input value
     */
    Date convertToDate(Object value);

    /**
     * converts the input value to Double. Note that if the value is null then the method convertToNull() is used
     * instead.
     * 
     * @param value
     *            the value to convert
     * @return a Double value corresponding to the input value
     */
    Double convertToNumber(Object value);

    /**
     * converts the input value to a string corresponding to the boolean value. Note that if the value is null then the
     * method convertToNull() is used instead.
     * 
     * @param value
     *            the value to convert
     * @return the input value to a string corresponding to the boolean value.
     */
    String convertToBoolean(Object value);

    /**
     * converts any value to null value. Normally, null value will be null. However, there are cases when we want to
     * specify a value for null values, e.g. "-". In these cases, this method can solve that problem easily.
     * 
     * @param value
     *            the value to convert
     * @return null value always
     */
    Object convertToNull(Object value);
    
    /**
     * converts any value to string empty.
     * 
     * @param value
     *            the value to convert
     * 
     * @return the empty string always
     */
    String convertToEmpty(Object value);
}
