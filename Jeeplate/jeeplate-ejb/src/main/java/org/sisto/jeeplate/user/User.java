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
package org.sisto.jeeplate.user;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.BusinessObject;

@RequestScoped
public class User extends BusinessObject<User> {
    
    @Inject
    private transient Logger log;
    
    @Inject
    @New
    private UserData data;
    
    /*
    UserModel
    UserView
    UserControl
    UserData
    UserRules
    UserLogic
    */
    
    public User bind(Long id) {
        
        this.data.bind(id);
        
        return this;
    }
    
    public User create() {
        this.data.create();
        
        return this;
    }
    
    public User read() {
        this.data.read();
        
        return this;
    }
    
    public User update(){
        this.data.update();
        
        return this;
    }
    
    public User delete() {
        this.data.delete();
        
        return this;
    }
    
    public String testPrint() {
        return "testprint()@User";
    }
    
    @Override
    public String toString() {
        return "User: id="+this.data.getId();
    }
    
}

