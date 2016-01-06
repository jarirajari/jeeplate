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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.sisto.jeeplate.jeeplate.AppBean;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.EmailMessage;

@Dependent
public class UserLogic implements Serializable {
    
    @Inject
    StringLogger log;
    
    @Inject
    UserMessageEngine messages;
    
    @EJB /* @EJB instead of @Inject for Accessing Local no-interface EJBs */
    AppBean app;
    
    @Inject
    User user;
    
    @PostConstruct
    private void initSecurityManager() {
        final String SHIRO_CONFIG = "classpath:META-INF/shiro.ini";
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory(SHIRO_CONFIG);
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }
    
    public String authenticatedAndAuthorizedUser() {
        Subject s = SecurityUtils.getSubject();
        String un = user.getData().getDataModel().getUsername();
        String pw = user.getData().getDataModel().getCredential().getPassword();
        String ret = "";
        if (s != null) {
            // OK s.login(new UsernamePasswordToken("tortoise", "shell", false));
            log.info("s.login" +un+", "+pw);
            s.login(new UsernamePasswordToken(un, pw, false));
            ret = ret + s.getPrincipal().toString()+" is authenticated "+s.isAuthenticated();
            ret = ret + " is permitted to walk="+s.isPermitted("swim");
            ret = ret + " is permitted to fly ="+s.isPermitted("fly");
        } else {
            ret = "error";
        }
        
        return ret;
    }
    
    public String testAppBean() {
        this.app.callMe();
        return (this.authenticatedAndAuthorizedUser());
    }
    
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
