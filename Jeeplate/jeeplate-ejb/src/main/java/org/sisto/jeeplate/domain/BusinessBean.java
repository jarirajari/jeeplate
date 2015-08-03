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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.ApplicationProperty;

@Dependent
public abstract class BusinessBean<D extends BusinessBean, E extends BusinessEntity> implements Serializable {
    
    @Inject @Any
    transient StringLogger log;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    String systemEmailAddress;
    
    @Inject @Any
    BusinessEntityStore<E> store;
    
    protected final Class<E> entityBeanType;
    protected final Class<D> dataBeanType;
    protected final String entityString;
    protected final String dataString;
    
    protected E entity;
    public E dataModel;
    
    protected void setEntity(E e) {
        this.entity = e;
    }
    
    protected E getEntity() {
        return (this.entity);
    }
    
    public E getDataModel() {
        return (this.getEntity());
    }
    
    public BusinessBean(Class<D> dataType, Class<E> entityType) {
        this.entityBeanType = entityType;
        this.dataBeanType   = dataType;
        this.entityString = parseName(entityBeanType.getSimpleName());
        this.dataString   = parseName(dataBeanType.getSimpleName());
    }
    
    private static String parseName(String name) {      
        final int lastDot = name.lastIndexOf(".");
        final int stop = name.length();
        final int start = (lastDot < 0) ? 0 : lastDot;
        final String beanName = name.substring(start, stop);
        
        return beanName;
    }
    
    // + Map<Long, UserData> findOneUser(final Long withId) // one not all
    
    // + UserData findOneUser(final String emailAddress) // alt db key
    
    // + find, bind, create, read, update, delete
    
    @Transactional
    public Map<Long, D> findAllUsersTestAbstract() {
        final String query = String.format("SELECT e FROM %s e", entityString);
        final Map<String, Object> params = new HashMap<>();
        final List<E> results = this.store.executeQuery(entityBeanType, query, params);
        
        return (results.stream().collect(
                Collectors.toMap((E e) -> e.getId(),(E e) -> {
                    D d;
                    try {
                        d = (D) this.dataBeanType.newInstance();
                        d.setEntity(e);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        d = null;
                    }
                    return d;
                })));
    }
}
