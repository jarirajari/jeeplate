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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

@Dependent
public class MultiValidator implements Serializable {
    private final String VALID_EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";    
    private final Pattern COMPILED_EMAIL_PATTERN = Pattern.compile(VALID_EMAIL_PATTERN);
    private final String VALID_MSISDN_PATTERN = "^\\+[1-9]{1}[0-9]{1,14}$"; // Standard E.164
    private final Pattern COMPILED_MSISDN_PATTERN = Pattern.compile(VALID_MSISDN_PATTERN);
     
    public Boolean validateUserEmail(String emailAddress) {
        Boolean validates;
        Matcher matcher;
        String email = (emailAddress == null) ? "" : emailAddress;
        
        matcher = COMPILED_EMAIL_PATTERN.matcher(email);
        validates = matcher.matches();
        
        return validates;
    }
    
    public Boolean validateUserName(String userName) {
        return validateUserEmail(userName);
    }
    
    public Boolean validateUserPhone(String userMsisdn) {
        Boolean validates;
        Matcher matcher;
        String phone = (userMsisdn == null) ? "" : userMsisdn;
        
        matcher = COMPILED_MSISDN_PATTERN.matcher(phone);
        validates = matcher.matches();
        
        return validates;
    }
    
    public Boolean validateURL(String surl) {
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
    
    public Boolean validateUserPassword(String spw) {
        Boolean validates;
        String password = (spw == null) ? "" : spw;
        
        validates = complexEnough(password);
        
        return validates;
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
                } else if (isSpecialCharacter(c) && NUMBER_OF_SPECIAL_CHARS_REQUIRED > 0) {
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
    
    private Boolean isSpecialCharacter(char c) {
        Boolean special = Boolean.FALSE;
        int cint = (int)c;
        
        if (cint < 48 || (cint > 57 && cint < 65) || (cint > 90 && cint < 97) || cint > 122) {
            special = Boolean.TRUE;
        }
        
        return special;
    }
}
