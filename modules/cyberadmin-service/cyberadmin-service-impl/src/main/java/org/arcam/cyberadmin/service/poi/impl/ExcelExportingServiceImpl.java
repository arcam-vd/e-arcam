/*
 * ExcelExportingServiceImpl.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.exception.CannotExportToExcelFileException;
import org.arcam.cyberadmin.service.poi.type.CellDataDto;
import org.arcam.cyberadmin.service.poi.type.CellDataTypeEnum;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.RowDataDto;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.service.poi.utils.CellFormatter;
import org.arcam.cyberadmin.service.poi.utils.ExcelExportingUtil;
import org.springframework.stereotype.Service;

/**
 * implementation of the service to export data to Excel file.
 * 
 * @author vlp
 * 
 */
@Service("excelExportingService")
public class ExcelExportingServiceImpl implements ExcelExportingService {

    private void setCellData(Workbook workbook, Cell cell, CellDataDto cellData, CellFormatter formatter,
            boolean forHSSF) {
        if (cellData == null || cellData.getValue() == null || cellData.getDataType() == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);

        } else if (CellDataTypeEnum.TEXT == cellData.getDataType()) {
            RichTextString value = null;
            if (forHSSF) {
                value = new HSSFRichTextString(cellData.getValue().toString());
            } else {
                value = new XSSFRichTextString(cellData.getValue().toString());
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(value);

        } else if (CellDataTypeEnum.NUMBER == cellData.getDataType()) {
            Double numValue = ExcelExportingUtil.convertToDoubleOrNull(cellData.getValue());
            if (numValue == null) {
                cell.setCellType(Cell.CELL_TYPE_BLANK);
            } else {
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(numValue);
            }

        } else if (CellDataTypeEnum.DATE == cellData.getDataType()) {
            Date dateValue = ExcelExportingUtil.castToDateOrNull(cellData.getValue());
            if (dateValue == null) {
                cell.setCellType(Cell.CELL_TYPE_BLANK);
            } else {
                // in case the date cell doesn't have a style for it (a new and unformated cell) we have to set the
                // style for the date because if we don't, the output result will be a decimal number.
                if (cell.getCellStyle() == null) {
                    CellStyle style = workbook.createCellStyle();
                    style.setDataFormat(workbook.createDataFormat().getFormat("dd.MM.yyyy"));
                    cell.setCellStyle(style);
                }
                cell.setCellValue(dateValue);
            }

        } else if (CellDataTypeEnum.FORMULAR == cellData.getDataType()) {
            cell.setCellType(Cell.CELL_TYPE_FORMULA);
            cell.setCellFormula(cellData.getValue().toString());
        }

        formatCellIfPossible(workbook, cell, formatter);
    }

    /**
     * formats the cell with the formatter if possible. When a property in the formatter is not specified (it is null)
     * the corresponding available style is used if possible.
     * 
     * @param workbook
     * @param cell
     * @param formatter
     */
    private void formatCellIfPossible(Workbook workbook, Cell cell, CellFormatter formatter) {
        if (formatter != null && formatter.isNotEmptyFormatter()) {
            CellStyle style = workbook.createCellStyle();
            CellStyle availStyle = cell.getCellStyle();

            if (StringUtils.isNotBlank(formatter.getDataFormatString())) {
                style.setDataFormat(workbook.createDataFormat().getFormat(formatter.getDataFormatString()));
            } else if (availStyle != null) {
                style.setDataFormat(availStyle.getDataFormat());
            }

            if (formatter.getAlignment() != null) {
                style.setAlignment(formatter.getAlignment());
            } else if (availStyle != null) {
                style.setAlignment(availStyle.getAlignment());
            }

            if (formatter.getWrapText() != null) {
                style.setWrapText(formatter.getWrapText());
            } else if (availStyle != null) {
                style.setWrapText(availStyle.getWrapText());
            }

            cell.setCellStyle(style);
        }
    }

    private Row getOrCreateRow(Sheet sheet, RowDataDto rowDto, int rowIdx) {
        Row dataRow = null;
        if (rowDto == null || rowDto.isNewRow()) {
            dataRow = sheet.createRow(rowIdx++);
        } else {
            dataRow = sheet.getRow(rowDto.getRowNum());
            if (dataRow == null) {
                dataRow = sheet.createRow(rowDto.getRowNum());
            }
        }

        return dataRow;
    }

    private Cell getOrCreateCell(Row dataRow, CellDataDto cellData, int cellIdx) {
        Cell cell = null;
        if (cellData.isNewCell()) {
            cell = dataRow.createCell(cellIdx);
        } else {
            cell = dataRow.getCell(cellData.getCellNum());
            if (cell == null) {
                cell = dataRow.createCell(cellIdx);
            }
        }

        return cell;
    }

    private void fillInDataToRow(Workbook workbook, boolean forHSSF, Sheet sheet, int rowIdx, RowDataDto rowDto,
            List<CellFormatter> formatters) {
        // retrieve the row to process
        Row dataRow = getOrCreateRow(sheet, rowDto, rowIdx);

        // process the cells of the row
        for (int cellIdx = 0; cellIdx < rowDto.getCells().size(); cellIdx++) {
            CellDataDto cellData = rowDto.getCells().get(cellIdx);
            Cell cell = getOrCreateCell(dataRow, cellData, cellIdx);
            CellFormatter formatter = null;
            if (CollectionUtils.isNotEmpty(formatters) && cellIdx < formatters.size()) {
                formatter = formatters.get(cellIdx);
            } else if (cellIdx < formatters.size()) {
                throw new CannotExportToExcelFileException(String.format(
                        "Can't retrieve the formatter for the cell at column at index %d (0-based)", cellIdx));
            }
            setCellData(workbook, cell, cellData, formatter, forHSSF);
        }
    }

