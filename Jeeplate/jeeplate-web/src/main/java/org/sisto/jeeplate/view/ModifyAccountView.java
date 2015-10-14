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
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.domain.user.UserData;

@Named @ViewScoped
public class ModifyAccountView extends AbstractView implements Serializable {
    private String domain;
    // name
    private String screenName;
    private String firstName;
    private String middleName;
    private String lastName;
    // address
    private String streetAddress;
    private String postalCode;
    // account
    private String mobileNumber;
    private String emailAddress;
    // localisation
    private String language;
    private String country;
    private String city;
    private String timezone;
    
    @Inject 
    private UserData user;

    @PostConstruct
    public void init() {
        this.screenName = "";
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.streetAddress = "";
        this.postalCode = "";
        this.mobileNumber = "";
        this.emailAddress = "";
        this.language = "";
        this.country = "";
        this.city = "";
        this.timezone = "";
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String completeDomain(String joinCodeHash) {
        String domainThatMachesCode = "";
        
        // system must hold a map of domains key=joinCodeHash, value=domainFqdnKey
        // domainFqdnKey is used to find out the "id" of the domain that user joins
        // domainThatMachesCode = 
         
        return domainThatMachesCode;
    }
    
    public void modify() {
        System.out.println("modify!");
        this.user.findLoggedInUser(this.currentUser());
        Boolean changed = Boolean.TRUE;
        
        if (changed) {
            RequestContext.getCurrentInstance().execute("PF('accountDlg').hide()");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_INFO, "NOT OK, not modify account");
        }
    }
}
