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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
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
import org.sisto.jeeplate.domain.base.DomainData;
import org.sisto.jeeplate.domain.group.DomainGroupData;
import org.sisto.jeeplate.domain.group.membership.DomainGroupMembershipData;
import org.sisto.jeeplate.domain.space.DomainSpaceData;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.MultiValidator;

@Named @ViewScoped
public class RestrictedView extends AbstractView implements Serializable {
    
    // Grouping different model etc objects & instances, models are general
    
    @Inject
    StringLogger log;
    @Inject
    Configuration appConf;
    @Inject
    MultiValidator validator;
    @Inject
    DomainSpaceData space;
    @Inject
    DomainData domain;
    @Inject
    DomainGroupData group;
    @Inject
    DomainGroupMembershipData membership;
    Long selectedUser;
    Long selectedGroup;
    String IOInput;
    String IODomain;
    
    @PostConstruct
    public void init() {
        this.selectedUser  = 0L;
        this.selectedGroup = 0L;
        this.IOInput = "";
        this.IODomain = "";
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
    
    public String getIOInput() {
        return (this.IOInput);
    }
 
    public void setIOInput(String newInput) {
        if (newInput != null) {
            this.IOInput = newInput;
        } else {
            this.IOInput = "";
        }
    }

    public String getIODomain() {
        return (this.IODomain);
    }

    public void setIODomain(String newdomain) {
        
        if (this.validator.validateURL(newdomain)) {
            this.IODomain = this.findDomain(newdomain);
        } else {
            this.IODomain = "";
        }
    }
    
    public void createNewDomain() {
        final String newDomainFQDN = this.getIODomain();
        space.insertNewDomain(newDomainFQDN);
        log.info("domain="+newDomainFQDN+", valid="+validator.validateFQDN(newDomainFQDN));
    }
    
    public Boolean addToGroup() {
        if (this.selectedUser != null && this.selectedGroup != null) {
            log.info("RestrictedView+UserGroupController -> add =>> u="+this.getSelectedUser()+"g="+this.getSelectedGroup()+"; "+this.toString());
            // old: membership.addNewMember(selectedUser, selectedGroup);
        }
        return Boolean.FALSE;
    }
    
    public Boolean showConfigureApplication() {
        return (! this.appConf.configurationExists());
    }
    
    public Boolean configureApplication() {
        // TODO
        this.appConf.configureIdempotent("root@us.er", "aaAA11!!","333222111");
        
        return Boolean.FALSE;
    }
    
    private String findDomain(String hostname) {
        URL url;
        String domain;
        
        try {
            url= new URL("https", hostname, "");
        } catch (MalformedURLException murle) {
            url = null;
        }
        if (url != null) {
            try {
                final URI res = url.toURI();
                final String host = res.getHost();
                domain = host.startsWith("www.") ? host.substring(4) : host;
            } catch (URISyntaxException urise) {
                domain = "";
            }
        } else {
            domain = "";
        }
        
        return domain;
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
