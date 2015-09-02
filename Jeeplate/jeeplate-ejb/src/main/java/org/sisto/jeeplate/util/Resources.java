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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.logging.StringLogger;

@Dependent
public class Resources {
    
    /*
     * Usage:
     * 
     * @Inject
     * private transient Logger log;
     */
    @Produces @Default
    public Logger jbossLogger(InjectionPoint ip) {
        final String name = ip.getMember().getDeclaringClass().getSimpleName();
        
        return Logger.getLogger(name);
    }
    
    @Produces @Default
    public StringLogger stringLogger(InjectionPoint ip) {
        final String name = ip.getMember().getDeclaringClass().getSimpleName();
        
        return (new StringLogger(name));
    }
    
}
