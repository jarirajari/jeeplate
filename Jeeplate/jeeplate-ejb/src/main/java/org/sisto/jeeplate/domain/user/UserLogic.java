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
package org.sisto.jeeplate.domain.user;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.EmailMessage;

@Dependent
public class UserLogic implements Serializable {
    
    @Inject
    StringLogger log;
    
    @Inject
    UserMessageEngine messages;
    
    public void userForRoleSwitch(UserData data, String recipient, String locale) {
        final Locale userLangLocale = new Locale(locale);
        final String pin = data.notifyUserFor2FA();
        final EmailMessage em = messages.buildRoleSwitchMessage(userLangLocale, recipient, pin);
        
        messages.sendEmailToUser(em);
    }
    
    public void userRegistrationAttempt(String recipient, String locale, String pin) {
        final Locale userLangLocale = new Locale(locale);
        final EmailMessage em = messages.buildRegistrationUserNotExistsMessage(userLangLocale, recipient, pin);
        
        messages.sendEmailToUser(em);
    }
    
    public void userPasswordReset(String recipient, String locale, String token, boolean userExists) {
        final Locale userLangLocale = new Locale(locale);
        final EmailMessage em;
        
        if (userExists) {
            em = messages.buildPasswordResetUserExistsMessage(userLangLocale, recipient, token);
        } else {
            em = messages.buildPasswordResetUserNotExistsMessage(userLangLocale, recipient);
        }
        
        messages.sendEmailToUser(em);
    }
}
