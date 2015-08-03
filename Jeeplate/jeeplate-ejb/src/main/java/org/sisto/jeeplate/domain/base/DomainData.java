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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipData;
import org.sisto.jeeplate.logging.StringLogger;

public class DomainData {
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
    private Domain domain;
    
    @Inject @New
    private transient BusinessEntityStore<DomainEntity> store;
    
    @Inject @Dependent
    private DomainGroupMembershipData groups;
    
    private DomainEntity entity;
    
    public DomainData() {
        this.entity = new DomainEntity();
    }
    
    public DomainData(DomainEntity ade) {
        this.entity = ade;
    }
    
    public void setEntity(DomainEntity ade) {
        this.entity = ade;
    }
    
    public DomainEntity getEntity() {
        return (this.entity);
    }
    
    
    // groups.addNewMember(this.getEntity().getId());
    @Transactional
    public void addNewDomain() {
        assert this.entity != null;
        // new domain with "all" group
        
    }
    
    @Transactional
    public String applyForUserAccount() {
        assert this.entity != null;
        Boolean userNotExist = this.getEntity().isDefault();
        DomainRegistration reg  = this.getEntity().getRegistration();
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
    public Boolean grantUserAccount(String typedMobile, String typedPassword, String emailedResetToken) {
        Boolean granted = Boolean.FALSE;
        DomainRegistration gdr = this.getEntity().getRegistration();
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
    
    @Transactional
    public List<DomainEntity> findDomainByDomainIdentifier(final String domainIdentifier) {
        final String query = "SELECT uge FROM GroupDomainEntity uge WHERE uge.registration.partDomainDeliveredSeparately = :domainId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("domainId", domainIdentifier);
        }};
        final List<DomainEntity> result = this.store.executeCustomQuery(DomainEntity.class, query, params);
        
        return result;
    }
    
    @Transactional
    public DomainData findOneDomain(final String domainIdentifier) {
        final List<DomainEntity> result = this.findDomainByDomainIdentifier(domainIdentifier);
        
        if ((result != null) && (result.isEmpty() == false) && (result.size() == 1)) {
            DomainEntity id = result.get(0);
            this.entity = id;
       } else {
            this.entity = new DomainEntity(); // dont construct new UserData
            log.error("Finding one domain by domain identifier address failed!");
        }
        
        return (this);
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
    
    @Transactional
    Boolean create() {
        log.info("GroupDomain.create()");
        this.entity = this.store.create(entity);
        
        return Boolean.TRUE;
    }
}
