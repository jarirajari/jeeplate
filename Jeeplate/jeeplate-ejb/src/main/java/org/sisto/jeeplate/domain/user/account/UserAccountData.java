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
package org.sisto.jeeplate.domain.user.account;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import org.sisto.jeeplate.domain.BusinessBean;
import org.sisto.jeeplate.domain.EntityBuilder;
import org.sisto.jeeplate.domain.user.UserData;

@SessionScoped @Stateful
public class UserAccountData extends BusinessBean<UserAccountData, UserAccountEntity> implements Serializable {
    
    public UserAccountData() {
        super(UserAccountData.class, UserAccountEntity.class);
    }
    public UserAccountData createNewAccountFor(UserData user) {
        final UserAccountEntity newAccount = EntityBuilder.of().UserAccountEntity();
        this.setEntity(newAccount);
        this.create();
        return this;
    }
}
