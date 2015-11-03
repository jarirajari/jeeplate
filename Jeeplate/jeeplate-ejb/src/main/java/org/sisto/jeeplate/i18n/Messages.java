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
package org.sisto.jeeplate.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.ejb.Stateful;

@Stateful
public class Messages extends ResourceBundle {
    
    protected static final String BUNDLE_NAME = "org.sisto.jeeplate.i18n.messages";
    protected static final String BUNDLE_EXTENSION = "properties";
    protected static final ResourceBundle.Control UTF8_CONTROL = new UTF8Control();

    public Messages() {
        setParent(ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), UTF8_CONTROL));

    }
    
    public void switchLanguage(Locale loc) {
        ResourceBundle.clearCache();
        setParent(ResourceBundle.getBundle(BUNDLE_NAME, loc, UTF8_CONTROL));
    }

    public String getLocalizedString(String key) {
        String val;
        
        try {
            val = this.getString(key);
        } catch (MissingResourceException mre) {
            val = key;
        }
        
        return val;
    }
    
    public String getLocalizedString(String key, Object... params) {
        String val;
        
        try {
            val = MessageFormat.format(this.getString(key), params);
        } catch (MissingResourceException mre) {
            val = key;
        }
        
        return val;
    }
    
    @Override
    protected Object handleGetObject(String key) {
        return parent.getObject(key);
    }

    @Override
    public Enumeration getKeys() {
        return parent.getKeys();
    }

    protected static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle
            (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException
        {
            // The below code is copied from default Control#newBundle() implementation.
            // Only the PropertyResourceBundle line is changed to read the file as UTF-8.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, BUNDLE_EXTENSION);
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
