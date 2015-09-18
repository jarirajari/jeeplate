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
package org.sisto.jeeplate.domain.space;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.base.DomainData;
import org.sisto.jeeplate.domain.group.DomainGroupData;

@Stateful 
public class DomainSpaceData extends BusinessBean<DomainSpaceData, DomainSpaceEntity> implements Serializable {
    
    private transient final static Long SINGLETON_SPACE = 1L;
    
    @Inject
    DomainSpace space;
    @Inject
    DomainData domain;
    @Inject
    DomainGroupData group;
    
    public DomainSpaceData() {
        super(DomainSpaceData.class, DomainSpaceEntity.class);
    }
    
    public DomainSpaceData findSingletonDomainSpace() {
        final Long singletonDomainId = SINGLETON_SPACE;
        final DomainSpaceData singleton = this.findOne(singletonDomainId);
        
        // if not exist, create one here, idempotent?
        
        return singleton;
    }
    
    public void originateSingletonDomainSpace() {
        
        this.create();  
    }
    
    // DRAFT
    
    private boolean isAuth() {                     // use producer here?
        String ADMIN = "admin";
        // domain:group:appRole(of-member):action:permissionInstance:resource // 6
        // String.format("jeeplate:test:secretary:view,modify:name=%s:homepageurl", "*");
        String PERM = "printer:print:laserjet4400n"; 
        Subject currentUser = SecurityUtils.getSubject();
        boolean auth;
        
        if (currentUser.hasRole(ADMIN) || 
            currentUser.isPermitted(PERM)) {
            auth = true;
        } else {
            auth = false;
        }
        
        return auth;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertNewDomain(String name) {
        /*
        
        save the transient instance before flushing: 
        org.sisto.jeeplate.domain.group.DomainGroupEntity
        
        */
        this.bind(SINGLETON_SPACE);
        log.info("insertNewDomain -> createNewDomain="+name);
        group.createDefaultALLDomainGroupForNewDomain();
        log.info("group ="+group.getDataModel().getId());
        domain.createNewApplicationDomain(name, group.getDataModel());
        log.info("domain="+domain.getDataModel().getId());
        // rules of who can do this, string comes from mvc
        
        /*
        DomainSpaceEntity dse = this.getEntity();
        dse.insertNewDomain(name, domain);
        this.setEntity(dse);
        */
        this.getEntity().insertNewDomain(name, domain);
        this.update();
    }
    
    public void removeOldDomain(String name, DomainData dd) {
        
        // rules of who can do this
        DomainSpaceEntity dse = this.getEntity();
        dse.removeOldDomain(name, dd);
        this.setEntity(dse);
        this.update();
    }
    
    public Integer size() {
        return (this.getEntity().size());
    }
}
