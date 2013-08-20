/*
 * ExcelExportingServiceTest.java
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

package org.arcam.cyberadmin.service.poi.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.CellDataDto;
import org.arcam.cyberadmin.service.poi.type.CellDataTypeEnum;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.FreezePanelDto;
import org.arcam.cyberadmin.service.poi.type.RowDataDto;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.service.poi.utils.CellFormatter;
import org.arcam.cyberadmin.service.poi.utils.ExcelExportingUtil;
import org.testng.annotations.Test;

/**
 * Exporting test class for all service exporting to excel.
 * 
 * @author vlp
 *
 */

//@Test
public class ExcelExportingServiceTest {
    private ExcelExportingService excelExportingService = new ExcelExportingServiceImpl();

    /**
     * This is the simplest test case. Its purpose is to create an excel file (in both XLS and XLSX format) with the
     * input data.
     */
    //@Test
    public void testCreateNewFile() {
        List<SheetDataDto> data = new ArrayList<SheetDataDto>();
        RowDataDto headerRow = new RowDataDto();
        headerRow.getCells().add(new CellDataDto("Col 1", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 2", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 3", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 4", CellDataTypeEnum.TEXT));

        SheetDataDto sheet1 = new SheetDataDto();
        sheet1.setName("First sheet");
        sheet1.setHeaderRow(headerRow);

        RowDataDto row1 = new RowDataDto();
        row1.getCells().add(new CellDataDto(1d, CellDataTypeEnum.NUMBER));
        row1.getCells().add(new CellDataDto("IF(A2 > 0, \"1\", \"0\")", CellDataTypeEnum.FORMULAR));
        row1.getCells().add(new CellDataDto("123456789", CellDataTypeEnum.NUMBER));
        row1.getCells().add(new CellDataDto(new Date(), CellDataTypeEnum.DATE));

        sheet1.getRows().add(row1);
        data.add(sheet1);

        excelExportingService.exportToExcel("t1.xls", data, false, ExcelFormatTypeEnum.XLS);
        excelExportingService.exportToExcel("t1.xlsx", data, false, ExcelFormatTypeEnum.XLSX);
    }

    /**
     * The purpose of this test case is to demonstrate appending data to an available excel file (in both XLS and XLSX
     * format) and the usage of ExcelExportingUtil.convert2DListToSheetDto(List<List<Object>>) method. The appended
     * sheet will be at the first position of the workbook.
     */
    //CHECKSTYLE:OFF MagicNumber
    //@Test(dependsOnMethods = { "testCreateNewFile" })
    public void testAppendToFile() {
        List<Object> row1 = new ArrayList<Object>();
        row1.add("Col 1");
        row1.add("Col 2");
        row1.add("Col 3");
        row1.add("Col 4");
        List<Object> row2 = new ArrayList<Object>();
        row2.add(123456L);
        row2.add("test string");
        row2.add(true);
        row2.add(new Date());
        List<List<Object>> rawData = new ArrayList<List<Object>>();
        rawData.add(row1);
        rawData.add(row2);
        SheetDataDto sheet1 = ExcelExportingUtil.convert2DListToSheetDto(rawData, true);
        sheet1.setName("Append sheet");
        sheet1.setFreezePanel(new FreezePanelDto(0, 1));

        List<SheetDataDto> data = new ArrayList<SheetDataDto>();
        data.add(sheet1);
        sheet1.setSheetIndex(0);
        excelExportingService.exportToExcel("t1.xls", data, true, ExcelFormatTypeEnum.XLS);
        excelExportingService.exportToExcel("t1.xlsx", data, true, ExcelFormatTypeEnum.XLSX);
    }
    //CHECKSTYLE:ON
    /**
     * the purpose of this test case is to demonstrate the ability to work with inputStream and outputStream of the
     * service. There are two use cases: create new excel workbook (the input stream is null) and fill in data to an
     * available excel workbook (input stream is not null).
     * 
     * @throws FileNotFoundException
     */
    //CHECKSTYLE:OFF MagicNumber
    //@Test
    public void testExportToStream() throws FileNotFoundException {
        List<SheetDataDto> data = new ArrayList<SheetDataDto>();
        RowDataDto headerRow = new RowDataDto();
        headerRow.getCells().add(new CellDataDto("Col 1", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 2", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 3", CellDataTypeEnum.TEXT));
        headerRow.getCells().add(new CellDataDto("Col 4", CellDataTypeEnum.TEXT));

        SheetDataDto sheet1 = new SheetDataDto();
        sheet1.setName("First sheet");
        sheet1.setHeaderRow(headerRow);

        RowDataDto row1 = new RowDataDto();
        row1.getCells().add(new CellDataDto(1d, CellDataTypeEnum.NUMBER));
        row1.getCells().add(new CellDataDto("IF(A2 > 0, \"1\", \"0\")", CellDataTypeEnum.FORMULAR));
        row1.getCells().add(new CellDataDto("123456789", CellDataTypeEnum.NUMBER));
        row1.getCells().add(new CellDataDto(new Date(), CellDataTypeEnum.DATE));

        sheet1.getRows().add(row1);
        data.add(sheet1);

        OutputStream out1 = new FileOutputStream(new File("t2-out.xls"));
        excelExportingService.exportToExcel(null, out1, data, ExcelFormatTypeEnum.XLS);

        InputStream in2 = getClass().getResourceAsStream("/exportData/t2-in.xlsx");
        OutputStream out2 = new FileOutputStream(new File("t2-out.xlsx"));
        sheet1.setName("Append Sheet");
        sheet1.setSheetIndex(0);
        excelExportingService.exportToExcel(in2, out2, data, ExcelFormatTypeEnum.XLSX);
    }
    //CHECKSTYLE:ON
    /**
     * this test case demonstrates the filling in data in an excel template file using formatter. For the columns where
     * the formatter is not empty, the format of the cell is overridden by the formats specified in the formatter. The
     * input is stream and the output is a file.
     * 
     * @throws IOException
     */
    //CHECKSTYLE:OFF AvoidInlineConditionals
    @Test
    public void testFillInDataToTemplate() throws IOException {
        List<List<Object>> rows = new ArrayList<List<Object>>();

        List<String> communeStrs = Arrays.asList("Aclens", "Allaman", "Aubonne", "Apples", "Bière", "Bremblens",
                "Bougy-Villars", "Berolle", "Chevilly");
        List<String> typeStrs = Arrays.asList("Résidence Secondaire", "Location", "Hotel", "Chambre", "Camping",
                "Institut");

        for (int i = 0; i < 25; i++) {
            List<Object> row = new ArrayList<Object>();
            row.add(new Long(900000L + i));
            row.add(i + 1);
            row.add(communeStrs.get(i % communeStrs.size()));
            row.add(typeStrs.get(i % typeStrs.size()));
            row.add(new Long(25000L * i));
            row.add(BooleanUtils.toBoolean(i % 2));
            row.add(new Date());
            row.add(DateUtils.addDays(new Date(), i + 1));
            row.add(String.format("%d piéces ou moins", i + 1));
            row.add(i + 1);
            row.add((i % 7 == 0 ? i : null));
            row.add((i % 7 == 1 ? i : null));
            row.add((i % 7 == 2 ? i : null));
            row.add((i % 7 == 3 ? i : null));
            row.add((i % 7 == 4 ? i : null));
            row.add((i % 7 == 5 ? i : null));
            row.add((i % 7 == 6 ? i : null));
            row.add(10 * i);
            row.add(200 * i);
            row.add(25 * i);
            row.add(i % 5 + 1);
            row.add(i % 6 + 1);
            row.add(BigDecimal.ZERO);
            row.add(1500 * (i + 1));
            row.add(null);

            rows.add(row);
        }

        SheetDataDto sheet1 = ExcelExportingUtil.convert2DListToSheetDto(rows, false);
        sheet1.setName("Data");
        sheet1.setOverriden(true);
        // re-assign the index of the rows in the sheet to fill in the template: ignore the first two rows
        for (int i = 2; i < 2 + sheet1.getRows().size(); i++) {
            RowDataDto row = sheet1.getRows().get(i - 2);
            row.setRowNum(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                CellDataDto cell = row.getCells().get(j);
                cell.setCellNum(j);
            }
        }

        // specify the format for col 7 and 8 (0-based)
        for (int i = 0; i < sheet1.getRows().size(); i++) {
            switch (i) {
            case 7:
                CellFormatter formatter = new CellFormatter("dd-MMM-yyyy");
                formatter.setAlignment(CellStyle.ALIGN_CENTER);
                sheet1.getFormatters().add(formatter);
                break;

            case 8:
                CellFormatter formatter1 = new CellFormatter();
                formatter1.setWrapText(false);
                sheet1.getFormatters().add(formatter1);
                break;

            default:
                sheet1.getFormatters().add(null);
                break;
            }
        }

        InputStream input = getClass().getResourceAsStream("/exportData/Statistics_v0.1.xls");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheet1), ExcelFormatTypeEnum.XLS);

        FileUtils.writeByteArrayToFile(new File("output-template.xls"), out.toByteArray());
    }
    //CHECKSTYLE:ON
}