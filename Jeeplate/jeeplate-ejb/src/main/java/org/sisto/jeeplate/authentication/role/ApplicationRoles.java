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
import java.util.BitSet;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

// The access type for an embedded object is determined by the access type of the entity in which it is embedded.
@Embeddable
public class ApplicationRoles implements Serializable {
    
    /*
     *  There are no individual roles, but groups with also one role
     */
    @Transient
    public transient static final BitSet EMPTY_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.NONE)
            .group();
    @Transient
    public transient static final BitSet NORMAL_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.NONE)
            .role(ApplicationRole.ACTOR)
            .group();
    /*
    
    these are just for example, programmer should redefine them for customization
    
    initially every user registered is SystemRoles...SYSTEM_USER which is 
    the only role that can have application roles
    
    System admin do not have application roles, but can temporarily get one...
    
    */
    
    /*
     * We do not persist roles (enums) but role group: so no @Enumerated!
     * For n roles there will be 2^n groups, where each role has index from enum
     */
    @Transient
    protected BitSet roleGroup;
    @Column(name ="application_roles_group")
    protected byte[] roleGroupAlias;

    public byte[] getRoleGroupAlias() {
        this.roleGroupAlias = this.roleGroup.toByteArray();
        
        return roleGroupAlias;
    }

    public void setRoleGroupAlias(byte[] roleGroupAlias) {
        this.roleGroupAlias = roleGroupAlias;
        
        this.roleGroup = BitSet.valueOf(roleGroupAlias);
    }
    
    public ApplicationRoles() {
        this.roleGroup = ApplicationRoles.EMPTY_GROUP;
        this.roleGroupAlias = this.roleGroup.toByteArray();
    }
    
    /* 
     * + elevate allow
     * - demote  deny
     */
    private void elevateGroup(ApplicationRole newRole) {
        this.roleGroup.set(newRole.bitIndex());
        this.roleGroupAlias = this.roleGroup.toByteArray();
    }
    
    private void demoteGroup(ApplicationRole oldRole) {
        this.roleGroup.clear(oldRole.bitIndex());
        this.roleGroupAlias = this.roleGroup.toByteArray();
    }
    
    public void allowAdmin() {
        this.elevateGroup(ApplicationRole.ADMINISTRATOR);
    }
    
    public void denyAdmin() {
        this.demoteGroup(ApplicationRole.ADMINISTRATOR);
    }
    
    public Boolean isNormalUser() {
        return (this.roleGroup.equals(NORMAL_GROUP));
    }
    
    final static ApplicationRoleGroupBuilder newGroup() {
        return (new ApplicationRoleGroupBuilder());
    }
    
    final static class ApplicationRoleGroupBuilder {
        private BitSet object;
        
        public ApplicationRoleGroupBuilder() {
            this.object = new BitSet(ApplicationRole.values().length);
            this.object.clear();
        }
        
        public ApplicationRoleGroupBuilder role(ApplicationRole newRole) {
            this.object.set(newRole.bitIndex());
            
            return this;
        }
        
        public BitSet group() {
            return (this.object);
        }
    }
}
