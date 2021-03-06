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
package org.sisto.jeeplate.domain.user.group;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import org.sisto.jeeplate.logging.StringLogger;

@Named @RequestScoped
public class UserGroupModel {
    
    @Inject @Default
    private StringLogger log;
    
    @Inject
    private UserGroupData backing;
    private Map<Long, UserGroupData> all;
    
    @PostConstruct
    private void init() {
        this.all = this.backing.findAll();
    }
    
    public Map<Long, UserGroupData> allGroups() {
        return (this.backing.findAll());
    } 
}
