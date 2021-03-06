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
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import org.sisto.jeeplate.authentication.role.ApplicationRole;
import org.sisto.jeeplate.authentication.role.ApplicationRoles;
import org.sisto.jeeplate.authentication.role.SystemRole;
import org.sisto.jeeplate.authentication.role.SystemRoles;
import org.sisto.jeeplate.domain.pk.SecondaryKeyField;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipEntity;
import org.sisto.jeeplate.domain.pk.TernaryKeyField;
import org.sisto.jeeplate.domain.user.account.UserAccountEntity;
import org.hibernate.annotations.Cascade;

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
    @Enumerated(EnumType.STRING)
    protected UserType type;
    @Embedded
    protected UserCredential credential;
    @Embedded
    protected ApplicationRoles appRole;
    @Embedded
    protected SystemRoles sysRole;
    // User is a member in many groups and each group can contain many members => membership
    @OneToMany(mappedBy = "systemUser")
    protected List<DomainGroupMembershipEntity> domaingroupMemberships;
    @OneToOne @Cascade({org.hibernate.annotations.CascadeType.ALL})
    protected UserAccountEntity oneAccount;
    
    public UserEntity() {
        this.id = DEFAULT_ID;
        this.username = "";
        this.mobile = 0L;
        this.type = UserType.UNKNOWN;
        this.credential = new UserCredential();
        this.appRole = new ApplicationRoles();
        this.sysRole = new SystemRoles();
        this.domaingroupMemberships = new ArrayList<>();
        this.oneAccount = null;
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
        super.setVersion(this.version);
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

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
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

    public UserAccountEntity getOneAccount() {
        return oneAccount;
    }

    public void setOneAccount(UserAccountEntity oneAccount) {
        this.oneAccount = oneAccount;
    }
    
    public UserEntity asRoot() {
        this.setType(UserType.SYSTEM);
        this.getSysRole().setRole(SystemRole.DOMAIN_SPACE_ADMIN);
        this.getAppRole().setRoleGroup(ApplicationRoles.EMPTY_GROUP);
        
        return this;
    }
    
    public UserEntity asSystemUser() {
        this.setType(UserType.SYSTEM);
        this.getSysRole().setRole(SystemRole.SYSTEM_USER);
        this.getAppRole().setRoleGroup(ApplicationRoles.EMPTY_GROUP);
        
        return this;
    }
    
    public UserEntity asApplicationUser() {
        this.setType(UserType.APPLICATION);
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
    
    public String currentRoleForUser() {
        final SystemRole userSysRole = this.sysRole.getCurrentRole();
        final ApplicationRole userAppRole = this.getAppRole().getCurrentRole();
        String roleName;
        
        if (isApplicationUserThereforeNotSystemUser()) {
            roleName = userAppRole.getRole();
        } else {
            roleName = userSysRole.getRole();
        }
        
        return roleName;
    }
    /*
    // OLD
    public Boolean isApplicationUserThereforeNotSystemUser() {
        final SystemRole userSysRole = this.sysRole.getCurrentRole();
        final BitSet userAssignedGroup = this.getAppRole().getRoleGroup();
        Boolean notEmptyAppGroup = (! ApplicationRoles.EMPTY_GROUP.equals(userAssignedGroup));
        Boolean isAppUsr;
        
        if ((SystemRole.SYSTEM_USER == userSysRole) && (notEmptyAppGroup)) {
            isAppUsr = Boolean.TRUE;
        } else {
            isAppUsr = Boolean.FALSE;
        }
        
        return isAppUsr;
    }
    */
    // NEW 
    public Boolean isApplicationUserThereforeNotSystemUser() {
        return (this.getType() == UserType.APPLICATION);
    }
    
    public Map<String, String> assignedRolesForUser(Boolean excludeCurrentRole) {
        final Map<String, String> usersRoles = new HashMap<>();
        
        if (isApplicationUserThereforeNotSystemUser()) {
            usersRoles.putAll(this.getAppRole().assignedRoles(excludeCurrentRole));
        } else {
            usersRoles.putAll(this.getSysRole().assignedRoles(excludeCurrentRole));
        }
        
        return usersRoles;
    }
    
    public Boolean requiresTwoFactorAuth(ApplicationRole newRole) {
        Boolean requires;
        
        if (isApplicationUserThereforeNotSystemUser()) {
            requires = this.getAppRole().requires2FAwhenRoleChange(newRole);
        } else {
            // Any role change for a system user will require 2FA!
            requires = this.getSysRole().requires2FAwhenRoleChange();
        }
        
        return requires;
    }
    
    public String twoFactorAuthPIN() {
        return (this.getCredential().newPIN());
    }
    
    public Boolean swithcRoleTo(String newRole, String pin2FA, Boolean req2FA) {
        Boolean success = Boolean.FALSE;
        Boolean appNotSys = isApplicationUserThereforeNotSystemUser();
        
        if (req2FA) {
            final String pin = this.getCredential().askPIN();
            Boolean pinsMatch = pin.equals(pin2FA);
            
            if (pinsMatch) {
                if (appNotSys) {
                    this.getAppRole().assignRole(ApplicationRole.convert(newRole));
                } else {
                    this.getSysRole().assignRole(SystemRole.convert(newRole));
                }
                success = Boolean.TRUE;
            }
        } else {
            if (appNotSys) {
                this.getAppRole().assignRole(ApplicationRole.convert(newRole));
            } else {
                this.getSysRole().assignRole(SystemRole.convert(newRole));
            }
            success = Boolean.TRUE;
        }
        
        return success;
    }
    
    public Boolean userHasRegistered() {
        Boolean registered = false;
        
        if (this.getOneAccount() == null) {
            registered = false;
        } else if (this.getOneAccount().getRegistrationCompleted()) {
            registered = (this.getOneAccount().getRegistrationCompleted());
        } else {
            registered = false;
        }
        
        return registered;
    }
}
