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
package org.sisto.jeeplate.domain.user;

import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import org.sisto.jeeplate.i18n.Messages;
import org.sisto.jeeplate.util.ApplicationProperty;
import org.sisto.jeeplate.util.Email;
import org.sisto.jeeplate.util.EmailMessage;

@Dependent
public class UserMessageEngine {
    
    @Inject @ApplicationProperty(name = "test.message", defaultValue = "jee@pla.te")
    String systemEmailAddress2;
    
    @Inject
    Email emailSender;
    
    @Inject
    Messages messages;
    
    public void sendEmailToUser(EmailMessage em) {
        final String subject = em.getSubject();
        final String content = em.getContent();
        final String recipient = this.systemEmailAddress2;
        final MimeMessage mm = emailSender.constructEmail(subject, content, recipient, em.getContentRecipient());
        
        emailSender.sendMessage(mm);
    }
    
    public EmailMessage buildRoleSwitchMessage(Locale userLangLocale, String recipient, String pin) {
        Locale loc = messages.switchLanguage(userLangLocale);
        String subject = messages.getLocalizedString("email.switch.role.subject");
        String content = messages.getLocalizedString("email.switch.role.content", pin);
        String contentRecipient = recipient;
        String contentSender = messages.getLocalizedString("email.reply.address");
        
        return (new EmailMessage(subject, content, contentRecipient, contentSender));
    }
    
    public EmailMessage buildRegistrationUserExistsMessage(Locale userLangLocale, String recipient) {
        Locale loc = messages.switchLanguage(userLangLocale);
        String subject = messages.getLocalizedString("email.register.account.old.subject");
        String content = messages.getLocalizedString("email.register.account.old.content");
        String contentRecipient = recipient;
        String contentSender = messages.getLocalizedString("email.reply.address");
        
        return (new EmailMessage(subject, content, contentRecipient, contentSender));
    }
    
    public EmailMessage buildRegistrationUserNotExistsMessage(Locale userLangLocale, String recipient, String registrationToken) {
        Locale loc = messages.switchLanguage(userLangLocale);
        String subject = messages.getLocalizedString("email.register.account.new.subject");
        String content = messages.getLocalizedString("email.register.account.new.content", registrationToken);
        String contentRecipient = recipient;
        String contentSender = messages.getLocalizedString("email.reply.address");
            
        return (new EmailMessage(subject, content, contentRecipient, contentSender));
    }
    
    public EmailMessage buildPasswordResetUserExistsMessage(Locale userLangLocale, String recipient, String resetToken) {
        Locale loc = messages.switchLanguage(userLangLocale);
        String subject = messages.getLocalizedString("email.reset.password.old.subject");
        String content = messages.getLocalizedString("email.reset.password.old.content", resetToken);
        String contentRecipient = recipient;
        String contentSender = messages.getLocalizedString("email.reply.address");
            
        return (new EmailMessage(subject, content, contentRecipient, contentSender));
    }
    
    public EmailMessage buildPasswordResetUserNotExistsMessage(Locale userLangLocale, String recipient) {
        Locale loc = messages.switchLanguage(userLangLocale);
        String subject = messages.getLocalizedString("email.reset.password.new.subject");
        String content = messages.getLocalizedString("email.reset.password.new.content");
        String contentRecipient = recipient;
        String contentSender = messages.getLocalizedString("email.reply.address");
            
        return (new EmailMessage(subject, content, contentRecipient, contentSender));
    }
}
