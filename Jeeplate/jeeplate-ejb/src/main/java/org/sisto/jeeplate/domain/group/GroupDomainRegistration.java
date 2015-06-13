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
package org.sisto.jeeplate.domain.group;

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
import org.sisto.jeeplate.util.DateAndTimeConverter;

@Embeddable
public class GroupDomainRegistration implements Serializable {
    @Transient
    protected static final String RESET = "";
    @Transient
    protected static final int EXPIRATION_PERIOD = 5 * 60; //5 min in seconds
    protected String partDomainDeliveredSeparately; // must be persisted, same for all
    @Transient 
    protected String partEmailExpiringRandom;
    @Transient
    protected String registrationToken; // token = domain part + email part
    @Temporal(TemporalType.TIMESTAMP)
    protected Date registrationTimestamp;
    
    public GroupDomainRegistration() {
        this.partDomainDeliveredSeparately = RESET;
        this.partEmailExpiringRandom = RESET;
        this.registrationToken = RESET;
        this.registrationTimestamp = Date.from(Instant.EPOCH);
    }

    public String getPartDomainDeliveredSeparately() {
        return partDomainDeliveredSeparately;
    }

    public void setPartDomainDeliveredSeparately(String partDomainDeliveredSeparately) {
        this.partDomainDeliveredSeparately = partDomainDeliveredSeparately;
    }

    public String getPartEmailExpiringRandom() {
        return partEmailExpiringRandom;
    }

    public void setPartEmailExpiringRandom(String partEmailExpiringRandom) {
        this.partEmailExpiringRandom = partEmailExpiringRandom;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }
    
    // setRegistrationToken

    public Date getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Date registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }
    
    private static Date now() {
        return (Date.from(Instant.now()));
    }
    
    public void activateRegistrationProtocol() {
        this.registrationToken = this.getHashedConcatenatedHashedToken();
        this.registrationTimestamp = GroupDomainRegistration.now();
    }
    
    public void deactivateRegistrationProtocol() {
        this.registrationToken = RESET;
        this.registrationTimestamp = Date.from(Instant.EPOCH);
    }
    
    public Boolean registrationIsValid() {
        final Date databaseTime = this.getRegistrationTimestamp();
        Instant epochTime = DateAndTimeConverter.convertToInstant(databaseTime);
        Instant expireTime = epochTime.plusSeconds(EXPIRATION_PERIOD);
        Instant currentTime = Instant.now();
        Boolean invalid = currentTime.isAfter(expireTime);
        
        return (! invalid);
    }
    
    private String getHashedConcatenatedHashedToken() {
        final int hashIterations = 23;
        final String hashSalt = "sisto.org";
        final String concat = String.format("%s%s", this.partDomainDeliveredSeparately, this.partEmailExpiringRandom);
        Sha256Hash hasher = new Sha256Hash(concat, hashSalt, hashIterations);

        return (hasher.toHex());
    }
    
    private static String generateRandomNumberToken() {
        final int length=4;
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource bs = rng.nextBytes(length);
        String randomStringHex8 = bs.toHex();
        
        return randomStringHex8;
    }
}
