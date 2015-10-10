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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

// The access type for an embedded object is determined by the access type of the entity in which it is embedded.
@Embeddable 
public class SystemRoles implements Serializable {

    /*
     * System means all domains,
     * One domain is many groups,
     * One group is many single users
     */
    
    @Enumerated(EnumType.STRING) @Column(name = "system_roles_group")
    protected SystemRole role;
    
    public SystemRoles() {
        this.role = SystemRole.GUEST_USER;
    }
    
    public SystemRole getRole() {
        return this.role;
    }
    
    public void setRole(SystemRole newRole) {
        this.role = newRole;
    }
    
    public SystemRoles asRoot() {
        this.setRole(SystemRole.DOMAIN_ADMIN);
        
        return this;
    }
    
    public Boolean isRoot() {
        Boolean fact = Boolean.FALSE;
        
        if (this.role == SystemRole.DOMAIN_SPACE_ADMIN) {
            fact = Boolean.TRUE;
        }
        
        return fact;
    }
    
    public boolean requires2FAwhenRoleChange() {
        return Boolean.TRUE;
    }
    
    public Map<String, String> assignedRoles() {
        final SystemRole[] allRoles = SystemRole.values();
        HashMap assigned = new HashMap<>();
        
        for (SystemRole role : allRoles) {
            if (role == SystemRole.GUEST_USER) {
                continue; // we don't want guest user
            }
            assigned.put(role.toString(), role.name());
        }
        
        return assigned;
    }
    
    @Override
    public String toString() {
        return (this.role.toString());
    }
}
