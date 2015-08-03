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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.user.group.UserGroupData;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.ApplicationProperty;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;

@SessionScoped
public class UserData extends BusinessBean<UserData, UserEntity> implements Serializable {
    
    @Inject
    private transient StringLogger slog;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    private String systemEmailAddress;
    
    @Inject
    private User user;
    
    @Inject
    private Email emailSender;
    
    @Inject
    private BusinessEntityStore<UserEntity> str;
    
    //private UserEntity entity; // this is basically sort of proxy object!
    //private UserEntity dataModel; // pseudo alias for "this.entity"
    
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
    
    
    public UserData() {
        super(UserData.class, UserEntity.class);
    }
    /*
    // public because of  bean specification
    public UserData() {
        super();
        this.entity = UserEntity.newUserEntityBuilder().build();
    }
    
    // private but needed because lambdas did not work
    private UserData(UserEntity ue) {
        super();
        this.entity = ue;
    }
    */
    /*
     * these could be replaced with a data model:
     * e.g. client side could have a function of form
     * anyview.amethod.cdibeanData.(MODEL.Username) that
     * would return entity.username. But for now let's 
     * expose the entity itself...
     */
    /*
    public UserEntity getDataModel() {
        
        return (this.entity);
    }
    
    protected void setEntity(UserEntity ude) {
        this.entity = ude;
    }
    
    protected UserEntity getEntity() {
        return (this.entity);
    }
    */
    
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
    
    
    /*
    @Transactional
    public Map<Long, UserData> findAllUsers() {
        final String query = "SELECT ue FROM UserEntity ue";
        final Map<String, Object> params = new HashMap<>();
        final List<UserEntity> results = this.str.executeQuery(UserEntity.class, query, params);
        
        return (results.stream().collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    
    @Transactional
    public Map<Long, UserData> findOneUser(final Long withId) {
        final String query = "SELECT ue FROM UserEntity ue WHERE ue.id = :userId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", withId);
        }};
        final List<UserEntity> results = this.str.executeQuery(UserEntity.class, query, params);
        
        return (results.stream().collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    */
    @Transactional
    public UserData findOneUser(final String emailAddress) {
        final List<UserEntity> result = this.findUserByEmail(emailAddress);
        
        if ((result != null) && (result.isEmpty() == false) && (result.size() == 1)) {
            UserEntity id = result.get(0);
            this.entity = id;
       } else {
            this.entity = UserEntity.newUserEntityBuilder().build(); // OK to construct JPA entity
            slog.error("Finding one user by email address failed!");
        }
        
        return (this);
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
        boolean userNotExist = this.getEntity().isDefault();
        EmailMessage message;
        
        if (userNotExist) {
            message = messageForNewUser;
            slog.debug("User registration requested for a user that has no account!");
        } else {
            message = messageForOldUser;
            slog.info("User registration requested for an existing user (id=%s).", String.valueOf(this.getEntity().getId()));
        }
        sendEmailToUser(message, replace, registrationToken);
    }
    
    @Transactional
    public String initializePasswordReset(EmailMessage messageForOldUser, EmailMessage messageForNewUser) {
        assert this.entity != null;
        final String replace = "${secret}";
        boolean userExists =! this.getEntity().isDefault();
        String resetToken;
        EmailMessage message;
        UserCredential uc  = this.getEntity().getCredential();
        
        if (userExists) {
            uc.activateResetProtocol();
            this.update();
        }
        if (userExists) {
            message = messageForOldUser;
            resetToken = uc.getPasswordResetToken();
            slog.debug("Password reset requested for an existing user.");
        } else {
            message = messageForNewUser;
            resetToken = "";
            slog.error("Password reset requested for a user that has no account!");
        }
        sendEmailToUser(message, replace, resetToken);
        
        return resetToken;
    }
    
    @Transactional
    public Boolean finalizePasswordReset(String typedMobile, String typedPassword, String emailedResetToken) {
        assert this.entity != null;
        UserCredential uc = this.getEntity().getCredential();
        Boolean changed = Boolean.FALSE;
        Boolean resetRequestValid = uc.resetIsValid();
        Boolean securityQuestionMobileNumberMatches = this.getEntity().mobileNumberIsSame(typedMobile);
        
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
                slog.info("Did not change password for user '%s'...", this.getEntity().getUsername());
            }
        } else {
            slog.error("Changing password for user '%s' failed: %s %s", this.getEntity().getUsername(),
                      resetRequestValid.toString(), securityQuestionMobileNumberMatches.toString());
        }
        
        return changed;
    }
    
    @Transactional
    public Boolean noUserWithEmail(final String emailAddress) {
        final List<UserEntity> result = this.findUserByEmail(emailAddress);
        Boolean noUser = Boolean.TRUE;
        
        if (! result.isEmpty() && result.size() == 1) {
            noUser = Boolean.FALSE;
        }
        
        return noUser;
    }
    
    @Transactional
    public List<UserEntity> findUserByEmail(final String emailAddress) {
        final String query = "SELECT ue FROM UserEntity ue WHERE ue.username = :username";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("username", emailAddress);
        }};
        final List<UserEntity> result = this.str.executeCustomQuery(UserEntity.class, query, params);
        
        return result;
    }
    
    public Boolean changeName(String name) {
        if (this.user.updateUserName()) {
            this.entity.setUsername(name);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    @Transactional
    public Boolean testHashing() {
        this.str.create(this.hashed);
        this.str.create(this.hashed2);
        this.str.create(this.hashed3);
        return Boolean.TRUE;
    }
    
    // Must be public and return identity (Long): AS ID GETTER
    @Transactional
    public Long find() {
        final Long id = this.getEntity().getId();
        
        return id;
    }
    
    // Must be public and return domain object (UserData): AS ID SETTER
    @Transactional
    public UserData bind(Long id) {
        UserEntity tmp = UserEntity.newUserEntityBuilder().renovate(id);
        
        this.entity = this.str.bind(tmp);
        
        return (this);
    }
    
    // Methods create(), read(), update(), and delete() must be package private!
    @Transactional
    Boolean create() {
        slog.info("UserData.create()");
        this.entity = this.str.create(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean read() {
        slog.info("UserData.read() discards changes");
        this.entity = this.str.read(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean update() {
        slog.info("UserData.update() overwrites");
        this.entity = this.str.update(entity);
        slog.info("Updated"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean delete() {
        slog.info("UserData.delete()");
        this.entity = this.str.delete(entity);
        
        return Boolean.TRUE;
    }
    void whatever() {}
}
