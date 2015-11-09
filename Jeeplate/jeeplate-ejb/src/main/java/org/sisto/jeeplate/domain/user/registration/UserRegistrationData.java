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
    
    public UserRegistrationData() {
        super(UserRegistrationData.class, UserRegistrationEntity.class);
    }
    
    private void removeIfExists(final String emailAddress) {
        this.setDataModel(this.findOneSecondary(emailAddress).getDataModel());
        this.delete();
    }
    
    private void createWithoutUpdate(final String emailAddress) {
        final UserRegistrationEntity ure = EntityBuilder.of().UserRegistrationEntity()
                .setRegistrationEmail(emailAddress);
        
        this.setEntity(ure);
        this.create();
    }
        
    private String updateCreated(String emailAddress) {    
        UserRegistrationEntity ure = this.getEntity();
        String token;
        
        ure.activateRegistrationProtocol(emailAddress);
        this.update();
        token = ure.getRegistrationToken();
        
        log.info("Registration attempt: %s is applying for account", emailAddress);
        
        return token;
    }
    
    public String applyForUserAccount(String emailAddress) {
        this.removeIfExists(emailAddress);
        this.createWithoutUpdate(emailAddress);
        return (this.updateCreated(emailAddress));
    }
    
    public Boolean grantUserAccount(String typedUsername, String typedPassword, String typedMobile, String emailedResetToken) {
        String token = this.getEntity().getRegistrationToken();
        Boolean granted = emailedResetToken.equals(token);
        
        return granted;
    }
}
