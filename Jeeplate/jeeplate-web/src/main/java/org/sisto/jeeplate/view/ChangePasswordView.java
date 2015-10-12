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
package org.sisto.jeeplate.view;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.domain.user.UserData;

@Named @ViewScoped
public class ChangePasswordView extends AbstractView implements Serializable {
    
    private String username;
    private String password;
    private String newPassword;
    @Inject 
    private UserData user;

    @PostConstruct
    public void init() {
        this.username = this.currentUser();
        this.password = "";
        this.newPassword = "";
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void change() {
        this.user.findLoggedInUser(this.username);
        Boolean changed = this.user.changeUserPassword(this.password, this.newPassword);
        
        if (changed) {
            RequestContext.getCurrentInstance().execute("PF('credentialsDlg').hide()");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "NOT OK, no changed password");

        }
    }
    
    public String currentUser() {
        Subject currentUsr = SecurityUtils.getSubject();
        String usr = (currentUsr.getPrincipal() != null) ? currentUsr.getPrincipal().toString() : "";
        
        return usr;
    }
}
