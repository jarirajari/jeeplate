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

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

public class Util {
    
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
    String getResourceBundleValue(String key) {
        FacesContext fc = FacesContext.getCurrentInstance();
        String msgBundleName = fc.getApplication().getMessageBundle();
        Locale loc = fc.getViewRoot().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(msgBundleName, loc);
        
        return bundle.getString(key);
    }
    
    // Example for page refresh
    void refreshPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String refreshpage = fc.getViewRoot().getViewId();
        ViewHandler ViewH = fc.getApplication().getViewHandler();
        UIViewRoot UIV = ViewH.createView(fc, refreshpage);
        UIV.setViewId(refreshpage);
        fc.setViewRoot(UIV);
    }
}
