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
package org.sisto.jeeplate.domain.user.group.membership;

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
import org.sisto.jeeplate.domain.BusinessEntity;

@Entity @Access(AccessType.FIELD)
@Table(name = "system_user_group_membership")
public class UserGroupMembershipEntity extends BusinessEntity implements Serializable {
    @Id @SequenceGenerator(name = "user_group_membership_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_group_membership_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    /*
     * When the foreign key references are not really references, but
     * just primary key values, we can better control loading and searching
     * of them, which is idea behind tearing down JPA collection mapping...
     * At this point NO FK constraints will be used!
     */
    private Long groupReference;
    private Long userReference;
    
    public UserGroupMembershipEntity() {
        
    }
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }

    public Long getGroupReference() {
        return groupReference;
    }

    public UserGroupMembershipEntity setGroupReference(Long groupReference) {
        this.groupReference = groupReference;
        
        return this;
    }

    public Long getUserReference() {
        return userReference;
    }

    public UserGroupMembershipEntity setUserReference(Long userReference) {
        this.userReference = userReference;
        
        return this;
    }
}
