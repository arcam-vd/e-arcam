/*
 * SheetDataDto.java
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.arcam.cyberadmin.service.poi.utils.CellFormatter;

/**
 * DTO represent a sheet in the workbook.
 * 
 * @author vlp
 * 
 */
public class SheetDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private RowDataDto headerRow;
    private List<RowDataDto> rows = new LinkedList<RowDataDto>();
    private Integer sheetIndex;
    private FreezePanelDto freezePanel;
    private boolean overriden = false;
    private List<CellFormatter> formatters = new ArrayList<CellFormatter>();

    /**
     * gets the name of the sheet in workbook, can contain space characters.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the sheet in workbook. This name can contains space characters but can't be null. In case we are
     * working with a template sheet (the flag newSheet = false) locating the sheet will be based on the sheet name
     * first. If it fails to retrieve the template sheet by name then the sheetIndex is used. Therefore, when working
     * with template sheet please make sure that the sheet's name and the sheetIndex is correct.
     * 
     * @param name
     *            the name of the sheet in workbook
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the rows in the sheet. The data structure which is used to contains these rows is LinkedList.
     * 
     * @return
     */
    public List<RowDataDto> getRows() {
        return rows;
    }

    /**
     * gets the index of the sheet in the workbook. This index value is 0-based.
     * 
     * @return
     */
    public Integer getSheetIndex() {
        return sheetIndex;
    }

    /**
     * sets the index of the sheet in the workbook. When this value is not null, the order of this sheet in the data to
     * be written to excel file is ignored. In case we are working with a template sheet (the flag newSheet = false)
     * locating the sheet will be based on the sheet name first. If it fails to retrieve the template sheet by name then
     * the sheetIndex is used. Therefore, when working with template sheet please make sure that the sheet's name and
     * the sheetIndex is correct.
     * 
     * @param sheetIndex
     *            the index of the sheet in the workbook. This value must be 0-based and less than the number of the
     *            sheets in the workbook.
     */
    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    /**
     * checks if this sheet is a new sheet or a template sheet.
     * 
     * @return true if this sheet is a template sheet. Otherwise, returns false.
     */
    public boolean isOverriden() {
        return overriden;
    }

    /**
     * sets the flag to check if this sheet is a new sheet or a template sheet.
     * 
     * @param overriden
     *            the flag value to set
     */
    public void setOverriden(boolean overriden) {
        this.overriden = overriden;
    }

    /**
     * gets the header row of the sheet.
     * 
     * @return
     */
    public RowDataDto getHeaderRow() {
        return headerRow;
    }

    /**
     * sets the header row of the sheet.
     * 
     * @param headerRow
     */
    public void setHeaderRow(RowDataDto headerRow) {
        this.headerRow = headerRow;
    }

    /**
     * gets the freezePanel DTO. This DTO contains the information about the freeze pane in the current sheet.
     * 
     * @return
     */
    public FreezePanelDto getFreezePanel() {
        return freezePanel;
    }

    /**
     * sets the freezePanel DTO. This DTO contains the information about the freeze pane in the current sheet.
     * 
     * @param freezePanel
     */
    public void setFreezePanel(FreezePanelDto freezePanel) {
        this.freezePanel = freezePanel;
    }

    /**
     * gets the list of formatters for the columns in the sheet. When this list is empty it means that all the columns
     * will use the default format or the available format in the template. If you don't want to specify the formatter
     * for a column or you want to use the available cell style in the template for that column, just leave the
     * corresponding formatter null. For example if formatters is [formatter1, null, formatter2] it means that the
     * second column will use the available cell format or this column doesn't have any format (equal to General format
     * in Excel). <b>Note that these formatters are not applied for the header row of the sheet and the numbers of the
     * formatters must be equal to the max number of the columns in the SheetDataDto.</b>
     * 
     * @return
     */
    public List<CellFormatter> getFormatters() {
        return formatters;
    }
}
