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
import javax.annotation.PostConstruct;
import javax.enterprise.inject.New;
import javax.enterprise.inject.TransientReference;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.sisto.jeeplate.domain.base.DomainData;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;
import org.sisto.jeeplate.util.Randomness;

@Named @ViewScoped
public class RegistrationView extends AbstractView implements Serializable {
    
    @Inject
    StringLogger log;
    @Inject
    Email emailSender;
    @Inject
    DomainData domain;
    @Inject
    UserData user;
    @Inject
    transient Randomness random;
    
    private String username;
    private String mobile;
    private String password;
    private String domaincode; // emailed = domain+user part, email address check
    private String actionsecret;
    private Boolean iacceptTermsAndConditions;
    private Boolean registered;
    private Boolean flowing;
    
    @PostConstruct
    public void init() {
        this.username = "";
        this.mobile = "";
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String phone) {
        this.mobile = String.format("+%s", phone.replaceAll("\\D+",""));
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
        this.domaincode = String.format("%s", domaincode.replaceAll("[-|_]",""));
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
        log.info("iacceptTermsAndConditions="+accept);
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
    
    public Boolean accountForEmailExists(String enteredUserEmailAddress) {
        log.info("accountForEmailExists="+enteredUserEmailAddress);
        
        boolean exists = user.noUserWithEmail(enteredUserEmailAddress);
        
        if (exists) {
            MimeMessage m = emailSender.constructEmail("Greeting from Jeeplate!", "Account already exists!", 
                                                       "jee@pla.te", enteredUserEmailAddress);
            emailSender.sendMessage(m);
        }
        
        return exists;
    }
    
    public Boolean checkUserAgreement() {
        Boolean userAgreement = getIacceptTermsAndConditions();
        
        if (! userAgreement) {
            this.showFacesMessage(FacesMessage.SEVERITY_WARN, "Please accept terms and conds!");
        }
        
        return userAgreement;
    }
    
    private void findUserAccount() {
        String em = this.getUsername();
        
        this.user = user.findOneUser(em);
        log.info("User registration request for user '%s'", em);
    }
    
    private void findDomain() {
        String dc = this.getDomaincode().substring(0, 8);
        
        this.domain = this.domain.findOneDomain(dc);
        log.info("User registration request for domain '%s'", dc);
    }
    
    public void beginUserRegistrationPhase() {
        final String replace = "${domain}";
        final String recipient = this.getUsername();
        EmailMessage newUserMsg = new EmailMessage("Requested user-req NEW", String.format("did you do this, if yes %s",replace), recipient, "Jeeplate corp.");
        EmailMessage oldUserMsg = new EmailMessage("Requested user-req OLD", "somebody tried to register this to our service", recipient, "Jeeplate corp."); 
        String token = "";
        
        token = this.domain.applyForUserAccount();
        this.findUserAccount();
        this.user.nofityUserForRegistration(oldUserMsg, newUserMsg, hideDomainPartFromCodeToBeEmailed(token));
    }
    
    public void endUserRegistrationPhase() {
        Boolean completed = Boolean.FALSE;
        Boolean actionSecretOK = userRegistrationCanBeCompleted(this.getActionsecret());
        
        if (actionSecretOK) {
            this.findDomain();
            completed = this.domain.grantUserAccount(this.getMobile(), this.getPassword(), this.getDomaincode());
        }
        if (completed) {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "OK, registered");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, "Failed, not registered. please try again");
        }
        this.registered = completed;
    }
    
    private String hideDomainPartFromCodeToBeEmailed(String code) {
        final int blockLength = 4;
        String[] parts = code.split(String.format("(?<=\\G.{%s})", blockLength));
        int length = parts.length;
        StringBuilder sb = new StringBuilder();
        
        for (int p=0; p<length; p++) {
            
            if (p < 2) {
                sb.append(parts[p]);
            } else {
                sb.append("____"); // 4x"_"
            }
            if (p < (length-1)) {
                sb.append("-"); // 1x"-"
            }
        }
        log.error("Add domain and fill random part '%s'", code);
        
        return (sb.toString());
    }
    
    private boolean userRegistrationCanBeCompleted(String hiddenActionSecretGenerated) {
        String hiddenActionSecretCarried = findRequestStringParamValue("signupForm:signupWzdHidden");
        boolean secretsAreSame = hiddenActionSecretGenerated.equals(hiddenActionSecretCarried);
        boolean resetInitialized = this.flowing;
        
        return (secretsAreSame && resetInitialized);
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
         * 0.default is no domains, users are separate group of all users and if
         *   they are added to a domain it means that they are actually added a 
         *   reference to a user group that is programmatically already added to
         *   a domain. Default user group (i.e. if only one domain) is called 
         *   "ALL" meaning a group of all users who are members in this _domain_!
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
         * 
         * => actually this is TOO COMPLEX design, let's just assume that first we
         *    define that there are always many domains and companys' domain codes 
         *    are distributed separately (e.g. SMS)
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
                    Boolean userAgreement = this.getIacceptTermsAndConditions();
                    if(userAgreement) {
                        next = step;
                    } else {
                        next = pets;
                    }
                    if(userAgreement) {
                        this.beginUserRegistrationPhase();
                        /*
                         * Two cases:
                         * 1. Sys type domain => Request-response challenge for [domain-part]^[user-part]
                         *    i.e. you kind of validate yourself as sys by producing signature of system
                         * 2. App type domain => Emailed code  = [domain-part]^[user-part]
                         * 
                         * User is created and registered when flow is completed!
                         * Email address gets validated
                         */
                        
                    }
                    break;
                case 2:
                    // Client: Valid reset token together with emailed reset secret (i.e. temp password)
                    //         changes the password if the action secret matches too, otherwise no change
                    // Server: 
                    if(this.flowing) {
                        this.endUserRegistrationPhase();
                    }
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
