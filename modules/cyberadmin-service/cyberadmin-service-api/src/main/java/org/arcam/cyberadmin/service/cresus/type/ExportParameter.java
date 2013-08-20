/*
 * ExportParameter.java
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

package org.arcam.cyberadmin.service.cresus.type;

import java.util.EnumSet;

/**
 * Object contains parameters which are used in the Cresus export process.
 * 
 * @author mmn
 *
 */
public class ExportParameter {

    private boolean keepStatus = false;
    private boolean testrun = false;
    private EnumSet<ExportDataCategoryEnum> dataCategories = EnumSet.allOf(ExportDataCategoryEnum.class);
    
    public boolean isKeepStatus() {
        return keepStatus;
    }

    public void setKeepStatus(boolean keepStatus) {
        this.keepStatus = keepStatus;
    }

    public boolean isTestrun() {
        return testrun;
    }

    public void setTestrun(boolean testrun) {
        this.testrun = testrun;
    }

    public EnumSet<ExportDataCategoryEnum> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(EnumSet<ExportDataCategoryEnum> dataCategories) {
        this.dataCategories = dataCategories;
    }
    
    public boolean hasCategory(ExportDataCategoryEnum category) {
        return dataCategories.contains(category);
    }
    
}
