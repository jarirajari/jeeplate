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
package org.sisto.jeeplate.hello;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.jboss.logging.Logger;

@Stateless
public class HelloService {
    
    @Inject
    private transient Logger log;
    
    // TODO: Testing import javax.ejb.embeddable.EJBContainer;
    
    // Stateful, stateless, singleton, and message-driven beans 
    // have different events
    
    public void testHelloServiceLogging(HelloEntity he) {
        String msg = (he == null) ? "null" : he.getMessage();
        log.info(String.format("HelloService prints HelloEntity message: '%s'", msg));
    }
}
