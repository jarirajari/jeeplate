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
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sisto.jeeplate.data.UserData;
import org.sisto.jeeplate.data.UserGroupData;
import org.sisto.jeeplate.generic.Model;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.user.UserModel;

@Named
@ViewScoped
public class UserGroupModel {
    
    @Inject
    transient private StringLogger log;
    
    @Inject
    private UserGroupData backingBean;
    
    public UserGroupData getBacking() {
        return (this.backingBean);
    }
    
    public void setBackingBean(UserGroupData ugd) {
        this.backingBean = ugd;
    }
    
    public Map<Long, UserGroupData> allGroups() {
        return (this.backingBean.findAllUserGroups());
    }
    
    public void addToGroup(Long user) {
        

    }
    
    public void removeFromGroup(Long user) {
        

    }
}
