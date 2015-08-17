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
import org.sisto.jeeplate.domain.BusinessBean;

public class DomainSpaceData extends BusinessBean<DomainSpaceData, DomainSpaceEntity> implements Serializable {
    
    @Inject @Default
    DomainSpace space;
    
    public DomainSpaceData() {
        super(DomainSpaceData.class, DomainSpaceEntity.class);
    }
    
    @Transactional
    public DomainSpaceData findSingletonDomainSpace() {
        final Long singletonDomainId = 1L;
        final DomainSpaceData singleton = this.findOne(singletonDomainId);
        
        // if not exist, create one here, idempotent?
        
        return singleton;
    }
    
    @Transactional
    public void originateSingletonDomainSpace() {this.create();
        this.create();
    }
}
