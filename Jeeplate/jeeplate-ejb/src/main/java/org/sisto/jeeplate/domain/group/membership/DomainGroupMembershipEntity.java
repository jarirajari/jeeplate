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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.sisto.jeeplate.association.cardinality.OneToMany;
import org.sisto.jeeplate.association.direction.Unidirectional;
import org.sisto.jeeplate.domain.BusinessEntity;
import static org.sisto.jeeplate.domain.ObjectEntity.DEFAULT_ID;

@Entity @Access(AccessType.FIELD)
@Table(name = "group_domain_membership")
public class DomainGroupMembershipEntity extends BusinessEntity 
 implements Serializable, Unidirectional, OneToMany {
    @Id @SequenceGenerator(name = "group_domain_membership_sqe", allocationSize = 1)
    @GeneratedValue(generator = "group_domain_membership_sqe", strategy = GenerationType.SEQUENCE)
    private Long id;
    /*
     * When the foreign key references are not really references, but
     * just primary key values, we can better control loading and searching
     * of them, which is idea behind tearing down JPA collection mapping...
     * At this point NO FK constraints will be used!
     */
    private Long groupMemberReference;
    private Long groupReference;
    private Long domainReference; // one user can belong to only one domain!
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }

    public Long getGroupMemberReference() {
        return groupMemberReference;
    }

    public void setGroupMemberReference(Long groupMemberReference) {
        this.groupMemberReference = groupMemberReference;
    }

    public Long getGroupReference() {
        return groupReference;
    }

    public void setGroupReference(Long groupReference) {
        this.groupReference = groupReference;
    }
    
    public Long getDomainReference() {
        return domainReference;
    }

    public void setDomainReference(Long domainReference) {
        this.domainReference = domainReference;
    }
    
    public static DomainGroupMembershipEntity defaultDomainGroupMembershipEntity() {
        return (new DomainGroupMembershipEntityBuilder()).build();
    }
    
    public static DomainGroupMembershipEntityBuilder newDomainGroupMembershipEntity() {
        return (new DomainGroupMembershipEntityBuilder());
    }
    
    public static class DomainGroupMembershipEntityBuilder {
        
        private DomainGroupMembershipEntity object;

        public DomainGroupMembershipEntityBuilder() {
            this.object = new DomainGroupMembershipEntity();
            this.defaults();
        }

        private void defaults() {
            
            this.object.domainReference = DEFAULT_ID;
            this.object.groupReference = DEFAULT_ID;
        }
        
        public DomainGroupMembershipEntityBuilder domain(Long domainId) {
            
            this.object.domainReference = domainId;
            
            return (this);
        }
        
        public DomainGroupMembershipEntityBuilder member(Long userId) {
            
            this.object.groupReference = userId;
            
            return (this);
        }
        
        public DomainGroupMembershipEntity build(){
            
            this.object.id = null;
            
            return (this.object);
        }
        
        public DomainGroupMembershipEntity renovate(Long id) {
            
            this.object.id = id;
            
            return (this.object);
        }
    }
}
