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
import javax.inject.Inject;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;

@Stateful
public class UserRegistrationData extends BusinessBean<UserRegistrationData, UserRegistrationEntity> implements Serializable {
    
    @Inject
    UserData user;
    
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
    
    public Boolean grantUserAccount(String typedUsername, String typedPassword, String typedMobile, String emailedResetToken) {
        String token = this.getEntity().getRegistrationToken();
        Boolean granted = emailedResetToken.equals(token);
        
        if (granted) {
            final UserEntity ue = EntityBuilder.of().UserEntity()
                    .setUsername(typedUsername)
                    .setPassword(typedPassword)
                    .setMobile(Long.valueOf(typedMobile))
                    .asApplicationUser();
            user.createNewUser(ue);
        }
        
        return granted;
    }
}
