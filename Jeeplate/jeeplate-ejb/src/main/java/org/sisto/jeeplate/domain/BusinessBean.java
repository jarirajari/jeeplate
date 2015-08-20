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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.pk.SecondaryKeyField;
import org.sisto.jeeplate.domain.pk.TernaryKeyField;
import org.sisto.jeeplate.domain.pk.TertiaryKeyField;
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
            this.store.create(e);
        }
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
    
    // N-ary
    protected String alternativeNaryKeyFieldName(int nary) {
        String name = "";
        final Field[] fields = entityBeanType.getDeclaredFields();
        Class<? extends Annotation> clazz;
        
        for (final Field field : fields) {
            if (nary <= 2) {
                clazz = SecondaryKeyField.class;
            } else if (nary == 3) {
                clazz = TernaryKeyField.class;
            } else if (nary == 4) {
                clazz = TertiaryKeyField.class;
            } else {
                break;
            }
            final boolean annotationIsPresent = field.isAnnotationPresent(clazz);
            final String defaultName = field.getName();
            
            if (annotationIsPresent) {
                if (nary <= 2) {
                    name = ((SecondaryKeyField) field.getAnnotation(clazz)).keyname();
                } else if (nary == 3) {
                    name = ((TernaryKeyField) field.getAnnotation(clazz)).keyname();
                } else if (nary == 4) {
                    name = ((TertiaryKeyField) field.getAnnotation(clazz)).keyname();
                } else {
                    name = "";
                }
                if (name.isEmpty()) {
                    name = defaultName;
                }
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
    private Map<Long, D> findAllAlternative(final int nary, final Object altKeyVal) {
        final List<E> results = this.findEntityByAlternativeKey(nary, altKeyVal);
        
        return (collectAllResult(results));
    }
    
    public Map<Long, D> findAllSecondary(final Object altKeyVal) {
        return (findAllAlternative(2, altKeyVal));
    }
    
    public Map<Long, D> findAllTertiary(final Object altKeyVal) {
        return (findAllAlternative(3, altKeyVal));
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
    private D findOneAlternative(final int nary, final Object altKeyVal) {
        List<E> results = this.findEntityByAlternativeKey(nary, altKeyVal);
        
        if ((results != null) && (results.isEmpty() == false) && (results.size() == 1)) {
            // pass;
        } else {
            results = new ArrayList<>();
        }
        
        return (collectOneResult(results));
    }
    
    public D findOneSecondary(final Object altKeyVal) {
        return (findOneAlternative(2, altKeyVal));
    }
    
    public D findOneTernary(final Object altKeyVal) {
        return (findOneAlternative(3, altKeyVal));
    }
    
    public D findOneTertiary(final Object altKeyVal) {
        return (findOneAlternative(4, altKeyVal));
    }
    
    // Helper
    @Transactional
    private List<E> findEntityByAlternativeKey(final int nary, final Object altKeyVal) {
        final String entAltKey = alternativeNaryKeyFieldName(nary);
        final String query = String.format("SELECT e FROM %s e WHERE e.%s = :%s", entityString, entAltKey, entAltKey);
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put(entAltKey, altKeyVal);
        }};
        final List<E> results;
        
        if (entAltKey.isEmpty()) {
            results = new ArrayList<>();
        } else {
            results = this.store.executeQuery(entityBeanType, query, params);
        }
        
        return results;
    }
    
    @Transactional
    protected void create() {
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
    
    // Returns objects id :: getter
    @Transactional
    public Long find() {
        final Long id = this.getEntity().getId();
        
        return id;
    }
    
    // Return objects id after binding it to a persisted entity :: setter
    @Transactional
    public Long bind(Long id) {
        Long lid;
        E tmp;
        Optional<E> bound;
        
        try {
            tmp = (E) entityBeanType.newInstance();
            tmp.setId(id); // renovate without pattern!
            bound = Optional.ofNullable(tmp);
            if(bound.isPresent()) {
                this.setEntity(this.store.bind(tmp));
            }
            lid = id;
        } catch (InstantiationException | IllegalAccessException | NullPointerException ex) {
            lid = 0L;
        }
        
        return lid;
    }
}
