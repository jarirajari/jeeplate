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
package org.sisto.jeeplate.domain.user;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped @Stateful
public class User implements Serializable {
    
    UserData data;
    UserRule rules;
    UserLogic logic;
    
    @Inject 
    public void setRule(UserRule rules) {
        this.rules = rules;
    }

    public UserData getData() {
        return data;
    }

    @Inject 
    public void setData(UserData data) {
        this.data = data;
    }

    public UserLogic getLogic() {
        return logic;
    }

    @Inject 
    public void setLogic(UserLogic logic) {
        this.logic = logic;
    }
}
