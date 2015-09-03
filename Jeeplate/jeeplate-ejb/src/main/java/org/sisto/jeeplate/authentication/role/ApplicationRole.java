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
package org.sisto.jeeplate.authentication.role;

public enum ApplicationRole {

    ADMINISTRATOR   ("administrator",   0b0010000),
    DIRECTOR        ("director",        0b0001000),
    ACTOR           ("actor",           0b0000100),
    VISITOR         ("visitor",         0b0000010),
    NONE            ("unknown",         0b0000001);

    String name;
    int bitIndex;

    ApplicationRole(String sname, int ibitIndex) {
        this.name = sname;
        this.bitIndex = ibitIndex;
    }
    
    public int bitIndex() {
        return this.bitIndex;
    }
}
