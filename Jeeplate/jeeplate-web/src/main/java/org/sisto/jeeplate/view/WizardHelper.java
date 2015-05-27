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
 * MERCHANATBILITY or FITNES SFOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.sisto.jeeplate.view;

public class WizardHelper {
    
    public static String extractFlow(String name) {
        
        return (name.substring(0, name.length()-1));
    }
    
    public static int extractPhase(String sphase) {
        final int ascii = 48;
        int iphase = 0;
        
        if (sphase == null || sphase.trim().length() < 1) {
            iphase = 0;
        } else {
            iphase = ((int) sphase.charAt(sphase.length() - 1)) - ascii;
        }
        
        return iphase;
    }
    
    public static boolean isFirstPhase(String phase) {
        final int definedFirstPhase = 0;
        int intPhase = extractPhase(phase);
        
        return (intPhase == definedFirstPhase);
    }
    
    public static boolean isLastPhase(String phase) {
        final int definedLastPhase = 9;
        int intPhase = extractPhase(phase);
        
        return (intPhase == definedLastPhase);
    }
    
    public static boolean isForward(String prevStep, String currStep) {
        int prev = extractPhase(prevStep);
        int curr = extractPhase(currStep);
        
        return ((prev-curr) < 0);
    }
    
    public static String firstPhase(String flowName) {
        final int definedFirstPhase = 0;
        
        return (flowName+definedFirstPhase);
    }

    public static String lastPhase(String flowName) {
        final int definedLastPhase = 9;
        
        return (flowName+definedLastPhase);
    }    
}
