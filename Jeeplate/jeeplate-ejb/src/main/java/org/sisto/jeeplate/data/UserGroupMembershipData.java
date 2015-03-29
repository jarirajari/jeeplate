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
package org.sisto.jeeplate.data;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.entity.UserGroupMembershipEntity;

@SessionScoped
public class UserGroupMembershipData implements Serializable {
    
    @Inject
    private transient Logger log;
    
    @Inject
    private transient BusinessEntityStore<UserGroupMembershipEntity> store;
    
    private transient UserGroupMembershipEntity entity;
    
    public void setEntity(UserGroupMembershipEntity uge) {
        this.entity = uge;
    }
    
    public UserGroupMembershipEntity getEntity() {
        return (this.entity);
    }
    
    public void test() {
        this.entity.addMembership(null, null);
    }
}
