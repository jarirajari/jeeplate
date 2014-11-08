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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.BusinessEntityStore;

@Dependent
public class UserData {
    
    @Inject
    private Logger log;
    
    @Inject
    private BusinessEntityStore<UserEntity> store;
    
    private UserEntity entity = UserEntity.newUserEntityBuilder().build();
    
    public Long getId() {
        return (entity.getId());
    }
    
    @Transactional
    public Boolean bind(Long id) {
        UserEntity tmp = UserEntity.newUserEntityBuilder().withId(id).build();
        
        this.entity = this.store.bind(tmp);
        log.info("Bound"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean create() {
        log.info("Initial"+this.entity.hashCode()+", "+this.entity.toString());
        this.entity.name = "test0c";
        this.entity = this.store.create(entity);
        log.info("Created"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean read() {
        this.entity.name = "test2r";
        this.entity = this.store.read(entity);
        log.info("Read"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean update() {
        this.entity.name = "test3u";
        this.entity = this.store.update(entity);
        log.info("Updated"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    public Boolean delete() {
        this.entity.name = "test4c";
        this.entity = this.store.delete(entity);
        log.info("Deleted"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
}
