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

import org.sisto.jeeplate.domain.pk.SecondaryKeyField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected StringLogger log;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    String systemEmailAddress;
    
    @Inject @Any
    BusinessEntityStore<E> store;
    
    protected final Class<E> entityBeanType;
    protected final Class<D> dataBeanType;
    protected final String entityString;
    protected final String dataString;
    
    protected E entity;
    protected E dataModel;
    
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
    
    public void createTestData(E ... ents) {
        for (E e : ents) {
            this.store.create(e)
;        }
    }
    
    private static String parseName(String name) {      
        final int lastDot = name.lastIndexOf(".");
        final int stop = name.length();
        final int start = (lastDot < 0) ? 0 : lastDot;
        final String beanName = name.substring(start, stop);
        
        return beanName;
    }
    
    // Creates new instance of type D!
    private Map<Long, D> collectAllResult(final List<E> results) {
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
    
    private D collectOneResult(final List<E> results) {
        final int FIRST = 0;
        D d;
        
        try {
            d = (D) this.dataBeanType.newInstance();
            d.setEntity(results.get(FIRST));
        } catch (InstantiationException | IllegalAccessException ex) {
            d = null;
        }
        
        return d;
    }
    
    protected String alternativePrimaryKeyFieldName() {
        String name = "";
        final Field[] fields = entityBeanType.getDeclaredFields();
    
        for (final Field field : fields) {
            final boolean annotationIsPresent = field.isAnnotationPresent(SecondaryKeyField.class);
            
            if (annotationIsPresent) {
                name = field.getName();
                break;
            }
        }
        
        return name;
    }
    
    // Find ALL users with primary PK id
    @Transactional
    public Map<Long, D> findAll() {
        final String query = String.format("SELECT e FROM %s e", entityString);
        final Map<String, Object> params = new HashMap<>();
        final List<E> results = this.store.executeQuery(entityBeanType, query, params);
        
        return (collectAllResult(results));
    }
    
    // Find ALL users with secondary (alternative) PK
    @Transactional
    public Map<Long, D> findAllAlternative() {
        final String entityAltPK = alternativePrimaryKeyFieldName();
        final List<E> results = this.findUserByAltPK(entityAltPK);
        
        return (collectAllResult(results));
    }
    
    // Find ONE with primary PK id, note return format
    @Transactional
    public D findOne(final Long withId) {
        final String entityId = "entityId";
        final String query = String.format("SELECT e FROM %s e WHERE e.id = :%s", entityString, entityId);
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put(entityId, withId);
        }};
        final List<E> results = this.store.executeQuery(entityBeanType, query, params);
        
        return (collectOneResult(results));
    }
    
    // Find ONE user with secondary (alternative) PK, note return format
    @Transactional
    public D findOneAlternative(final Object entityAltPK) {
        List<E> results = this.findUserByAltPK(entityAltPK);
        
        if ((results != null) && (results.isEmpty() == false) && (results.size() == 1)) {
            // pass;
       } else {
            results = new ArrayList<>();
        }
        
        return (collectOneResult(results));
    }
    
    // Helper
    @Transactional
    private List<E> findUserByAltPK(final Object altPKValue) {
        final String entityAltPK = alternativePrimaryKeyFieldName();
        final String query = String.format("SELECT e FROM %s e WHERE e.%s = :%s", entityString, entityAltPK, entityAltPK);
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put(entityAltPK, altPKValue);
        }};
        final List<E> results;
        
        if (entityAltPK.isEmpty()) {
            results = new ArrayList<>();
        } else {
            results = this.store.executeQuery(entityBeanType, query, params);
        }
        
        return results;
    }
    
    @Transactional
    void create() {
        this.entity = this.store.create(entity);
    }
    
    @Transactional
    protected void read() {
        this.entity = this.store.read(entity);
    }
    
    @Transactional
    protected void update() {
        this.entity = this.store.update(entity);
    }
    
    @Transactional
    protected void delete() {
        this.entity = this.store.delete(entity);
    }
    
    // Returns objects id
    @Transactional
    public Long find() {
        final Long id = this.getEntity().getId();
        
        return id;
    }
    
    // Return objects id after binding it to a persisted entity
    @Transactional
    public Long bind(Long id) {
        Long lid;
        E tmp;
        
        try {
            tmp = (E) entityBeanType.newInstance();
            tmp.setId(id); // renovate without pattern!
            this.setEntity(this.store.bind(tmp));
            lid = id;
        } catch (InstantiationException | IllegalAccessException | NullPointerException ex) {
            lid = 0L;
        }
        
        return lid;
    }
}
