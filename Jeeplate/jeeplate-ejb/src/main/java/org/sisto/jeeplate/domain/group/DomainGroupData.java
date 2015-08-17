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
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessBean;

@SessionScoped
public class DomainGroupData extends BusinessBean<DomainGroupData, DomainGroupEntity> implements Serializable {
    
    private final Long NONE_GROUP = 0L;
    private final Long ALL_GROUP =  1L;
    
    @Inject @Default
    DomainGroup group;
    
    public DomainGroupData() {
        super(DomainGroupData.class, DomainGroupEntity.class);
    }
    
    @Transactional
    public DomainGroupData createNewDomainGroupForDomain() {
        this.create();
        
        return this;
    }
            
    @Transactional
    public Map<Long, DomainGroupData> findDomainGroups(final Long domainId) {
        return (this.findAllSecondary(domainId));
    }
}
