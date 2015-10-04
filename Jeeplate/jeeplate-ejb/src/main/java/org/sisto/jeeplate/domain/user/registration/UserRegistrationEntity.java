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
package org.sisto.jeeplate.domain.user.registration;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.util.ByteSource;
import org.sisto.jeeplate.domain.BusinessEntity;
import org.sisto.jeeplate.util.DateAndTimeConverter;

@Entity @Access(AccessType.FIELD) 
@Table(name = "system_user_registration")
public class UserRegistrationEntity extends BusinessEntity implements Serializable {
    
    @Transient
    protected transient static final String RESET = "";
    @Transient
    protected transient static final int EXPIRATION_PERIOD = 5 * 60; //5 min in seconds
    @Id @SequenceGenerator(name="user_registration_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_registration_seq", strategy = GenerationType.SEQUENCE)
    protected Long id;
    protected String registrationEmail;
    protected String registrationToken;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date registrationTimestamp;
    protected Boolean registrationEmailVerified;
    
    public UserRegistrationEntity() {
        this.id = BusinessEntity.DEFAULT_ID;
        this.registrationEmail = RESET;
        this.registrationToken = RESET;
        this.registrationTimestamp = Date.from(Instant.EPOCH);
        this.registrationEmailVerified = Boolean.TRUE; /////////////// FALSE!
    }
    
    @PostLoad @PostPersist @PostUpdate 
    @Override
    protected void updateParentId() {
        super.setId(this.id);
    }

    public String getRegistrationEmail() {
        return registrationEmail;
    }

    public void setRegistrationEmail(String registrationEmail) {
        this.registrationEmail = registrationEmail;
    }
    
    public String getRegistrationToken() {
        return registrationToken;
    }
    
    public void setRegistrationToken(String token) {
        registrationToken = token;
    }

    public Date getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Date registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public Boolean getRegistrationEmailVerified() {
        return registrationEmailVerified;
    }

    public void setRegistrationEmailVerified(Boolean registrationEmailVerified) {
        this.registrationEmailVerified = registrationEmailVerified;
    }
    
    private static Date now() {
        return (Date.from(Instant.now()));
    }
    
    public void activateRegistrationProtocol() {
        this.registrationToken = UserRegistrationEntity.generateRandomNumberToken();
        this.registrationTimestamp = UserRegistrationEntity.now();
    }
    
    public void deactivateRegistrationProtocol() {
        this.registrationToken = RESET;
        this.registrationTimestamp = Date.from(Instant.EPOCH);
    }
    
    public Boolean registrationIsValid(String token) {
        final Date databaseTime = this.getRegistrationTimestamp();
        final Instant epochTime = DateAndTimeConverter.convertToInstant(databaseTime);
        final Instant expireTime = epochTime.plusSeconds(EXPIRATION_PERIOD);
        final Instant currentTime = Instant.now();
        final Boolean invalid = currentTime.isAfter(expireTime);
        final Boolean tokensMatch = this.registrationToken.equals(token);
        
        return (! invalid && tokensMatch);
    }
    
    private static String generateRandomNumberToken() {
        final int length=16; // 16*2=32
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource bs = rng.nextBytes(length);
        String randomStringHex8 = bs.toHex();
        
        return randomStringHex8;
    }
}
