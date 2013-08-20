/*
 * PageableSortableDataModel
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

package org.arcam.cyberadmin.ui.core.widget.table.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * <p>
 * This class serves as the base model for common pageable and sortable tables in Cyber Admin. 
 * </p>
 * 
 * @param <T> The CyberAdmin entity type.
 * 
 * @author mmn
 *
 */
public class PageableSortableDataModel<T> extends LazyDataModel<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private DataProvider<T> dataProvider;
    
    /**
     * <p>
     * The constructor.
     * </p>
     * 
     * @param dataProvider The data provider is used in this table model
     */
    public PageableSortableDataModel(DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int rowCount = getRowCount();
        if (first < 0) {
            first = 0;
            throw new IllegalArgumentException("Illegal start index value: " + first);
        }

        int numberOfRows = pageSize;
        if (numberOfRows <= 0) {
            numberOfRows = rowCount;
        }
        
        int end = first + numberOfRows;
        if (end > rowCount) {
            end = rowCount;
        }
        
        // Populates only the needed data from data provider.
        return dataProvider.getItemsByRange(first, end, sortField, sortOrder);
    }
    
    @Override
    public int getRowCount() {
        return dataProvider.getRowCount();
    }
    
    @Override
    public T getRowData(String rowKey) {
        T result = null;
        if (rowKey != null) {
            result = dataProvider.getItemByKey(rowKey);
        }
        return result;
    }

    @Override
    public Object getRowKey(T object) {
        return dataProvider.getKey(object);
    }

    @Override
    public void setRowIndex(int rowIndex) {
        // MMN: The following is in ancestor (LazyDataModel):
        // this.rowIndex = rowIndex == -1 ? rowIndex : (rowIndex % pageSize);
        // We must check the pageSize value before calling the super to avoid
        // java.lang.ArithmeticException: / by zero when pageSize = 0
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }
    
    public void setDataProvider(DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public DataProvider<T> getDataProvider() {
        return dataProvider;
    }
    
}
