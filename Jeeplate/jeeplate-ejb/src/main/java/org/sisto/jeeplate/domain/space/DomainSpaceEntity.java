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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.base.DomainData;
import org.sisto.jeeplate.domain.base.DomainEntity;

@Entity @Access(AccessType.FIELD) @Table(name = "system_application_space")
public class DomainSpaceEntity extends BusinessEntity implements Serializable {
    // single entity
    // java.util.collection of domains.
    @Transient
    public static Long SINGLETON_DOMAIN_SPACE = 1L;
    
    @Id
    protected Long id;
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected Map<String, DomainEntity> allDomains; // loose if <Long> or tight if <DomainEntity>
    
    public DomainSpaceEntity() {
        this.id = SINGLETON_DOMAIN_SPACE;
        this.allDomains = new ConcurrentHashMap<>();
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public Boolean insertNewDomain(String qualifiedDomainname, DomainData dd) {
        Boolean inserted;
        
        // create or renovate
        
        if (! this.allDomains.containsKey(qualifiedDomainname)) {
            this.allDomains.put(qualifiedDomainname, dd.getDataModel());
            inserted = Boolean.TRUE;
        } else {
            inserted = Boolean.FALSE;
        }
        
        return inserted;
    }
    
    public Boolean removeOldDomain(String qualifiedDomainname, DomainData dd) {
        Boolean removed;
        
        // create or renovate
        
        if (this.allDomains.containsKey(qualifiedDomainname)) {
            this.allDomains.remove(qualifiedDomainname);
            removed = Boolean.TRUE;
        } else {
            removed = Boolean.FALSE;
        }
        
        return removed;
    }
    
    public Integer size() {
        return (this.allDomains.size());
    }
}
