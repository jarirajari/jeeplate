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
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sisto.jeeplate.domain.user.UserData;

@Named @ViewScoped
public class ModifyAccountView extends AbstractView implements Serializable {

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
    // other
    
    @Inject 
    private UserData user;

    @PostConstruct
    public void init() {
        
    }
}
