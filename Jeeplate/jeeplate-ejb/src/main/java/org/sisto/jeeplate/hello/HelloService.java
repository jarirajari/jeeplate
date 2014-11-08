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
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.user.User;

@Stateless
public class HelloService {
    
    @Inject
    private transient Logger log;
    
    @Inject
    @New(User.class)
    private Instance<User> users;
    
    public String testHelloServiceLogging() {
        
        User test = users.get();
        log.info("START: "+test.toString());
        test.create();log.info("1: "+test.toString());
        test.read();log.info("2: "+test.toString());
        test.update();log.info("3: "+test.toString());
        test.delete();log.info("4: "+test.toString());
        test.bind(1L);log.info("RE-1: "+test.toString());
        log.info("END "+test.toString());
        
        return "HelloService";
    }
}
