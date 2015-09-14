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
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.group.DomainGroupData;

@SessionScoped
public class DomainGroupMembershipData extends BusinessBean<DomainGroupMembershipData, DomainGroupMembershipEntity> implements Serializable {
    
    @Inject @Default
    DomainGroupMembership membership;
    
    @Inject @Default
    DomainGroupData member;
    
    public DomainGroupMembershipData() {
        super(DomainGroupMembershipData.class, DomainGroupMembershipEntity.class);
    }
    
    public Map<Long, DomainGroupData> findDomainGroups(final Long domainId) {
        final Map<Long, DomainGroupData> results = this.member.findDomainGroups(domainId);
        
        return results;
    }
    
    public void makeUserNewMemberToDomain(Long userIdNewMember) {
        DomainGroupMembershipEntity mship = EntityBuilder.of().DomainGroupMembershipEntity()
                .setGroupMemberReference(userIdNewMember);
        this.setEntity(mship);
        this.create();
    }
    
    public void addNewMember(Long userIdNewMember) {
        this.member.createNewDomainGroupForDomain();
        Long memberId = this.member.getDataModel().getId();
        DomainGroupMembershipEntity mship = EntityBuilder.of().DomainGroupMembershipEntity()
                .setGroupMemberReference(memberId);
        this.setEntity(mship);
        this.create();
    }
}
