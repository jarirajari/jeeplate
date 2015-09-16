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
package org.sisto.jeeplate.domain.user.group.membership;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;

@Stateful
public class UserGroupMembershipData extends BusinessBean<UserGroupMembershipData, UserGroupMembershipEntity> implements Serializable {
    
    @Inject 
    UserGroupMembership membership;
    
    public UserGroupMembershipData() {
        super(UserGroupMembershipData.class, UserGroupMembershipEntity.class);
    }
    
    @Transactional
    public void addNewMember(Long user, Long toGroup) {
        UserGroupMembershipEntity member = EntityBuilder.of().UserGroupMembershipEntity()
                .setGroupReference(toGroup)
                .setUserReference(user);
        this.setEntity(member);
        this.create();
    }
}
