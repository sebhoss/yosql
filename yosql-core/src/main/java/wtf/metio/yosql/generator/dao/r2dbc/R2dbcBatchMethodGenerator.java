/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.yosql.generator.dao.r2dbc;

import com.squareup.javapoet.MethodSpec;
import wtf.metio.yosql.generator.api.BatchMethodGenerator;
import wtf.metio.yosql.generator.api.LoggingGenerator;
import wtf.metio.yosql.generator.blocks.generic.ControlFlows;
import wtf.metio.yosql.generator.blocks.generic.Methods;
import wtf.metio.yosql.generator.blocks.generic.Parameters;
import wtf.metio.yosql.generator.blocks.jdbc.JdbcBlocks;
import wtf.metio.yosql.generator.blocks.jdbc.JdbcTransformer;
import wtf.metio.yosql.model.sql.SqlConfiguration;
import wtf.metio.yosql.model.sql.SqlStatement;

import java.util.List;

public final class R2dbcBatchMethodGenerator implements BatchMethodGenerator {

    private final ControlFlows controlFlow;
    private final Methods methods;
    private final Parameters parameters;
    private final LoggingGenerator logging;
    private final JdbcBlocks jdbc;
    private final JdbcTransformer transformer;

    public R2dbcBatchMethodGenerator(
            final ControlFlows controlFlow,
            final Methods methods,
            final Parameters parameters,
            final LoggingGenerator logging,
            final JdbcBlocks jdbc,
            final JdbcTransformer transformer) {
        this.controlFlow = controlFlow;
        this.methods = methods;
        this.parameters = parameters;
        this.logging = logging;
        this.jdbc = jdbc;
        this.transformer = transformer;
    }

    @Override
    public MethodSpec batchWriteMethod(final SqlConfiguration configuration, final List<SqlStatement> vendorStatements) {
        return null;
    }
}
