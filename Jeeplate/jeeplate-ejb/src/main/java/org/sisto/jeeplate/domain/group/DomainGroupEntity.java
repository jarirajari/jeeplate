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
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
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
import javax.persistence.Transient;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.base.DomainEntity;
import org.sisto.jeeplate.domain.group.member.DomainGroupMemberEntity;

@Entity
@Access(AccessType.FIELD)
@Table(name = "domain_groups")
public class DomainGroupEntity extends BusinessEntity implements Serializable {
    @Transient
    public static final String ALL_GROUP = "ALL"; // everyone belongs
    @Transient
    public static final String NONE_GROUP = "NONE"; // noone belongs
    @Id @SequenceGenerator(name="domain_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "domain_group_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String groupname;
    @ManyToOne @JoinColumn(name = "domain_fk")
    protected DomainEntity domain;
    // Domain groups will be mapped independently with separated association
    @OneToMany(mappedBy = "domaingroup")
    protected List<DomainGroupMemberEntity> allDomaingroupmembers;
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }
    
    public String getGroupname() {
        return this.groupname;
    }
    
    public void setGroupname(String name) {
        this.groupname = name;
    }  
}
