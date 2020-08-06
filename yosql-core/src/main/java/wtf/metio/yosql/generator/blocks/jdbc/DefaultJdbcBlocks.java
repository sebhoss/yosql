package wtf.metio.yosql.generator.blocks.jdbc;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import de.xn__ho_hia.javapoet.TypeGuesser;
import io.reactivex.Flowable;
import wtf.metio.yosql.generator.api.LoggingGenerator;
import wtf.metio.yosql.generator.blocks.api.ControlFlows;
import wtf.metio.yosql.generator.blocks.api.GenericBlocks;
import wtf.metio.yosql.generator.blocks.api.Names;
import wtf.metio.yosql.generator.blocks.api.Variables;
import wtf.metio.yosql.generator.helpers.TypicalTypes;
import wtf.metio.yosql.model.configuration.RuntimeConfiguration;
import wtf.metio.yosql.model.sql.SqlConfiguration;
import wtf.metio.yosql.model.sql.SqlParameter;
import wtf.metio.yosql.model.sql.SqlStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static wtf.metio.yosql.generator.blocks.generic.CodeBlocks.code;

final class DefaultJdbcBlocks implements JdbcBlocks {

    private final RuntimeConfiguration runtime;
    private final GenericBlocks blocks;
    private final ControlFlows controlFlows;
    private final Names names;
    private final Variables variables;
    private final JdbcFields jdbcFields;
    private final JdbcMethods jdbcMethods;
    private final LoggingGenerator logging;

    DefaultJdbcBlocks(
            final RuntimeConfiguration runtime,
            final GenericBlocks blocks,
            final ControlFlows controlFlows,
            final Names names,
            final Variables variables,
            final JdbcFields jdbcFields,
            final JdbcMethods jdbcMethods,
            final LoggingGenerator logging) {
        this.runtime = runtime;
        this.blocks = blocks;
        this.names = names;
        this.variables = variables;
        this.controlFlows = controlFlows;
        this.jdbcFields = jdbcFields;
        this.jdbcMethods = jdbcMethods;
        this.logging = logging;
    }

    @Override
    public CodeBlock connectionVariable() {
        return variables.variable(runtime.jdbcNames().connection(), Connection.class,
                jdbcMethods.dataSource().getConnection());
    }

    @Override
    public CodeBlock statementVariable() {
        return variables.variable(runtime.jdbcNames().statement(), PreparedStatement.class,
                jdbcMethods.connection().prepareStatement());
    }

    @Override
    public CodeBlock callableVariable() {
        return variables.variable(runtime.jdbcNames().statement(), CallableStatement.class,
                jdbcMethods.connection().prepareCallable());
    }

    @Override
    public CodeBlock readMetaData() {
        return variables.variable(runtime.jdbcNames().metaData(), ResultSetMetaData.class,
                jdbcMethods.resultSet().getMetaData());
    }

    @Override
    public CodeBlock reactColumnCount() {
        return variables.variable(runtime.jdbcNames().columnCount(), int.class,
                jdbcMethods.metaData().getColumnCount());
    }

    @Override
    public CodeBlock resultSetVariable() {
        return variables.variable(runtime.jdbcNames().resultSet(), ResultSet.class,
                jdbcMethods.statement().executeQuery());
    }

    @Override
    public CodeBlock executeUpdate() {
        return blocks.returnValue(jdbcMethods.statement().executeUpdate());
    }

    @Override
    public CodeBlock executeBatch() {
        return blocks.returnValue(jdbcMethods.statement().executeBatch());
    }

    @Override
    public CodeBlock closeResultSet() {
        return blocks.close(runtime.jdbcNames().resultSet());
    }

    @Override
    public CodeBlock closePrepareStatement() {
        return blocks.close(runtime.jdbcNames().statement());
    }

    @Override
    public CodeBlock closeConnection() {
        return blocks.close(runtime.jdbcNames().connection());
    }

    @Override
    public CodeBlock closeState() {
        return blocks.close(names.state());
    }

    @Override
    public CodeBlock executeStatement() {
        return controlFlows.tryWithResource(resultSetVariable());
    }

    @Override
    public CodeBlock openConnection() {
        return controlFlows.tryWithResource(connectionVariable());
    }

    @Override
    public CodeBlock tryPrepareCallable() {
        return controlFlows.tryWithResource(callableVariable());
    }

    @Override
    public CodeBlock createStatement() {
        return controlFlows.tryWithResource(statementVariable());
    }

    @Override
    public CodeBlock prepareBatch(final SqlConfiguration config) {
        controlFlows.forLoop(
                code("int $N = 0; $N < $N.length; $N++",
                        runtime.jdbcNames().batch(),
                        runtime.jdbcNames().batch(),
                        config.getParameters().get(0).getName(),
                        runtime.jdbcNames().batch()),
                jdbcMethods.statement().addBatch()
        );

        return CodeBlock.builder()
                .beginControlFlow("for (int $N = 0; $N < $N.length; $N++)",
                        runtime.jdbcNames().batch(),
                        runtime.jdbcNames().batch(),
                        config.getParameters().get(0).getName(),
                        runtime.jdbcNames().batch())
                .add(setBatchParameters(config))
                .add(jdbcMethods.statement().addBatch())
                .endControlFlow()
                .build();
    }

