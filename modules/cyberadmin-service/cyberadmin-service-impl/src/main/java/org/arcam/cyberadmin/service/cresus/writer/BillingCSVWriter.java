/*
 * BillingCSVWriter.java
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

import org.arcam.cyberadmin.service.cresus.type.BillingDTO;
import org.arcam.cyberadmin.utils.ExportDataUtils;

/**
 * A CSV writer for {@link BillingDTO} object.
 * 
 * @author mmn
 *
 */
public class BillingCSVWriter extends AbstractCSVWriter<BillingDTO> {

    public BillingCSVWriter(OutputStream outputStream) throws IOException {
        super(outputStream);
    }

    @Override
    protected String[] getHeaders() {
        return new String[] { "DEC_ID", "ASSUJETTI_ID", "LIBELLE_FACTURE", "MONTANT_TOTAL_FACTURE", "NOM", "PRENOM",
                "ORGANISATION", "RUE", "NO", "ADRESSE", "NPA", "LOCALITE", "PAYS", "DESIGNATION", "QUANTITE", "UNITE",
                "PRIX_UNITAIRE", "MONTANT_POSITION" };
    }
    
    @Override
    public void writeRecord(BillingDTO billing) throws IOException {
        if (billing == null) {
            return;
        }
        
        int i = 0;
        String[] data = new String[getHeaders().length];
        data[i++] = billing.getDecId() + "";
        data[i++] = billing.getTaxpayerId() + "";
        data[i++] = ExportDataUtils.encodeStringValue(billing.getBillingDescription());
        data[i++] = ExportDataUtils.encodeNumberValue(billing.getTotalAmount());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getName());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getFirstname());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getOrganisation());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getRue());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getNo());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getAddress());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getNpa());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getLocalite());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getPays());
        data[i++] = ExportDataUtils.encodeStringValue(billing.getDesignation());
        data[i++] = billing.getQuantity();
        data[i++] = ExportDataUtils.encodeStringValue(billing.getUnit());
        data[i++] = billing.getPriceUnit();
        data[i++] = billing.getPositionAmount();
        
        writeRecord(data);
    }

}
