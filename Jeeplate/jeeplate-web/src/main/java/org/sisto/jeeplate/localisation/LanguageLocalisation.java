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
package org.sisto.jeeplate.localisation;

import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.sisto.jeeplate.logging.StringLogger;

@SessionScoped @Named("language")
public class LanguageLocalisation implements Serializable {
    private transient static final String DEFAULT_LANG = "europeanunion";
    private transient static final String USA       = "en"; // en_US
    private transient static final String ENGLISH   = "en"; // en_GB
    private transient static final String FINNISH   = "fi"; // fi_FI
    
    @Inject
    private StringLogger log;
    
    // Locale from Accept-Language header of users browser
    private Locale browserLocale;
    // Local from JSF view
    private Locale viewLocale;
    // Locale from server
    private Locale serverLocale;
    // Languages
    private static Map<String, Object> availableLanguages;
    
    @PostConstruct
    public void init() {
        availableLanguages = new LinkedHashMap<>();
        availableLanguages.put("English(US)", USA);
        availableLanguages.put("English(UK)", ENGLISH);
        availableLanguages.put("Suomi", FINNISH);
        
        browserLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        viewLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        serverLocale = Locale.getDefault();
    }
    
    public Map<String, Object> getAvailableLanguages() {
        return availableLanguages;
    }

    public Locale getCurrentLocale() {
        Locale tmp = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        
        return tmp;
    }
    
    public void changeLanguage(ValueChangeEvent e) {
        String newLang = e.getNewValue().toString();
        
        this.setLanguage(newLang);
        this.changeLocale();
    }
    
    public void changeLocale() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(this.serverLocale);

    }
    
    public String getFlagImageString() {
        String flag;
        Locale current = this.getCurrentLocale();
        String language = current.getLanguage();
        String country = current.getCountry();
        
        if (language.equalsIgnoreCase(ENGLISH)) {
            
            if (country.equalsIgnoreCase("US")) {
                flag = "us";
            } else if (country.equalsIgnoreCase("GB")) {
                flag = "gb";
            } else {
                flag = DEFAULT_LANG;
            }
        } else {
            flag = language;
        }
        
        return String.format("resources/images/flags/%s.png", flag);
    }
    
    public String getLocalisedLanguage() {
        return (this.serverLocale.getDisplayLanguage());
    }
    
    public String getLanguage() {
        return this.serverLocale.getLanguage();
    }

    public void setLanguage(String language) {
        boolean isValidLang = availableLanguages.containsValue(language);
        
        if (isValidLang) {
            String country = this.serverLocale.getCountry();
            this.serverLocale = new Locale(language, country);
        }
    }
}
