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
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String password = value.toString();
        
        if (! complexEnough(password)) {
            FacesMessage fm = new FacesMessage("Password quality!");
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(fm);
        }

    }
    private Boolean complexEnough(String password) {
        Boolean complex;
        
        if (password == null || password.trim().length() == 0) {
            complex = Boolean.FALSE;
        } else {
            complex = calculate(password);
        }
        
        return complex;
    }
    
    private Boolean calculate(String password) {
        int NUMBER_OF_LOWERCASE_CHARS_REQUIRED    = 2;
        int NUMBER_OF_UPPERCASE_CHARS_REQUIRED    = 2;
        int NUMBER_OF_DIGIT_CHARS_REQUIRED        = 2;
        int NUMBER_OF_SPECIAL_CHARS_REQUIRED      = 2;
        int NUMBER_OF_OTHER_CHARS_PRESENT         = 2;
        int NUMBER_OF_TOTAL_LENGTH_REQUIRED       = 8;
        int length = password.length();
        Boolean decision = Boolean.FALSE;
         
        if (length >= NUMBER_OF_TOTAL_LENGTH_REQUIRED) {
            for (int i = 0; i < length; i++) {
                char c = password.charAt(i);
                if (Character.isLowerCase(c) && NUMBER_OF_LOWERCASE_CHARS_REQUIRED > 0) {
                    NUMBER_OF_LOWERCASE_CHARS_REQUIRED--;
                } else if (Character.isUpperCase(c) && NUMBER_OF_UPPERCASE_CHARS_REQUIRED > 0) {
                    NUMBER_OF_UPPERCASE_CHARS_REQUIRED--;
                } else if (Character.isDigit(c) && NUMBER_OF_DIGIT_CHARS_REQUIRED > 0) {
                    NUMBER_OF_DIGIT_CHARS_REQUIRED--;
                } else if (PasswordValidator.isSpecialCharacter(c) && NUMBER_OF_SPECIAL_CHARS_REQUIRED > 0) {
                    NUMBER_OF_SPECIAL_CHARS_REQUIRED--;
                } else {
                    NUMBER_OF_OTHER_CHARS_PRESENT ++;
                }
            }
            if ((NUMBER_OF_LOWERCASE_CHARS_REQUIRED + NUMBER_OF_UPPERCASE_CHARS_REQUIRED +
                NUMBER_OF_DIGIT_CHARS_REQUIRED + NUMBER_OF_SPECIAL_CHARS_REQUIRED) <= 0) {
                decision = Boolean.TRUE;
            }
        } else {
            decision = Boolean.FALSE;
        }
        
        return decision;
    }
    
    private static Boolean isSpecialCharacter(char c) {
        Boolean special = Boolean.FALSE;
        int cint = (int)c;
        
        if (cint < 48 || (cint > 57 && cint < 65) || (cint > 90 && cint < 97) || cint > 122) {
            special = Boolean.TRUE;
        }
        
        return special;
    }
}
