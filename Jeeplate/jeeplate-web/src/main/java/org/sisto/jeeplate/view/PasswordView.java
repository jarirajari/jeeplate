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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.New;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;
import org.sisto.jeeplate.util.Randomness;

@Named
@ViewScoped
public class PasswordView extends AbstractView implements Serializable {
    
    @Inject
    transient private StringLogger log;
    @Inject
    transient private Email emailSender;
    @Inject @New
    transient private UserData user;
    @Inject
    transient private Randomness random;
    
    private String mobile;
    private String username;
    private String password;
    private String emailedsecret;
    private String actionsecret; // hidden from user (alternative to reset URL)
    private Boolean iamhuman;
    private Boolean reseted;
    private Boolean flowing;
    
    @PostConstruct
    public void init() {
        this.mobile = "";
        this.username = "";
        this.password = "";
        this.emailedsecret = "";
        this.actionsecret = "";
        this.iamhuman = Boolean.FALSE;
        this.reseted = Boolean.FALSE;
        this.flowing = Boolean.FALSE;
    }
    
    private void resetAllFieldValues() {
        this.init();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = String.format("+%s", mobile.replaceAll("\\D+",""));
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
    
    public Boolean getIamhuman() {
        return iamhuman;
    }

    public void setIamhuman(Boolean human) {
        if(human) {
            this.newActionsecret();
        }
        this.iamhuman = human;
    }

    public Boolean getReseted() {
        return reseted;
    }

    public void setReseted(Boolean reseted) {
        this.reseted = reseted;
    }

    public String getEmailedsecret() {
        return (this.emailedsecret);
    }

    public void setEmailedsecret(String emailed) {
        this.emailedsecret = emailed;
    }
    
    public void newActionsecret() {
        if (! this.flowing) {
            if (actionsecret.isEmpty()) {
                this.actionsecret = this.random.generateRandomString(8); // 2x
            }
            this.flowing = true;
        } else {
            this.flowing = false;
        }
    }
    
    private void findUserAccount() {
        String em = this.getUsername();
        
        this.user = user.findOneUser(em);
        log.info("Lost password recovery for user '%s' (%s)", em, String.valueOf(this.user.getEntity().getId()));
    }
    
    private boolean passwordResetCanBeCompleted(String hiddenActionSecretGenerated) {
        String hiddenActionSecretCarried = findRequestStringParamValue("resetForm:resetWzdHidden");
        boolean secretsAreSame = hiddenActionSecretGenerated.equals(hiddenActionSecretCarried);
        boolean resetInitialized = this.flowing;
        
        return (secretsAreSame && resetInitialized);
    }
    
    public void beginPasswordResetPhase() {
        final String replace = "${secret}";
        final String recipient = this.getUsername();
        EmailMessage newUserMsg = new EmailMessage("Requested pw reset NEW", "secret is ", recipient, "Jeeplate corp.");
        EmailMessage oldUserMsg = new EmailMessage("Requested pw reset OLD", String.format("did you do this, if yes %s",replace), recipient, "Jeeplate corp."); 
        log.info("******************"+oldUserMsg.toString());
        this.findUserAccount();
        this.user.initializePasswordReset(oldUserMsg, newUserMsg);
    }
    
    public void endPasswordResetPhase() {
        String typedMobile = this.getMobile();
        String typedPassword = this.getPassword();
        String emailedResetToken = this.getEmailedsecret();
        String hiddenActionSecretGenerated = this.getActionsecret();
        Boolean completed = Boolean.FALSE;
        
        if (passwordResetCanBeCompleted(hiddenActionSecretGenerated)) {
            this.findUserAccount();
            completed = this.user.finalizePasswordReset(typedMobile, typedPassword, emailedResetToken, hiddenActionSecretGenerated);
        }
        if (completed) {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "OK, changed password");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, "Failed, not changing password");
        }
        this.reseted = completed;
    }
    
    // PrimeFaces use inherently wrong design for Wizards
    public String onFlowProcess(FlowEvent event) {
        final String flowName = "resetWzd";
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
        
        log.info("Account reset: step=%s, phase=%s", step, ""+phase);
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
                    this.beginPasswordResetPhase();
                    next = step;
                    break;
                case 2:
                    // Client: Valid reset token together with emailed reset secret (i.e. temp password)
                    //         changes the password if the action secret matches too, otherwise no change
                    // Server: 
                    this.endPasswordResetPhase();
                    next = step;
                    break;
                case 3:
                    // Client: Two factor authentication (2FA) would be here...
                    //         This would meaning issuing Request-Response challenge vie mobile channel
                    // Server:
                    next = step;
                    break;
                default:
                    ctx.execute("PF('resetWzd').hide()");
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
