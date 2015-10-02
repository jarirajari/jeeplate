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
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.group.DomainGroupData;
import org.sisto.jeeplate.domain.group.DomainGroupEntity;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipData;
import org.sisto.jeeplate.logging.StringLogger;

@Stateful 
public class DomainData extends BusinessBean<DomainData, DomainEntity> implements Serializable {
    // id, name as reverse domain (ldap?), free description, domain code (i.e. hash?)
    // dn: uid=<userId>,ou=<groupname>,dc=<subdomain e.g.country code>,dc=sisto,dc=org 
    // but just drop uid and ou, and use two char country code?
    
    // requires also association table to link to users: bidir 1-M since user is 
    // allowed to belong to only one app-domain: user has account and belongs to one domain
    
    // => user <-> usergroup (assoc=usergroupmembership) <->
    // usergroupgroup i.e. app-domain (assoc=DomainMembership)
    
    // Loan something from LDAP world! DN gives nice hierarchy and multiple geolocations!
    @Inject
    Domain domain;
    
    @Inject
    DomainGroupData group;
    
    @Inject
    DomainGroupMembershipData groups;
    
    public DomainData() {
        super(DomainData.class, DomainEntity.class);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createNewApplicationDomain(String domainName, DomainGroupEntity dge) {
        DomainEntity de = EntityBuilder.of().DomainEntity()
                .setDomainname(domainName)
                .setDomaintype(DomainType.Type.APPLICATION);
        // domain space key and domain name of entity are same
        de.insertNewGroup(domainName, dge);
        this.setEntity(de);
        //this.create();
    }
    
    public DomainData findOneDomain(final String domainIdentifier) {
        return (this.findOneSecondary(domainIdentifier));
    }
    
    public void testHashing() {
        DomainEntity gde = new DomainEntity();
        gde.setDomainname("com.example");
        gde.setDomaintype(DomainType.Type.APPLICATION);
        gde.setDescription("This is a test company");
        this.entity = gde;
        this.create();
    }
}
