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
import org.sisto.jeeplate.logging.StringLogger;

@Named
@ViewScoped
public class RestrictedView implements Serializable {
    
    // Grouping different model etc objects & instances, models are general
    
    @Inject
    transient private StringLogger log;
    
    private Object selectedUser;
    private Long selectedGroup;
    private String input;
    
    @PostConstruct
    private void init() {
        log.info("init"+this.toString());
    }
    
    public String getInput()
    {
        log.info("getinput"+this.toString());
        return input;
    }
 
    public void setInput(String input)
    {
        log.info("setinput"+this.toString());
        this.input = input;
    }
    
    public void setSelectedUser(Object ud) {
        if(ud!=null){
        log.info("setud"+this.toString());
        }
        this.selectedUser = ud;
        if(ud!=null){
        log.info("sel ud"+selectedUser.toString());
        }
    }
    
    public Object getSelectedUser() {
        if(this.selectedUser!=null)
            log.info("getud"+this.selectedUser.toString());
        return (this.selectedUser);
    }
    
    public void setSelectedGroup(Long ud) {
        
        this.selectedGroup = ud;
    }
    
    public Long getSelectedGroup() {
        
        return (this.selectedGroup);
    }
    
    public Boolean addToGroup() {
        log.info("RestrictedView+add -> u="+this.getSelectedUser()+"g="+selectedGroup+"; "+this.toString());
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
