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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.sisto.jeeplate.logging.StringLogger;

@Dependent @Stateless
public class Email implements Serializable {

    private transient static final String TEXT_PLAIN = "text/plain";
    private transient static final String TEXT_HTML = "text/html";
    private transient static final String EMAIL_JNDI_RESOURCE = "java:jboss/mail/Default";

    @Inject
    StringLogger log;
    @Resource(mappedName = EMAIL_JNDI_RESOURCE)
    Session mailSession;
    
    public Email() {}
    
    public void sendMessage(MimeMessage m) {
        
        try {
            MockTransport.send(m);
        } catch (MessagingException e) {
            log.error("Email error: s%", e.getMessage());
        }
    }

    public MimeMessage constructEmail(String subject, String content, String source, String destination) {
        MimeMessage m = buildEmail(source, destination);
        try {
            m.setSubject(subject);
            m.setContent(content, TEXT_PLAIN);
        } catch (MessagingException e) {
            log.error("Email error: s%", e.getMessage());
        }
        
        return m;
    }

    private MimeMessage buildEmail(String source, String destination) {
        MimeMessage m = new MimeMessage(mailSession);

        try {
            Address from = convertStringToAddress(source);
            Address[] to = new InternetAddress[]{convertStringToAddress(destination)};

            m.setFrom(from);
            m.setRecipients(Message.RecipientType.TO, to);
            m.setSentDate(new java.util.Date());
        } catch (MessagingException | NullPointerException e) {
            log.error("Email error: s%", e.getMessage());
        }

        return m;
    }

    private InternetAddress convertStringToAddress(String username) {
        InternetAddress converted;
        try {
            converted = new InternetAddress(username);
        } catch (AddressException ae) {
            converted = new InternetAddress();
        }

        return converted;
    }
    
    private static class MockTransport {
        public static void send(MimeMessage m) throws MessagingException {
            
            try {
                StringBuilder sb = new StringBuilder("");
                Address[] tos = m.getAllRecipients();
                String adrs = (tos == null || tos.length == 0) ? "No addresses!" : Arrays.toString(tos);
                sb.append("\n========= ");
                sb.append("\n    EMAIL ");
                sb.append("\n      TO: ").append(adrs);
                sb.append("\n SUBJECT: ").append(String.valueOf(m.getSubject()));
                sb.append("\n CONTENT: ").append(String.valueOf(m.getContent()));
                sb.append("\n========= ");
                System.out.println(sb.toString());
                System.out.flush();
            } catch (IOException | NullPointerException e) {
                throw new MessagingException(e.getMessage());
            }
        }
    }
}
