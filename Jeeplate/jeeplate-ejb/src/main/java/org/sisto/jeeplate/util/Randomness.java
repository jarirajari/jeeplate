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

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.util.ByteSource;

@Dependent
public class Randomness {
    
    private RandomNumberGenerator rng;
    
    @PostConstruct
    public void init() {
        rng = new SecureRandomNumberGenerator();
    }
    
    public String generateRandomString(int length) {
        ByteSource bs = rng.nextBytes(length);
        String randomStringHexLenth = bs.toHex();
        
        return randomStringHexLenth;
    }
    
    public Integer generateRandomInteger() {
        return Integer.MAX_VALUE;
    }
}
