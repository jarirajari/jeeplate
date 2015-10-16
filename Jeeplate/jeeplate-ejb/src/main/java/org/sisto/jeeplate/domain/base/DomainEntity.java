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
package org.sisto.jeeplate.domain.base;

import org.sisto.jeeplate.domain.user.registration.UserRegistrationEntity;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.group.DomainGroupEntity;
import org.sisto.jeeplate.domain.space.DomainSpaceEntity;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_domains", uniqueConstraints = { 
       @UniqueConstraint(columnNames = "domainFQDN")})
public class DomainEntity extends BusinessEntity implements Serializable {
    
    @Id @SequenceGenerator(name = "group_domain_seq", allocationSize = 1)
    @GeneratedValue(generator = "group_domain_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String domainFQDN; // FQDN ends with dot '.', ROOT is only '.'
    protected String description;
    @Enumerated(EnumType.STRING)
    protected DomainType.Type domaintype;
    @ManyToOne @JoinColumn(name = "domainspace_fk")
    protected DomainSpaceEntity domainspace;
    @OneToMany(mappedBy = "parentdomain", cascade = {CascadeType.ALL})
    protected Map<String, DomainGroupEntity> allDomaingroups;
    /*
    
    When you create a new domain => create also "ALL" group first!
    String => name => immutable key, and "ALL" is reserved
    NONE group is zero and default (empty)
    
    0L NONE (default)
    1L ALL
    .. <NAME>
    
    Again, after space created => "root" creates "domains"
    
    */
    
    public DomainEntity() {
        this.id = DEFAULT_ID;
        this.domainFQDN = "";
        this.description = "";
        this.domaintype = DomainType.Type.UNKNOWN;
        this.allDomaingroups = new HashMap<>();
        this.domainspace = null;
    }
    
    // should be replaced with a builder
    public DomainEntity(DomainSpaceEntity dse) {
        this();
        this.domainspace = dse;
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
        super.setVersion(this.version);
    }

    public String getDomainFQDN() {
        return domainFQDN;
    }

    public DomainEntity setDomainFQDN(String domainFQDN) {
        this.domainFQDN = domainFQDN;
        
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DomainEntity setDescription(String description) {
        this.description = description;
        
        return this;
    }

    public DomainType.Type getDomaintype() {
        return domaintype;
    }

    public DomainEntity setDomaintype(DomainType.Type domaintype) {
        this.domaintype = domaintype;
        
        return this;
    }

    public DomainSpaceEntity getDomainspace() {
        return domainspace;
    }

    public DomainEntity setDomainspace(DomainSpaceEntity domainspace) {
        this.domainspace = domainspace;
        
        return this;
    }
    
    public void insertNewGroup(String fqdn, DomainGroupEntity dge) {
        dge.setDomain(this);
        this.allDomaingroups.put(fqdn, dge);
    }
    
    public void removeOldGroup(String fqdn, DomainGroupEntity dge) {
        dge.setDomain(null);
        this.allDomaingroups.remove(fqdn);
    }
}
