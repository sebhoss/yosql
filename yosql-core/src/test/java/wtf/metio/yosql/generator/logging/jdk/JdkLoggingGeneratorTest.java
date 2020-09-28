/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.yosql.generator.logging.jdk;

import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static wtf.metio.yosql.generator.blocks.generic.GenericBlocksObjectMother.fields;
import static wtf.metio.yosql.generator.blocks.generic.GenericBlocksObjectMother.names;

@DisplayName("JdkLoggingGenerator")
class JdkLoggingGeneratorTest {

    private JdkLoggingGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new JdkLoggingGenerator(names(), fields());
    }

    @Test
    void logger() {
        final var logger = generator.logger(TypeName.BOOLEAN);
        Assertions.assertTrue(logger.isPresent());
        Assertions.assertEquals("""
                @javax.annotation.processing.Generated(
                    value = "test",
                    comments = "field"
                )
                private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(boolean.class.getName());
                """, logger.get().toString());
    }

    @Test
    void queryPicked() {
        Assertions.assertEquals("""
                LOG.finer(() -> String.format("Picked query [%s]", "test"));
                """, generator.queryPicked("test").toString());
    }

    @Test
    void indexPicked() {
        Assertions.assertEquals("""
                LOG.finer(() -> String.format("Picked index [%s]", "test"));
                """, generator.indexPicked("test").toString());
    }

    @Test
    void vendorQueryPicked() {
        Assertions.assertEquals("""
                LOG.finer(() -> String.format("Picked query [%s]", "test"));
                """, generator.vendorQueryPicked("test").toString());
    }

    @Test
    void vendorIndexPicked() {
        Assertions.assertEquals("""
                LOG.finer(() -> String.format("Picked index [%s]", "test"));
                """, generator.vendorIndexPicked("test").toString());
    }

    @Test
    void vendorDetected() {
        Assertions.assertEquals("""
                LOG.fine(() -> java.lang.String.format("Detected database vendor [%s]", databaseProductName));
                """, generator.vendorDetected().toString());
    }

    @Test
    void executingQuery() {
        Assertions.assertEquals("""
                LOG.fine(() -> java.lang.String.format("Executing query [%s]", executedQuery));
                """, generator.executingQuery().toString());
    }

    @Test
    void shouldLog() {
        Assertions.assertEquals("""
                LOG.isLoggable(java.util.logging.Level.FINE)""", generator.shouldLog().toString());
    }

    @Test
    void isEnabled() {
        Assertions.assertTrue(generator.isEnabled());
    }

    @Test
    void entering() {
        Assertions.assertEquals("""
                LOG.entering("repo", "method");
                """, generator.entering("repo", "method").toString());
    }

}