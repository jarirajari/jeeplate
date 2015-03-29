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
package org.sisto.jeeplate.generic;

public abstract class ObjectModel {
    
    public transient static Long DEFAULT_ID = 0L;
    
    public abstract String identity();
    public abstract boolean isDefault();
    public abstract String find();
    public abstract void bind(String id);
    
    public abstract void reset();
    
    public Long getLongId(String stringId) {
        Long lid = null;
        
        try {
                lid = Long.valueOf(stringId);
            } catch (NumberFormatException nfe) {
                lid = ObjectModel.DEFAULT_ID;
            }
        
        return lid;
    }
    
    public String getStringId(Long longId) {
        String sid = null;
        String tmp = null;
        
        tmp = String.valueOf(longId);
        if (tmp == null) {
            sid = String.valueOf(ObjectModel.DEFAULT_ID);
        } else {
            sid = tmp;
        }
        
        return sid;
    }
    
    
}
