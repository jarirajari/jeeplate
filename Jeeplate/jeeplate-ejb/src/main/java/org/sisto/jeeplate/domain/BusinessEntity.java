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

import javax.inject.Inject;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.jboss.logging.Logger;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BusinessEntity {
    @Transient
    public transient static Long DEFAULT_ID = 0L;
    
    @Inject
    private transient Logger log;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id = 0L;
    @Version
    private Long version = 0L;
    
    public Long getId() {
        return (this.id);
    }
    
    public boolean isDefaultId() {
        return (this.id == DEFAULT_ID);
    }
    
    public void reset() {
        this.id = DEFAULT_ID;
    }
}
