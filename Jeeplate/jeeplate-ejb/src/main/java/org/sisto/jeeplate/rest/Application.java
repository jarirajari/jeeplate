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
package org.sisto.jeeplate.rest;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.sisto.jeeplate.application.Configuration;

@Path("restricted")
public class Application {
    
    @Inject
    private Configuration application;
    
    
    /*
    
    Initiate app through this public-key secured URL:
    1. create domain space
    2. create root user (ONLY SYSTEM ROLES)
    3. if both domain space and root user are created => initialize application!
    
    For example: root user = "panda" and password = "bear"
    initialize/user?username=panda&password"bear"
    
    */
    
    @GET @Path("initialize/{pathParameter}")
    public Response responseMsg(@PathParam("pathParameter") String pathParameter,
            @DefaultValue("Nothing to say") @QueryParam("queryParameter") String queryParameter) {
        final Boolean conf = this.application.configurationExists();
        final String response = String.format("Hello from: %s %s. Conf=%s", pathParameter, queryParameter, String.valueOf(conf));

        
        return (Response.status(200).entity(response).build());
    }
    
    // create the first user (root) here! => system user can have only one sys-role
    // and no app-roles (i.e. empty) set of them! => normal users have one sys-role
    // and 1-to-N many app-roles
    
    // all users are first created with sys-role => "registered-user" and app
    // users are such until they UNREGISTER!
}
