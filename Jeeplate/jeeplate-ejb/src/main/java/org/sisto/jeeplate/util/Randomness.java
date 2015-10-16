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

import java.security.SecureRandom;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
public class Randomness {
    
    private SecureRandom rng;
    
    @PostConstruct
    public void init() {
        rng = new SecureRandom();
    }
    
    public String generateRandomString(int length) {
        
        final String randomStringHexLenth = alphanumericRandomString(length);
        
        return randomStringHexLenth;
    }
    
    private String alphanumericRandomString(int length) {
        // alphabet - { i, l, I, 0 } = 32 = 5 bit
        final char[] chars = "123456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ".toCharArray();
        final StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    public Integer generateRandomInteger() {
        return Integer.MAX_VALUE;
    }
}
