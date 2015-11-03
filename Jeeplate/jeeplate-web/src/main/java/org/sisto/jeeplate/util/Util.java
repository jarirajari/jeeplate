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
package org.sisto.jeeplate.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named
public class Util implements Serializable {
    
    // How to build a Faces message with severity type
    void showFacesMessage(FacesMessage.Severity type, String text) {
        FacesMessage msg = new FacesMessage(text);
        FacesContext ctx = FacesContext.getCurrentInstance();
        
        msg.setSeverity(type);
        ctx.addMessage(null, msg);
    }
    
    // How to find HTTP Request param value by key
    String findRequestStringParamValue(String key) {
        Map<String,String> params = FacesContext.getCurrentInstance()
                                    .getExternalContext().getRequestParameterMap();
	String val = params.get(key);
        
        return val;
    }
    
    // Example of how to use ajax listener
    public void emailedsecretChanged(AjaxBehaviorEvent event) {
        String example = (String) ((UIOutput)event.getSource()).getValue();
    }
    
    // Get resource bundle value by key, also ...i18n.Messages.java
    public String getResourceBundleValue(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        Locale locale = context.getViewRoot().getLocale();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ResourceBundle bundle = ResourceBundle.getBundle("org.sisto.jeeplate.i18n.messages", locale, loader);
        String val;
        ResourceBundle.clearCache();
        try {
            val = bundle.getString(key);
        } catch (MissingResourceException | NullPointerException e) {
            val = String.format("%s: %s", locale.getLanguage(), key);
        }
        
        return val;
    }
    
    // Example for page refresh
    public void refreshPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String refreshpage = fc.getViewRoot().getViewId();
        ViewHandler ViewH = fc.getApplication().getViewHandler();
        UIViewRoot UIV = ViewH.createView(fc, refreshpage);
        UIV.setViewId(refreshpage);
        fc.setViewRoot(UIV);
    }

    // Setting cookie value: >0 expire_seconds, <0 not stored, =0 deletes it
    public void setCookie(String name, String val, int expire_seconds) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        Cookie cookie = null;
        Cookie[] userCookies = request.getCookies();
        String value;
        
        if (userCookies != null && userCookies.length > 0) {
            for (Cookie userCookie : userCookies) {
                if (userCookie.getName().equals(name)) {
                    cookie = userCookie;
                    break;
                }
            }
        }
        
        try {
            value = URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException | NullPointerException ex) {
            value = "";
        }
        if (cookie != null) {
            cookie.setValue(value);
            cookie.setMaxAge(expire_seconds);
            cookie.setPath("/");
        } else {
            cookie = new Cookie(name, value);
            cookie.setPath(request.getContextPath());
            cookie.setMaxAge(expire_seconds);
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }

    // Getting cookie value (getRequestCookieMap?)
    public String getCookie(String name) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        HttpServletResponse response;
        Cookie cookie = null;
        Cookie[] userCookies = request.getCookies();
        String value;
        
        if (userCookies != null && userCookies.length > 0) {
            for (Cookie userCookie : userCookies) {
                if (userCookie.getName().equals(name)) {
                    cookie = userCookie;
                    break;
                }
            }
        }
        try {
            value = URLDecoder.decode(cookie.getValue(), "UTF-8");
        } catch (UnsupportedEncodingException | NullPointerException ex) {
            value = "";
        }
        
        return value;
    }

}
