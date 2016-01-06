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

    DOMAIN_SPACE_ADMIN("system-root", 5),
    DOMAIN_ADMIN("domain-admin", 4),
    DOMAIN_GROUP_ADMIN("group-admin", 3),
    DOMAIN_GROUP_MEMBER("group-member", 2),
    SYSTEM_USER("registered-user", 1),
    GUEST_USER("unregistered-user", 0);

    private String role = "";
    private int level = 0;

    SystemRole(String newRole, int newLevel) {
        this.role = newRole;
        this.level = newLevel;
    }
    
    public String getRole() {
        return (this.role);
    }
    
    public Integer getLevel() {
        return (this.level);
    }
    
    public Boolean comparedRequiresElevation(SystemRole compared) {
        final int compareLevel = compared.getLevel();
        boolean requires = false;
        
        if (this.level < compareLevel) {
            requires = true;
        }
        
        return requires;
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
