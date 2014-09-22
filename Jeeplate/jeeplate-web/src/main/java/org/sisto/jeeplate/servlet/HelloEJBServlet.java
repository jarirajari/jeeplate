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
package org.sisto.jeeplate.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sisto.jeeplate.HelloEJBBean;

@WebServlet(urlPatterns = {"/test"})
public class HelloEJBServlet extends HttpServlet {
    
    @EJB(name = "helloBean")
    HelloEJBBean bean;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
    throws ServletException, IOException {
        try (PrintWriter out = res.getWriter()) {
            String body = String.format("<body>%s</body>", "EJB says hello!");
            String title = String.format("<title>%s</title>", bean.sayHello());
            String header = String.format("<head>%s</head>", title);
            String html = String.format("<html>%s%s</html>", header, body);
            
            res.setContentType("text/html");
            out.println(html);
        }
    }
}
