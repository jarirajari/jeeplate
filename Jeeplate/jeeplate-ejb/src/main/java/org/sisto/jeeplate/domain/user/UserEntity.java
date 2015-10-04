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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import org.sisto.jeeplate.authentication.role.ApplicationRoles;
import org.sisto.jeeplate.authentication.role.SystemRole;
import org.sisto.jeeplate.authentication.role.SystemRoles;
import org.sisto.jeeplate.domain.pk.SecondaryKeyField;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipEntity;
import org.sisto.jeeplate.domain.pk.TernaryKeyField;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_users", uniqueConstraints = {
       @UniqueConstraint(columnNames = "username")})
public class UserEntity extends BusinessEntity implements Serializable {
    
    @Id @SequenceGenerator(name = "user_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    @SecondaryKeyField(keyname="username", description = "Email address")
    protected String username;
    @TernaryKeyField(keyname="mobile", description = "Mobile phone MSISDN")
    @Digits(integer = 15, fraction = 0)
    protected Long mobile;
    @Embedded
    protected UserCredential credential;
    @Embedded
    protected ApplicationRoles appRole;
    @Embedded
    protected SystemRoles sysRole;
    // User is a member in many groups and each group can contain many members => membership
    @OneToMany(mappedBy = "systemUser")
    protected List<DomainGroupMembershipEntity> domaingroupMemberships;
    //protected UserAccount account; // User has one account
    
    public UserEntity() {
        this.id = DEFAULT_ID;
        this.username = "";
        this.mobile = 0L;
        this.credential = new UserCredential();
        this.appRole = new ApplicationRoles();
        this.sysRole = new SystemRoles();
        this.domaingroupMemberships = new ArrayList<>();
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public String getUsername() {
        return (this.username);
    }
    
    public UserEntity setUsername(String setUsername) {
        this.username = setUsername;
        
        return this;
    }

    public Long getMobile() {
        return mobile;
    }

    public UserEntity setMobile(Long mobile) {
        this.mobile = mobile;
        
        return this;
    }

    public UserCredential getCredential() {
        return credential;
    }

    public ApplicationRoles getAppRole() {
        return appRole;
    }

    public UserEntity setAppRole(ApplicationRoles appRole) {
        this.appRole = appRole;
        
        return this;
    }

    public SystemRoles getSysRole() {
        return sysRole;
    }

    public UserEntity setSysRole(SystemRoles sysRole) {
        this.sysRole = sysRole;
        
        return this;
    }

    public List<DomainGroupMembershipEntity> getDomaingroupMemberships() {
        return domaingroupMemberships;
    }

    public UserEntity setDomaingroupMemberships(List<DomainGroupMembershipEntity> memberships) {
        this.domaingroupMemberships = memberships;
        
        return this;
    }
    
    public UserEntity asRoot() {
        this.getSysRole().setRole(SystemRole.DOMAIN_SPACE_ADMIN);
        this.getAppRole().setRoleGroup(ApplicationRoles.EMPTY_GROUP);
        
        return this;
    }
    
    public UserEntity asRegisteredUser() {
        this.getSysRole().setRole(SystemRole.SYSTEM_USER);
        this.getAppRole().setRoleGroup(ApplicationRoles.NORMAL_GROUP);
        
        return this;
    }
    
    // Not a property since no field
    public UserEntity setPassword(String password) {
        this.credential.refresh(password);
        
        return this;
    }
    
    public Boolean mobileNumberIsSame(String msisdn) {
        Boolean same;
        
        try {
            Long temp = Long.valueOf(msisdn);
            if (this.mobile.equals(temp)) {
                same = Boolean.TRUE;
            } else {
                same = Boolean.FALSE;
            }
        } catch (NumberFormatException nfe) {
            same = Boolean.FALSE;
        }
        
        return same;
    }
}
