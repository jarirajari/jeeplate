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
package org.sisto.jeeplate.webserver.jetty;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EmbeddedWebserverTest {
    
    private static final int SERVER_PORT = 8888;
    private static final String SERVER_PATH = "/jettytest";
    private Server webserver;
    
    @Before
    public void startServer() throws Exception {
        webserver = new Server(SERVER_PORT);
        webserver.setStopAtShutdown(true);
        webserver.setHandler(newContext());
        webserver.start();
    }
    
    private WebAppContext newContext() {
        WebAppContext wac = new WebAppContext();
        
        wac.setContextPath(SERVER_PATH);
        wac.setResourceBase("src/test/context");
        wac.setClassLoader(getClass().getClassLoader());
        
        return wac;
    }
    
    @After
    public void stopServer() throws Exception {
        webserver.stop();
    }
    
    @Ignore
    @Test
    public void testWebserver() throws Exception {
        HttpClient browser = HttpClientBuilder.create().build(); 
        HttpGet getRequest = new HttpGet(String.format("http://localhost:%s/%s", 
                SERVER_PORT, SERVER_PATH));
        HttpResponse response = browser.execute(getRequest);
        int responseStatusCode = response.getStatusLine().getStatusCode();
        
        if (responseStatusCode != 200) {
            Assert.fail(String.format("Jetty returned '%s'!", responseStatusCode));
        }
    }
}
