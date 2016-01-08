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
 * MERCHANATBILITY or FITNES SFOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.sisto.jeeplate.view.menu;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.sisto.jeeplate.domain.flow.UserFlows;
import org.sisto.jeeplate.jsf.Navigator;
import org.sisto.jeeplate.view.AbstractView;

@Named @ViewScoped
public class SwitchRoleMenu extends AbstractView implements Serializable {
    
    @Inject 
    UserFlows registrationFlow;
    
    private String pin;
    private String role;
    private Boolean requiresPIN;
    
    @PostConstruct
    public void init() {
        pin = "";
        role = "";
        requiresPIN = Boolean.TRUE;      
    }
    
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getRequiresPIN() {
        return requiresPIN;
    }

    public void setRequiresPIN(Boolean requiresPIN) {
        this.requiresPIN = requiresPIN;
    }
    
    public Map<String,String> allUserRoles() {
        Map<String, String> roles = this.registrationFlow.findUser().assignedRolesForUser();
        
        return roles;
    }
    
    public void flushPIN(AjaxBehaviorEvent event){
        final String userTypedPin = (String) ((UIOutput)event.getSource()).getValue();
	this.setPin(userTypedPin);
    }
    
    public String generatePIN() {
        return (this.registrationFlow.generateNewPinForRoleSwitch());
    }
    
    // external context function called from javascript
    public Boolean requires2FA() {
        Map<String,String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String nrk = requestParams.get("newRoleKey");
        String nrv = requestParams.get("newRoleVal");
        final Boolean requiresTwoFactorAuth = (this.registrationFlow.findUser().requiresTwoFactorAuth(nrk));
        final String alwaysPIN = this.generatePIN();
        
        if (requiresTwoFactorAuth) {
            this.registrationFlow.userForRoleSwitch(this.currentUser(), this.currentLocale());
        } else {
            this.setPin(alwaysPIN);
        }
        this.setRequiresPIN(requiresTwoFactorAuth);
        
        return requiresTwoFactorAuth;
    }
    
    public String submit() {
        Map<String,String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String r = requestParams.get("fa2Form:fa2roleHidden");
        String p = requestParams.get("fa2Form:fa2PIN");
        
        trySwitchingRoleNow(r, p);
        
        return Navigator.Target.HOME.page();
    }
    
    public void trySwitchingRoleNow(String newRole, String pin) {
        Boolean switched = this.registrationFlow.switchCurrentUserRole(newRole, pin);
        
    }
    
    public String currentUser() {
        Subject currentUsr = SecurityUtils.getSubject();
        String usr = (currentUsr.getPrincipal() != null) ? currentUsr.getPrincipal().toString() : "";
        
        return usr;
    }
    
    public String currentRole() {
        return (this.registrationFlow.findUser().currentRoleForUser());
    }
}
