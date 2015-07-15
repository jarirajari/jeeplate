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
package org.sisto.jeeplate.domain.group.membership;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.logging.StringLogger;

@SessionScoped
public class DomainGroupMembershipData implements Serializable {
    
    @Inject
    private transient StringLogger log;
    
    @Inject
    private transient BusinessEntityStore<DomainGroupMembershipEntity> store;
    
    private transient DomainGroupMembershipEntity entity;
    
    public DomainGroupMembershipData() {
        this.entity = DomainGroupMembershipEntity.defaultDomainGroupMembershipEntity();
    }
    
    protected DomainGroupMembershipData(DomainGroupMembershipEntity adme) {
        this.entity = adme;
    }
    
    public void setEntity(DomainGroupMembershipEntity adme) {
        this.entity = adme;
    }
    
    public DomainGroupMembershipEntity getEntity() {
        return (this.entity);
    }
    
    @Transactional
    public Boolean addNewMember(Long domain, Long group) {
        DomainGroupMembershipEntity member = DomainGroupMembershipEntity
                .newDomainGroupMembershipEntity()
                .domain(domain)
                .member(group)
                .build();
        this.setEntity(this.store.create(member));
        
        return Boolean.TRUE;
    }
    
}