    @Override
    public CodeBlock pickVendorQuery(final List<SqlStatement> sqlStatements) {
        final var builder = CodeBlock.builder();
        if (sqlStatements.size() > 1) {
            builder.addStatement("final $T $N = $N.getMetaData().getDatabaseProductName()",
                    String.class, names.databaseProductName(), runtime.jdbcNames().connection())
                    .add(logging.vendorDetected());
            if (logging.isEnabled()) {
                builder.addStatement("$T $N = null", String.class, names.rawQuery());
            }
            builder.addStatement("$T $N = null", String.class, names.query())
                    .addStatement("$T $N = null", TypicalTypes.MAP_OF_STRING_AND_ARRAY_OF_INTS,
                            runtime.jdbcNames().index())
                    .beginControlFlow("switch ($N)", names.databaseProductName());
            sqlStatements.stream()
                    .map(SqlStatement::getConfiguration)
                    .filter(config -> Objects.nonNull(config.getVendor()))
                    .forEach(config -> {
                        final var query = jdbcFields.constantSqlStatementFieldName(config);
                        builder.add("case $S:\n", config.getVendor())
                                .addStatement("$>$N = $N", names.query(), query)
                                .add(logging.vendorQueryPicked(query));
                        if (logging.isEnabled()) {
                            final var rawQuery = jdbcFields.constantRawSqlStatementFieldName(config);
                            builder.addStatement("$N = $N", names.rawQuery(), rawQuery);
                        }
                        if (config.hasParameters()) {
                            final var indexName = jdbcFields.constantSqlStatementParameterIndexFieldName(config);
                            builder.addStatement("$N = $N", runtime.jdbcNames().index(), indexName)
                                    .add(logging.vendorIndexPicked(indexName));
                        }
                        builder.addStatement("break$<");
                    });
            final var firstConfigWithoutVendor = sqlStatements.stream()
                    .map(SqlStatement::getConfiguration)
                    .filter(config -> Objects.isNull(config.getVendor()))
                    .findFirst();
            if (firstConfigWithoutVendor.isPresent()) {
                final var config = firstConfigWithoutVendor.get();
                final var query = jdbcFields.constantSqlStatementFieldName(config);
                builder.add("default:\n")
                        .addStatement("$>$N = $N", names.query(), query)
                        .add(logging.vendorQueryPicked(query));
                if (logging.isEnabled()) {
                    final var rawQuery = jdbcFields.constantRawSqlStatementFieldName(config);
                    builder.addStatement("$N = $N", names.rawQuery(), rawQuery);
                }
                if (config.hasParameters()) {
                    final var indexName = jdbcFields.constantSqlStatementParameterIndexFieldName(config);
                    builder.addStatement("$N = $N", runtime.jdbcNames().index(), indexName)
                            .add(logging.vendorIndexPicked(indexName));
                }
                builder.addStatement("break$<");
            } else {
                builder.add("default:\n")
                        .addStatement("$>throw new $T($T.format($S, $N))$<", IllegalStateException.class, String.class,
                                "No suitable query defined for vendor [%s]", names.databaseProductName());
            }
            builder.endControlFlow();
        } else {
            final var config = sqlStatements.get(0).getConfiguration();
            final var query = jdbcFields.constantSqlStatementFieldName(config);
            builder.addStatement("final $T $N = $N", String.class, names.query(), query)
                    .add(logging.queryPicked(query));
            if (logging.isEnabled()) {
                final var rawQuery = jdbcFields.constantRawSqlStatementFieldName(config);
                builder.addStatement("final $T $N = $N", String.class, names.rawQuery(), rawQuery);
            }
            if (config.hasParameters()) {
                final var indexFieldName = jdbcFields.constantSqlStatementParameterIndexFieldName(config);
                builder.addStatement("final $T $N = $N", TypicalTypes.MAP_OF_STRING_AND_ARRAY_OF_INTS,
                        runtime.jdbcNames().index(), indexFieldName)
                        .add(logging.indexPicked(indexFieldName));
            }
        }
        return builder.build();
    }

    @Override
    public CodeBlock logExecutedQuery(final SqlConfiguration sqlConfiguration) {
        final var builder = CodeBlock.builder();
        if (runtime.logging().shouldLog()) {
            builder.beginControlFlow("if ($L)", logging.shouldLog());
            builder.add("final $T $N = $N", String.class, names.executedQuery(), names.rawQuery());
            sqlConfiguration.getParameters()
                    .forEach(parameter -> {
                        if (TypeGuesser.guessTypeName(parameter.getType()).isPrimitive()) {
                            builder
                                    .add("\n$>.replace($S, $T.valueOf($N))$<", ":" + parameter.getName(),
                                            String.class, parameter.getName());
                        } else {
                            builder
                                    .add("\n$>.replace($S, $N == null ? $S : $N.toString())$<",
                                            ":" + parameter.getName(), parameter.getName(), "null",
                                            parameter.getName());
                        }
                    });
            builder.add(";\n");
            builder.add(logging.executingQuery());
            builder.endControlFlow();
        }
        return builder.build();
    }

