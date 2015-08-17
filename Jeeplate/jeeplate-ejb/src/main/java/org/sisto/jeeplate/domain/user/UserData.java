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
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.BusinessObject;
import org.sisto.jeeplate.domain.ObjectEntity;
import org.sisto.jeeplate.util.ApplicationProperty;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;

@SessionScoped
public class UserData extends BusinessBean<UserData, UserEntity> implements Serializable {
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    String systemEmailAddress;
    
    @Inject @Dependent
    User user;
    
    @Inject @Any
    Email emailSender;
    
    private transient final UserEntity hashed = UserEntity.newUserEntityBuilder()
            .withUsername("hashis")
            .withPassword("good")
            .build();
    private transient final UserEntity hashed2 = UserEntity.newUserEntityBuilder()
            .withUsername("un")
            .withPassword("pw")
            .build();
    private transient final UserEntity hashed3 = UserEntity.newUserEntityBuilder()
            .withUsername("user@na.me")
            .withPassword("pw")
            .build();
    
    @Transactional
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

    @Transactional
    public UserData findOneUser(final String emailAddress) {
        return (findOneAlternative(emailAddress));
    }
    
    private void sendEmailToUser(EmailMessage em, String secretKey, String secretVal) {
        String subject = em.getSubject();
        // Convention and loose contract that secretKey will be replaced with secretVal
        String content = em.getContent().replace(secretKey, secretVal);
        MimeMessage mm = emailSender.constructEmail(subject, content, this.systemEmailAddress, em.getContentRecipient());
        
        emailSender.sendMessage(mm);
    }
    
    @Transactional
    public void nofityUserForRegistration(EmailMessage messageForOldUser, EmailMessage messageForNewUser, String registrationToken) {
        assert this.entity != null;
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
    
    @Transactional
    public String initializePasswordReset(EmailMessage messageForOldUser, EmailMessage messageForNewUser) {
        assert this.entity != null;
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
    
    @Transactional
    public Boolean finalizePasswordReset(String typedMobile, String typedPassword, String emailedResetToken) {
        assert this.entity != null;
        UserCredential uc = this.getDataModel().getCredential();
        Boolean changed = Boolean.FALSE;
        Boolean resetRequestValid = uc.resetIsValid();
        Boolean securityQuestionMobileNumberMatches = this.getDataModel().mobileNumberIsSame(typedMobile);
        
        if (resetRequestValid && securityQuestionMobileNumberMatches) {
            final boolean resetTokenValid = uc.getPasswordResetToken().equals(emailedResetToken);
            if (resetTokenValid) {
                uc.deactivateResetProtocol();
                uc.refresh(typedPassword);
                changed = Boolean.TRUE; 
            }
            if (changed) {
                this.update();
            } else {
                log.info("Did not change password for user '%s'...", this.getDataModel().getUsername());
            }
        } else {
            log.error("Changing password for user '%s' failed: %s %s", this.getDataModel().getUsername(),
                      resetRequestValid.toString(), securityQuestionMobileNumberMatches.toString());
        }
        
        return changed;
    }
    
    @Transactional
    public Boolean noUserWithEmail(final String emailAddress) {
        final UserData ud = this.findOneAlternative(emailAddress);
        Boolean noUser = Boolean.FALSE;
        
        if (ud == null || ud.getDataModel() == null || 
            ud.getDataModel().getId().equals(ObjectEntity.DEFAULT_ID)) {
            noUser = Boolean.TRUE;
        }
        
        return noUser;
    }
    
    public Boolean changeName(String name) {
        if (this.user.updateUserName()) {
            this.entity.setUsername(name);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
