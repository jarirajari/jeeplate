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
package org.sisto.jeeplate.jsf;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.commons.lang3.ArrayUtils;
import org.primefaces.context.RequestContext;

@Named
public class Oracle {
    
    private String saysWho = "";
    private Map<String,String> timezones = new LinkedHashMap<>(); // Almost all
    private Map<String,String> countries = new LinkedHashMap<>(); // ISO 3166-2
    private Map<String,String> languages = new LinkedHashMap<>(); // ISO 639-2
    private Map<String,String> systemLanguages = new LinkedHashMap<>(); // ISO 639-2
    private final String[] supportedLanguages = {"fi", "en"}; // ISO 639-2
            
    @PostConstruct
    public void init() {
        this.populateTimezones();
        this.populateCountries();
        this.populateLanguages();
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
    
    public String getSaysWho() {
        return "says universal Oracle!";
    }
    
    public void open() {
        xpenDialog(Navigator.Target.SIGNIN);
    }

    public void close() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    
    public void noop(){}
    
    public void xpenDialog(Navigator.Target target) {
        Map<String, Object> options = new TreeMap<>();
        options.put("modal", false);
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("closable", true);

        RequestContext.getCurrentInstance().openDialog(target.view(), options, null);
        
    }
    
    public Map<String,String> getTimezones() {
        return (this.timezones);
    }
    
    public Map<String,String> getCountries() {
        return (this.countries);
    }
    
    public Map<String,String> getLanguages() {
        return (this.languages);
    }
    
    public Map<String,String> getSystemLanguages() {
        return (this.systemLanguages);
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
}
