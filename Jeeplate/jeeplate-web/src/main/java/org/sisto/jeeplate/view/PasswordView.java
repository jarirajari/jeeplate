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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.Application;
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
public class PasswordView implements Serializable {
    
    @Inject
    transient private StringLogger log;
    @Inject
    transient private Email emailSender;
    @Inject
    transient private UserData user;
    @Inject
    transient private Randomness random;
    
    private boolean flowing = false;
    private String email;
    private String mobile;
    private String username;
    private String password;
    private String actionsecret; // hidden from user (alternative to reset URL)
    private String emailedsecret;
    private Boolean iamhuman;
    
    @PostConstruct
    public void init() {
        this.flowing = false;
        this.email = "";
        this.mobile = "";
        this.username = "";
        this.password = "";
        this.emailedsecret = "";
        this.actionsecret = "";
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

    public String getActionsecret() {
        return actionsecret;
    }

    public void setActionsecret(String actionsecret) {
        this.actionsecret = actionsecret;
    }
    
    public Boolean getIamhuman() {
        return iamhuman;
    }

    public void setIamhuman(Boolean iamhuman) {
        if(iamhuman) {
            this.newActionsecret();
        }
        this.iamhuman = iamhuman;
    }

    public String getEmailedsecret() {
        return emailedsecret;
    }

    public void setEmailedsecret(String emailedsecret) {
        this.emailedsecret = emailedsecret;
    }
    
    public void newActionsecret() {
        if (! this.flowing) {
            if (actionsecret.isEmpty()) {
                this.actionsecret = this.generateRandomString(8);
            }
            this.flowing = true;
        } else {
            this.flowing = false;
        }
    }
    
    private String generateRandomString(int length) {
        String rnd = this.random.generateRandomString(length);
                
        return rnd;
    }
    
    public void save() {        
        
    }
    
    private void resetAllFieldValues() {
        this.init();
    }
    
    private void showFacesMessage(FacesMessage.Severity type, String text) {
        FacesMessage msg = new FacesMessage(type.toString(), text);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    private String getResourceBundleValue(String key) {
        FacesContext fc = FacesContext.getCurrentInstance();
        String msgBundleName = fc.getApplication().getMessageBundle();
        Locale loc = fc.getViewRoot().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(msgBundleName, loc);
        
        return bundle.getString(key);
    }
    
    private String findRequestStringParamValue(String key) {
        Map<String,String> params = FacesContext.getCurrentInstance()
                                    .getExternalContext().getRequestParameterMap();
	String val = params.get(key);
        
        return val;
    }
    
    private void findUserAccount() {
        this.user = user.findOneUser(this.email);
        log.info("Lost password recovery for user %s (%s)", this.email.toString(), String.valueOf(this.user.getEntity().getId()));
    }
    
    public void requestPasswordResetPhase() {
        EmailMessage newUserMsg = new EmailMessage("Requested pw reset NEW", "secret is ", "Jari K.", "Jeeplate corp.");
        EmailMessage oldUserMsg = new EmailMessage("Requested pw reset OLD", "did you do this, if yes ${secret}", "Jari K.", "Jeeplate corp."); 
        log.info("rpp -> "+this.user.getEntity().getId());
        this.user.initializePasswordReset(oldUserMsg, newUserMsg);
    }
    
    private boolean passwordResetCanBeCompleted(String hiddenActionSecretGenerated) {
        String hiddenActionSecretCarried = findRequestStringParamValue("resetForm:resetWzdHidden");
        boolean secretsAreSame = hiddenActionSecretGenerated.equals(hiddenActionSecretCarried);
        boolean resetInitialized = this.flowing;
        
        return (secretsAreSame && resetInitialized);
    }
    
    public void completePasswordResetPhase() {
        String typedPassword = this.getPassword();
        String emailedResetToken = this.getEmailedsecret();
        String hiddenActionSecretGenerated = this.getActionsecret();
        Boolean completed = Boolean.FALSE;
        log.info("cpr -> "+this.user.getEntity().getId());
        if (passwordResetCanBeCompleted(hiddenActionSecretGenerated)) {       
            completed = this.user.completePasswordReset(typedPassword, emailedResetToken, hiddenActionSecretGenerated);
        }
        if (completed) {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "OK, changed password");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, "Failed, not changing password");
        }
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
        
        log.info("step=%s, phase=%s", step, ""+phase);
        if (forward) {
            if (first) {
                log.info("do some reset inits here");
                next = step;
            } else if (last) {
                this.resetAllFieldValues();
                ctx.update("@form");
                ctx.execute("PF('resetWzd').hide()");              
                next = start;
            } else {
                switch (phase) {
                    case 0:
                        // Client: User enters email, number, and checks Captcha
                        // Server: Creates action secret
                        break;
                    case 1:
                        // Client: User enters new password, retypes it, and inputs emailed reset secret;
                        //         action secret is also needed in a
                        // Server: Sends email 1) No account or 2) Reset request with temp password
                        //         Creates reset token and timestamp
                        this.findUserAccount();
                        this.requestPasswordResetPhase();
                        break;
                    case 2:
                        // Client: Valid reset token together with emailed reset secret (i.e. temp password)
                        //         changes the password if the action secret matches too, otherwise no change
                        // Server: 
                        this.completePasswordResetPhase();
                        break;
                    case 3:
                        // Client: Two factor authentication (2FA) would be here...
                        //         This would meaning issuing Request-Response challenge vie mobile channel
                        // Server:
                        break;
                    default:
                        break;
                }
                next = step;
            }
        } else {
            next = step;
        }
        
        return next;
    }
}
