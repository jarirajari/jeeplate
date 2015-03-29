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
package org.sisto.jeeplate.hello;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.jboss.logging.Logger;

@SuppressWarnings("ConsistentAccessType")
@Entity
@Table(name="hello_entity")
@Access(AccessType.FIELD)
public class HelloEntity implements Serializable {
    
    @Transient
    @Inject
    protected Logger log;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Long version;
    
    @Column(name = "message")
    private String message;
    
    @PostConstruct
    public void init() {
        
    }
    
    /**
     * Getter and setter are used for references
     * prop() and prop(type) are used for value (for get and set)
     */
    @Id
    public Long getId() {
        return id;
    }
    
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String s) {
        this.message = s;
    }
    
    @Transient
    public static HelloEntityBuilder newHelloEntityBuilder() {
        return (new HelloEntityBuilder());
    }
    
    public static class HelloEntityBuilder {
        private HelloEntity entity;
        
        private HelloEntityBuilder() {
            this.entity = new HelloEntity();
            this.entity.message = "";
        }
        
        public HelloEntity build() {
            return (this.entity);
        }
        
        public HelloEntityBuilder withMessage(String s) {
            this.entity.message = s;
            
            return (this);
        }
    }


}
