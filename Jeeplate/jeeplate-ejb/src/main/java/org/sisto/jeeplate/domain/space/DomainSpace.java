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
package org.sisto.jeeplate.domain.space;

import java.io.Serializable;
import javax.enterprise.context.Dependent;

@Dependent
public class DomainSpace implements Serializable {
    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    Domain space is not initially created!
    
    As "root" user logs in and creates the system =>
    the domain space is created (if not exists) too.
    
    This means that the system can be created from nothing,
    and still operate, but we NOW HAVE a creation button event from the root!
    
    This API URL will be protected with public key authentication!
    
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    */
}
