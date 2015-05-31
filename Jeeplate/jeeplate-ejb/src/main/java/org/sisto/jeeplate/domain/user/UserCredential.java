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

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.sisto.jeeplate.security.shiro.Salt;
import org.sisto.jeeplate.util.DateAndTimeConverter;

@Embeddable
public class UserCredential implements Serializable {
    @Transient
    protected static final String RESET = "";
    @Transient
    protected static final int EXPIRATION_PERIOD = 5 * 60; //5 min in seconds
    protected String salt;
    protected String password; // hashed with salt
    protected String passwordResetToken;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date passwordResetTimestamp;
    
    public UserCredential() {
        this.salt = RESET;
        this.password = RESET;
        this.passwordResetToken = RESET;
        this.passwordResetTimestamp = Date.from(Instant.EPOCH);
    }
    
    private static Date now() {
        return (Date.from(Instant.now()));
    }
    
    public void refresh(String newPassword) {
        this.salt = Salt.getSaltString();
        this.password = newPassword;
        this.password = UserCredential.getSaltedAndHashedPassword(this.salt, this.password);
    }
    
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Date getPasswordResetTimestamp() {
        return passwordResetTimestamp;
    }

    public void setPasswordResetTimestamp(Date passwordResetTimestamp) {
        this.passwordResetTimestamp = passwordResetTimestamp;
    }
    
    private static String getSaltedAndHashedPassword(String salt, String password) {
        final int hashIterations = 101; // must match with shiro.ini
        Sha256Hash hasher = new Sha256Hash(password, salt, hashIterations);

        return (hasher.toHex());
    }
    
    public void activateResetProtocol() {
        this.passwordResetToken = UserCredential.generateRandomNumberToken();
        this.passwordResetTimestamp = UserCredential.now();
    }
    
    public void deactivateResetProtocol() {
        this.passwordResetToken = RESET;
        this.passwordResetTimestamp = Date.from(Instant.EPOCH);
    }
    
    public Boolean resetIsValid() {
        final Date databaseTime = this.getPasswordResetTimestamp();
        Instant epochTime = DateAndTimeConverter.convertToInstant(databaseTime);
        Instant expireTime = epochTime.plusSeconds(EXPIRATION_PERIOD);
        Instant currentTime = Instant.now();
        Boolean invalid = currentTime.isAfter(expireTime);
        
        return (! invalid);
    }

    private static String generateRandomNumberToken() {
        final int length=16;
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource bs = rng.nextBytes(length);
        String randomStringHex16 = bs.toHex();
        
        return randomStringHex16;
    }
}
