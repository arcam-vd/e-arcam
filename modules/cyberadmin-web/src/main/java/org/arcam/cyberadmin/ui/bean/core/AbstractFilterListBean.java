/*
 * AbstractFilterListBean.java
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

package org.arcam.cyberadmin.ui.bean.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.criteria.core.CyberAdminCriteria;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.core.widget.table.CyberAdminDataTable;
import org.arcam.cyberadmin.ui.core.widget.table.model.DataProvider;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.SortOrder;

/**
 * Base bean for all list screen backing bean.
 * 
 * @author mmn
 * 
 * @param <T> the type of entity which is going to be displayed as search result.
 * @param <C> the type of search criteria
 *
 */
public abstract class AbstractFilterListBean<T extends AbstractCyberAdminEntity, C extends CyberAdminCriteria> 
        extends AbstractCyberAdminBean implements DataProvider<T> {

    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_RESULT_COMPONENT = "tableResultId";

    /**
     * Object to collect the user input criteria.
     */
    protected C criteria;
    
    private CyberAdminDataTable<T> dataTable;
    
    /**
     * Data list that will be rendered and sent to client side.
     */
    protected List<T> cachedDataList = null;
    
    /**
     * The total number of rows from db that match the user's input.
     */
    protected int cachedRowCount = -1;
    
    private boolean validCriteria = true;
    
    public AbstractFilterListBean() {
        criteria = instantiateCriteria();
        setDefaultCriteria();
    }
    
    /**
     * Reset the search criteria to the original values. 
     * Can not use the reset button of primefaces because of its restriction when reset selectOneMenu component.
     */
    public void resetSearchCriteriaToOriginal() {
        resetCachedRowCount();
        criteria = instantiateCriteria();
    }
    
    protected abstract C instantiateCriteria();
    
    protected void setDefaultCriteria() {
        // nothing to do
    }

    /**
     * Loads data with row number between <code>firstRow</code> and <code>endRow</code>.
     * 
     * @return List of data with row number between <code>firstRow</code> and <code>endRow</code>.
     */
    protected abstract List<T> loadData(int startRow, int endRow);
    
    public final void search(ActionEvent event) {
        //ARCANCYBERADM-95: When a new search is performed, the table should reset to page 1        
        resetTableResult(event);
        validCriteria = validateCriteria();
        resetCachedRowCount();
        enrichCriteria();
    }
    
    private void resetTableResult(ActionEvent event) {
        if (event != null) {
            String tableId = (String) event.getComponent().getAttributes().get(TABLE_RESULT_COMPONENT);
            if (StringUtils.isNotBlank(tableId)) {
                DataTable tableResult = (DataTable) FacesUtils.getFacesContext().getViewRoot().findComponent(tableId);
                tableResult.reset();
            }
        }
    }
    
    protected void enrichCriteria() {
        // placeholder, do nothing
    }

    protected void resetCachedRowCount() {
        cachedRowCount = -1;
    }
    
    protected boolean validateCriteria() {
        return true;
    }

    /**
     * Counts the number of rows from db that match the user's input.
     * 
     * @return The number of rows from db that match the user's input.
     */
    protected abstract int countData();
    
    public CyberAdminDataTable<T> getDataTable() {
        if (dataTable == null) {
            // init data table
            dataTable = new CyberAdminDataTable<T>();
            dataTable.setDataProvider(this);
        }
        return dataTable;
    }
    
    /**
     * Getter method for <code> dataList </code>.
     * 
     * @return Returns the dataList.
     */
    private List<T> getCachedDataList() {
        
        // Caches the data list once it has been loaded.
        if (cachedDataList != null) {
            return cachedDataList;
        }
        return new ArrayList<T>();
    }

    @Override
    public List<T> getItemsByRange(int startRow, int endRow, String sortColName, SortOrder sortOrder) {
        if (validCriteria) {
            logger.debug("Load data from range: " + startRow + " - " + endRow);
            setNavigationInfo(startRow, endRow, sortColName, sortOrder);
            
            List<T> entities = null;
            if (startRow == endRow && startRow == 0) {
                entities = new ArrayList<T>();
            } else {
                entities = loadData(startRow, endRow);
            }
            cachedDataList = new ArrayList<T>(entities);
        }
        resetCriteria();
        return getCachedDataList();
    }

    @Override
    public int getRowCount() {
        if (!validCriteria) {
            return cachedRowCount;
        }
        
        // Caches the number of row once it has been set.
        if (cachedRowCount == -1) {
            cachedRowCount = countData();
        }
        return cachedRowCount;
    }
    
    protected void resetCriteria() {
        // placeholder, do nothing
    }

    @Override
    public T getItemByKey(Object key) {
        if (key == null) {
            return null;
        }
        
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            for (T item : cachedDataList) {
                if (key.equals(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public Object getKey(T o) {
        return o.getId();
    }
    
    /**
     * Getter method for <code> criteria </code>.
     * 
     * @return Returns the criteria.
     */
    public final CyberAdminCriteria getCriteria() {
        return criteria;
    }

    /**
     * Setter for attribute <code> criteria </code>.
     * 
     * @param criteria The criteria to set.
     */
    public final void setCriteria(C criteria) {
        this.criteria = criteria;
    }
    
    public void export() {
        ServletOutputStream out = null;
        String filename = getExportFileName();
        try {
            HttpServletResponse res = (HttpServletResponse) FacesUtils.getFacesContext().getExternalContext()
                    .getResponse();
            res.setContentType(WebConstants.MIME_TYPE_EXCEL);
            res.setHeader("Content-disposition", "attachment; filename=" + filename);
            Cookie cookie = new Cookie(screenUUID, "true");
            res.addCookie(cookie);
            out = res.getOutputStream();
            out.write(prepareDataToExport());
            out.flush();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            logger.error("Cannot export the list of data ", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * Prepare data to put in exported file.
     * @return
     */
    protected byte[] prepareDataToExport() {
        return new byte[]{};
    }

    /**
     * Must provide export file name when using export function.
     * @return
     */
    protected String getExportFileName() {
        return "";
    }

    private void setNavigationInfo(int startRow, int endRow, String sortColName, SortOrder sortOrder) {
        criteria.setFirstResult(startRow);
        criteria.setMaxResults(endRow - startRow);
        
        criteria.clear();
        if (StringUtils.isNotBlank(sortColName)) {
            if (sortOrder == SortOrder.ASCENDING) {
                criteria.getAscs().add(sortColName);
            } else {
                criteria.getDescs().add(sortColName);
            }
        }
    }
}
