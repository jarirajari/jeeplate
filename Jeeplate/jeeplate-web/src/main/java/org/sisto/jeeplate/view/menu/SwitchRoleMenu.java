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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.util.EmailMessage;

@Named @ViewScoped
public class SwitchRoleMenu implements Serializable {

    @Inject
    private UserData user;
    private MenuModel switchRoleMenuModel;
    private DefaultMenuItem item;
    private Map<String, String> roles;
    private Set<String> roleKeys;
    private String pin;
    private String role;
    private DefaultSubMenu submenu;
    private Boolean req2FA;
    
    @PostConstruct
    public void init() {
        this.roleKeys = new HashSet<>();
        submenu = new DefaultSubMenu("");
        switchRoleMenuModel = new DefaultMenuModel();
        switchRoleMenuModel.addElement(submenu);
        req2FA = Boolean.TRUE;
    }
    
    private UserEntity user() {
        UserEntity ue;
        user.findLoggedInUser(this.currentUser());
        ue = user.getDataModel();
        
        return ue;
    }
    
    public void populateData() {
        user.findLoggedInUser(currentUser());
        this.setRole(String.format("%s", user.currentRoleForUser()));
        roles = user.assignedRolesForUser();
        roleKeys = roles.keySet();
        submenu.setLabel(String.format("%s", role));
        submenu.getElements().clear();
        for (String key : roleKeys) {
            item = new DefaultMenuItem(roles.get(key));
            item.setIcon("ui-icon-close");
            item.setCommand(String.format("#{switchRoleMenu.switchToRole('%s')}",key));      
            item.setValue(key);
            submenu.addElement(item);
        }
        
    }
    
    public void flushPIN(AjaxBehaviorEvent event){
        final String userTypedPin = (String) ((UIOutput)event.getSource()).getValue();
	this.setPin(userTypedPin);
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

    public MenuModel getSwitchRoleMenuModel() {
        return switchRoleMenuModel;
    }
    
    public Boolean requires2FA() {
        return (this.req2FA);
    }
    
    public void switchNow() {
        
        Boolean ok = user.switchCurrentUserRole(role, pin);
        if (ok) {
            submenu.setLabel(String.format("%s", role));
            this.setRole(role);
            this.setPin("");
        }
        closeConditionally(ok);
    }
    
    public void clickedRoleSwitchedButton() {
        
    }
    
    public String currentUser() {
        Subject currentUsr = SecurityUtils.getSubject();
        String usr = (currentUsr.getPrincipal() != null) ? currentUsr.getPrincipal().toString() : "";
        
        return usr;
    }
    
    public void openConditionally() {
        final String replace = "${pin}";
        final boolean cond = user.requiresTwoFactorAuth(role);
        if (cond) {
            final String recipient = currentUser();
            final EmailMessage em = new EmailMessage("Your role PIN", String.format("Role switch 2FA PIN is %s",replace), recipient, "Jeeplate corp.");
            this.user.notifyUserFor2FA(em);
        }
        RequestContext.getCurrentInstance().execute("PF('fa2Dlg').show()");
    }
    
    public void closeConditionally(Boolean ok) {
        if (ok) {
            RequestContext.getCurrentInstance().execute("PF('fa2Dlg').hide()");
        }        
    }
    
    public void switchToRole(String newRole) {
        
        this.setRole(newRole);
        this.req2FA = (user.requiresTwoFactorAuth(newRole));
        RequestContext.getCurrentInstance().update("fa2Form");
        openConditionally();
    }
}
