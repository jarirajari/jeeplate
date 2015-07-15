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
package org.sisto.jeeplate.domain.space;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.domain.base.DomainEntity;
import org.sisto.jeeplate.logging.StringLogger;

public class DomainSpaceData implements Serializable {
    
    @Inject
    private transient StringLogger log;
    
    @Inject @New
    private transient BusinessEntityStore<DomainEntity> store;
    
    private DomainSpaceEntity entity;
    
    @Transactional
    public List<DomainSpaceEntity> findSingletonDomainSpace() {
        final int domainSpaceId = 1;
        final String query = "SELECT dse FROM DomainSpaceEntity dse WHERE dse.id = :domainSpaceId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("domainSpaceId", domainSpaceId);
        }};
        final List<DomainSpaceEntity> result = this.store.executeCustomQuery(DomainSpaceEntity.class, query, params);
        
        return result;
    }
}
