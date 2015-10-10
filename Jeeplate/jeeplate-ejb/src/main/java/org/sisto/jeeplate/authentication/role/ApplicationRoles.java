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
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.apache.commons.lang3.ArrayUtils;

// The access type for an embedded object is determined by the access type of the entity in which it is embedded.
@Embeddable
public class ApplicationRoles implements Serializable {
    
    /*
     *  There are no individual roles, but groups with also one role
     */
    @Transient
    public transient static final BitSet EMPTY_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.VISITOR)
            .group();
    @Transient
    public transient static final BitSet NORMAL_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.ACTOR)
            .group();
    @Transient
    public transient static final BitSet DIRECTOR_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.ACTOR)
            .role(ApplicationRole.DIRECTOR)
            .group();
    @Transient
    public transient static final BitSet ADMIN_GROUP = ApplicationRoles.newGroup()
            .role(ApplicationRole.ACTOR)
            .role(ApplicationRole.DIRECTOR)
            .role(ApplicationRole.ADMINISTRATOR)
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
    protected ApplicationRole currentRole;
    @Transient
    protected BitSet roleGroup;
    @Column(name ="application_roles_group_hex")
    protected byte[] roleGroupAlias;
    
    public ApplicationRoles() {
        this.roleGroup = (BitSet) ApplicationRoles.EMPTY_GROUP.clone();
        this.roleGroupAlias = this.roleGroup.toByteArray();
        this.currentRole = ApplicationRole.VISITOR;
    }

    public ApplicationRole getCurrentRole() {
        if (currentRole == ApplicationRole.VISITOR) {
            this.setCurrentRole(ApplicationRole.ACTOR);
        }
        
        return currentRole;
    }

    public void setCurrentRole(ApplicationRole currentRole) {
        this.currentRole = currentRole;
    }
    
    public void setRoleGroup(BitSet newRoleGroup) {
        this.roleGroup = newRoleGroup;
        this.roleGroupAlias = newRoleGroup.toByteArray();
    }
    
    public BitSet getRoleGroup() {
        this.roleGroup = BitSet.valueOf(roleGroupAlias);
        
        return this.roleGroup;
    }
    
    public byte[] getRoleGroupAlias() {
        
        return roleGroupAlias;
    }
    
    public void setRoleGroupAlias(byte[] roleGroupAlias) {
        this.roleGroupAlias = ArrayUtils.clone(roleGroupAlias);
        
        this.roleGroup = BitSet.valueOf(roleGroupAlias);
    }
    
    public Boolean requires2FAwhenRoleChange(ApplicationRole newRole) {
        final Boolean newLevelHigher = (newRole.bitIndex()) > (this.currentRole.bitIndex());
        
        return newLevelHigher;
    }
    
    public void assignRole(ApplicationRole newRole) {
        this.currentRole = newRole;
    }
    
    public Map<String, String> assignedRoles() {
        final ApplicationRole[] allRoles = ApplicationRole.values();
        HashMap assigned = new HashMap<>();
        
        for (ApplicationRole role : allRoles) {
            if (role == ApplicationRole.VISITOR) {
                continue; // we don't want visitor user
            }
            BitSet test = (BitSet) getRoleGroup().clone();
            test.and(role.bitSet());
            if (! test.isEmpty()) {
                assigned.put(role.toString(), role.name());
            }
        }
        
        return assigned;
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
            final int INT_B = 32;
            this.object = new BitSet(INT_B);
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
