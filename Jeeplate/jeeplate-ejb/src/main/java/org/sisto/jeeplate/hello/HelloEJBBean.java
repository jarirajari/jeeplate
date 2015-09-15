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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.util.ApplicationProperty;

@Stateless
public class HelloEJBBean {

    @Inject
    Logger log;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "test msg prop ok")
    String myProperty;
    
    public HelloEJBBean() {}
    
    public String sayHello() {
        
        return ("Hello EJB! with prop="+myProperty);
    }
    
    @PostConstruct
    public void init() {
        log.info("HelloEJBBean@PostConstruct");
    }
    
    @PreDestroy
    public void lize() {
        log.info("HelloEJBBean@PreDestroy");
    }
}
