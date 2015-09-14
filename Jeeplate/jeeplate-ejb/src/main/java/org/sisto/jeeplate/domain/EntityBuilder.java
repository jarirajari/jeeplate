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
package org.sisto.jeeplate.domain;

import org.sisto.jeeplate.domain.base.DomainEntity;
import org.sisto.jeeplate.domain.group.DomainGroupEntity;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipEntity;
import org.sisto.jeeplate.domain.space.DomainSpaceEntity;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.domain.user.group.UserGroupEntity;
import org.sisto.jeeplate.domain.user.group.membership.UserGroupMembershipEntity;

public class EntityBuilder<T> {

    private Class<T> type;
    private Object entity;

    private EntityBuilder() {
    }

    private Class init(Class clazz) {
        type = clazz;
        try {
            entity = type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            entity = null;
        }

        return clazz;
    }

    public static EntityBuilder of() {
        return (new EntityBuilder());
    }

    /*
     * Hacking semi-generic builder for entities 
     */
    public UserEntity UserEntity() {
        Class c = this.init(UserEntity.class);
        UserEntity e = (UserEntity) entity;

        return e;
    }

    public UserGroupEntity UserGroupEntity() {
        Class c = this.init(UserGroupEntity.class);
        UserGroupEntity e = (UserGroupEntity) entity;

        return e;
    }

    public UserGroupMembershipEntity UserGroupMembershipEntity() {
        Class c = this.init(UserGroupMembershipEntity.class);
        UserGroupMembershipEntity e = (UserGroupMembershipEntity) entity;

        return e;
    }

    public DomainSpaceEntity DomainSpaceEntity() {
        Class c = this.init(DomainSpaceEntity.class);
        DomainSpaceEntity e = (DomainSpaceEntity) entity;

        return e;
    }

    public DomainEntity DomainEntity() {
        Class c = this.init(DomainEntity.class);
        DomainEntity e = (DomainEntity) entity;

        return e;
    }
    
    public DomainGroupEntity DomainGroupEntity() {
        Class c = this.init(DomainGroupEntity.class);
        DomainGroupEntity e = (DomainGroupEntity) entity;

        return e;
    }

    public DomainGroupMembershipEntity DomainGroupMembershipEntity() {
        Class c = this.init(DomainGroupMembershipEntity.class);
        DomainGroupMembershipEntity e = (DomainGroupMembershipEntity) entity;

        return e;
    }
}
