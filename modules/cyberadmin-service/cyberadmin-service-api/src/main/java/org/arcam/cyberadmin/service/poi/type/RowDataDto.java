/*
 * RowDataDto.java
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
import java.util.List;

/**
 * DTO represents the data in a row of the sheet.
 * 
 * @author vlp
 * 
 */
public class RowDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CellDataDto> cells = new ArrayList<CellDataDto>();
    private Integer rowNum;

    /**
     * gets the cells in the row.
     * 
     * @return
     */
    public List<CellDataDto> getCells() {
        return cells;
    }

    /**
     * gets the flag which indicate if the row is a new row or an available row in the sheet. When the rowNum is not
     * null this row is considered an available row in the sheet.
     * 
     * @return
     */
    public boolean isNewRow() {
        return rowNum == null;
    }

    /**
     * creates an empty row. This row is usually used when export data to a template file.
     * 
     * @return
     */
    public static RowDataDto createEmptyRow() {
        return new RowDataDto();
    }

    /**
     * gets the number in 0-based of the row in the sheet. This value is logical. The value for the row number is based
     * on the sheet where it belongs to, not on the list of the SheetDataDto.
     * 
     * @return
     */
    public Integer getRowNum() {
        return rowNum;
    }

    /**
     * sets the number in 0-based of the row in the sheet. This value is logical. The value for the row number is based
     * on the sheet where it belongs to, not on the list of the SheetDataDto.
     * 
     * @param rowNum
     */
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}
