/*
 * FreezePanelDto.java
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

/**
 * DTO represents a freeze pane in Excel file.
 * 
 * @author vlp
 * 
 */
public class FreezePanelDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int columnSplit = 0;
    private int rowSplit = 0;

    /**
     * default constructor for the FreezePanelDto. The initial values is used for an empty freezePanel (no freezePanel
     * is applied in the sheet).
     */
    public FreezePanelDto() {
        setColumnSplit(0);
        setRowSplit(0);
    }

    /**
     * constructs a new freeze panel in excel sheet. The split is specified by the column and the row (start value is
     * 1). For example, to freeze the first row use the following constructor "FreezePanelDto(0, 1)".
     * 
     * @param columnSplit
     * @param rowSplit
     */
    public FreezePanelDto(int columnSplit, int rowSplit) {
        this.setColumnSplit(columnSplit);
        this.setRowSplit(rowSplit);
    }

    /**
     * gets the value for the column split in the freeze pane of the excel. This value is 0-based.
     * 
     * @return
     */
    public int getColumnSplit() {
        return columnSplit;
    }

    /**
     * sets the value for the column split in the freeze pane of the excel. This value is 0-based.
     * 
     * @param columnSplit
     */
    public void setColumnSplit(int columnSplit) {
        this.columnSplit = columnSplit;
    }

    /**
     * gets the value for the row split in the freeze pane of the excel. This value is 0-based.
     * 
     * @return
     */
    public int getRowSplit() {
        return rowSplit;
    }

    /**
     * sets the value for the row split in the freeze pane of the excel. This value is 0-based.
     * 
     * @param rowSplit
     */
    public void setRowSplit(int rowSplit) {
        this.rowSplit = rowSplit;
    }
}
