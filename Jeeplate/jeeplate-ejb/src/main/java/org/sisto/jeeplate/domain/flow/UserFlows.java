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
package org.sisto.jeeplate.domain.flow;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.user.User;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.domain.user.registration.UserRegistration;

@SessionScoped @Stateful
public class UserFlows implements Serializable {
    
    @Inject
    User user;
    
    @Inject
    UserRegistration registration;
    
    public UserFlows forUser() {
        return this;
    }
    
    public UserFlows forUser(String currentUser) {
        final UserData data = this.user.getData();
        
        if (! data.isLoaded()) {
            data.findLoggedInUser(currentUser);
        } else if (! data.currentNameForUser().equals(currentUser)) {
            data.findLoggedInUser(currentUser);
        } else {
            // pass;
        }
        
        return this;
    }
    
    public UserData findUser() {
        return (this.user.getData());
    }
    
    public void userRegistrationStart(String userLocale, String mailRecipient) {
        final String creationToken = this.registration.getData().applyForUserAccount(mailRecipient);
        this.user.getLogic().userRegistrationAttempt(mailRecipient, userLocale, creationToken);
    }
    
    public Boolean userRegistrationStop(String username, String password, String mobile, String emailedSecret) {
        final Boolean regGranted = this.registration.getData().grantUserAccount(username, password, mobile, emailedSecret);
        
        if (regGranted) {
            final UserEntity ue = EntityBuilder.of().UserEntity()
                    .setUsername(username)
                    .setPassword(password)
                    .setMobile(Long.valueOf(mobile))
                    .asApplicationUser();
            this.user.getData().createNewUser(ue);
        }
        
        return regGranted;
    }
    
    public void userForRoleSwitch(String recipient, String locale) {
        this.user.getLogic().userForRoleSwitch(this.user.getData(), recipient, locale);
    }
    
    public String initializePasswordReset(String recipient, String locale) {
        final String resetToken = this.user.getData().initializePasswordReset(recipient, locale);
        Boolean userExists = this.user.getData().checkAnySecondaryExists(recipient);
        
        this.user.getLogic().userPasswordReset(recipient, locale, resetToken, userExists);
        
        return resetToken;
    }
    
    public Boolean finalizePasswordReset(String typedMobile, String typedPassword, String emailedResetToken){
        Boolean reseted = this.user.getData().finalizePasswordReset(typedMobile, typedPassword, emailedResetToken);
        
        return reseted;
    }
    
    public Boolean updateUserAccountLocalisation(String lang, String country, String city, String timezone) {
        Boolean updated = this.user.getData().updateUserAccountLocalisation(lang, country, city, timezone);
        
        return updated;
    }
    
    public Boolean changeUserPassword(String oldPW, String newPW) {
        Boolean changed = this.user.getData().changeUserPassword(oldPW, newPW);
        
        return changed;
    }
    
    public String generateNewPinForRoleSwitch() {
        return (this.user.getData().generateNewPinForRoleSwitch());
    }
    
    public Boolean switchCurrentUserRole(String newRole, String pin) {
        return (this.user.getData().switchCurrentUserRole(newRole, pin));
    }
}
