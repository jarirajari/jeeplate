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

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
/**
 *
 * @author Jari
 */
public class ObjectId implements Serializable {
    
    public static final Long DEFAULT_ID = 0L;
    private Long id = DEFAULT_ID;
    
    public ObjectId() {
        this.id = DEFAULT_ID;
    }
    
    public ObjectId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return (this.id);
    }
    
    // value type setter
    // write should be 'null' protected because
    // the value is initialized with default or empty value!
    public void id(Long newId) {
        if (newId == null) {
            this.id = DEFAULT_ID;
        } else {
            this.id = newId;
        }
        
        return;
    }
    
    public void reset() {
        this.id = DEFAULT_ID;
    }
    
    // value type getter
    public Long id() {
        return (this.id);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj == null) {
            equal = false;
        } else if (obj == this) {
            equal = false;
        } else if (obj.getClass() != this.getClass()) {
            equal =  false;
        } else {
            ObjectId oid = (ObjectId) obj;
            equal = (new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(id, oid.id)
                    .isEquals());
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        
        hash = (new HashCodeBuilder(17, 31).
                append(id).
                toHashCode());
        
        return hash;
    }
    
    public static Boolean xisNew(ObjectId oid) {
        boolean isNew = false;
        boolean isNull  = (oid == null) ? true : false;
        boolean isIdNull = (oid == null || oid.id() == null) ? true : false;
        
        if (isNull || isIdNull) {
            isNew = true;
        } else {
            Long id = oid.id();
            boolean isDefault = (id.compareTo(DEFAULT_ID) == 0) ? true : false;
            
            if (isDefault) {
                isNew = true;
            } else {
                isNew = false;
            }
        }
        
        return isNew;
    }
}
