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
package org.sisto.jeeplate.security.shiro;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
/*
TODO: REFACTOR AWAY TO WEB WHEN REGISTARTION WORKS
*/
public class Salt {
    
    public static String getSaltString() {
        return (byteSourceToString(getSalt()));
    }
    
    public static ByteSource getSalt() {
        return (new SecureRandomNumberGenerator().nextBytes());
    }
    
    public static ByteSource stringToByteSource(String salt) {
        ByteSource bs = ByteSource.Util.bytes(Hex.decode(salt));
        
        return bs;
    }
    
    public static String byteSourceToString(ByteSource bs) {
        return (bs.toHex());
    }
}
