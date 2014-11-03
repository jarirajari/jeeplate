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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext(type=PersistenceContextType.TRANSACTION, unitName = "h2PU")
    private EntityManager h2EntityManager;
    
    @PersistenceContext(type=PersistenceContextType.TRANSACTION, unitName = "pgPU")
    private EntityManager pgEntityManager;

    @Produces
    @Default
    public EntityManager createDefault() {
        return this.h2EntityManager;
    }
    
    @Produces
    @H2EM
    public EntityManager createH2() {
        return this.h2EntityManager;
    }
    
    @Produces
    @PGEM
    public EntityManager createPG() {
        return this.pgEntityManager;
    }
}
