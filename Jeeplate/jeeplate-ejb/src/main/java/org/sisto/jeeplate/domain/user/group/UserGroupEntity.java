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
package org.sisto.jeeplate.domain.user.group;


import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
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
import org.sisto.jeeplate.domain.BusinessEntity;

@Entity
@Access(AccessType.FIELD)
@Table(name = "system_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = "groupname")})
public class UserGroupEntity extends BusinessEntity implements Serializable {
    @Id
    @SequenceGenerator(name="user_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_group_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String groupname;
    
    @PostLoad
    @PostPersist
    @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public String getGroupname() {
        return this.groupname;
    }
    
    public void setGroupname(String name) {
        this.groupname = name;
    }
    
    public static UserGroupEntity defaultUserGroupEntity() {
        return (new UserGroupEntityBuilder()).build();
    }
    
    public static UserGroupEntityBuilder newUserGroupEntityBuilder() {
        return (new UserGroupEntityBuilder());
    }
    
    public static class UserGroupEntityBuilder {
        
        private UserGroupEntity object;

        public UserGroupEntityBuilder() {
            
            this.object = new UserGroupEntity();
            this.defaults();
        }

        private void defaults() {
            this.object.groupname = "";
        }
        
        public UserGroupEntityBuilder withName(String name) {
            this.object.groupname = name;
            
            return (this);
        }
        
        public UserGroupEntity build() {
            
            this.object.id = null;
            
            return (this.object);
        }
        
        public UserGroupEntity renovate(Long id) {
            
            this.object.id = id;
            
            return (this.object);
        }
    }
}
