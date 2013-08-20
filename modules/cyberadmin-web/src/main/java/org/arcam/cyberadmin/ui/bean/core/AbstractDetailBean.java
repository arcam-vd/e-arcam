/*
 * AbstractDetailBean.java
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

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import org.apache.commons.lang3.StringUtils;
import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.service.core.LocaliteService;
import org.arcam.cyberadmin.ui.core.exception.CyberAdminRuntimeException;
import org.arcam.cyberadmin.ui.core.utils.CyberAdminBeanHelper;
import org.arcam.cyberadmin.ui.core.utils.FacesUtils;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Base entity for all detail backing beans.
 * 
 * @param <T>
 * 
 * @author vtn
 * 
 */
public abstract class AbstractDetailBean<T extends AbstractCyberAdminEntity> extends AbstractCyberAdminBean {

    /**
     * Message key for constraint validation.
     */
    public static final String CYBERADMIN_COMMON_CONSTRAINT_VIOLATION = "cyberadmin.common.ConstraintViolation";
    /**
     * Message key for data integrity violation.
     */
    public static final String CYBERADMIN_COMMON_DATA_INTEGRITY_VIOLATION = "cyberadmin.common.DataIntegrityViolation";
    /**
     * Message key for stale object state.
     */
    public static final String CYBERADMIN_COMMON_STALE_OBJECT_STATE = "cyberadmin.common.StaleObjectState";
    /**
     * Message key for optimistic locking.
     */
    public static final String CYBERADMIN_COMMON_OPTIMISTIC_LOCKING = "cyberadmin.common.OptimisticLocking";
    
    /**
     * Message key for loading entity.
     */
    public static final String CYBERADMIN_COMMON_LOADING_ENTITY = "cyberadmin.common.DataRetrievalFailureException";
    
    private static final long serialVersionUID = 1L;

    /**
     * Id of the entity. This id is not equal to TRANSIENT_ENTITY_ID when we are editing an existed entity. Otherwise,
     * this entity is a new one.
     */
    protected Long id = AbstractCyberAdminEntity.TRANSIENT_ENTITY_ID;
    
    protected boolean entityExist = true;

    protected T entity;
    
    @SuppressWarnings("unchecked")
    private final Class<T> entityClazz = CyberAdminBeanHelper.getClass(this.getClass(), 0);
    
    @ManagedProperty(value = "#{localiteService}")
    protected LocaliteService localiteService;
    
    public T getEntity() {
        if (this.entity == null) {
            try {
                this.entity = entityClazz.newInstance();
            } catch (final Exception ex) {
                logger.error("Unexpected exception when initializing the installationDetailBean", ex);
                // CyberAdminFacesUtils.addErrorMessage(ExceptionKeys.TECHNICAL_EXCEPTION);
            }
            // Set default values
            this.doAfterNewEntityInstance();
        }
        return this.entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
    
    /**
     * Place other default value for entity after initialized.
     */
    protected void doAfterNewEntityInstance() {
        // Do nothing by default
    }

    public String save() {
        logger.info("Save or update the entity in db (AbstractDetailBean class)");
        String outCome = StringUtils.EMPTY;
        try {
            if (!beforeSave()) {
                return null;
            }
            
            outCome = saveEntity();
            id = entity.getId();
            afterSave();
        } catch (OptimisticLockingFailureException ex) {
            logger.error("OptimisticLockingFailureException happened during saving", ex);
            displayErrorMessage(CYBERADMIN_COMMON_OPTIMISTIC_LOCKING);
        } catch (StaleObjectStateException ex) {
            logger.error("StaleObjectStateException happened during saving", ex);
            displayErrorMessage(CYBERADMIN_COMMON_STALE_OBJECT_STATE);
        } catch (DataIntegrityViolationException ex) {
            logger.error("DataIntegrityViolationException happened during saving", ex);
            displayErrorMessage(CYBERADMIN_COMMON_DATA_INTEGRITY_VIOLATION);
        } catch (ConstraintViolationException ex) {
            logger.error("Unexpected error happened during saving", ex);
            displayErrorMessage(CYBERADMIN_COMMON_CONSTRAINT_VIOLATION);
        }

        return outCome;
    }

    private void displayErrorMessage(String msgKey) {
        FacesUtils.getFacesContext().addMessage(null,
                FacesUtils.getI18nErrorMessage(msgKey, FacesMessage.SEVERITY_ERROR));
    }

    public abstract String saveEntity();

    /**
     * Performs the business validation on the current entity.
     * 
     * @return <code>true</code> if the entity is ready to save into DB. <br/>
     *         <code>false</code> if there is any business error after validation.
     */
    protected boolean beforeSave() {
        // Do nothing by default
        return true;
    }
    
    protected void afterSave() {
        // Do nothing by default
    }
    
    public List<Localite> getLocalites(String npa) {
        return localiteService.findLocaliteByCriteria(npa);
    }

    /**
     * Check whether the parameter id in url is valid (i.e match to an existing id in the db).
     * 
     * @return
     */
    public boolean idInRequestIsValid() {
        return (id != null) && (id != AbstractCyberAdminEntity.TRANSIENT_ENTITY_ID);
    }

    /**
     * Retrieve entity from db and populate it in the bean.
     * 
     * @return
     */
    public T populateEntity() {
        // do not reload entity if this is already loaded
        // if (this.entity != null) {
        // return this.entity;
        // }

        // TODO vtn not load data when request is AJAX
        boolean isAjaxRequest = FacesUtils.isAjaxRequest();
        if (isAjaxRequest) {
            return entity;
        }
        try {
            if (this.idInRequestIsValid()) {
            logger.info("AbstractDetailBean.populateEntity: Load from db ");
            entity = loadEntity(id);
            } else {
                loadEntityInSession();
                // Check if the 'entityId' parameter existing in URL
                // In this case, the entityId value is invalid
                // if (FacesUtils.getRequestParam("id") != null) {
                // this.entityExist = false;
                // }
    
            }
        } catch (DataRetrievalFailureException ex) {
            logger.error(ex);
            throw new CyberAdminRuntimeException(FacesUtils.getErrorMessage(CYBERADMIN_COMMON_LOADING_ENTITY)); 
        }
        return entity;
    }

    protected void loadEntityInSession() {

    }

    public abstract T loadEntity(Long id);

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setLocaliteService(LocaliteService localiteService) {
        this.localiteService = localiteService;
    }
}
