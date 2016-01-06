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
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.sisto.jeeplate.authentication.role.ApplicationRole;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.base.DomainEntity;
import org.sisto.jeeplate.domain.space.DomainSpaceData;
import org.sisto.jeeplate.domain.user.account.UserAccountData;
import org.sisto.jeeplate.domain.user.account.UserAccountEntity;

@Dependent
public class UserData extends BusinessBean<UserData, UserEntity> implements Serializable {
    
    @Inject
    User user;
    
    @Inject
    UserAccountData account;
    
    @Inject
    DomainSpaceData space;
    
    private transient final UserEntity hashed = EntityBuilder.of().UserEntity()
            .setUsername("hashis")
            .setPassword("good");
    private transient final UserEntity hashed2 = EntityBuilder.of().UserEntity()
            .setUsername("un")
            .setPassword("pw");
    private transient final UserEntity hashed3 = EntityBuilder.of().UserEntity()
            .setUsername("user@na.me")
            .setPassword("pw");
    
    public Boolean testHashing() {
        this.createTestData(hashed, hashed2, hashed3);
        return Boolean.TRUE;
    }
    
    public UserData() {
        super(UserData.class, UserEntity.class);
    }
    
    public void findOneUser(final String emailAddress) {
        this.setDataModel(this.findOneSecondary(emailAddress).getDataModel());
    }
    
    public Map<String, String> assignedRolesForUser() {
        return (this.getDataModel().assignedRolesForUser());
    }
    
    public String currentRoleForUser() {
        final String role = this.getDataModel().currentRoleForUser();
        
        return role;
    }
    
    public String currentNameForUser() {
        final String name = this.getDataModel().getUsername();
        
        return name;
    }
    
    public void findLoggedInUser(final String userEmailPrincipal) {
        final Boolean userAlreadyFound = (this.getDataModel().getUsername().equals(userEmailPrincipal));
        if (! userAlreadyFound) {
            findOneUser(userEmailPrincipal);
        } else {
            this.find();
        }
    }
    
    private void updateRetainTransientCurrentRole() {
        this.update();
    }
    
    public Boolean switchCurrentUserRole(String newRoleName, String typedPIN) {
        Boolean fa2required = this.requiresTwoFactorAuth(newRoleName);
        Boolean ok = this.getDataModel().swithcRoleTo(newRoleName, typedPIN, fa2required);
        
        if (ok) {
            updateRetainTransientCurrentRole();
        }
        
        return ok;
    }
    
    public Boolean requiresTwoFactorAuth(String newRoleName) {
        return (this.getDataModel().requiresTwoFactorAuth(ApplicationRole.convert(newRoleName)));
    }
    
    public String generateNewPinForRoleSwitch() {
        String pin = this.getDataModel().twoFactorAuthPIN();
        this.updateRetainTransientCurrentRole();
        
        return pin;
    }
    
    public String notifyUserFor2FA() {
        final String pin = this.getDataModel().getCredential().getPIN2FA();
        
        return pin;
    }
    
    public String initializePasswordReset(String recipient, String locale) {
        boolean userExists =! this.getDataModel().isDefault();
        UserCredential uc  = this.getDataModel().getCredential();
        String resetToken;
        
        if (userExists) {
            uc.activateResetProtocol();
            this.update();
        }
        if (userExists) {
            resetToken = uc.getPasswordResetToken();
            log.info("Password reset requested for an existing user.");
        } else {
            resetToken = "";    
            log.error("Password reset requested for a user that has no account!");
        }
        
        return resetToken;
    }
    
    public Boolean finalizePasswordReset(String typedMobile, String typedPassword, String emailedResetToken) {
        UserCredential uc = this.getDataModel().getCredential();
        Boolean changed = Boolean.FALSE;
        Boolean resetRequestValid = uc.resetIsValid();
        Boolean securityQuestionMobileNumberMatches = this.getDataModel().mobileNumberIsSame(typedMobile);
        
        if (resetRequestValid && securityQuestionMobileNumberMatches) {
            final boolean resetTokenValid = uc.getPasswordResetToken().equals(emailedResetToken);
            if (resetTokenValid) {
                uc.deactivateResetProtocol();
                uc.refresh(typedPassword);
                this.update();
                changed = Boolean.TRUE; 
            }
        } else {
            log.error("Changing password for user '%s' failed: %s %s", this.getDataModel().getUsername(),
                      resetRequestValid.toString(), securityQuestionMobileNumberMatches.toString());
        }
        
        return changed;
    }
    
    public Boolean changeUserPassword(String oldPW, String newPW) {
        UserCredential uc = this.getDataModel().getCredential();
        Boolean changed = Boolean.FALSE;
        
        if (uc.passwordMatchesWhenHashedWithSameSalt(oldPW)) {
            uc.refresh(newPW);
            this.update();
            changed = Boolean.TRUE;
        }
        
        return changed;
    }
    
    public void createNewUser(UserEntity ue) {
        final UserAccountData createdAccount = this.account.createNewAccountFor(this);
        final UserAccountEntity uae = createdAccount.getDataModel();
        
        ue.setOneAccount(uae);
        this.setEntity(ue);
        this.create();
    }
    
    public Boolean changeName(String name) {
        this.entity.setUsername(name);
        
        return Boolean.TRUE;
    }
    
    public Boolean updateUserAccountLocalisation(String lang, String country, String city, String timezone) {
        final UserAccountEntity uae = this.getDataModel().getOneAccount();
        
        uae.setLang(lang)
           .setCity(city)
           .setCountry(country)
           .setTimezone(timezone);
        this.getDataModel().setOneAccount(uae);
        this.update();
        return Boolean.TRUE;
    }
    
    public void registerToDomain(String userFirstName, String userLastName, String domainFQDN) {
        final UserEntity ue = this.getDataModel();
        final UserAccountEntity ua = ue.getOneAccount();
        
        ua.setRegistrationCompleted(Boolean.TRUE);
        ua.setFirstName(userFirstName);
        ua.setLastName(userLastName);
        if (DomainEntity.isRootDomain(domainFQDN)) {
            ue.setType(UserType.SYSTEM);
        } else {
            ue.setType(UserType.APPLICATION);
        }
        
        // add to the all group of the domain of domainFQDN
        
        this.update();
        
    }
}
