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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "group_domain", uniqueConstraints = { 
       @UniqueConstraint(columnNames = "domainname")})
public class DomainEntity extends BusinessEntity implements Serializable {
    
    @Id @SequenceGenerator(name = "group_domain_seq", allocationSize = 1)
    @GeneratedValue(generator = "group_domain_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String domainname; // alias, qualified name?
    protected String description;
    @Enumerated(EnumType.STRING)
    protected DomainType.Type domaintype;
    @Embedded
    protected DomainRegistration registration;
    @ManyToOne @JoinColumn(name = "domain_space")
    protected DomainSpaceEntity domainspace;
    /*
    
    When you create a new domain => create also "ALL" group first!
    String => name => immutable key, and "ALL" is reserved
    NONE group is zero and default (empty)
    
    0L NONE (default)
    1L ALL
    .. <NAME>
    
    Again, after space created => "root" creates "domains"
    
    */
    @OneToMany(mappedBy = "domain")
    protected Map<String, DomainGroupEntity> allDomaingroups;
    
    public DomainEntity() {
        this.id = DEFAULT_ID;
        this.domainname = "";
        this.domaintype = DomainType.Type.UNKNOWN;
        this.registration = new DomainRegistration();
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
    }

    public String getDomainname() {
        return domainname;
    }

    public void setDomainname(String domainname) {
        this.domainname = domainname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DomainType.Type getDomaintype() {
        return domaintype;
    }

    public void setDomaintype(DomainType.Type domaintype) {
        this.domaintype = domaintype;
    }

    public DomainRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(DomainRegistration registration) {
        this.registration = registration;
    }

    public DomainSpaceEntity getDomainspace() {
        return domainspace;
    }

    public void setDomainspace(DomainSpaceEntity domainspace) {
        this.domainspace = domainspace;
    }
}