    private void fillInDataToExcel(String fileName, List<SheetDataDto> data, Workbook workbook, boolean forHSSF) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(fileName));
            fillInDataToExcel(os, data, workbook, forHSSF);

        } catch (Exception e) {
            IOUtils.closeQuietly(os);
            throw new CannotExportToExcelFileException("There was an error while trying to export data to Excel file: "
                    + e.getMessage(), e);
        }
    }

    private void fillInDataToExcel(OutputStream os, List<SheetDataDto> data, Workbook workbook, boolean forHSSF) {
        for (SheetDataDto sheetDto : data) {
            Sheet sheet = null;
            if (!sheetDto.isOverriden()) {
                // validate the sheetname before creating it
                WorkbookUtil.validateSheetName(sheetDto.getName());
                // sheetname is ok, create excel sheet for the workbook
                sheet = workbook.createSheet(sheetDto.getName());

            } else {
                sheet = workbook.getSheet(sheetDto.getName());
                if (sheet == null) {
                    sheet = workbook.getSheetAt(sheetDto.getSheetIndex());
                }
                if (sheet == null) {
                    throw new CannotExportToExcelFileException(String.format(
                            "Cannot locate the sheet in the workbook by name " + "\"%s\" or by index \"%s\". ",
                            sheetDto.getName(), sheetDto.getSheetIndex()));
                }
            }

            // update the index for the sheet in case the sheet is new in the workbook
            if (sheetDto.getSheetIndex() != null && !sheetDto.isOverriden()) {
                if (sheetDto.getSheetIndex() >= 0 && sheetDto.getSheetIndex() < workbook.getNumberOfSheets()) {
                    workbook.setSheetOrder(sheetDto.getName(), sheetDto.getSheetIndex());
                } else {
                    throw new IllegalArgumentException(String.format(
                            "The index of the sheet \"%s\" is invalid. It must be from 0 to %s", sheetDto.getName(),
                            workbook.getNumberOfSheets()));
                }
            }

            int rowIdx = 0;
            // create header row
            RowDataDto headerRowDto = sheetDto.getHeaderRow();
            if (headerRowDto != null) {
                fillInDataToRow(workbook, forHSSF, sheet, rowIdx++, headerRowDto, null);
            }

            // create row data
            for (RowDataDto rowDto : sheetDto.getRows()) {
                fillInDataToRow(workbook, forHSSF, sheet, rowIdx++, rowDto, sheetDto.getFormatters());
            }

            // freeze the first row to view data if necessary
            if (sheetDto.getFreezePanel() != null) {
                sheet.createFreezePane(sheetDto.getFreezePanel().getColumnSplit(), sheetDto.getFreezePanel()
                        .getRowSplit());
            }
        }

        try {
            workbook.write(os);
        } catch (Exception e) {
            IOUtils.closeQuietly(os);
            throw new CannotExportToExcelFileException("There was an error while trying to export data: "
                    + e.getMessage(), e);
        }
    }

    //CHECKSTYLE:OFF LineLength
    @Override
    public void exportToExcel(String fileName, List<SheetDataDto> data, boolean append, ExcelFormatTypeEnum exportType) {
        Workbook workbook = null;
        if (!append) {
            if (exportType == ExcelFormatTypeEnum.XLS) {
                workbook = new HSSFWorkbook();
            } else if (exportType == ExcelFormatTypeEnum.XLSX) {
                workbook = new XSSFWorkbook();
            } else {
                throw new IllegalArgumentException("The export type must not be null or it is not supported.");
            }
        } else {
            try {
                FileInputStream s = new FileInputStream(new File(fileName));

                if (exportType == ExcelFormatTypeEnum.XLS) {
                    workbook = new HSSFWorkbook(s);
                } else if (exportType == ExcelFormatTypeEnum.XLSX) {
                    workbook = new XSSFWorkbook(s);
                } else {
                    throw new IllegalArgumentException("The export type must not be null or it is not supported.");
                }

            } catch (FileNotFoundException e) {
                throw new CannotExportToExcelFileException(
                        "The excel file to append data to doesn't exist in the path: " + fileName, e);
            } catch (IOException e) {
                throw new CannotExportToExcelFileException(
                        "There was an error while trying to open the excel file to append data: " + e.getMessage(), e);
            }
        }

        fillInDataToExcel(fileName, data, workbook, exportType.isForHSSF());
    }
    //CHECKSTYLE:ON
    //CHECKSTYLE:OFF AvoidInlineConditionals
    @Override
    public void exportToExcel(InputStream input, OutputStream output, List<SheetDataDto> data,
            ExcelFormatTypeEnum exportType) {

        Workbook workbook = null;
        try {
            if (exportType == ExcelFormatTypeEnum.XLS) {
                workbook = input == null ? new HSSFWorkbook() : new HSSFWorkbook(input);
            } else if (exportType == ExcelFormatTypeEnum.XLSX) {
                workbook = input == null ? new XSSFWorkbook() : new XSSFWorkbook(input);
            } else {
                throw new IllegalArgumentException("The export type must not be null or it is not supported.");
            }
        } catch (IOException e) {
            throw new CannotExportToExcelFileException(
                    "An error happened while trying to open the excel file with the input stream: " + e.getMessage(), e);
        }

        fillInDataToExcel(output, data, workbook, exportType.isForHSSF());
    }
    //CHECKSTYLE:ON
}
