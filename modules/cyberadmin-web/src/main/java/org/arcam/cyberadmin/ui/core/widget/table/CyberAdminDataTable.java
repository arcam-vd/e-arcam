/*
 * AbstractDataTable
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

package org.arcam.cyberadmin.ui.core.widget.table;

import java.io.Serializable;
import java.util.List;

import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;
import org.arcam.cyberadmin.ui.core.widget.table.model.DataProvider;
import org.arcam.cyberadmin.ui.core.widget.table.model.PageableSortableDataModel;

/**
 * The class represents a paging table. It also supports sorting by default.
 * 
 * @param <T> The entity type contained in the table.
 * 
 * @author mmn
 *
 */
public class CyberAdminDataTable<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected DataProvider<T> dataProvider;
    
    /**
     * The data model for table.
     */
    private PageableSortableDataModel<T> dataModel;
    
    public List<ColumnModel> getColumns() {
        return dataProvider.getColumns();
    }
    
    public void setDataProvider(DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setDataModel(PageableSortableDataModel<T> dataModel) {
        this.dataModel = dataModel;
    }
    
    public PageableSortableDataModel<T> getDataModel() {
        if (dataModel == null) {
            dataModel = new PageableSortableDataModel<T>(dataProvider);
        }
        return dataModel;
    }
    
    /**
     * @return the number of rows from DB that match the user's input.
     */
    public int getRowCount() {
        return dataProvider.getRowCount();
    }
}