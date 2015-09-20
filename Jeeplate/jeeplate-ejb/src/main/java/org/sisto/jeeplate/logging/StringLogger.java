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
package org.sisto.jeeplate.logging;

import java.io.Serializable;
import java.util.Arrays;
import javax.enterprise.inject.Vetoed;
import org.jboss.logging.Logger;

@Vetoed
public class StringLogger implements Serializable {
    
    public transient static final String[] EMPTY_ARRAY = new String[0];
    private Logger jbossLogger;
    
    public StringLogger() {
        
    }
    
    public StringLogger(String name) {
        this.jbossLogger = Logger.getLogger(name);
    }
    
    public StringLogger(Class clazz) {
        this.jbossLogger = Logger.getLogger(clazz.getName());
    }

    public Logger getJbossLogger() {
        return jbossLogger;
    }

    public void setJbossLogger(Logger jbossLogger) {
        this.jbossLogger = jbossLogger;
    }
    
    private static String buildMessage(String text, String ... args) {
        int size = args.length;
        boolean onlyBody = (size <= 0);
        StringBuilder sb = new StringBuilder("");

        if (onlyBody) {
            sb.append(text.replaceAll("%s", ""));
        } else {
            sb.append(String.format(text, Arrays.asList(args).toArray()));
        }
        
        return (sb.toString());
    }
    
    public void error(String format, String... args) {
        this.jbossLogger.error(buildMessage(format, args));
    }

    public void warn(String format , String... args) {
        this.jbossLogger.warn(buildMessage(format, args));
    }

    public void info(String format , String... args) {
        this.jbossLogger.info(buildMessage(format, args));
    }

    public void debug(String format , String... args) {
        this.jbossLogger.debug(buildMessage(format, args));
    }

    public void trace(String format , String... args) {
        this.jbossLogger.trace(buildMessage(format, args));
    }
}
