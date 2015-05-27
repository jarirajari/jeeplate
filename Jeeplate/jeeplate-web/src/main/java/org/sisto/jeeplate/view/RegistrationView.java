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

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.sisto.jeeplate.domain.user.User;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.Email;

@Named
@ViewScoped
public class RegistrationView implements Serializable {
    
    @Inject
    transient private StringLogger log;
    @Inject
    transient private Email emailSender;
    @Inject
    transient private UserData user;
    
    private String email;
    private String mobile;
    private String username;
    private String password;
    private String securitycode;
    private Boolean iacceptTermsAndConditions;
    private String hidden;

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
    
    public Boolean getIacceptTermsAndConditions() {
        return iacceptTermsAndConditions;
    }

    public void setIacceptTermsAndConditions(Boolean iacceptTermsAndConditions) {
        this.iacceptTermsAndConditions = iacceptTermsAndConditions;
    }
    
    @PostConstruct
    public void init() {
        this.iacceptTermsAndConditions = Boolean.FALSE;
    }
    
    private void resetAllFieldValues() {
        
    }
    
    public boolean accountForEmailExists(String enteredUserEmailAddress) {
        log.info("accountForEmailExists="+enteredUserEmailAddress);
        
        boolean exists = user.noUserWithEmail(enteredUserEmailAddress);
        
        if (exists) {
            MimeMessage m = emailSender.constructEmail("Greeting from Jeeplate!", "Account already exists!", 
                                    "jee@pla.te", enteredUserEmailAddress);
            emailSender.sendMessage(m);
        }
        
        return exists;
    }
    
    public String onFlowProcess(FlowEvent event) {
        final String START = "signupWzdStart";
        final String ACCOUNT = "signupWzdAccount";
        final String STOP = "signupWzdStop";
        
        if (event == null) return null;
        
        String step = event.getNewStep();
        RequestContext ctx = RequestContext.getCurrentInstance();
        
        if (step.equals(STOP)) {
            resetAllFieldValues();
            ctx.execute("PF('signupWzd').hide()");
            return START;
        } else {
            
        }
        return event.getNewStep();
    }
}
