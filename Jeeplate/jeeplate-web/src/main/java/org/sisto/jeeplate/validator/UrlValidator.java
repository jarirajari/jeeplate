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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("urlValidator")
public class UrlValidator implements Validator {
    
    @Override
    public void validate(FacesContext fc, UIComponent c, 
                         Object value) throws ValidatorException {
        String url = (value == null) ? "" : (String) value;
        
        // No value is not ok
        if (url.isEmpty()) {
            FacesMessage msg = new FacesMessage("Email Validation Error");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        
        if (! UrlValidator.isValidURL(url)) {
            FacesMessage msg = new FacesMessage("Email Validation Error");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
    public static boolean isValidURL(String surl) {
        URL url = null;
        boolean valid = true;
        
        try {
            url= new URL(surl);
        } catch (MalformedURLException murle) {
            valid = false;
        }
        if (valid) {
            try {
                url.toURI();
            } catch (URISyntaxException urise) {
                valid = false;
            }
        }
        
        return valid;
    }
}
