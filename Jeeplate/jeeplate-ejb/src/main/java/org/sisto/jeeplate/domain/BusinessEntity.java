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
package org.sisto.jeeplate.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BusinessEntity extends ObjectEntity {
    
    @Transient
    protected Long transientSuperId = DEFAULT_ID;
    
    @Version
    protected Long version = DEFAULT_VN;
    
    public Long getId() {
        return (this.transientSuperId);
    }
    
    public void setId(Long id) {
        this.transientSuperId = id;
    }
    
    @Override
    public Long identity() {
        return (this.getId());
    }
    
    @Override
    public boolean isDefault() {
        return (this.getId().equals(DEFAULT_ID));
    }
    
    @Override
    public void reset() {
        this.transientSuperId = DEFAULT_ID;
    }
}
