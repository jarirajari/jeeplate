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
package org.sisto.jeeplate.domain;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TransactionRequiredException;
import org.jboss.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.sisto.jeeplate.domain.ObjectEntity;
import org.sisto.jeeplate.domain.ObjectId;
import org.sisto.jeeplate.logging.StringLogger;
import org.sisto.jeeplate.util.H2EM;
import org.sisto.jeeplate.util.PGEM;

@Stateful @TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EntityStore<T extends ObjectEntity> {
    
    @Inject
    private transient StringLogger log;
    
    @Inject
    @PGEM
    private EntityManager em;
    
    private EntityManager em() {
            
        return this.em;
    }
    
    /**
     * Creates an object in database
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T bind(T be) throws PersistenceException  {
        
        T bound = this.safeFind(be);
        
        return bound;
    }
    
    /**
     * Creates an object in database
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T create(T be) throws PersistenceException  {
        T created = be;
        boolean isNotInDB = this.isNew(be);
        
        if (isNotInDB) {
            created = (T) this.safeAssociate(be);
        }
        
        return created;
    }
    
    /**
     * Reads from database and overwrites any changes to be
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T read(T be) throws PersistenceException {
        T read = null;
        boolean isInDB = this.isOld(be);
        
        if (isInDB) {
            read = (T) this.safeRefresh(be);
        } else {
            read = (T) this.safeAssociate(be);
        }
        
        return read;
    }
    
    /**
     * Updates to database and overwrites any changes in db
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T update(T be) throws PersistenceException {
        T updated = null;
        boolean isInDB = this.isOld(be);
        
        if (isInDB) {
            updated = (T) this.safeUpdate(be);
        } else {
            updated = (T) this.safeAssociate(be);
        }
        
        return updated;
    }
    
    /**
     * Deletes but leaves object in memory
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T delete(T be) throws PersistenceException {
        T deleted = null;
        boolean isInDB = this.isOld(be);
        
        
        if (isInDB) {
            deleted = (T) this.safeUnassociate(be);
        } else {
            deleted = be;
        }
        deleted.reset();
        
        return deleted;
    }
    
    protected final Boolean isNew(T be) {
        boolean isNew = false;
        
        if (be.isDefault()) {
            isNew = true;
        } else {
            try {
                T tmp = (T) this.em().find(be.getClass(), be.identity());
                isNew = (tmp == null) ? true : false;
            } catch (IllegalArgumentException iae) {
                isNew = false;
                log.warn("isNew error: " + iae.getMessage());
            }
        }
        
        return isNew;
    }
    
    protected final Boolean isOld(T be) {
        return (! this.isNew(be));
    }
    
    protected Boolean isLoaded(T be) {
        EntityManagerFactory emf = this.em().getEntityManagerFactory();
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        Boolean loaded = util.isLoaded(this);
        
        return loaded;
    }
    
    protected ObjectId getManagedEntityObjectId(T be) {
        EntityManagerFactory emf = this.em().getEntityManagerFactory();
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        ObjectId oid = (ObjectId) util.getIdentifier(this);
        
        return oid;
    }
    
    protected Boolean isManaged(T be) {
        boolean isManaged = false;
        
        try {
            isManaged = this.em().contains(be);
        } catch (IllegalArgumentException ex) {
            isManaged = false;
        }
        
        return isManaged;
    }
    
    private T safeFind(T be) {
        boolean isInDB = this.isOld(be);
        T found = be;
        
        if (isInDB) {
            try {
                boolean isManaged = this.isManaged(be);
                if (isManaged) {
                    found = be;
                } else {
                    found = (T) this.em().find(be.getClass(), be.identity());
                }
            } catch (IllegalArgumentException ex) {
                log.debug(ex.getMessage());
                found = null;
            }
        }
        
        return found;
    }
    
    private T safeAssociate(T be) {
        boolean isNotManaged = (! this.isManaged(be));
        
        try {
            if (isNotManaged) {
                this.em().persist(be);
            }
        } catch (EntityExistsException | IllegalArgumentException |
                TransactionRequiredException | NullPointerException ex) {
            log.error("safeAssociate error: " + ex.getMessage());
        }
        
        return be;
    }

    private T safeUnassociate(T be) {
        boolean isManaged = this.isManaged(be);
        T removed = be;
        T tmp = null;
        
        try {
            if (isManaged) {
                removed = (T) this.em().merge(be);
                this.em().remove(removed);
            } else {
                tmp = (T) this.safeFind(be);
                removed = (T) this.em().merge(tmp);
                this.em().remove(removed);
            }
            this.em().detach(removed);
        } catch (IllegalArgumentException | TransactionRequiredException |
                EntityNotFoundException | NullPointerException ex) {
            log.error("safeUnassociate error: " + ex.getMessage());
        }
        
        return removed;
    }
    
    private T safeRefresh(T be) {
        boolean isManaged = this.isManaged(be);
        T refreshed = be;

        try {
            if (isManaged) {
                this.em().refresh(be);
            } else {
                refreshed = (T) this.safeFind(be);
            }
        } catch (IllegalArgumentException | TransactionRequiredException |
                EntityNotFoundException | NullPointerException ex) {
            log.error("safeRefresh error: " + ex.getMessage());
        }

        return refreshed;
    }
    
    private T safeUpdate(T be) {
        T updated = be;
        
        try {
            updated = (T) this.em().merge(be);
        } catch (IllegalArgumentException | TransactionRequiredException |
                EntityNotFoundException | NullPointerException ex) {
            log.error("safeUpdate error: " + ex.getMessage());
        }
        
        return updated;
    }
}
