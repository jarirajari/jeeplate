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

public class ObjectVersion {
    
    public static final Long DEFAULT_VERSION = 0L;
    private Long version = DEFAULT_VERSION;
    
    public ObjectVersion(Long id) {
        this.version = id;
    }
    
    // value type setter
    // write should be 'null' protected because
    // the value is initialized with default or empty value!
    public void version(Long newVersion) {
        if (newVersion == null) {
            this.version = DEFAULT_VERSION;
        } else {
            this.version = newVersion;
        }
        
        return;
    }
    
    // value type getter
    public Long version() {
        return (this.version);
    }
    
}
