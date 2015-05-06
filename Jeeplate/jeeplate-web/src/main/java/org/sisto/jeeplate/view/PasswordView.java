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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.sisto.jeeplate.logging.StringLogger;

@Named
@ViewScoped
public class PasswordView implements Serializable {
    
    @Inject
    transient private StringLogger log;
    
    private String email;
    private String mobile;
    private String username;
    private String password;
    private String securitycode;
    private Boolean iamhuman;

    private String hidden;
    
    @PostConstruct
    public void init() {
        this.iamhuman = Boolean.FALSE;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getSecuritycode() {
        return securitycode;
    }

    public void setSecuritycode(String securitycode) {
        this.securitycode = securitycode;
    }
    
    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }
    
    public Boolean getIamhuman() {
        return iamhuman;
    }

    public void setIamhuman(Boolean iamhuman) {
        this.iamhuman = iamhuman;
    }
    
    public void save() {        
        FacesMessage msg = new FacesMessage("Successful", "Please login :" + this.username);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    private void resetAllFieldValues() {
        
    }
    
    public String onFlowProcess(FlowEvent event) {
        final String START = "resetWzdStart";
        final String STOP = "resetWzdStop";
        
        if (event == null) return null;
        
        String step = event.getNewStep();
        RequestContext ctx = RequestContext.getCurrentInstance();
        log.info("step="+step);
        
        if (step.equals(STOP)) {
            resetAllFieldValues();
            ctx.execute("PF('resetWzd').hide()");
            return START;
        } else {
            
        }
        return event.getNewStep();
    }
}
