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
package org.sisto.jeeplate.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped; // Do NOT confuse with  @javax.faces.bean.ViewScoped
import org.sisto.jeeplate.application.Configuration;
import org.sisto.jeeplate.domain.user.group.membership.UserGroupMembershipData;
import org.sisto.jeeplate.logging.StringLogger;

@Named @ViewScoped
public class RestrictedView implements Serializable {
    
    // Grouping different model etc objects & instances, models are general
    
    @Inject
    transient private StringLogger log;
    @Inject
    private UserGroupMembershipData membership;
    @Inject
    private Configuration appConf;
    private Long selectedUser;
    private Long selectedGroup;
    private String input;
    
    @PostConstruct
    private void init() {
        this.selectedUser  = 0L;
        this.selectedGroup = 0L;
    }
    
    public String getInput() {
        return input;
    }
 
    public void setInput(String input) {
        if (input != null) {
            this.input = input;
        }
    }
    
    public Long getSelectedUser() {
        
        return (this.selectedUser);
    }
    
    public void setSelectedUser(Long user) {
        if (user != null){
            this.selectedUser = user;
        }
    }
    
    public void setSelectedGroup(Long ud) {
        if (ud != null) {
            this.selectedGroup = ud;
        }
    }
    
    public Long getSelectedGroup() {
        
        return (this.selectedGroup);
    }
    
    public Boolean addToGroup() {
        if (this.selectedUser != null && this.selectedGroup != null) {
            log.info("RestrictedView+UserGroupController -> add =>> u="+this.getSelectedUser()+"g="+this.getSelectedGroup()+"; "+this.toString());
            membership.addNewMember(selectedUser, selectedGroup);
        }
        return Boolean.FALSE;
    }
    
    public Boolean showConfigureApplication() {
        return (! this.appConf.configurationExists());
    }
    
    public Boolean configureApplication() {
        this.appConf.configureIdempotent("root@us.er", "aaAA11!!","333222111");
        
        return Boolean.FALSE;
    }
    
    public void debug_HTTP_POST() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                                 .getExternalContext().getRequest();
        Map<String, String[]> params =  req.getParameterMap();
        Set<String> keys = params.keySet();
        
        log.info("HTTP_POST:");
        for (String key : keys) {
            String[] val = params.get(key);
            
            log.info("%s = %s", key, Arrays.toString(val));
        }
    } 
}
