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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;

import org.apache.shiro.authz.AuthorizationException;
import org.jboss.logging.Logger;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.JdbcUtils;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Adopted from: https://github.com/SomMeri/SimpleShiroSecuredApplication
 */
public class JNDIAndSaltAwareJdbcRealm extends JdbcRealm {
    
    private static Logger log = Logger.getLogger(JNDIAndSaltAwareJdbcRealm.class.getName());
    protected String jndiDataSourceName;

    public JNDIAndSaltAwareJdbcRealm() {
        log.info("Initializing JNDIAndSaltAwareJdbcRealm...");
    }
    
    public String getJndiDataSourceName() {
        return (this.jndiDataSourceName);
    }

    /**
     * As all properties are configurable in ini-file, so new 'JNDIDS' property
     * will be automatically configurable too!
     */
    public void setJndiDataSourceName(String ds) {
        this.jndiDataSourceName = ds;
        try {
            InitialContext ic = new InitialContext();
            this.dataSource = ((DataSource) ic.lookup(ds));
        } catch (NamingException ne) {
            throw new AuthorizationException(ne);
        }
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return (authenticationToken instanceof UsernamePasswordToken);
    }
  
    /**
     * Database table contains "username", "password", and "salt"
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
        String username = userPassToken.getUsername();
        PasswordSalt passwordSalt = getPasswordSaltForUser(username);
        
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, passwordSalt.password(), super.getName());
        info.setCredentialsSalt(new SimpleByteSource(passwordSalt.salt()));
        
        return info;
    }

    private PasswordSalt getPasswordSaltForUser(String user) {
        Connection jdbcConnection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String username = (user == null) ? "" : user;
        String salt = null;
        String password = "";
        
        try {
            jdbcConnection = this.dataSource.getConnection();
            statement = jdbcConnection.prepareStatement(authenticationQuery);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            boolean hasAccount = resultSet.next();
            
            if (hasAccount) {
                password = resultSet.getString(1);
                salt = resultSet.getString(2);
                boolean notUniqueAccount = resultSet.next();
                if (notUniqueAccount) {
                    throw new AuthenticationException(String.format("User '%s' does not have unique account!", user));
                }
            } else {
                throw new AuthenticationException(String.format("User '%s' does not have account!", user));
            }
        } catch (SQLException e) {
            throw new AuthenticationException(e.getMessage());
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(jdbcConnection);
        }
        
        return (new PasswordSalt(password, salt));
    }   
}
