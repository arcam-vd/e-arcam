/*
 * DateUtils.java
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.arcam.cyberadmin.dom.core.PeriodiciteTypeEnum;

/**
 * Utils class for date time values.
 * 
 * @author mmn
 *
 */
public final class DateHelper {

    /**
     * The first day of a month.
     */
    public static final int FIRST_DAY_OF_MONTH = 1;
    
    /**
     * The first day of a year.
     */
    public static final int FIRST_DAY_OF_YEAR = 1;
    
    /**
     * The end day of September.
     */
    public static final int END_DAY_OF_SEPTEMBER = 30;
    
    /**
     * Number of days in a weeks.
     */
    public static final int DAYS_IN_WEEK = 7;

    /**
     * The first month of year
     */
    private static final int FIRST_MONTH_OF_YEAR = 0;
    
    /**
     * The  start year in declaration.
     */
    public static final int YEAR_START = 2011;
    
    /**
     * The end year in declaration.
     */
    public static final int YEAR_END = Calendar.getInstance().get(Calendar.YEAR) + 1;
    
    /**
     * The default year of java.
     */
    private static final int DEFAULT_YEAR_IN_JAVA = 1900;
    
    private DateHelper() {
        // to hide
    }
    
    /**
     * Returns current datetime.
     * 
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * Returns current date excluding time.
     * 
     * @return
     */
    public static Date today() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Returns the starting of given date. For example, "20.10.2010 15:12:00" will be converted to
     * "20.10.2010 00:00:00".
     * 
     * @param date
     * @return
     */
    public static Date getDateStart(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Returns the starting date of the month which the given date belongs to. 
     * For example, "20.11.2012" will be converted to "01.11.2012"
     * 
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        return DateUtils.truncate(date, Calendar.MONTH);
    }
    
    /**
     * Returns the end of given date. For example, "20.10.2010 15:12:00" will be converted to "20.10.2010 23:59:59".
     * 
     * @param date
     * @return
     */
    public static Date getDateEnd(Date date) {
        // CHECKSTYLE:OFF MagicNumber
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        
        // CHECKSTYLE:ON MagicNumber
        
        return cal.getTime();
    }
    
    /**
     * null is smaller than not null.
     */
    public static int compare(Date date1, Date date2) {
        int result = 0;
        if (date1 == null && date2 == null) {
            result = 0;
        } else if (date2 == null && date1 != null) {
            result = 1;
        } else if (date2 != null && date1 == null) {
            result = -1;
        } else if (date2 != null && date1 != null) {
            result = date1.compareTo(date2);
        }
        return result;
    }
    
    /**
     * null is smaller than not null.
     */
    public static int compareIgnoreTime(Date date1WithoutTime, Date date2WithoutTime) {
        if (date1WithoutTime != null) {
            date1WithoutTime = DateUtils.truncate(date1WithoutTime, Calendar.DATE);
        }
        if (date2WithoutTime != null) {
            date2WithoutTime = DateUtils.truncate(date2WithoutTime, Calendar.DATE);
        }
        return compare(date1WithoutTime, date2WithoutTime);
    }
    
    /**
     * Gets the date object from the given day, month, year. 
     * <b> 
     * IMPORTANT: The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0; 
     * </b>
     * 
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Date getDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        return cal.getTime();
    }
    
    /**
     * Get the first date of the current year.
     * 
     * @return
     */
    public static Date getFirstDateOfCurrentYear() {
        return getDate(FIRST_DAY_OF_YEAR, FIRST_MONTH_OF_YEAR, getYear(now()));
    }
    
