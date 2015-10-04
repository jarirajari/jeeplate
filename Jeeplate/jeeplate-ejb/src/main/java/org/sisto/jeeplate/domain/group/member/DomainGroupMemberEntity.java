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
package org.sisto.jeeplate.domain.group.member;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.group.DomainGroupEntity;
import org.sisto.jeeplate.domain.user.UserEntity;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_domain_group_members")
public class DomainGroupMemberEntity extends BusinessEntity implements Serializable {
    @Id @SequenceGenerator(name="domain_group_member_seq", allocationSize = 1)
    @GeneratedValue(generator = "domain_group_member_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String memberalias;
    @OneToOne @JoinColumn(name = "user_fk")
    protected UserEntity ISAUser;  // Here we use inline or normal JPA way because IS-A, one-to-many
    @ManyToOne @JoinColumn(name = "domaingroup_fk")
    protected DomainGroupEntity domaingroup;
    
    public DomainGroupMemberEntity() {
        this.id = BusinessEntity.DEFAULT_ID;
        this.memberalias = "";
        this.ISAUser = null;
        this.domaingroup = null;
    }
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }

    public String getMemberalias() {
        return memberalias;
    }

    public void setMemberalias(String memberalias) {
        this.memberalias = memberalias;
    }

    public UserEntity getISAUser() {
        return ISAUser;
    }

    public void setISAUser(UserEntity ISAUser) {
        this.ISAUser = ISAUser;
    }

    public DomainGroupEntity getDomaingroup() {
        return domaingroup;
    }

    public void setDomaingroup(DomainGroupEntity domaingroup) {
        this.domaingroup = domaingroup;
    }
    
    
}
