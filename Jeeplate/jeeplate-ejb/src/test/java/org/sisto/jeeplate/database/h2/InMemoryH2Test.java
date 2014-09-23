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
package org.sisto.jeeplate.database.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class InMemoryH2Test {
    
    private static DataSource ds;
    
    @BeforeClass
    public static void initDatasource() {
        final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        final String username = "user";
        final String password = "pass";
        ds = JdbcConnectionPool.create(url, username, password);
    }
    
    @AfterClass
    public static void lizeDatasource() {
        ds = null;
    }
    
    @Test
    public void alwaysPassingTest() {
        /* First test */
    }
    
    @Ignore
    @Test
    public void datasourceConnectsToDatabaseTest() {
        final String createTable = "CREATE TABLE IF NOT EXISTS test_table;";
        try (Connection c = ds.getConnection()) {
            Statement s = c.createStatement();
            s.execute(createTable);
        } catch (SQLException sqle) {
            Assert.fail(sqle.getMessage());
        }
    }
    
    
}
