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
package org.sisto.jeeplate.util;

import java.util.Objects;

public class Name extends java.lang.Object implements Comparable<Name> {
    
    public static final String EMPTY = "";
    public static final Name NONAME = new Name("");
    private String name;
    
    public Name(String s) {
        this.name = new String(s);
    }
    
    public String name() {
        return (this.name);
    }
    
    @Override
    public String toString() {
        return (this.name);
    }
    
    public String name(String s) {
        this.name = s;
        
        return (this.name);
    }
    
    @Override
    public boolean equals(Object s) {
        return (this.name.equals(s));
    }

    @Override
    public int hashCode() {
        int hash = 19 * 7 + Objects.hashCode(this.name);
        
        return hash;
    }
    
    @Override
    public Name clone() {
        return (new Name(this.name()));
    }
    
    @Override
    public int compareTo(Name n) {
        return (this.name.compareTo(n.name()));
    }
;}
