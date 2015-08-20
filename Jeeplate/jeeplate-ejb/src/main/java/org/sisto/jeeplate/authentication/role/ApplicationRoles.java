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
package org.sisto.jeeplate.authentication.role;

import org.sisto.jeeplate.association.cardinality.OneToMany;

public class ApplicationRoles implements OneToMany {
    
    /*
    
    these are just for example, programmer should redefine them for customization
    
    initially every user registered is SystemRoles...SYSTEM_USER which is 
    the only role that can have application roles
    
    System admin do not have application roles, but can temporarily get one...
    
    */
    
    public enum Role {
        ADMINISTRATOR("administrator"),
        DIRECTOR("director"),
        ACTOR("actor"),
        VISITOR("visitor"),
        UNKNOWN("unknown");
        
        String role = "";
        
        Role(String s) {
            this.role = s;
        }
    }
}
