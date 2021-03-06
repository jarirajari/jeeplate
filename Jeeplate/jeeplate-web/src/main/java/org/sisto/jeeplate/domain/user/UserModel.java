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
package org.sisto.jeeplate.domain.user;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sisto.jeeplate.logging.StringLogger;

@Named
public class UserModel { // model view control are backing beans => BACKING MVC?
    
    @Inject
    StringLogger log;
    
    @Inject
    UserData backing;  
    Map<Long, UserData> all;
    
    
    @PostConstruct
    public void init() {
        this.all = this.backing.findAll();
    }
    
    public Map<Long, UserData> allUsers() {
        return (this.backing.findAll());
    }
    
}
