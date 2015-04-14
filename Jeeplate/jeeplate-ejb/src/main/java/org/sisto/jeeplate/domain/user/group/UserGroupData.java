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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Init;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.user.group.membership.UserGroupMembershipData;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.domain.user.group.UserGroupEntity;

@SessionScoped
public class UserGroupData implements Serializable {
    
    @Inject
    private transient Logger log;

    @Inject
    private transient BusinessEntityStore<UserGroupEntity> store;
    
    private transient UserGroupEntity entity;
    
    @Inject
    private UserGroupMembershipData usersGroups;
    
    private transient final UserGroupEntity hashed = UserGroupEntity.newUserGroupEntityBuilder()
            .withName("vip-group")
            .build();
    private transient final UserGroupEntity hashed2 = UserGroupEntity.newUserGroupEntityBuilder()
            .withName("basic-group")
            .build();
    
    public UserGroupData() {
        this.entity = UserGroupEntity.defaultUserGroupEntity();
    }
    
    protected UserGroupData(UserGroupEntity uge) {
        this.entity = uge;
    }
    
    public void setEntity(UserGroupEntity uge) {
        this.entity = uge;
    }
    
    public UserGroupEntity getEntity() {
        return (this.entity);
    }
    
    // User(E) <-membership-> MapOfUsergroups(E) <-membership-> Usergroup(E)
    
    @Transactional
    public Boolean testHashing() {
        
        this.store.create(this.hashed);
        this.store.create(this.hashed2);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public List<UserGroupData> findAllUserGroups() {
        final String query = "SELECT uge FROM UserGroupEntity uge";
        final Map<String, Object> params = new HashMap<>();
        final List<UserGroupEntity> result = this.store.executeQuery(UserGroupEntity.class, query, params);
        
        return (result.stream()
                .map(UserGroupData::new)
                .collect(Collectors.toList()));
    }
    
    @Transactional
    public Map<Long, UserGroupData> findOneUserGroup(final Long withId) {
        final String query = "SELECT uge FROM UserGroupEntity uge WHERE uge.id = :groupId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("groupId", withId);
        }};
        final List<UserGroupEntity> result = this.store.executeQuery(UserGroupEntity.class, query, params);
        
        return (result.stream()
                .collect(Collectors.toMap(UserGroupEntity::getId, UserGroupData::new)));
    }
    
    @Transactional
    public UserGroupData find() {
        return (this);
    }
    
    @Transactional
    public UserGroupData bind(Long id) {
        UserGroupEntity entity = UserGroupEntity.newUserGroupEntityBuilder().renovate(id);
        UserGroupEntity group = this.store.bind(entity);
        Optional<UserGroupEntity> bound = Optional.ofNullable(group);
        
        if(bound.isPresent()) {
            this.setEntity(group);
        }
        
        return (this);
    }
    
    @Transactional
    public Boolean create() {
        log.info("UserGroupData.create()");
        this.entity = this.store.create(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean read() {
        log.info("UserGroupData.read() discards changes");
        this.entity = this.store.read(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean update() {
        log.info("UserGroupData.update() overwrites");
        this.entity = this.store.update(entity);
        log.info("Updated"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean delete() {
        log.info("UserGroupData.delete()");
        this.entity = this.store.delete(entity);
        
        return Boolean.TRUE;
    }
}
