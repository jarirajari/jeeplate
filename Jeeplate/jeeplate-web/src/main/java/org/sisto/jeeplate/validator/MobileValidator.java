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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("mobileValidator")
public class MobileValidator implements Validator {
    
    private final static String VALID_MSISDN_PATTERN = "^\\+[1-9]{1}[0-9]{1,14}$"; // Standard E.164
    private final static Pattern COMPILED_MSISDN_PATTERN = Pattern.compile(VALID_MSISDN_PATTERN);
    
    @Override
    public void validate(FacesContext fc, UIComponent c, 
                         Object value) throws ValidatorException {
        Matcher matcher = null;
        String msn = (value == null) ? "" : (String) value;
        String msisdn = (msn.startsWith("+")) ? msn : "+".concat(msn);
        
        // No value is not ok
        if (msisdn.isEmpty()) {
            FacesMessage msg = new FacesMessage("Msisdn Validation Error");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        // The email matcher
        matcher = COMPILED_MSISDN_PATTERN.matcher(msisdn);

        if (!matcher.matches()) {
            FacesMessage msg = new FacesMessage("Msisdn Validation Error");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
}
