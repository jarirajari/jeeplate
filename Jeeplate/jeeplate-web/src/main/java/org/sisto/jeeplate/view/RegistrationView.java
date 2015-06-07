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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.Randomness;

@Named
@ViewScoped
public class RegistrationView extends AbstractView implements Serializable {
    
    @Inject
    transient private StringLogger log;
    @Inject
    transient private Email emailSender;
    @Inject
    transient private UserData user;
    @Inject
    transient private Randomness random;
    
    private String email;
    private String mobile;
    private String username;
    private String password;
    private String domaincode; // used also for emaild address validation
    private String actionsecret;
    private Boolean iacceptTermsAndConditions;
    private Boolean registered;
    private Boolean flowing;
    
    @PostConstruct
    public void init() {
        this.email = "";
        this.mobile = "";
        this.username = "";
        this.password = "";
        this.domaincode = "";
        this.actionsecret = "";
        this.iacceptTermsAndConditions = Boolean.FALSE;
        this.registered = Boolean.FALSE;
        this.flowing = Boolean.FALSE;
    }
    
    private void resetAllFieldValues() {
        this.init();
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
        this.mobile = String.format("+%s", mobile.replaceAll("\\D+",""));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomaincode() {
        return domaincode;
    }

    public void setDomaincode(String domaincode) {
        this.domaincode = domaincode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActionsecret() {
        return actionsecret;
    }

    public void setActionsecret(String actionsecret) {
        this.actionsecret = actionsecret;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }
    
    public Boolean getIacceptTermsAndConditions() {
        return iacceptTermsAndConditions;
    }

    public void setIacceptTermsAndConditions(Boolean accept) {
        if(accept) {
            this.newActionsecret();
        }
        this.iacceptTermsAndConditions = accept;
    }
    
    public void newActionsecret() {
        if (! this.flowing) {
            if (actionsecret.isEmpty()) {
                this.actionsecret = this.random.generateRandomString(8);
            }
            this.flowing = true;
        } else {
            this.flowing = false;
        }
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
    
    public void requestAccountCreatePhase() {
        
    }
    
    public void completeAccountCreatePhase() {
        
    }
    
    // PrimeFaces use inherently wrong design for Wizards
    public String onFlowProcess(FlowEvent event) {
        final String flowName = "signupWzd";
        String next = null;
        String step = event.getNewStep();
        String pets = event.getOldStep();
        boolean forward = WizardHelper.isForward(pets, step);
        boolean first = WizardHelper.isFirstPhase(step);
        boolean last = WizardHelper.isLastPhase(step);
        String start = WizardHelper.firstPhase(flowName);
        String end = WizardHelper.lastPhase(flowName);
        int phase = WizardHelper.extractPhase(step);
        RequestContext ctx = RequestContext.getCurrentInstance();
        
        log.info("Sign up: step=%s, phase=%s", step, ""+phase);
        if (forward) {
            switch (phase) {
                case 0:
                    // Client: User enters email, number, and checks Captcha
                    // Server: Creates action secret
                    next = step;
                    break;
                case 1:
                    // Client: User enters new password, retypes it, and inputs emailed reset secret;
                    //         action secret is also needed in a
                    // Server: Sends email 1) No account or 2) Reset request with temp password
                    //         Creates reset token and timestamp
                    this.requestAccountCreatePhase();
                    next = step;
                    break;
                case 2:
                    // Client: Valid reset token together with emailed reset secret (i.e. temp password)
                    //         changes the password if the action secret matches too, otherwise no change
                    // Server: 
                    this.completeAccountCreatePhase();
                    next = step;
                    break;
                case 3:
                    // Client: Two factor authentication (2FA) would be here...
                    //         This would meaning issuing Request-Response challenge vie mobile channel
                    // Server:
                    next = step;
                    break;
                default:
                    ctx.execute("PF('signupWzd').hide()");
                    this.resetAllFieldValues();
                    next = start;
                    break;
            }
        } else {
            next = step;
        }
        
        return next;
    }
}
