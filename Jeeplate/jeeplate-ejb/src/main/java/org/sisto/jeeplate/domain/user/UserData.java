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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.user.group.UserGroupData;
import org.sisto.jeeplate.domain.user.group.membership.UserGroupMembershipData;
import org.sisto.jeeplate.domain.BusinessEntityStore;

@SessionScoped
public class UserData implements Serializable {

    @Inject
    private transient Logger log;
    
    @Inject @New
    private transient BusinessEntityStore<UserEntity> store;
    
    private transient UserEntity entity;
    
    @Inject
    private User user;
    
    @Inject
    private UserGroupMembershipData usersGroups;
    
    private transient final UserEntity hashed = UserEntity.newUserEntityBuilder()
            .withUsername("hashis")
            .withPassword("good")
            .build();
    private transient final UserEntity hashed2 = UserEntity.newUserEntityBuilder()
            .withUsername("un")
            .withPassword("pw")
            .build();
    private transient final UserEntity hashed3 = UserEntity.newUserEntityBuilder()
            .withUsername("user@na.me")
            .withPassword("pw")
            .build();
    
    public UserData() {}
    
    public UserData(UserEntity ue) {
        this.entity = ue;
    }
    
    public void setEntity(UserEntity ude) {
        this.entity = ude;
    }
    
    public UserEntity getEntity() {
        return (this.entity);
    }
    
    @Transactional
    public Map<Long, UserGroupData> findGroupsUserBelongsTo() {
        
        return null;
    }
    
    @Transactional
    public Map<Long, UserData> findAllUsers() {
        final String query = "SELECT ue FROM UserEntity ue";
        final Map<String, Object> params = new HashMap<>();
        final List<UserEntity> result = this.store.executeQuery(UserEntity.class, query, params);
        
        return (result.stream()
                .collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    
    @Transactional
    public Map<Long, UserData> findOneUser(final Long withId) {
        final String query = "SELECT ue FROM UserEntity ue WHERE ue.id = :userId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", withId);
        }};
        final List<UserEntity> result = this.store.executeQuery(UserEntity.class, query, params);
        
        return (result.stream()
                .collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    
    @Transactional
    public Boolean noUserWithEmail(final String emailAddress) {
        final String query = "SELECT COUNT(ue) FROM UserEntity ue WHERE ue.username = :username";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("username", emailAddress);
        }};
        final List<Long> result = this.store.executeCustomQuery(Long.class, query, params);
        Long count = 0L;
        Boolean used = Boolean.TRUE;
        
        if (! result.isEmpty()) {
            count = result.get(0);
            if (count == 0L) {
                used = Boolean.FALSE;
            }
        }
        
        return used;
    }
    
    public Boolean changeName(String name) {
        if (this.user.updateUserName()) {
            this.entity.setUsername(name);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    @Transactional
    public Boolean testHashing() {
        this.store.create(this.hashed);
        this.store.create(this.hashed2);
        this.store.create(this.hashed3);
        return Boolean.TRUE;
    }
    
    @Transactional
    public UserData find() {
        return (this);
    }
    
    @Transactional
    public UserData bind(Long id) {
        UserEntity tmp = UserEntity.newUserEntityBuilder().renovate(id);
        
        this.entity = this.store.bind(tmp);
        
        return (this);
    }
    
    @Transactional
    public Boolean create() {
        log.info("UserData.create()");
        this.entity = this.store.create(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean read() {
        log.info("UserData.read() discards changes");
        this.entity = this.store.read(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean update() {
        log.info("UserData.update() overwrites");
        this.entity = this.store.update(entity);
        log.info("Updated"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean delete() {
        log.info("UserData.delete()");
        this.entity = this.store.delete(entity);
        
        return Boolean.TRUE;
    }
}
