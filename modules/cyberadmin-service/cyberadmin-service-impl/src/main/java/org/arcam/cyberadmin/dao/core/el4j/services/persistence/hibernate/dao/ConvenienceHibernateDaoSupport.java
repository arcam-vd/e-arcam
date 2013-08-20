/*
 * ConvenienceHibernateDaoSupport
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
package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.dao;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * Convenience Hibernate dao support class to be able to return the convenience Hibernate template
 * without casting it.
 * 
 * @author Alex Mathey (AMA)
 */
public class ConvenienceHibernateDaoSupport extends HibernateDaoSupport {

    /**
     * @return Returns the Hibernate template casted to the convenience model of it.
     */
    public ConvenienceHibernateTemplate getConvenienceHibernateTemplate() {
        return (ConvenienceHibernateTemplate) this.getHibernateTemplate();
    }

    /**
     * @param template
     *            Is the convenience Hibernate template to set.
     */
    public void setConvenienceHibernateTemplate(final ConvenienceHibernateTemplate template) {
        this.setHibernateTemplate(template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HibernateTemplate createHibernateTemplate(final SessionFactory sessionFactory) {
        return new ConvenienceHibernateTemplate(sessionFactory);
    }
}
