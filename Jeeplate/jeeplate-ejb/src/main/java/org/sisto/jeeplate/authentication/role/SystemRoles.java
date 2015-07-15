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
package org.sisto.jeeplate.authentication.role;

public class SystemRoles {
    /*
     * System means all domains,
     * One domain is many groups,
     * One group is many single users
     */
    public enum Role {
        SYSTEM_ADMIN("root"),
        DOMAIN_ADMIN("domain-admin"),
        GROUP_ADMIN("group-admin"),
        NOT_ADMIN("registered-user"),
        UNKNOWN("unknown");
        
        private String role;
        
        Role(String s) {
            this.role = s;
        }
    }
}
