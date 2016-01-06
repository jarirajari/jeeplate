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
package org.sisto.jeeplate.application;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.sisto.jeeplate.authentication.role.SystemRole;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.ObjectEntity;
import org.sisto.jeeplate.domain.space.DomainSpaceData;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.util.PGEM;

@Stateless
public class Configuration implements Serializable {
    
    @Inject @PGEM
    EntityManager em;
    
    @Inject
    DomainSpaceData domainSpace;
    
    @Inject
    UserData user;
    
    // Idempotent, call from rest service?
    // create domainspace, create root system user with root role
    // only after that we can start cre+add domains to domainspace and
    // cre+add default-all groups to cre+add domain-groups
    public void configureIdempotent(String rootUsername, String rootPassword, String rootMsisdn) {
        boolean aldreadyCreated = this.originateConfiguration();
        
        if (! aldreadyCreated) {
            final UserEntity root = EntityBuilder.of().UserEntity()
                .setUsername(rootUsername)
                .setPassword(rootPassword)
                .setMobile(Long.valueOf(rootMsisdn))
                .asRoot();
            this.user.createNewUser(root);
            this.domainSpace.originateSingletonDomainSpace();
        }
    }
    
    public Boolean configurationExists() {
        PersistedConfiguration pc = this.find();
        Boolean exists = (pc != null);
        
        return exists;
    }
    
    private Boolean originateConfiguration() {
        PersistedConfiguration pc = this.find();
        Boolean exists = (pc != null);
        
        if (exists) {
            pc.setApplicationConfigured(Boolean.TRUE);
        } else {
            pc = new PersistedConfiguration(Boolean.TRUE);
        }
        this.createOrUpdate(pc);
        
        return exists;
    }
    
    private PersistedConfiguration createOrUpdate(PersistedConfiguration pc) {    
        return em.merge(pc);
    }
    
    private PersistedConfiguration find() {
        return em.find(PersistedConfiguration.class, PersistedConfiguration.APPLICATION_CONFIGURATION);
    }
}
