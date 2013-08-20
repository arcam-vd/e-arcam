/*
 * ExcelExportingService.java
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

package org.arcam.cyberadmin.service.poi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;

/**
 * service to export data to Excel file.
 * 
 * @author vlp
 * 
 */
public interface ExcelExportingService {

    /**
     * exports data to Excel file.
     * 
     * @param fileName
     *            the full path to the excel file.
     * @param data
     *            the list of the sheets with data to export. The order of the SheetDataDto items in the list is the
     *            order of the created sheets. However, this order can be affected by the property sheetIndex of the
     *            SheetDataDto.
     * @param append
     *            the flag indicates whether we need to append data on an available excel file or create a new file.
     *            Note that filling in data or creating a new sheet for the input stream depends on the parameter "data"
     * @param exportType
     *            the expected export type. There are only two supported types: XLS and XLSX.
     */
    void exportToExcel(String fileName, List<SheetDataDto> data, boolean append, ExcelFormatTypeEnum exportType);

    /**
     * exports data to Excel file.
     * 
     * @param input
     *            the inputStream which contains data to export. If this parameter is null that means we will create new
     *            workbook and output the result to outputStream. If this parameter is not null that means we will
     *            append the data to the current workbook in the inputStream. Note that Filling in data or creating a
     *            new sheet for the input stream depends on the parameter "data".
     * @param output
     *            the outStream which receive the exported data.
     * @param data
     *            the list of the sheets with data to export. The order of the SheetDataDto items in the list is the
     *            order of the created sheets. However, this order can be affected by the property sheetIndex of the
     *            SheetDataDto.
     * @param exportType
     *            the expected export type. There are only two supported types: XLS and XLSX.
     */
    void exportToExcel(InputStream input, OutputStream output, List<SheetDataDto> data, ExcelFormatTypeEnum exportType);
}
