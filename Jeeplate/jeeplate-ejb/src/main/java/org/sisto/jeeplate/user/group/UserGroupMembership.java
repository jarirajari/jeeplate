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
package org.sisto.jeeplate.user.group;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import org.sisto.jeeplate.data.UserData;
import org.sisto.jeeplate.data.UserGroupData;

@Dependent
public class UserGroupMembership implements Serializable {
    
    public UserGroupMembership() {}
    public UserGroupMembership(UserData user, UserGroupData group) {
        
    }
    
    public void test() {
        
    }
    
}