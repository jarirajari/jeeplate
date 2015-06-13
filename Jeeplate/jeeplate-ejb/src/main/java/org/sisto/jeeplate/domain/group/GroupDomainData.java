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

import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.logging.StringLogger;

public class GroupDomainData {
    // id, name as reverse domain (ldap?), free description, domain code (i.e. hash?)
    // dn: uid=<userId>,ou=<groupname>,dc=<subdomain e.g.country code>,dc=sisto,dc=org 
    // but just drop uid and ou, and use two char country code?
    
    // requires also association table to link to users: bidir 1-M since user is 
    // allowed to belong to only one app-domain: user has account and belongs to one domain
    
    // => user <-> usergroup (assoc=usergroupmembership) <->
    // usergroupgroup i.e. app-domain (assoc=DomainMembership)
    
    // Loan something from LDAP world! DN gives nice hierarchy and multiple geolocations!
    @Inject
    private transient StringLogger log;
    
    @Inject
    private GroupDomain domain;
    
    @Inject @New
    private transient BusinessEntityStore<GroupDomainEntity> store;
    
    private GroupDomainEntity entity;
    
    public GroupDomainData() {
        this.entity = new GroupDomainEntity();
    }
    
    public GroupDomainData(GroupDomainEntity ade) {
        this.entity = ade;
    }
    
    public void setEntity(GroupDomainEntity ade) {
        this.entity = ade;
    }
    
    public GroupDomainEntity getEntity() {
        return (this.entity);
    }
    
    @Transactional
    public String applyForUserAccount() {
        assert this.entity != null;
        Boolean userNotExist = this.getEntity().isDefault();
        GroupDomainRegistration reg  = this.getEntity().getRegistration();
        String token;
        
        if (userNotExist) {
            reg.activateRegistrationProtocol();
        }
        if (userNotExist) {
            token = reg.getRegistrationToken();
            log.debug("User registration requested for a user that has no account!");
        } else {
            token = "";
            log.info("User registration requested for an existing user (id=%s).", String.valueOf(this.getEntity().getId()));
        }
        
        return token;
    }
    
    @Transactional
    public Boolean grantUserAccount(String typedMobile, String typedPassword, String emailedResetToken, String hiddenActionSecret) {
        
        
        return Boolean.FALSE;
    }
}
