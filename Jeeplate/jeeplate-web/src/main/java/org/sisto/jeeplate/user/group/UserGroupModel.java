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
package org.sisto.jeeplate.user.group;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sisto.jeeplate.data.UserGroupData;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.logging.StringLogger;

@Named
@ViewScoped
public class UserGroupModel {
    
    @Inject
    transient private StringLogger log;
    
    @Inject
    private UserGroupData backing;
    private Map<Long, UserGroupData> all;
    private Long selected = BusinessEntity.DEFAULT_ID;
    
    @PostConstruct
    private void init() {
        this.all = this.backing.findAllUserGroups();
    }
    
    public void setSelected(Long ud) {
        
        this.selected = ud;
    }
    
    public Long getSelected() {
        
        return (this.selected);
    }
    
    public Map<Long, UserGroupData> allGroups() {
        return (this.backing.findAllUserGroups());
    }
    
    public void addToGroup(Long user) {
        log.debug("trying to add user %s", String.valueOf(user));
    }
    
    public void removeFromGroup(Long user) {
        log.debug("trying to rem user %s", String.valueOf(user));
    }
}
