/*
 * NumberUtils.java
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

package org.arcam.cyberadmin.utils;

import java.text.DecimalFormat;


/**
 * @author mmn
 *
 */
public final class NumberUtils {

    private NumberUtils() {
        // to hide
    }

    /**
     * <p>
     * Returns either the passed in Long, or if the Long is {@code null}, the value of {@code defaultVal}.
     * </p>
     * 
     * <pre>
     * NumberUtils.defaultIfNull(null, 0) = 0
     * NumberUtils.defaultIfNull(new Long(1), 0) = 1
     * </pre>
     * 
     * @param value
     *            the Long to check, may be null
     * @param defaultVal
     *            the default value to return if the input is {@code null}, may be null
     * @return the passed in Long, or the default if it was {@code null}
     */
    public static long defaultIfNull(Long value, long defaultVal) {
        if (value == null) {
            return defaultVal;
        }
        return value.longValue();
    }

    /**
     * <p>
     * Returns either the passed in Integer, or if the Integer is {@code null}, the value of {@code defaultVal}.
     * </p>
     * 
     * <pre>
     * NumberUtils.defaultIfNull(null, 0) = 0
     * NumberUtils.defaultIfNull(new Integer(1), 0) = 1
     * </pre>
     * 
     * @param value
     *            the Integer to check, may be null
     * @param defaultVal
     *            the default value to return if the input is {@code null}, may be null
     * @return the passed in Integer, or the default if it was {@code null}
     */
    public static int defaultIfNull(Integer value, int defaultVal) {
        if (value == null) {
            return defaultVal;
        }
        return value.intValue();
    }

    public static String formatNumber(Number number, String format) {
        if (number == null || format == null) {
            return number.toString();
        }
    
        DecimalFormat formatter = new DecimalFormat(format);
        return formatter.format(number);
    }
    
}
