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
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.sisto.jeeplate.domain.BusinessBean;

public class DomainSpaceData extends BusinessBean<DomainSpaceData, DomainSpaceEntity> implements Serializable {
    
    @Inject @Default
    DomainSpace space;
    
    public DomainSpaceData() {
        super(DomainSpaceData.class, DomainSpaceEntity.class);
    }
    
    public DomainSpaceData findSingletonDomainSpace() {
        final Long singletonDomainId = 1L;
        final DomainSpaceData singleton = this.findOne(singletonDomainId);
        
        // if not exist, create one here, idempotent?
        
        return singleton;
    }
    
    @Transactional
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
    
    public void insertNewDomain(String fqdn) {
        
        // rules of who can do this, string comes from mvc
        
        this.getEntity().insertNewDomain(fqdn);
        this.setEntity(null);
        this.update();
    }
    
    public void removeOldDomain(String fqdn) {
        
        // rules of who can do this
        
        this.getEntity().removeOldDomain(fqdn);
        this.setEntity(null);
        this.update();
    }
}
