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

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.sisto.jeeplate.application.Configuration;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.MultiValidator;

@RequestScoped @Path("restricted")
public class Application {
    
    @Inject
    StringLogger log;
    
    @Inject
    Configuration application;
    
    @Inject
    MultiValidator validator;
    
    /*
    
    Initiate app through this public-key secured URL:
    1. create domain space
    2. create root user (ONLY SYSTEM ROLES)
    3. if both domain space and root user are created => initialize application!
    
    For example: root user = "panda" and password = "bear" (URL decode!)
    initialize/root?username=panda@be.ar&msisdn=+111222333&password=china
    
    */
    
    // From Java docs: "Values are URL decoded"
    @GET @Path("initialize/{root}") @PKIRestricted
    public Response responseMsg(@PathParam("root") String initializeSystemCreateRootCmd,
            @DefaultValue("") @QueryParam("username") String username,
            @DefaultValue("") @QueryParam("password") String password,
            @DefaultValue("") @QueryParam("msisdn") String msisdn) {
        final Boolean confCreated = this.application.configurationExists();
        final Boolean validCMD = initializeSystemCreateRootCmd.equals("root");
        final Boolean validUN = validator.validateUserName(username);
        final Boolean validPW = validator.validateUserPassword(password);
        final Boolean validPN = validator.validateUserPhone(msisdn);
        final String response = String.format("Conf exists?=%s. un=%s pw=%s pn=%s", String.valueOf(confCreated),username,password,msisdn);

        if (confCreated) {
            log.info("Application already initialized!");
        } else if (validCMD && validUN && validPW && validPN) {
            log.info("***************** CREATING ROOT USER !!!!!!!!!!!!!!!!!!!!!!");
            
            application.configureIdempotent(username, password, msisdn.replaceAll("[+]", ""));
        } else {
            log.error("err %s, %s, %s, %s", initializeSystemCreateRootCmd, username, password, msisdn);
        }
        
        return (Response.status(200).entity(response).build());
    }
    
    // create the first user (root) here! => system user can have only one sys-role
    // and no app-roles (i.e. empty) set of them! => normal users have one sys-role
    // and 1-to-N many app-roles
    
    // all users are first created with sys-role => "registered-user" and app
    // users are such until they UNREGISTER!
}
