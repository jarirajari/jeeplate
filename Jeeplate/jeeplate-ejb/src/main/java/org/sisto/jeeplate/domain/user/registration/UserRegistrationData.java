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
package org.sisto.jeeplate.domain.user.registration;

import java.io.Serializable;
import javax.ejb.Stateful;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.logging.StringLogger;

@Stateful
public class UserRegistrationData extends BusinessBean<UserRegistrationData, UserRegistrationEntity> implements Serializable {
    
    public UserRegistrationData() {
        super(UserRegistrationData.class, UserRegistrationEntity.class);
    }
    
    public String applyForUserAccount() {
        Boolean userNotExist = this.getDataModel().isDefault();
        UserRegistrationEntity reg  = this.getDataModel();
        String token;
        
        if (userNotExist) {
            reg.activateRegistrationProtocol();
        }
        if (userNotExist) {
            token = reg.getRegistrationToken();
            log.debug("User registration requested for a user that has no account!");
        } else {
            token = "";
            log.info("User registration requested for an existing user (id=%s).", String.valueOf(this.getDataModel().getId()));
        }
        
        return token;
    }
    
    public Boolean grantUserAccount(String typedMobile, String typedPassword, String emailedResetToken) {
        Boolean granted = Boolean.FALSE;
        String token = this.getEntity().getRegistrationToken();
        Boolean oneDomainFound = emailedResetToken.startsWith(token);
        
        // Side-channel (email) process:
        // a. email is sent to user
        // b. user replies to it, which activates reg.ent boolean field
        // c. on TRUE => registration can be completed => grant ok
        // ?? for testing we can skip to c. directly
        
        // Registration ok:
        // 1. user becomes a member of ALL group in the domain
        // 2. user is assingned an APPLICATION role and system role is revoked
        // 3. user gets a account for detailed personal information
        //
        if (oneDomainFound) {
            log.info("User registration, found application domain"); // application domain
        } else {
            boolean systemUserRequestResponseChallengeHash = false;
            if (systemUserRequestResponseChallengeHash) {
                log.info("User registration, found system domain"); // system domain
            } else {
                log.info("User registration, found unknown domain"); // unknown domain
            }
        }
        
        return granted;
    }
}
