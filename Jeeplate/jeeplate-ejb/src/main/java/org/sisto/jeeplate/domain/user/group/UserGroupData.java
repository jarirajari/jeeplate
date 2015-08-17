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
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessBean;

@SessionScoped
public class UserGroupData extends BusinessBean<UserGroupData, UserGroupEntity> implements Serializable {
    
    @Inject @Default
    UserGroup group;
    
    private transient final UserGroupEntity hashed = UserGroupEntity.newUserGroupEntityBuilder()
            .withName("vip-group")
            .build();
    private transient final UserGroupEntity hashed2 = UserGroupEntity.newUserGroupEntityBuilder()
            .withName("basic-group")
            .build();
    
    public UserGroupData() {
        super(UserGroupData.class, UserGroupEntity.class);
    }
    
    @Transactional
    public Boolean testHashing() {
        this.createTestData(hashed, hashed2);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public Map<Long, UserGroupData> findOneUserGroup(final Long withId) {
        return this.findAllSecondary(withId);
    }
}
