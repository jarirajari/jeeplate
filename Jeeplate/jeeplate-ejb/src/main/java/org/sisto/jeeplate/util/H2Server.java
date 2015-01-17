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
import org.h2.tools.Server;
import org.jboss.logging.Logger;
/**
 * To access an in-memory database from another process or from another computer, 
 * you need to start a TCP server in the same process as the in-memory database 
 * was created. The other processes then need to access the database over TCP/IP 
 * or TLS, using a database URL such as: jdbc:h2:tcp://localhost/mem:db1
 */
@Singleton
@Startup
public class H2Server {
    
    private static Logger log = Logger.getLogger(H2Server.class.getName());
    private Server h2tcp;
    
    @PostConstruct
    public void init() {
        final String tcpConfig = "-tcpAllowOthers"; // 9092
        try {
            this.h2tcp = Server.createTcpServer(tcpConfig).start();
        } catch (SQLException ex) {
            log.error("Error H2: "+ex.getMessage());
        }
    }
    
    @PreDestroy
    public void lize() {
        try {
            this.h2tcp.stop();
        } catch (NullPointerException ex) {
            log.error("Error H2: "+ex.getMessage());
        }
    }
}
