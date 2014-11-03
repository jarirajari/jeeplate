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
import org.sisto.jeeplate.domain.BusinessEntity;

/**
 * Business object model for an actual business object
 */
@Entity
@Table(name = "jee_user")
public class UserEntity extends BusinessEntity implements Serializable {
    
    public String name = "";
    
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
            this.object.name = "";
        }

        public UserEntityBuilder withName(String sname) {
            this.object.name = sname;
            return (this);
        }

        public UserEntity build() {
            
            
            return (this.object);
        }
    }
}
