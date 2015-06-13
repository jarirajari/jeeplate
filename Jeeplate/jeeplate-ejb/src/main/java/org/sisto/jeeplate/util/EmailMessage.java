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

public class EmailMessage {
    private String subject;
    private String content;
    private String contentRecipient;
    private String contentSender;
    
    public EmailMessage() {
        this("", "", "", "");
    }
    
    public EmailMessage(String subject, String content,
                        String recipient, String sender) {
        this.subject = subject;
        this.content = content;
        this.contentRecipient = recipient;
        this.contentSender = sender;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentRecipient() {
        return contentRecipient;
    }

    public void setContentRecipient(String contentRecipient) {
        this.contentRecipient = contentRecipient;
    }

    public String getContentSender() {
        return contentSender;
    }

    public void setContentSender(String contentSender) {
        this.contentSender = contentSender;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Email:");
        sb.append("  subject  = ").append(this.subject);
        sb.append(", recipient= ").append(this.contentRecipient);
        sb.append(", sender   = ").append(this.contentSender);
        sb.append(", content  = ").append(this.content);
        
        return (sb.toString());
    }
}
