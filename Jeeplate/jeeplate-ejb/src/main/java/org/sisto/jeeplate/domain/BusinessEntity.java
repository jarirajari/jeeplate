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

import java.util.Objects;
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
    
    protected abstract void updateParentId();
    
    public BusinessEntity build() {
        this.setId(DEFAULT_ID);
        
        return this;
    }
    
    public BusinessEntity renovate(Long id) {
        this.setId(id);
        
        return this;
    }
    
    /*
     * Java Language Specification: method name and parameter list are part of 
     * method signature, thus No, return type is not part of method signature!
     *
     * Then http://www.webr2.com/does-java-bean-s-setter-permit-return-this/
     */
    
    public Long getId() {
        return (this.transientSuperId);
    }
    
    public void setId(Long id) {
        this.transientSuperId = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Override
    public Long identity() {
        return (this.getId());
    }
    
    @Override
    public Boolean isDefault() {
        return (this.getId().equals(DEFAULT_ID));
    }
    
    @Override
    public void reset() {
        this.transientSuperId = DEFAULT_ID;
    }

    /*
     * http://www.onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html?page=2:
     * "Don't let Hibernate manage your ids. All of the problems discussed so far 
     * derive from trying to create and maintain separate definitions of 
     * identity for objects and database rows. These problems all go away if we 
     * unify all forms of identity. That is, instead of having a database-centric 
     * ID, or an object-centric ID" => see subclass impl of "updateParentId"
     */
    
    @Override
    public int hashCode() {
        int hash = 7;
        
        hash = 71 * hash + Objects.hashCode(this.transientSuperId);
        hash = 71 * hash + Objects.hashCode(this.version);
        
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        final boolean objectIsNotNull = (obj != null);
        
        if (objectIsNotNull) {
            final boolean isBusinessEntity = (obj instanceof BusinessEntity);

            if (isBusinessEntity) {
                final BusinessEntity other = (BusinessEntity) obj;
                final boolean idsEqual = this.getId().equals(other.getId());
                final boolean versionsEqual = this.getVersion().equals(other.getVersion());

                if (idsEqual && versionsEqual) {
                    equal = true;
                }
            }
        }
        
        return equal;
    }
}
