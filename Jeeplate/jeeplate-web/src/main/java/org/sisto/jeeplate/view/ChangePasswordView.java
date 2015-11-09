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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.domain.flow.UserFlows;
import org.sisto.jeeplate.domain.user.User;
import org.sisto.jeeplate.util.Util;

@Named @ViewScoped
public class ChangePasswordView extends AbstractView implements Serializable {
    
    @Inject 
    private User user;
    
    @Inject
    Util util;
    
    @Inject 
    UserFlows registrationFlow;
    
    private String username;
    private String password;
    private String newPassword;

    @PostConstruct
    public void init() {
        this.username = this.currentUser();
        this.password = "";
        this.newPassword = "";
    }
    
    public void populateData() {
        
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
        Boolean changed = this.registrationFlow.changeUserPassword(this.password, this.newPassword);
        
        if (changed) {
            RequestContext.getCurrentInstance().execute("PF('credentialsDlg').hide()");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, util.getResourceBundleValue("view.change.password.error.generic.change"));
        }
    }
}
