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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import org.sisto.jeeplate.domain.pk.SecondaryKeyField;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.group.member.DomainGroupMemberEntity;
import org.sisto.jeeplate.domain.pk.TernaryKeyField;

/**
 * Business object model for an actual business object
 */
@Entity
@Access(AccessType.FIELD)
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
    @Enumerated(EnumType.STRING)
    protected UserType.Type type;
    @OneToOne(mappedBy = "ISAUser")
    protected DomainGroupMemberEntity associateddomain; 
    
    protected UserEntity() {
        this.id = DEFAULT_ID;
        this.username = "";
        this.mobile = 0L;
        this.credential = new UserCredential();
        this.type = UserType.Type.UNKNOWN;
        this.associateddomain = null; // unfortunately we will have to use null!
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public String getUsername() {
        return (this.username);
    }
    
    public void setUsername(String setUsername) {
        this.username = setUsername;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public UserCredential getCredential() {
        return credential;
    }

    public UserType.Type getType() {
        return type;
    }

    public void setType(UserType.Type type) {
        this.type = type;
    }

    public DomainGroupMemberEntity getAssociateddomain() {
        return associateddomain;
    }

    public void setAssociateddomain(DomainGroupMemberEntity associateddomain) {
        this.associateddomain = associateddomain;
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
    
    /*
    
    username (Actor as login), and embedded
    + ActorRole -> act_pw, act_salt, act_role (Role.ACTOR)
    + DirectorRole -> dir_pw, dir_salt, dir_role (Role.DIR...)
    + AdministratorRole -> adm_pw, adm_salt, adm_role (Role.ADM...) 
    
    */
    
    public static UserEntityBuilder newUserEntityBuilder() {
        return (new UserEntityBuilder());
    }
    
    public static class UserEntityBuilder {

        private UserEntity object;

        public UserEntityBuilder() {
            
            this.object = new UserEntity();
            this.defaults();
        }

        private void defaults() {
            this.object.username = "";
            this.object.mobile = 0L;
            this.object.credential = new UserCredential();
        }
        
        public UserEntityBuilder withUsername(String name) {
            this.object.username = name;
            
            return (this);
        }
        
        public UserEntityBuilder withMobile(Long mobile) {
            this.object.mobile = mobile;
            
            return (this);
        }
        
        public UserEntityBuilder withPassword(String password) {
            this.object.credential.refresh(password);
            
            return (this);
        }
        
        public UserEntity build() {
            
            this.object.id = null;     
            
            return (this.object);
        }
        
        public UserEntity renovate(Long id) {
            
            this.object.id = id;
            
            return (this.object);
        }
    }
}
