/*
 * DateHelperTest.java
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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class DateHelperTest {
    
    public void testToday() throws Exception {
        Date today = DateHelper.today();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        Assert.assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, cal.get(Calendar.MINUTE));
        Assert.assertEquals(0, cal.get(Calendar.SECOND));
        Assert.assertEquals(0, cal.get(Calendar.MILLISECOND));
    }
    
    public void testGetDate() {
        Date date = DateHelper.getDate(5, Calendar.MAY, 2010);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Assert.assertEquals(2010, cal.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.MAY, cal.get(Calendar.MONTH));
        Assert.assertEquals(5, cal.get(Calendar.DAY_OF_MONTH));
    }
    
    public void testGetDateEnd() {
        Date end = DateHelper.getDateEnd(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        Assert.assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(59, cal.get(Calendar.MINUTE));
        Assert.assertEquals(59, cal.get(Calendar.SECOND));
    }
    
    private Date getSpecialDate() {
        return DateHelper.getDate(22, 11, 2000);
    }
    
    public void testGetDateStart() {
        Date dateStart = DateHelper.getDateStart(getSpecialDate());
        Assert.assertEquals(2000, DateHelper.getYear(dateStart));
        Assert.assertEquals(11, DateHelper.getMonth(dateStart));
        Assert.assertEquals(22, DateHelper.getDay(dateStart));
    }
    
    public void testGetMonthStart() {
        Date date = DateHelper.getMonthStart(DateHelper.getDate(20, Calendar.NOVEMBER, 2012));
        Assert.assertEquals(2012, DateHelper.getYear(date));
        Assert.assertEquals(Calendar.NOVEMBER, DateHelper.getMonth(date));
        Assert.assertEquals(1, DateHelper.getDay(date));
        
        date = DateHelper.getMonthStart(DateHelper.getDate(1, Calendar.SEPTEMBER, 2012));
        Assert.assertEquals(2012, DateHelper.getYear(date));
        Assert.assertEquals(Calendar.SEPTEMBER, DateHelper.getMonth(date));
        Assert.assertEquals(1, DateHelper.getDay(date));
    }

    public void testCompare() {
        //not null
        Date now = new Date();
        Assert.assertTrue(DateHelper.compare(now, DateUtils.addHours(now, 1)) < 0);
        Assert.assertTrue(DateHelper.compare(now, DateUtils.addMinutes(now, -1)) > 0);
        Assert.assertTrue(DateHelper.compare(now, DateUtils.addMinutes(now, 0)) == 0);
        
        //null
        Assert.assertTrue(DateHelper.compare(now, null) > 0);
        Assert.assertTrue(DateHelper.compare(null, now) < 0);
        Assert.assertTrue(DateHelper.compare(null, null) == 0);
    }

    public void testCompareIgnoreTime() {
        Date now = new Date();
        Assert.assertTrue(DateHelper.compareIgnoreTime(now, DateUtils.addHours(now, 1)) == 0);
        Assert.assertTrue(DateHelper.compareIgnoreTime(now, DateUtils.addDays(now, -1)) > 0);
        Assert.assertTrue(DateHelper.compareIgnoreTime(now, DateUtils.addMonths(now, 1)) < 0);
        Assert.assertTrue(DateHelper.compareIgnoreTime(now, null) > 0);
        Assert.assertTrue(DateHelper.compareIgnoreTime(null, now) < 0);
        Assert.assertTrue(DateHelper.compareIgnoreTime(null, null) == 0);
    }
    
    public void testGetDifferenceInWeek() throws Exception {
        Date date1 = DateHelper.getDate(30, Calendar.JUNE, 2012); 
        Date date2 = DateHelper.getDate(1, Calendar.JUNE, 2012);
        Assert.assertEquals(DateHelper.getDifferenceInWeek(date1, date2), 5);
        
        date1 = DateHelper.getDate(2, Calendar.JUNE, 2012); 
        date2 = DateHelper.getDate(1, Calendar.JUNE, 2012);
        Assert.assertEquals(DateHelper.getDifferenceInWeek(date1, date2), 1);
        
        date1 = DateHelper.getDate(7, Calendar.JUNE, 2012); 
        date2 = DateHelper.getDate(1, Calendar.JUNE, 2012);
        Assert.assertEquals(DateHelper.getDifferenceInWeek(date1, date2), 1);
        
        date1 = DateHelper.getDate(8, Calendar.JUNE, 2012); 
        date2 = DateHelper.getDate(1, Calendar.JUNE, 2012);
        Assert.assertEquals(DateHelper.getDifferenceInWeek(date1, date2), 2);
    }
    
// CHECKSTYLE:ON MagicNumber
}
