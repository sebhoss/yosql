/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package de.xn__ho_hia.yosql.generator.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.xn__ho_hia.yosql.model.SqlConfiguration;
import de.xn__ho_hia.yosql.model.SqlStatement;
import de.xn__ho_hia.yosql.testutils.ValidationFile;
import de.xn__ho_hia.yosql.testutils.ValidationFileTest;

class TypicalJavadocTest extends ValidationFileTest {

    @Test
    @SuppressWarnings("static-method")
    public void shouldGenerateJavaDocForRepositories(final ValidationFile validationFile) {
        validate(TypicalJavadoc.repositoryJavadoc(createTestSqlStatements()), validationFile);
    }

    @SuppressWarnings("nls")
    private static List<SqlStatement> createTestSqlStatements() {
        final List<SqlStatement> statements = new ArrayList<>();
        final Path path = Paths.get("test/path/file.sql");
        final SqlConfiguration config = new SqlConfiguration();
        final String raw = "select x from t";
        final SqlStatement statement = new SqlStatement(path, config, raw);
        statements.add(statement);
        return statements;
    }

}