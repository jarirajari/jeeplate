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
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import org.sisto.jeeplate.authentication.role.ApplicationRole;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.ObjectEntity;
import org.sisto.jeeplate.domain.user.account.UserAccountData;
import org.sisto.jeeplate.domain.user.account.UserAccountEntity;
import org.sisto.jeeplate.util.ApplicationProperty;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;

@SessionScoped @Stateful
public class UserData extends BusinessBean<UserData, UserEntity> implements Serializable {
    
    @Inject
    User user;
    
    @Inject
    UserAccountData account;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    String systemEmailAddress2;
    
    @Inject
    Email emailSender;
    
    ApplicationRole transientCurrentRole = ApplicationRole.VISITOR;
    
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
    
    /*
     *
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    
    Actually, KISS! Let's give up for now from the strict enterpise layers:
    
    CDI SPEC: "A producer method acts as a source of objects to be injected, 
    where the objects to be injected are not required to be instances of beans"
    
    So we are creating UserData objects with JPA entity dependency
    
    
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    *
    */
    public void findOneUser(final String emailAddress) {
        this.setDataModel(this.findOneSecondary(emailAddress).getDataModel());
    }
    
    public Map<String, String> assignedRolesForUser() {
        return (this.getDataModel().assignedRolesForUser());
    }
    
    public String currentRoleForUser() {
        return (this.getDataModel().currentRoleForUser());
    }
    
    public void findLoggedInUser(final String userEmailPrincipal) {
        final Boolean userAlreadyFound = (this.getDataModel().getUsername().equals(userEmailPrincipal));
        if (! userAlreadyFound) {
            findOneUser(userEmailPrincipal);
        } else {
            this.find();
        }
    }
    
    public Boolean switchCurrentUserRole(String newRoleName, String typedPIN) {
        Boolean fa2required = this.requiresTwoFactorAuth(newRoleName);
        Boolean ok = this.getDataModel().swithcRoleTo(ApplicationRole.convert(newRoleName), typedPIN, fa2required);
        
        if (ok) {
            transientCurrentRole = this.getDataModel().getAppRole().getCurrentRole();
            this.update();
            this.getDataModel().getAppRole().setCurrentRole(transientCurrentRole);
        }
        
        return ok;
    }
    
    public Boolean requiresTwoFactorAuth(String newRoleName) {
        return (this.getDataModel().requiresTwoFactorAuth(ApplicationRole.convert(newRoleName)));
    }
    
    private void sendEmailToUser(EmailMessage em, String key, String val) {
        String subject = em.getSubject();
        // Convention and loose contract that secretKey will be replaced with secretVal
        String content = em.getContent().replace(key, val);
        MimeMessage mm = emailSender.constructEmail(subject, content, this.systemEmailAddress2, em.getContentRecipient());
        emailSender.sendMessage(mm);
    }
    
    public void notifyUserFor2FA(EmailMessage em) {
        final String replace = "${pin}";
        final String pin = this.getDataModel().twoFactorAuthPIN();
        sendEmailToUser(em, replace, pin);
    }
    
    public void nofityUserForRegistration(EmailMessage messageForOldUser, EmailMessage messageForNewUser, String registrationToken) {
        final String replace = "${domain}";
        boolean userNotExist = this.getDataModel().isDefault();
        EmailMessage message;
        
        if (userNotExist) {
            message = messageForNewUser;
            log.debug("User registration requested for a user that has no account!");
        } else {
            message = messageForOldUser;
            log.info("User registration requested for an existing user (id=%s).", String.valueOf(this.getDataModel().getId()));
        }
        sendEmailToUser(message, replace, registrationToken);
    }
    
    public String initializePasswordReset(EmailMessage messageForOldUser, EmailMessage messageForNewUser) {
        final String replace = "${secret}";
        boolean userExists =! this.getDataModel().isDefault();
        String resetToken;
        EmailMessage message;
        UserCredential uc  = this.getDataModel().getCredential();
        
        if (userExists) {
            uc.activateResetProtocol();
            this.update();
        }
        if (userExists) {
            message = messageForOldUser;
            resetToken = uc.getPasswordResetToken();
            log.debug("Password reset requested for an existing user.");
        } else {
            message = messageForNewUser;
            resetToken = "";
            log.error("Password reset requested for a user that has no account!");
        }
        sendEmailToUser(message, replace, resetToken);
        
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
    
    public Boolean noUserWithEmail(final String emailAddress, String subject, String content, 
                                   String source, String destination) {
        final UserData ud = this.findOneSecondary(emailAddress);
        Boolean noUser;
        
        if (ud == null || ud.getDataModel() == null || 
            ud.getDataModel().getId().equals(ObjectEntity.DEFAULT_ID)) {
            noUser = Boolean.TRUE;
        } else {
            MimeMessage mm = emailSender.constructEmail(subject, content, this.systemEmailAddress2, destination);
            emailSender.sendMessage(mm);
            noUser = Boolean.FALSE;
        }
        
        return noUser;
    }
    
    public void createNewUser(UserEntity ue) {
        final UserAccountData createdAccount = this.account.createNewAccountFor(this);
        final UserAccountEntity uae = createdAccount.getDataModel();
        
        ue.setOneAccount(uae);
        this.setEntity(ue);
        this.create();
    }
    
    public Boolean changeName(String name) {
        if (this.user.updateUserName()) {
            this.entity.setUsername(name);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
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
}
