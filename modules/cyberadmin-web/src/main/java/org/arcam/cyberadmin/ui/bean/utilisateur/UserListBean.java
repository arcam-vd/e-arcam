/*
 * UserListBean
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

package org.arcam.cyberadmin.ui.bean.utilisateur;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.arcam.cyberadmin.criteria.business.UserCriteria;
import org.arcam.cyberadmin.dom.authorisation.User;
import org.arcam.cyberadmin.service.core.UserService;
import org.arcam.cyberadmin.service.poi.ExcelExportingService;
import org.arcam.cyberadmin.service.poi.type.ExcelFormatTypeEnum;
import org.arcam.cyberadmin.service.poi.type.SheetDataDto;
import org.arcam.cyberadmin.ui.bean.core.AbstractFilterListBean;
import org.arcam.cyberadmin.ui.core.utils.CyberAdminBackingBeanUtils;
import org.arcam.cyberadmin.ui.core.utils.ExportUtils;
import org.arcam.cyberadmin.ui.core.utils.WebConstants;
import org.arcam.cyberadmin.ui.core.widget.table.model.ColumnModel;

/**
 * Handle all action on User List screen.
 * 
 * @author dtl
 * 
 */

@ManagedBean(name = "userListBean")
@ViewScoped
public class UserListBean extends AbstractFilterListBean<User, UserCriteria> {
    private static final long serialVersionUID = 1L;
    
    /**
     * Link to file template user.
     */
    public static final String TEMPLATES_USER_EXPORT = "/templates/Users_0.2.xls";
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    @ManagedProperty(value = "#{excelExportingService}")
    private ExcelExportingService excelExportingService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ColumnModel> getColumns() {
        // The data table on this screen is built manually (not use the <arcam:dataTable> tag).
        // Thus There is no need to defined column model.
        return new ArrayList<ColumnModel>(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UserCriteria instantiateCriteria() {
        return new UserCriteria();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> loadData(int startRow, int endRow) {
        return userService.findByCriteria(criteria, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int countData() {
        return userService.countByCriteria(criteria);
    }
    
    @Override
    protected boolean validateCriteria() {
        // Convert from freeText to some of UserTypeEnum.
        CyberAdminBackingBeanUtils.matchingUserTypesFromResource(criteria);
        
        return super.validateCriteria();
    }

    @Override
    protected byte[] prepareDataToExport() {
        List<User> users = userService.findByCriteria(criteria, true);
        SheetDataDto sheetDeclaration = ExportUtils.prepareDataUsers(users);
        InputStream input = getClass().getResourceAsStream(TEMPLATES_USER_EXPORT);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportingService.exportToExcel(input, out, Arrays.asList(sheetDeclaration), ExcelFormatTypeEnum.XLS);
        return out.toByteArray();
    }

    public void exportUsers() {
        this.export();
    }
    
    @Override
    protected String getExportFileName() {
        return WebConstants.EXPORTED_USER_FILENAME;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void setExcelExportingService(ExcelExportingService excelExportingService) {
        this.excelExportingService = excelExportingService;
    }


}
