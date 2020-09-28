/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.yosql.generator.blocks.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static wtf.metio.yosql.generator.blocks.generic.GenericBlocksObjectMother.names;
import static wtf.metio.yosql.generator.blocks.jdbc.JdbcObjectMother.jdbcNamesConfiguration;

@DisplayName("DefaultJdbcConnectionMethods")
class DefaultJdbcConnectionMethodsTest {

    private DefaultJdbcConnectionMethods generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultJdbcConnectionMethods(names(), jdbcNamesConfiguration());
    }

    @Test
    void prepareStatement() {
        Assertions.assertEquals("""
                connection.prepareStatement(query)""", generator.prepareStatement().toString());
    }

    @Test
    void prepareCallable() {
        Assertions.assertEquals("""
                connection.prepareCall(query)""", generator.prepareCallable().toString());
    }

}