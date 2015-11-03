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
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.sisto.jeeplate.domain.user.UserData;
import org.sisto.jeeplate.domain.user.UserEntity;
import org.sisto.jeeplate.domain.user.account.UserAccountEntity;
import org.sisto.jeeplate.localisation.LanguageLocalisation;
import org.sisto.jeeplate.util.Util;

@Named @ViewScoped
public class ChangeLocalisationView extends AbstractView implements Serializable {
    private final Map<String,String> timezones = new LinkedHashMap<>();         // Almost all
    private final Map<String,String> countries = new LinkedHashMap<>();         // ISO 3166-2
    private final Map<String,String> languages = new LinkedHashMap<>();         // ISO 639-2
    private final Map<String,String> systemLanguages = new LinkedHashMap<>();   // ISO 639-2
    private final String[] supportedLanguages = {"fi", "en"};                   // ISO 639-2
    
    private String language;
    private String country;
    private String city;
    private String timezone;
    
    @Inject
    UserData user;
    
    @Inject
    LanguageLocalisation loc;
    
    @Inject
    Util util;
    
    @PostConstruct
    public void init() {
        this.populateTimezones();
        this.populateCountries();
        this.populateLanguages();
        this.populateSupported();
        
        this.populateData();
    }
    
    public void populateData() { 
        // the pattern is always the same: first data is read and populated, 
        // everything else comes after this...
        
        UserAccountEntity ue = this.user().getOneAccount();
        this.setLanguage(ue.getLang());
        this.setCountry(ue.getCountry());
        this.setCity(ue.getCity());
        this.setTimezone(ue.getTimezone());
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
    
    private UserEntity user() {
        UserEntity ue;
        user.findLoggedInUser(this.currentUser());
        ue = user.getDataModel();
        
        return ue;
    }
    
    public void change() {
        final Locale newLoc = new Locale(this.language, this.country);
        Boolean noLocaleAvailable = ! localeIsAvailable(newLoc);
        Boolean changed;
        
        user();
        changed = this.user.updateUserAccountLocalisation(language, country, city, timezone);
        if (noLocaleAvailable) {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, util.getResourceBundleValue("view.change.localization.error.specific.change"));
        } else if (changed) {
            RequestContext.getCurrentInstance().execute("PF('localisationDlg').hide()");
        } else {
            this.showFacesMessage(FacesMessage.SEVERITY_ERROR, util.getResourceBundleValue("view.change.localization.error.generic.change"));
        }
    }
    
    private static Boolean localeIsAvailable(Locale test) {
        Locale list[] = DateFormat.getAvailableLocales();
        Boolean available = Boolean.FALSE;
        
        for (Locale l : list) {
            if (l.equals(test)) {
                available = Boolean.TRUE;
                break;
            }
        }
        
        return available;
    }
    
    private void populateSupported() { // refactor away, not needed in this view
        for (String lang : supportedLanguages) {
            Locale systemLocale = new Locale(lang);
            this.systemLanguages.put(systemLocale.getDisplayCountry(), lang);
        }
    }
    
    private void populateTimezones() {
        String[] allTimezones = TimeZone.getAvailableIDs();
        
        this.timezones.put("UTC", "UTC");
        for (String timezone : allTimezones) {
            if (timezone.startsWith("SystemV/")) {
                continue;
            } else if (timezone.startsWith("Etc/")) {
                continue;
            } else if (timezone.contains("/")) {
                this.timezones.put(timezone, timezone);
            } else {
                continue;
            }
        }
    }
    
    private void populateCountries() {
        String[] allCountries = Locale.getISOCountries();
        for (String countryCode : allCountries) {
            Locale l = new Locale("", countryCode);
            this.countries.put(l.getCountry(), countryCode);
        }
    }
    
    private void populateLanguages() {
        String[] allLanguages = Locale.getISOLanguages();
        for (String languageCode : allLanguages) {
            Locale l = new Locale(languageCode);
            this.languages.put(l.getLanguage(), languageCode);
        }
    }
    
    public Map<String,String> localizedCountries() {
        Map<String,String> localized = new TreeMap<>();
        Locale viewLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        
        this.countries.keySet().stream().forEach((key) -> {
            String val = (String) this.countries.get(key);
            Locale loc = new Locale("", val);
            String display = loc.getDisplayCountry(viewLocale);
            if (display.length() > 2) {
                localized.put(display, key);
            }
        });
        
        return localized;
    }
        
    public Map<String,String> localizedLanguages() {
        Map<String,String> localized = new TreeMap<>();
        Locale viewLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        
        this.languages.keySet().stream().forEach((key) -> {
            String val = (String) this.languages.get(key);
            Locale loc = new Locale(val);
            String display = loc.getDisplayLanguage(viewLocale);
            if (display.length() > 0) {
                localized.put(display, key);
            }
        });
        
        return localized;
    }
    
    public Map<String,String> localizedTimezones() {
        // no actual localization available
        return (this.timezones);
    }
}