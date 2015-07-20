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
import org.sisto.jeeplate.domain.space.DomainSpaceData;
import org.sisto.jeeplate.util.PGEM;

@Stateless
public class Configuration implements Serializable {
    
    @Inject @PGEM
    EntityManager em;
    
    @Inject
    DomainSpaceData domainSpace;
    
    // Idempotent
    public void configure() {
        boolean aldreadyCreated = this.originateConfiguration();
        
        if (! aldreadyCreated) {
            this.domainSpace.originateSingletonDomainSpace();
        }
    }
    
    public Boolean configurationExists() {
        PersistedConfiguration pc = this.find();
        Boolean exists;
        
        if (pc == null) {
            exists = Boolean.FALSE;
        } else {
            exists = Boolean.TRUE;
        }
        
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
