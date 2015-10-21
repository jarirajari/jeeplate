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

public enum SystemRole {

    DOMAIN_SPACE_ADMIN("system-root"),
    DOMAIN_ADMIN("domain-admin"),
    DOMAIN_GROUP_ADMIN("group-admin"),
    DOMAIN_GROUP_MEMBER("group-member"),
    SYSTEM_USER("registered-user"),
    GUEST_USER("unregistered-user");

    private final String role;

    SystemRole(String s) {
        this.role = s;
    }
    
    public static SystemRole convert(String name) {
        SystemRole converted;
        
        try {
            converted = SystemRole.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException ex) {
            converted = GUEST_USER;
        }
        
        return converted;
    }
}
