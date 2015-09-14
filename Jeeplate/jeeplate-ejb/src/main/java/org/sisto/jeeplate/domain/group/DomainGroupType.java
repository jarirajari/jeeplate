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
package org.sisto.jeeplate.domain.group;

public enum DomainGroupType {

    EMPTY   ("EMPTY", 0L),
    ALL     ("ALL", 1L),
    SOME    ("SOME", 2L); // 2...MAX

    private String name;
    private Long id;

    DomainGroupType(String name, Long id) {
        this.name = name;
        this.id = id;
    }
    
    public Long id() {
        return (this.id);
    }
    
    public Long emptyGroup() {
        return 0L;
    }
    
    public Long allGroup() {
        return 1L;
    }
}
