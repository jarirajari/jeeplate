/*
 * Jeeplate
 * Copyright (C) 2014 Jari Kuusisto
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.sisto.jeeplate.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateAndTimeConverter {
    public static Instant convertToInstant(Date ts) {
        Instant i = Instant.ofEpochMilli(ts.getTime());
        
        return i;
    }
    public static Date convertToDate(Instant i) {
        Date d = Date.from(i);
        
        return d;
    }
    /*
     * Convert java.util.Date to java.time.LocalDateTime that is a
     * date-time without a time-zone in the ISO-8601 calendar system,
     * such as 2007-12-03T10:15:30. It does not store or represent a time-zone!
     */
    public static LocalDateTime convertToLocalDateTime(Date ts) {
        // instant is offset from epoch
        Instant i = Instant.ofEpochMilli(ts.getTime());
        LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
        
        return ldt;
    }
    /*
     * Convert java.time.LocalDateTime to java.util.Date
     */
    public static Date convertToDate(LocalDateTime ldt) {
        Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        Date d = Date.from(instant);
        
        return d;
    }
    /*
     * Convert java.util.Date to java.time.LocalDate that is a
     * date that represents a date without a time zone, such as 20-12-2013. 
     */
    public static LocalDate convertToLocalDate(Date ts) {
        Instant i = Instant.ofEpochMilli(ts.getTime());
        LocalDate ld = LocalDateTime.ofInstant(i, ZoneId.systemDefault()).toLocalDate();
        
        return ld;
    }
    /*
     * Convert java.time.LocalDate to java.util.Date
     */
    public static Date convertToDate(LocalDate ld) {
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date d = Date.from(instant);
        
        return d;
    }
    /*
     * Convert java.util.Date to java.time.LocalTime that is a
     * time that represents a time without a time zone, such as 01:23:455.67
     */
    public static LocalTime convertToLocalTime(Date ts) {
        Instant i = Instant.ofEpochMilli(ts.getTime());
        LocalTime lt = LocalDateTime.ofInstant(i, ZoneId.systemDefault()).toLocalTime();
        
        return lt;
    }
    /*
     * Convert java.time.LocalTime to java.util.Date
     */
    public static Date convertToDate(LocalTime lt) {
        LocalDate ld = LocalDate.now();
        Instant instant = lt.atDate(LocalDate.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth())).atZone(ZoneId.systemDefault()).toInstant();
        Date d = Date.from(instant);
        
        return d;
    }
}
