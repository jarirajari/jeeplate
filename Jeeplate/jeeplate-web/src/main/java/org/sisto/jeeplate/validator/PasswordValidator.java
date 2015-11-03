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
package org.sisto.jeeplate.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import org.sisto.jeeplate.util.Util;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {
    
    @Inject
    private org.sisto.jeeplate.util.MultiValidator validator;
    @Inject
    private Util util;
        
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String password = (value == null) ? "" : (String) value;
        Boolean validates = validator.validateUserPassword(password);
        
        if (!validates) {
            FacesMessage fm = new FacesMessage(util.getResourceBundleValue("validator.password.quality"));
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(fm);
        }
    }
} 
