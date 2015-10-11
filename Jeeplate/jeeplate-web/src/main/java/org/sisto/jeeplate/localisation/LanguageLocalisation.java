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
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.Util;

@SessionScoped @Named("language")
public class LanguageLocalisation implements Serializable {
    private transient final String DEFAULT_LANG = "en";
    private transient final Locale USA       = new Locale("en_US"); // en-US
    private transient final Locale ENGLISH   = new Locale("en_GB"); // en-GB
    private transient final Locale FINNISH   = new Locale("fi"); // fi-FI
    
    final String LOCALE_COOKIE = "jeeplate_locale";
    
    @Inject
    StringLogger log;
    @Inject
    Util util;
    
    // Locale from Accept-Language header of users browser
    private Locale browserLocale;
    // Local from JSF view
    private Locale viewLocale;
    // Locale from server
    private Locale serverLocale;
    // Locale selected
    private String selectedLocale;
    // Languages
    private Map<String, Object> availableLocales;
    
    @PostConstruct
    public void init() {
        final String cookieLocaleString = this.util.getCookie(LOCALE_COOKIE);
        availableLocales = new LinkedHashMap<>();
        availableLocales.put(USA.toString(), "English(US)");
        availableLocales.put(ENGLISH.toString(), "English(UK)");
        availableLocales.put(FINNISH.toString(), "Suomi");
        
        browserLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        viewLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        serverLocale = (cookieLocaleString.isEmpty()) ? new Locale(DEFAULT_LANG) : new Locale(cookieLocaleString);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(serverLocale);
    }

    public String getSelectedLocale() {
        selectedLocale = this.util.getCookie(LOCALE_COOKIE);
        return selectedLocale;
    }

    public void setSelectedLocale(String selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
    
    public Map<String, Object> getAvailableLocales() {
        return availableLocales;
    }

    public Locale getCurrentLocale() {
        return (this.serverLocale);
    }
    
    public void changeLanguage(ValueChangeEvent e) {
        final String changedlocale = e.getNewValue().toString();
        this.changeLocale(changedlocale);
    }
    
    public void changeLocale(String loc) {
        Locale newLocale = new Locale(loc);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(newLocale);
        this.util.setCookie(LOCALE_COOKIE, newLocale.toString(), 365*24*60*60);
        this.serverLocale = newLocale;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(serverLocale);
        System.out.println("-->"+newLocale);
    }
    
    public String getFlagImageString() {
        String flag;
        Locale current = this.getCurrentLocale();
        String language = current.getLanguage();
        String country = current.getCountry();
        
        if (current.equals(Locale.US) || current.equals(Locale.UK)) {
            
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
}
