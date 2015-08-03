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
package org.sisto.jeeplate.domain.group;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.domain.BusinessEntityStore;

public class DomainGroupData implements Serializable {
    
    public static final Long NONE_GROUP = 0L;
    public static final Long ALL_GROUP =  1L;
    
    @Inject
    private transient Logger log;

    @Inject
    private transient BusinessEntityStore<DomainGroupEntity> store;
    
    private transient DomainGroupEntity entity;
    
    public DomainGroupData() {
        this.entity = new DomainGroupEntity();
    }
    
    public DomainGroupData(DomainGroupEntity ade) {
        this.entity = ade;
    }
    
    public void setEntity(DomainGroupEntity ade) {
        this.entity = ade;
    }
    
    public DomainGroupEntity getEntity() {
        return (this.entity);
    }
    
    @Transactional
    public DomainGroupData createNewDomainGroupForDomain() {
        this.create();
        
        return this;
    }
    
    @Transactional
    Boolean create() {
        this.entity = this.store.create(entity);
        
        return Boolean.TRUE;
    }
            
    @Transactional
    public Map<Long, DomainGroupData> findDomainGroups(final Long domainId) {
        final String query = "SELECT dge FROM DomainGroupEntity dge WHERE dge.id = :domainId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("domainId", domainId);
        }};
        final List<DomainGroupEntity> results = this.store.executeQuery(DomainGroupEntity.class, query, params);
        
        return (results.stream().collect(
                Collectors.toMap(DomainGroupEntity::getId, DomainGroupData::new)));
    }
}
