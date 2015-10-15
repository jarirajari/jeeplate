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
package org.sisto.jeeplate.domain.user.account;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.domain.user.UserEntity;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_user_account")
public class UserAccountEntity extends BusinessEntity implements Serializable {
    
    @Id @SequenceGenerator(name = "account_seq", allocationSize = 1)
    @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String screenName;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String streetAddress;
    protected String postalCode;
    protected String mobileNumber;
    protected String emailAddress;
    protected String lang;
    protected String country;
    protected String city;
    protected String timezone;
    @OneToOne(mappedBy = "oneAccount")
    protected UserEntity user;
    
    public UserAccountEntity() {
        this.id = DEFAULT_ID;
        this.screenName = "";
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.streetAddress = "";
        this.postalCode = "";
        this.mobileNumber = ""; // cannot be edited
        this.emailAddress = ""; // cannot be edited
        this.lang = "en";
        this.country = "GB"; // not UK
        this.city = "";
        this.timezone = "UTC";
        this.user = null;
    }
    
    @PostLoad @PostPersist @PostUpdate
    @Override
    protected void updateParentId() {
        super.setId(this.id);
        super.setVersion(this.version);
    }

    public String getScreenName() {
        return screenName;
    }

    public UserAccountEntity setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserAccountEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public UserAccountEntity setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserAccountEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public UserAccountEntity setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public UserAccountEntity setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public UserAccountEntity setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public UserAccountEntity setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public UserAccountEntity setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserAccountEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserAccountEntity setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public UserAccountEntity setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public UserAccountEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }
    
}
