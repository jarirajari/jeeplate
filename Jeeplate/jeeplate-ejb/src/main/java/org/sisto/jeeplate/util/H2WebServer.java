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
package org.sisto.jeeplate.util;

import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import org.h2.tools.Server;
import org.jboss.logging.Logger;

@Singleton
@Startup
public class H2WebServer {
    
    @Inject
    private transient Logger log;
    
    private Server h2ws;
    
    @PostConstruct
    public void init() {
        final String config = "-web";
        try {
            this.h2ws = Server.createWebServer(config).start();
            log.info(String.format("Started H2 Webserver (%s)", config));
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
    @PreDestroy
    public void lize() {
        try {
            this.h2ws.stop();
            log.info(String.format("Stopped H2 Webserver"));
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
