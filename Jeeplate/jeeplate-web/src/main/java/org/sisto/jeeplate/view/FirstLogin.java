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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.domain.space.DomainSpaceData;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.util.MultiValidator;

@Named
public class FirstLogin extends AbstractView implements Serializable {
    // account
    private String mobileNumber;
    private String emailAddress;
    // ask
    private String domain;
    private String firstName;
    private String lastName;
    
    @Inject
    UserData user;
    @Inject
    DomainSpaceData space;
    @Inject
    MultiValidator validator;

    @PostConstruct
    public void init() {
        this.mobileNumber = "";
        this.emailAddress = "";
        
        this.domain = "";
        this.firstName = "";
        this.lastName = "";
    }
    
    private UserEntity user() {
        UserEntity ue;
        user.findLoggedInUser(this.currentUser());
        ue = user.getDataModel();
        
        return ue;
    }
    
    public void populateData() {
        UserEntity ue = user();
        this.setMobileNumber(ue.getMobile().toString());
        this.setEmailAddress(ue.getUsername());
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public Boolean isFirstLogin() {
        Boolean yes=true;
        
        if (yes) {
            this.populateData();
            RequestContext.getCurrentInstance().update("firstForm");
        }
        System.out.println(this.user().getMobile());
        
        return yes;
    }
    
    public List<String> completeDomain(String joinCode) {
        // system must hold a map of domains key=joinCodeHash, value=domainFqdnKey
        // domainFqdnKey is used to find out the "id" of the domain that user joins
        // domainThatMachesCode = , otherwiser return empty "". Root is "."
        ArrayList<String> domainFQDN = new ArrayList<>();
        domainFQDN.add(this.space.translateSearched(joinCode));
        
        return domainFQDN;
    }
    
    public void firstLoginCompleted() {
        System.out.println("firstLoginComplete!");
        Boolean changed = Boolean.TRUE;
        
        // must also check that domain is ok!
        
        if (changed) {
            RequestContext.getCurrentInstance().execute("PF('firstDlg').hide()");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "NOT OK, not 1st account");
        }
    }
}
