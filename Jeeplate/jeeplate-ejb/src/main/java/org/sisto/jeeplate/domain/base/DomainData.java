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
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipData;

@SessionScoped
public class DomainData extends BusinessBean<DomainData, DomainEntity> implements Serializable {
    // id, name as reverse domain (ldap?), free description, domain code (i.e. hash?)
    // dn: uid=<userId>,ou=<groupname>,dc=<subdomain e.g.country code>,dc=sisto,dc=org 
    // but just drop uid and ou, and use two char country code?
    
    // requires also association table to link to users: bidir 1-M since user is 
    // allowed to belong to only one app-domain: user has account and belongs to one domain
    
    // => user <-> usergroup (assoc=usergroupmembership) <->
    // usergroupgroup i.e. app-domain (assoc=DomainMembership)
    
    // Loan something from LDAP world! DN gives nice hierarchy and multiple geolocations!
    @Inject @Default
    Domain domain;
    
    @Inject @Default
    DomainGroupMembershipData groups;
    
    public DomainData() {
        super(DomainData.class, DomainEntity.class);
    }
    
    public void createNewApplicatoinDomain(String domainName) {
        DomainEntity entity = EntityBuilder.of().DomainEntity()
                .setDomainname(domainName)
                .setDomaintype(DomainType.Type.APPLICATION);
        this.setEntity(entity);
        this.create();
    }
    
    public String applyForUserAccount() {
        Boolean userNotExist = this.getDataModel().isDefault();
        DomainRegistration reg  = this.getDataModel().getRegistration();
        String token;
        
        if (userNotExist) {
            reg.activateRegistrationProtocol();
        }
        if (userNotExist) {
            token = reg.getRegistrationToken();
            log.debug("User registration requested for a user that has no account!");
        } else {
            token = "";
            log.info("User registration requested for an existing user (id=%s).", String.valueOf(this.getDataModel().getId()));
        }
        
        return token;
    }
    
    public Boolean grantUserAccount(String typedMobile, String typedPassword, String emailedResetToken) {
        Boolean granted = Boolean.FALSE;
        DomainRegistration gdr = this.getDataModel().getRegistration();
        Boolean oneDomainFound = emailedResetToken.startsWith(gdr.getPartDomainDeliveredSeparately());
        
        if (oneDomainFound) {
            log.info("User registration, found application domain"); // application domain
        } else {
            boolean systemUserRequestResponseChallengeHash = false;
            if (systemUserRequestResponseChallengeHash) {
                log.info("User registration, found system domain"); // system domain
            } else {
                log.info("User registration, found unknown domain"); // unknown domain
            }
        }
        
        return granted;
    }
    
    public DomainData findOneDomain(final String domainIdentifier) {
        return (this.findOneSecondary(domainIdentifier));
    }
    
    public void testHashing() {
        DomainEntity gde = new DomainEntity();
        gde.setDomainname("com.example");
        gde.setDomaintype(DomainType.Type.APPLICATION);
        gde.getRegistration().setPartDomainDeliveredSeparately("01020304");
        gde.setDescription("This is a test company");
        this.entity = gde;
        this.create();
    }
}
