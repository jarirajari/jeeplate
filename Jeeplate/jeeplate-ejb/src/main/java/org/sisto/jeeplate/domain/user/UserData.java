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
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.sisto.jeeplate.domain.user.group.UserGroupData;
import org.sisto.jeeplate.domain.user.group.membership.UserGroupMembershipData;
import org.sisto.jeeplate.domain.BusinessEntityStore;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.ApplicationProperty;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;

@SessionScoped
public class UserData implements Serializable {
    
    @Inject
    private transient StringLogger log;
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    private String systemEmailAddress;
    
    @Inject
    private User user;
    
    @Inject
    private Email emailSender;
    
    @Inject
    private UserGroupMembershipData usersGroups;
    
    @Inject @New
    private transient BusinessEntityStore<UserEntity> store;
    
    private UserEntity entity;
    
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
        this.entity = UserEntity.newUserEntityBuilder().build();
    }
    
    public UserData(UserEntity ue) {
        this.entity = ue;
    }
    
    public void setEntity(UserEntity ude) {
        this.entity = ude;
    }
    
    public UserEntity getEntity() {
        return (this.entity);
    }
    
    @Transactional
    public Map<Long, UserGroupData> findGroupsUserBelongsTo() {
        
        return new HashMap<>();
    }
    
    @Transactional
    public Map<Long, UserData> findAllUsers() {
        final String query = "SELECT ue FROM UserEntity ue";
        final Map<String, Object> params = new HashMap<>();
        final List<UserEntity> result = this.store.executeQuery(UserEntity.class, query, params);
        
        return (result.stream()
                .collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    
    @Transactional
    public Map<Long, UserData> findOneUser(final Long withId) {
        final String query = "SELECT ue FROM UserEntity ue WHERE ue.id = :userId";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", withId);
        }};
        final List<UserEntity> result = this.store.executeQuery(UserEntity.class, query, params);
        
        return (result.stream()
                .collect(Collectors.toMap(UserEntity::getId, UserData::new)));
    }
    
    @Transactional
    public UserData findOneUser(final String emailAddress) {
        final List<UserEntity> result = this.findUserByEmail(emailAddress);
                
        if ((result != null) && (result.isEmpty() == false) && (result.size() == 1)) {
            UserEntity id = result.get(0);
            this.entity = id;
       } else {
            log.error("Finding one user by email address failed!");
        }
        
        return (this);
    }
    
    private void sendPasswordResetEmailForUser(EmailMessage em, String secret) {
        String subject = em.getSubject();
        // Convention and loose contract that '${secret}' will be replaced
        String content = em.getContent().replace("${secret}", secret);
        MimeMessage mm = emailSender.constructEmail(subject, content, this.systemEmailAddress, this.entity.username);
        
        emailSender.sendMessage(mm);
    }
    
    @Transactional
    public String initializePasswordReset(EmailMessage messageForOldUser, EmailMessage messageForNewUser) {
        assert this.entity != null;
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
            log.debug("Password reset requested for an existing user.");
        } else {
            message = messageForNewUser;
            resetToken = "";
            log.error("Password reset requested for a user that has no account!");
        }
        sendPasswordResetEmailForUser(message, resetToken);
        
        return resetToken;
    }
    
    @Transactional
    public Boolean finalizePasswordReset(String typedMobile, String typedPassword, String emailedResetToken, String hiddenActionSecret) {
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
                log.info("Did not change password for user '%s'...", this.getEntity().getUsername());
            }
        } else {
            log.error("Changing password for user '%s' failed: %s %s", this.getEntity().getUsername(),
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
        final List<UserEntity> result = this.store.executeCustomQuery(UserEntity.class, query, params);
        
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
        this.store.create(this.hashed);
        this.store.create(this.hashed2);
        this.store.create(this.hashed3);
        return Boolean.TRUE;
    }
    /*
     * Methods find() and bind(id) must be public
     */
    @Transactional
    public UserData find() {
        return (this);
    }
    
    @Transactional
    public UserData bind(Long id) {
        UserEntity tmp = UserEntity.newUserEntityBuilder().renovate(id);
        
        this.entity = this.store.bind(tmp);
        
        return (this);
    }
    /*
     * Methods create(), read(), update(), and delete() must be package private!
     */
    @Transactional
    Boolean create() {
        log.info("UserData.create()");
        this.entity = this.store.create(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean read() {
        log.info("UserData.read() discards changes");
        this.entity = this.store.read(entity);
        
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean update() {
        log.info("UserData.update() overwrites");
        this.entity = this.store.update(entity);
        log.info("Updated"+this.entity.hashCode()+", "+this.entity.toString());
        return Boolean.TRUE;
    }
    
    @Transactional
    Boolean delete() {
        log.info("UserData.delete()");
        this.entity = this.store.delete(entity);
        
        return Boolean.TRUE;
    }
    void whatever() {}
}
