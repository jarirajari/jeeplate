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
package org.sisto.jeeplate.user;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.security.shiro.Salt;

/**
 * Business object model for an actual business object
 */
@Entity
@Table(name = "system_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")})
public class UserEntity extends BusinessEntity implements Serializable {
    
    protected String username;
    protected String salt;
    protected String password;
    
    /*
    @Enumerated(EnumType.STRING)
    protected ApplicationRoles.Role role;
    */
    
    
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
        private Sha256Hash hasher;

        public UserEntityBuilder() {
            this.object = new UserEntity();
            this.defaults();
        }

        private void defaults() {
            this.object.username = "";
            this.object.password = "";
            this.object.salt = "";
        }
        
        public UserEntityBuilder withId(Long id) {
            this.object.id = id;
            
            return (this);
        }
        
        public UserEntityBuilder withName(String name) {
            this.object.username = name;
            
            return (this);
        }
        
        public UserEntityBuilder withPassword(String password) {
            this.object.password = password;
            
            return (this);
        }
        
        private String getSaltedAndHashedPassword() {
            final int hashIterations = 1;
            this.hasher = new Sha256Hash(this.object.password, this.object.salt, hashIterations);
            
            return (this.hasher.toHex());
        }
        
        public UserEntity build() {
            this.object.salt = Salt.getSaltString();
            this.object.password = this.getSaltedAndHashedPassword();
            
            return (this.object);
        }
    }
}
