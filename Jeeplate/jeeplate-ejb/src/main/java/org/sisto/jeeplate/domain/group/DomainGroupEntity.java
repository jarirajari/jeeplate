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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.base.DomainEntity;
import org.sisto.jeeplate.domain.pk.SecondaryKeyField;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_domain_groups") 
public class DomainGroupEntity extends BusinessEntity implements Serializable {
    
    @SecondaryKeyField(description = "For find out certain groups")
    @Id @SequenceGenerator(name="domain_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "domain_group_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String groupname;
    @Enumerated(EnumType.STRING)
    protected DomainGroupType type;
    @ManyToOne(cascade = {CascadeType.ALL}) @JoinColumn(name = "domain_fk")
    protected DomainEntity parentdomain;
    
    /*
     * DomainGroupMembers accessed via memberhips:
     * NO! DomainGroupMembershipEntity allDomaingroupMemberships;
     */
    
    public DomainGroupEntity() {
        this.id = BusinessEntity.DEFAULT_ID;
        this.groupname = "";
        this.type = DomainGroupType.EMPTY;
        this.parentdomain = null; // unfortunately we will have to use null
    }
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public String getGroupname() {
        return this.groupname;
    }
    
    public DomainGroupEntity setGroupname(String name) {
        this.groupname = name;
        
        return this;
    }

    public DomainEntity getDomain() {
        return parentdomain;
    }

    public DomainGroupEntity setDomain(DomainEntity domain) {
        this.parentdomain = domain;
        
        return this;
    }
    
    public DomainGroupType getType() {
        return type;
    }

    public DomainGroupEntity setType(DomainGroupType type) {
        this.type = type;
        
        return this;
    }
    
    public DomainGroupEntity defaultALL() {
        final DomainGroupType tall = DomainGroupType.ALL;
        this.setType(tall);
        this.setGroupname(tall.toString());
        this.setId(tall.id());
        
        return this;
    }
    
    public DomainGroupEntity switchTo(DomainGroupType type) {
        
        return (this.setType(type));
    }
}
