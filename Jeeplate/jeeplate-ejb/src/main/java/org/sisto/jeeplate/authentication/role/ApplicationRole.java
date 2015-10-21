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
package org.sisto.jeeplate.authentication.role;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public enum ApplicationRole {
    // note that first bit is really index=0
    ADMINISTRATOR   ("administrator", 2, 0b100),
    DIRECTOR        ("director"     , 1, 0b010),
    ACTOR           ("actor"        , 0, 0b001),
    VISITOR         ("visitor"      , 0, 0b000);

    String name;
    int bindex;
    int bvalue;

    ApplicationRole(String sname, int bindex, int bvalue) {
        this.name = sname;
        this.bindex = bindex;
        this.bvalue = bvalue;
    }
    
    public int toValue() {
        return this.bvalue;
    }
    
    public int bitIndex() {
        return this.bindex;
    }
    
    public BitSet bitSet() {
        final BitSet bs = BitSet.valueOf(leIntToByteArray(this.bvalue));
        
        return bs;
    }

    public static int byteArrayToLeInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public static byte[] leIntToByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }
    
    public static ApplicationRole convert(String name) {
        ApplicationRole converted;
        
        try {
            converted = ApplicationRole.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException ex) {
            converted = VISITOR;
        }
        
        return converted;
    }
}
