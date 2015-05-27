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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.sisto.jeeplate.logging.StringLogger;

@ApplicationScoped
public class Email {

    private static final String TEXT_PLAIN = "text/plain";
    private static final String TEXT_HTML = "text/html";
    private static final String EMAIL_JNDI_RESOURCE = "java:jboss/mail/Default";
    
    @Inject
    private transient StringLogger log;

    @Resource(mappedName = EMAIL_JNDI_RESOURCE)
    private Session mailSession;

    public void sendMessage(MimeMessage m) {
        
        try {
            MockTransport.send(m);
            log.info("Sent email succesfully!");
        } catch (MessagingException e) {
            log.error("Error sending email!");
        }
    }
    
    private static class MockTransport {
        public static void send(MimeMessage m) throws MessagingException {       
            try {
                System.out.println("MOCK EMAIL");
                //System.out.println("TO:       "+m.getAllRecipients()[0]);
                System.out.println("SUBJECT:  "+m.getSubject());
                System.out.println("CONTENT:  "+m.getContent());
                System.out.flush();
            } catch (IOException | NullPointerException e) {
                throw new MessagingException(e.getMessage());
            }
        }
    }

    public MimeMessage constructEmail(String subject, String content, String source, String destination) {
        MimeMessage m = buildEmail(source, destination);
        try {
            m.setSubject(subject);
            m.setContent(content, TEXT_PLAIN);
        } catch (MessagingException e) {
            log.error("Could not fill email template with information!");
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
            
            log.error("Could not build email template!");
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
}
