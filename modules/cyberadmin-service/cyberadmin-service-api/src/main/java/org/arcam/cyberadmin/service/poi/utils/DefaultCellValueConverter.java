/*
 * DefaultCellValueConverter.java
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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * The default converter which is used in ExcelExportingUtil.convert2DListToSheetDto(List<List<Object>>) method.
 * 
 * @author vlp
 * 
 */
public class DefaultCellValueConverter implements CellValueConverter {

    public DefaultCellValueConverter() {
    }

    @Override
    public boolean isNull(Object value) {
        return value == null;
    }

    @Override
    public boolean isString(Object value) {
        return value instanceof String;
    }

    @Override
    public boolean isDate(Object value) {
        return value instanceof Date;
    }

    @Override
    public boolean isNumber(Object value) {
        return NumberUtils.isNumber(value.toString());
    }

    @Override
    public boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public String convertToString(Object value) {
        return value.toString();
    }

    @Override
    public Date convertToDate(Object value) {
        return (Date) value;
    }

    @Override
    public Double convertToNumber(Object value) {
        return NumberUtils.createDouble(value.toString());
    }

    @Override
    public String convertToBoolean(Object value) {
        //return BooleanUtils.toString((Boolean) value, "yes", "no");
        return BooleanUtils.toString((Boolean) value, "Oui", "Non");
    }

    @Override
    public Object convertToNull(Object value) {
        return null;
    }
    
    @Override
    public String convertToEmpty(Object value) {
        return StringUtils.EMPTY;
    }
}
