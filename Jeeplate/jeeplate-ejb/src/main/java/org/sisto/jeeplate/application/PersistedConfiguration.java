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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity @Access(AccessType.FIELD)
@Table(name = "system_application_configuration")
public class PersistedConfiguration implements Serializable {
    @Transient
    public static Long APPLICATION_CONFIGURATION = 1L;
    
    @Id
    private Long id;
    private Boolean applicationConfigured;
    
    public PersistedConfiguration() {
        this.id = APPLICATION_CONFIGURATION;
        this.applicationConfigured = Boolean.FALSE;
    }
    
    public PersistedConfiguration(Boolean configured) {
        this.id = APPLICATION_CONFIGURATION;
        this.applicationConfigured = configured;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getApplicationConfigured() {
        return applicationConfigured;
    }

    public void setApplicationConfigured(Boolean applicationConfigured) {
        this.applicationConfigured = applicationConfigured;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistedConfiguration)) {
            return false;
        }
        PersistedConfiguration other = (PersistedConfiguration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sisto.jeeplate.application.PersistedConfiguration[ id=" + id + " ]";
    }
    
}