    /**
     * Extracts the year value from the given date.
     * 
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        if (date == null) {
            return 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * Extracts the month value from the given date. Note that the month value is count from zero.
     * 
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }
    
    /**
     * Extracts the day value from the given date.
     * 
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }
    
    /**
     * Gets the difference (in day) between <code>date1</code> and <code>date2</code>. 
     * CAUTION: It's wrong if the two dates are on each side of a daylight saving change
     * 
     * @param date1
     *            The first date.
     * @param date2
     *            The second date.
     * @return The number of days which is the result of <code>date1</code>-
     *         <code>date2</code>.
     *         <P>
     * 
     * Returns 0 if one of the two dates is null.
     * <p>
     * 
     * For example: date1 = 31.01.2005 <br>
     * date2 = 01.02.2005 <br>
     * getDifference(date1, date2) = -1
     */
    public static int getDifference(Date date1, Date date2) {
        // it's wrong if the two dates are on each side of a daylight saving change
        if ((date1 == null) || (date2 == null)) {
            return 0;
        }
        Date date1Tmp = DateUtils.truncate(date1, Calendar.DATE);
        Date date2Tmp = DateUtils.truncate(date2, Calendar.DATE);
        long diff = date1Tmp.getTime() - date2Tmp.getTime();

        return (int) (diff / DateUtils.MILLIS_PER_DAY);
    }
    
    /**
     * Same as the getDifference(Date, Date) but the result will be the difference in week.
     * 
     * @param date
     * @return
     */
    public static int getDifferenceInWeek(Date date1, Date date2) {
        int dayDifference = getDifference(date1, date2);
        float weeks = new Float(dayDifference + 1) / new Float(DAYS_IN_WEEK);
        BigDecimal result = new BigDecimal(weeks);
        return result.setScale(0, BigDecimal.ROUND_UP).intValue();
        
    }
    
    /**
     * Checks if the two given dates have the same month.
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean sameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return getMonth(date1) == getMonth(date2);
    }
    
    /**
     * Checks if the two given dates have the same quater.
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean sameQuater(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        
        final List<Integer> quater1 = Arrays.asList(Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH);
        final List<Integer> quater2 = Arrays.asList(Calendar.APRIL, Calendar.MAY, Calendar.JUNE);
        final List<Integer> quater3 = Arrays.asList(Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER);
        final List<Integer> quater4 = Arrays.asList(Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER);
        
        return (quater1.contains(getMonth(date1)) && quater1.contains(getMonth(date2))) 
                || (quater2.contains(getMonth(date1)) && quater2.contains(getMonth(date2)))
                || (quater3.contains(getMonth(date1)) && quater3.contains(getMonth(date2)))
                || (quater4.contains(getMonth(date1)) && quater4.contains(getMonth(date2)));
    }
    
    /**
     * Checks if the two given dates have the same semeter.
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean sameSemeter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        
        final List<Integer> semeter1 = Arrays.asList(Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH,
                Calendar.APRIL, Calendar.MAY, Calendar.JUNE);
        final List<Integer> semeter2 = Arrays.asList(Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER,
                Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER);
        
        return (semeter1.contains(getMonth(date1)) && semeter1.contains(getMonth(date2)))
                || (semeter2.contains(getMonth(date1)) && semeter2.contains(getMonth(date2)));
    }
    
    /**
     * Checks if the two given date have the same year.
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean sameYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return getYear(date1) == getYear(date2);
    }
    
    public static String getFormattedDate(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(CyberAdminConstants.DATE_PATTERN);
        return df.format(date);
    }
    
    public static String getFormattedYear(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(CyberAdminConstants.FISCALE_YEAR_PATTERN);
        return df.format(date);
    }
    
    /**
     * Get year after calculated the default year of Java.
     * Because Java start counting from year 1900.
     * Then need subtract 1900 when saving year.
     */
    public static int getCaculatedJavaYear(int year) {
        return year - DEFAULT_YEAR_IN_JAVA;
    }
    
    public static Date getPeridEnd(Date fiscaleDate, PeriodiciteTypeEnum periodiciteTypeEnum){
    	Date endDate = DateUtils.addMonths(fiscaleDate, periodiciteTypeEnum.getMonths());
        endDate = DateUtils.addDays(endDate, -1); // last day of the period instead of first day of the next one
        return endDate;
    }
}
