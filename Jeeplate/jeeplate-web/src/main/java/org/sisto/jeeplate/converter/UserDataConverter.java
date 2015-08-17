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
package org.sisto.jeeplate.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import org.sisto.jeeplate.domain.user.UserData;

@Named("userToNameConverter")
public class UserDataConverter implements Converter {

    /*
     * UserData bean should not even know about it's identity
     */
    @Inject
    private UserData bean; // two-level lazy loading: 1st bean, 2nd database

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String uiValue) {
        final Long id = Long.valueOf(uiValue);
        final Long boundId = bean.bind(id);
        
        if (boundId != id) {
            
        }
                
        return (this.bean); // null is basically UserData with id=0L
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object uiValue) {
        final UserData ud = (UserData) uiValue;
        final Long id = ud.find();
        
        return (id.toString()); // null is basically UserData with id=0L
    }

}
