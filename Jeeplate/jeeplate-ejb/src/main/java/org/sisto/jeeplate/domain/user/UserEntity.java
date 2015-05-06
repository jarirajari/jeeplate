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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.security.shiro.Salt;

/**
 * Business object model for an actual business object
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "system_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")})
public class UserEntity extends BusinessEntity implements Serializable {
    
    @Id
    @SequenceGenerator(name = "user_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String username; // email address
    protected String mobile; // mobile msisdn
    // requires later a reference to a person or organsation individual
    @Embedded
    protected UserCredential credential;
    
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public UserCredential getCredential() {
        return credential;
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
            this.object.mobile = "";
            this.object.credential = new UserCredential();
        }
        
        public UserEntityBuilder withUsername(String name) {
            this.object.username = name;
            
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
