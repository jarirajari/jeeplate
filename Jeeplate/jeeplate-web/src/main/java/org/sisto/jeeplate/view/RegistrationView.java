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
    
    public void beginUserRegistrationPhase() {
        
    }
    
    public void endUserRegistrationPhase() {
        
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
        /* 
         * Note that email will be sent always to validate the address!
         * Also password reset must be possible! And guessing domain impossible!
         * Domain code is 16 digits long string: 1234-5566-7788-9900
         *
         * 1.user regs as app user, 1 domains => domain code is sent to user via email 
         *   and the code is same for all. Users do not know code beforehand, and email 
         *   delivery is enough since only same domain code! "default domain"
         * 2.user regs as app user, N domains => domain code is sent to user via email 
         *   and the code is unique for each (app) domain. 
         *   Special case so that 1+(N-1) domains => N domains meaning that first domain 
         *   is "default" which in turn means that in email "user is asked to input alt-
         *   ernative domain code" that is common and distributed via other channels. 
         *   For example, company might put this info to their Intranet site!
         * 3.user regs as sys user, 1 domains => domain code is sent to user via email. 
         *   However, this is not entered to "domain code" field but rather the user 
         *   is put to the "sys users" group if side-channel step is ok: basically 
         *   this means that the emailed domain code is the Challenge in Request-
         *   Response challenge. User puts the domain code into his/hers mobile device 
         *   (number from the 'mobile' field) and gets "translated" security code. 
         *   If this security code is same as one of the system codes the user is added 
         *   to a group. Note that "system" code is similar but not same as "domain"!
         * 4.user regs as sys user, M domains => same instructions as in the prev 
         *   since there is _always_exactly_one_ system group that holds admins...
         */
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
                    this.beginUserRegistrationPhase();
                    next = step;
                    break;
                case 2:
                    // Client: Valid reset token together with emailed reset secret (i.e. temp password)
                    //         changes the password if the action secret matches too, otherwise no change
                    // Server: 
                    this.endUserRegistrationPhase();
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
