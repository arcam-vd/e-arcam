/*
 * TaxpayerCSVWriter.java
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

package org.arcam.cyberadmin.service.cresus.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.arcam.cyberadmin.dom.authorisation.Person;
import org.arcam.cyberadmin.dom.business.Adresse;
import org.arcam.cyberadmin.dom.business.Assujetti;
import org.arcam.cyberadmin.utils.ExportDataUtils;

/**
 * A CSV writer for {@link Assujetti} object.
 * 
 * @author mmn
 *
 */
public class TaxpayerCSVWriter extends AbstractCSVWriter<Assujetti> {

    public TaxpayerCSVWriter(OutputStream outputStream) throws IOException {
        super(outputStream);
    }

    @Override
    protected String[] getHeaders() {
        return new String[] { "ASSUJETTI_ID", "NOM", "PRENOM", "ORGANISATION", "RUE", "NO", "ADRESSE", "NPA",
                "LOCALITE", "PAYS", "TELEPHONE", "EMAIL" };
    }
    
    @Override
    public void writeRecord(Assujetti taxpayer) throws IOException {
        if (taxpayer == null) {
            return;
        }
        
        int i = 0;
        String[] data = new String[getHeaders().length];
        data[i++] = taxpayer.getId() + "";
        Person person = taxpayer.getPerson();
        if (person == null) {
            person = new Person();
        }
        data[i++] = ExportDataUtils.encodeStringValue(person.getNom());
        data[i++] = ExportDataUtils.encodeStringValue(person.getPrenom());
        data[i++] = ExportDataUtils.encodeStringValue(person.getOrganisation());
        
        Adresse address = taxpayer.getAdresse();
        if (address == null) {
            address = new Adresse();
        }
        data[i++] = ExportDataUtils.encodeStringValue(address.getRue());
        data[i++] = ExportDataUtils.encodeStringValue(address.getNo());
        data[i++] = ExportDataUtils.encodeStringValue(address.getAdresse());
        data[i++] = ExportDataUtils.encodeStringValue(address.getNpa());
        data[i++] = ExportDataUtils.encodeStringValue(address.getLocalite());
        data[i++] = ExportDataUtils.encodeStringValue(address.getPays());
        data[i++] = ExportDataUtils.encodeStringValue(address.getTelephone());
        data[i++] = ExportDataUtils.encodeStringValue(address.getEmail());
        
        writeRecord(data);
    }

}
