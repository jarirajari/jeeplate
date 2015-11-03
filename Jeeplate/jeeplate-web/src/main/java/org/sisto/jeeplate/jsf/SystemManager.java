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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.sisto.jeeplate.jsf;

import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.logging.StringLogger;

@Named("mgr") @RequestScoped
public class SystemManager {
    
    @Inject
    private transient StringLogger log;
    
    private String username = "";
    private String password = "";
    private boolean remember = false;
    
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
    
    public boolean authenticated() {
        return SecurityUtils.getSubject().isAuthenticated();
    }
    
    public void doLogout() {
        // shiro logout action
        // invalidate jsf session
        // redirect to home
        SecurityUtils.getSubject().logout();
        try {
            FacesContext fcontext = FacesContext.getCurrentInstance();
            ExternalContext econtext = fcontext.getExternalContext();
            HttpServletRequest hreq = (HttpServletRequest) econtext.getRequest();

            // invalidating sess
            econtext.invalidateSession();
            econtext.redirect(hreq.getContextPath() + "/");
        } catch (IOException ioe) {
        
        } 
        
    }
    
    public String sysadmin() {
        return "sysadmin";
    }

    public void submit() throws IOException {
        FacesContext fcontext = FacesContext.getCurrentInstance();
        RequestContext rcontext = RequestContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(new UsernamePasswordToken(username, password, remember));
            HttpServletRequest hreq = (HttpServletRequest)econtext.getRequest();
            SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(hreq);
            econtext.redirect(savedRequest != null ? savedRequest.getRequestUrl() : hreq.getContextPath()+"/restricted.xhtml");
        }
        catch (AuthenticationException e) {
            FacesMessage fmessage = new FacesMessage(null);
            fmessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            fcontext.addMessage("signinForm:signinResult", fmessage);
            rcontext.addCallbackParam("invalid", "true");
            log.error("Jee auth excp: "+e.getMessage());
        }
    }
    
    /* not needed anymore !!! */
    public void doRedirect() {
        try {
            FacesContext fcontext = FacesContext.getCurrentInstance();
            ExternalContext context = fcontext.getExternalContext();
            HttpSession session = (HttpSession) fcontext.getExternalContext().getSession(false);
            
            if (session == null) {

                HttpServletRequest request =  (HttpServletRequest)context.getRequest();
 
                //context.redirect(request.getContextPath());
                context.redirect(request.getContextPath()+"/testtpl.xhtml");

                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void doLogin(ActionEvent event) {
        
    }
}
