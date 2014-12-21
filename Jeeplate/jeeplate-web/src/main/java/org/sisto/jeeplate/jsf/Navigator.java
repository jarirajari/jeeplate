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

import javax.faces.bean.ApplicationScoped;

@ApplicationScoped
public class Navigator {
    public enum Target {
        EMPTY(""),
        HOME("index.xhtml"),
        SIGNIN("signin.xhtml"),
        SIGNOUT("signout.xhtml"),
        SIGNAS("signas.xhtml"),
        SIGNUP("signup.xhtml"),;
        
        private String page;
        
        private Target(String targetPage) {
            this.page = targetPage;
        }
        
        public String page() {
            return ("/".concat(page));
        }
        
        public String view() {
            String stripped = this.stripXHTML();
            
            return stripped;
        }
        
        private String stripXHTML() {
            int start = 0;
            int end = this.page.indexOf(".xhtml");
            
            return (this.page.substring(start, end));
        }
    }
}