    @Override
    public CodeBlock logExecutedBatchQuery(final SqlConfiguration sqlConfiguration) {
        final var builder = CodeBlock.builder();
        if (runtime.logging().shouldLog()) {
            builder.beginControlFlow("if ($L)", logging.shouldLog());
            builder.add("final $T $N = $N", String.class, names.executedQuery(), names.rawQuery());
            sqlConfiguration.getParameters()
                    .forEach(parameter -> {
                        if (TypeGuesser.guessTypeName(parameter.getType()).isPrimitive()) {
                            builder
                                    .add("\n$>.replace($S, $T.toString($N))$<", ":" + parameter.getName(),
                                            Arrays.class, parameter.getName());
                        } else {
                            builder
                                    .add("\n$>.replace($S, $N == null ? $S : $T.toString($N))$<",
                                            ":" + parameter.getName(), parameter.getName(), "null",
                                            Arrays.class, parameter.getName());
                        }
                    });
            builder.add(";\n");
            builder.add(logging.executingQuery());
            builder.endControlFlow();
        }
        return builder.build();
    }

    @Override
    public CodeBlock.Builder prepareReturnList(final TypeName listOfResults, final String converterAlias) {
        return CodeBlock.builder()
                .addStatement("final $T $N = new $T<>()",
                        listOfResults,
                        runtime.jdbcNames().list(),
                        ArrayList.class)
                .add(controlFlows.whileHasNext())
                .addStatement("$N.add($N.asUserType($N))",
                        runtime.jdbcNames().list(),
                        converterAlias,
                        names.state())
                .endControlFlow();
    }

    @Override
    public CodeBlock returnAsList(final TypeName listOfResults, final String converterAlias) {
        return prepareReturnList(listOfResults, converterAlias)
                .addStatement("return $N", runtime.jdbcNames().list())
                .build();
    }

    @Override
    public CodeBlock returnAsStream(final TypeName listOfResults, final String converterAlias) {
        return prepareReturnList(listOfResults, converterAlias)
                .addStatement("return $N.stream()", runtime.jdbcNames().list())
                .build();
    }

    @Override
    public CodeBlock streamStateful(final TypeSpec spliterator, final TypeSpec closer) {
        return CodeBlock.builder()
                .addStatement("return $T.stream($L, false).onClose($L)",
                        StreamSupport.class,
                        spliterator,
                        closer)
                .build();
    }

    @Override
    public CodeBlock createResultState() {
        return variables.variable(names.state(), runtime.result().resultStateClass(),
                code("new $T($N, $N, $N)",
                        runtime.result().resultStateClass(),
                        runtime.jdbcNames().resultSet(),
                        runtime.jdbcNames().metaData(),
                        runtime.jdbcNames().columnCount()));
    }

    @Override
    public CodeBlock returnNewFlowState() {
        return CodeBlock.builder()
                .addStatement("return new $T($N, $N, $N, $N, $N)", runtime.rxJava().flowStateClass(),
                        runtime.jdbcNames().connection(),
                        runtime.jdbcNames().statement(),
                        runtime.jdbcNames().resultSet(),
                        runtime.jdbcNames().metaData(),
                        runtime.jdbcNames().columnCount())
                .build();
    }

    @Override
    public CodeBlock newFlowable(final TypeSpec initialState, final TypeSpec generator, final TypeSpec disposer) {
        return CodeBlock.builder()
                .addStatement("return $T.generate($L, $L, $L)",
                        Flowable.class,
                        initialState,
                        generator,
                        disposer)
                .build();
    }

    @Override
    public CodeBlock parameterAssignment(
            final SqlConfiguration config,
            final String codeStatement,
            final Function<String, Object[]> parameterSetter) {
        final var builder = CodeBlock.builder();
        final var parameters = config.getParameters();

        if (parameters != null && !parameters.isEmpty()) {
            for (final SqlParameter parameter : config.getParameters()) {
                builder.beginControlFlow("for (final int $N : $N.get($S))",
                        runtime.jdbcNames().jdbcIndex(),
                        runtime.jdbcNames().index(), parameter.getName())
                        .add(CodeBlock.builder().addStatement(codeStatement,
                                parameterSetter.apply(parameter.getName())).build())
                        .endControlFlow();
            }
        }

        return builder.build();
    }

    @Override
    public CodeBlock setParameters(final SqlConfiguration config) {
        return parameterAssignment(config, "$N.setObject($N, $N)",
                parameterName -> new String[]{
                        runtime.jdbcNames().statement(),
                        runtime.jdbcNames().jdbcIndex(),
                        parameterName});
    }

    @Override
    public CodeBlock setBatchParameters(final SqlConfiguration config) {
        return parameterAssignment(config, "$N.setObject($N, $N[$N])",
                parameterName -> new String[]{
                        runtime.jdbcNames().statement(),
                        runtime.jdbcNames().jdbcIndex(),
                        parameterName,
                        runtime.jdbcNames().batch()});
    }

}