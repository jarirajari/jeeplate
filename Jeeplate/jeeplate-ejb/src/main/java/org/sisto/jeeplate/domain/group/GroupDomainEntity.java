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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.sisto.jeeplate.domain.BusinessEntity;

@Entity
@Access(AccessType.FIELD)
@Table(name = "group_domain", uniqueConstraints = { 
       @UniqueConstraint(columnNames = "domainname")})
public class GroupDomainEntity extends BusinessEntity implements Serializable {
    
    @Id @SequenceGenerator(name = "group_domain_seq", allocationSize = 1)
    @GeneratedValue(generator = "group_domain_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String domainname; // alias
    protected String description;
    @Enumerated(EnumType.STRING)
    protected GroupDomainType.Type domaintype;
    @Embedded
    protected GroupDomainRegistration registration;
            
    public GroupDomainEntity() {
        this.id = DEFAULT_ID;
        this.domainname = "";
        this.domaintype = GroupDomainType.Type.UNKNOWN;
        this.registration = new GroupDomainRegistration();
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

    public GroupDomainType.Type getDomaintype() {
        return domaintype;
    }

    public void setDomaintype(GroupDomainType.Type domaintype) {
        this.domaintype = domaintype;
    }

    public GroupDomainRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(GroupDomainRegistration registration) {
        this.registration = registration;
    }
    
    
}
