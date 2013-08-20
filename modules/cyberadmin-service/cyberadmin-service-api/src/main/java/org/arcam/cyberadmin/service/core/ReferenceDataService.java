/*
 * ReferenceDataService.java
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

package org.arcam.cyberadmin.service.core;

import java.util.Date;
import java.util.List;

import org.arcam.cyberadmin.dom.reference.Commune;
import org.arcam.cyberadmin.dom.reference.Country;
import org.arcam.cyberadmin.dom.reference.Localite;
import org.arcam.cyberadmin.dom.reference.Tarif;

/**
 * Load reference data.
 * 
 * @author nln
 *
 */
public interface ReferenceDataService {
    
    /**
     * Loads all Commune in DB.
     * @return commune.
     */
    List<Commune> getAllCommune();
    
    /**
     * Gets all countries with a specific language.
     * @param language EN or FR, language in iso code.
     * @return List of countries.
     */
    List<Country> getAllCountry(String language);
    
    /**
     * Get country with a specific language and country code. 
     */
    Country getCountry(String code, String language); 
    
    /**
     * Gets all Localites.
     * @return localites.
     */
    List<Localite> getAllLocalite();
    
    /**
     * Gets the current tarification from DB.
     * 
     * @param queryDate 
     *          the date which the Tarif has effect on.
     * @return
     */
    Tarif getTarif(Date queryDate);
}
