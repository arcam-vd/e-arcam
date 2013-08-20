/*
 * Tarif.java
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

package org.arcam.cyberadmin.dom.reference;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.arcam.cyberadmin.dom.core.AbstractCyberAdminEntity;

/**
 * @author vtn
 * 
 */
@Entity
@Table(name = "TARIF")
@AttributeOverride(name = "id", column = @Column(name = "TARIF_ID"))
public class Tarif extends AbstractCyberAdminEntity {

    private static final long serialVersionUID = 1L;

    private Date dateDebut;
    private Date dateFin;
    private double coefficientResidence;
    private double maxResidence;
    private double minResidence;
    private double locataire2p;
    private double maxLocataire2p;
    private double locataire3p;
    private double maxLocataire3p;
    private double nuitInstitut;
    private double residentielInstitut;
    private double nuitcamping;
    private double residentielCamping;
    private double nuitHotel;
    private double nuitChambre;

    @Column(name = "DATE_DEBUT", nullable = false, unique = true)
    @NotNull
    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Column(name = "DATE_FIN", nullable = false)
    @NotNull
    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    @Column(name = "COEFFICIENT_RESIDENCE", nullable = false)
    @NotNull
    public double getCoefficientResidence() {
        return coefficientResidence;
    }

    public void setCoefficientResidence(double coefficientResidence) {
        this.coefficientResidence = coefficientResidence;
    }

    @Column(name = "MAX_RESIDENCE", nullable = false)
    @NotNull
    public double getMaxResidence() {
        return maxResidence;
    }

    public void setMaxResidence(double maxResidence) {
        this.maxResidence = maxResidence;
    }

    @Column(name = "MIN_RESIDENCE", nullable = false)
    @NotNull
    public double getMinResidence() {
        return minResidence;
    }

    public void setMinResidence(double minResidence) {
        this.minResidence = minResidence;
    }

    @Column(name = "LOCATAIRE2P", nullable = false)
    @NotNull
    public double getLocataire2p() {
        return locataire2p;
    }

    public void setLocataire2p(double locataire2p) {
        this.locataire2p = locataire2p;
    }

    @Column(name = "MAXLOCATAIRE2P", nullable = false)
    @NotNull
    public double getMaxLocataire2p() {
        return maxLocataire2p;
    }

    public void setMaxLocataire2p(double maxLocataire2p) {
        this.maxLocataire2p = maxLocataire2p;
    }

    @Column(name = "LOCATAIRE3P", nullable = false)
    @NotNull
    public double getLocataire3p() {
        return locataire3p;
    }

    public void setLocataire3p(double locataire3p) {
        this.locataire3p = locataire3p;
    }

    @Column(name = "MAXLOCATAIRE3P", nullable = false)
    public double getMaxLocataire3p() {
        return maxLocataire3p;
    }

    public void setMaxLocataire3p(double maxLocataire3p) {
        this.maxLocataire3p = maxLocataire3p;
    }

    @Column(name = "NUIT_INSTITUT", nullable = false)
    public double getNuitInstitut() {
        return nuitInstitut;
    }

    public void setNuitInstitut(double nuitInstitut) {
        this.nuitInstitut = nuitInstitut;
    }

    @Column(name = "RESIDENTIEL_INSTITUT", nullable = false)
    public double getResidentielInstitut() {
        return residentielInstitut;
    }

    public void setResidentielInstitut(double residentielInstitut) {
        this.residentielInstitut = residentielInstitut;
    }

    @Column(name = "NUIT_CAMPING", nullable = false)
    public double getNuitcamping() {
        return nuitcamping;
    }

    public void setNuitcamping(double nuitcamping) {
        this.nuitcamping = nuitcamping;
    }

    @Column(name = "RESIDENTIEL_CAMPING", nullable = false)
    public double getResidentielCamping() {
        return residentielCamping;
    }

    public void setResidentielCamping(double residentielCamping) {
        this.residentielCamping = residentielCamping;
    }

    @Column(name = "NUIT_HOTEL", nullable = false)
    public double getNuitHotel() {
        return nuitHotel;
    }

    public void setNuitHotel(double nuitHotel) {
        this.nuitHotel = nuitHotel;
    }

    @Column(name = "NUIT_CHAMBRE", nullable = false)
    @NotNull
    public double getNuitChambre() {
        return nuitChambre;
    }

    public void setNuitChambre(double nuitChambre) {
        this.nuitChambre = nuitChambre;
    }

}
