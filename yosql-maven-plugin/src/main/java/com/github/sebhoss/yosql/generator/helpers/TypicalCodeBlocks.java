package com.github.sebhoss.yosql.generator.helpers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.sebhoss.yosql.generator.LoggingGenerator;
import com.github.sebhoss.yosql.generator.logging.JdkLoggingGenerator;
import com.github.sebhoss.yosql.model.LoggingAPI;
import com.github.sebhoss.yosql.model.SqlConfiguration;
import com.github.sebhoss.yosql.model.SqlParameter;
import com.github.sebhoss.yosql.model.SqlStatement;
import com.github.sebhoss.yosql.plugin.PluginConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.reactivex.Flowable;

@Named
@Singleton
public class TypicalCodeBlocks {

    public static CodeBlock setFieldToSelf(final String name) {
        return CodeBlock.builder()
                .addStatement("this.$N = $N", name, name)
                .build();
    }

    public static CodeBlock getMetaData() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.getMetaData()", ResultSetMetaData.class, TypicalNames.META_DATA,
                        TypicalNames.RESULT_SET)
                .build();
    }

    public static CodeBlock getColumnCount() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.getColumnCount()", int.class, TypicalNames.COLUMN_COUNT,
                        TypicalNames.META_DATA)
                .build();
    }

    public static CodeBlock executeQuery() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.executeQuery()", ResultSet.class, TypicalNames.RESULT_SET,
                        TypicalNames.STATEMENT)
                .build();
    }

    public static CodeBlock executeUpdate() {
        return CodeBlock.builder()
                .addStatement("return $N.executeUpdate()", TypicalNames.STATEMENT)
                .build();
    }

    public static CodeBlock prepareBatch(final SqlConfiguration configuration) {
        return CodeBlock.builder()
                .beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", TypicalNames.BATCH, TypicalNames.BATCH,
                        configuration.getParameters().get(0).getName(), TypicalNames.BATCH)
                .add(TypicalCodeBlocks.setBatchParameters(configuration))
                .add(TypicalCodeBlocks.addBatch())
                .endControlFlow()
                .build();
    }

    public static CodeBlock executeBatch() {
        return CodeBlock.builder()
                .addStatement("return $N.executeBatch()", TypicalNames.STATEMENT)
                .build();
    }

    public static CodeBlock addBatch() {
        return CodeBlock.builder()
                .addStatement("$N.addBatch()", TypicalNames.STATEMENT)
                .build();
    }

    public static CodeBlock getConnection() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.getConnection()", Connection.class, TypicalNames.CONNECTION,
                        TypicalNames.DATA_SOURCE)
                .build();
    }

    public static CodeBlock prepareStatement(final SqlConfiguration configuration) {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.prepareStatement($N)", PreparedStatement.class,
                        TypicalNames.STATEMENT, TypicalNames.CONNECTION,
                        TypicalFields.constantSqlStatementFieldName(configuration))
                .build();
    }

    public static CodeBlock prepareStatement() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.prepareStatement($N)", PreparedStatement.class,
                        TypicalNames.STATEMENT, TypicalNames.CONNECTION, TypicalNames.QUERY)
                .build();
    }

    public static CodeBlock prepareCallable() {
        return CodeBlock.builder()
                .addStatement("final $T $N = $N.prepareCall($N)", CallableStatement.class,
                        TypicalNames.STATEMENT, TypicalNames.CONNECTION, TypicalNames.QUERY)
                .build();
    }

    public static CodeBlock newFlowable(final TypeSpec initialState, final TypeSpec generator,
            final TypeSpec disposer) {
        return CodeBlock.builder()
                .addStatement("return $T.generate($L, $L, $L)", Flowable.class, initialState, generator, disposer)
                .build();
    }

    public static CodeBlock streamStatefull(final TypeSpec spliterator, final TypeSpec closer) {
        return CodeBlock.builder()
                .addStatement("return $T.stream($L, false).onClose($L)", StreamSupport.class, spliterator, closer)
                .build();
    }

    public static CodeBlock tryExecute() {
        return CodeBlock.builder()
                .beginControlFlow("try ($L)", TypicalCodeBlocks.executeQuery())
                .build();
    }

    public static CodeBlock tryConnect() {
        return CodeBlock.builder()
                .beginControlFlow("try ($L)", TypicalCodeBlocks.getConnection())
                .build();
    }

    public static CodeBlock tryPrepareStatement() {
        return CodeBlock.builder()
                .beginControlFlow("try ($L)", TypicalCodeBlocks.prepareStatement())
                .build();
    }

    public static CodeBlock tryPrepareCallable() {
        return CodeBlock.builder()
                .beginControlFlow("try ($L)", TypicalCodeBlocks.prepareCallable())
                .build();
    }

    public static CodeBlock setParameters(final SqlConfiguration configuration) {
        return parameterAssignment(configuration, "$N.setObject($N, $N)",
                parameterName -> new String[] { TypicalNames.STATEMENT,
                        TypicalNames.JDBC_INDEX, parameterName });
    }

    public static CodeBlock setBatchParameters(final SqlConfiguration configuration) {
        return parameterAssignment(configuration, "$N.setObject($N, $N[$N])",
                parameterName -> new String[] { TypicalNames.STATEMENT,
                        TypicalNames.JDBC_INDEX, parameterName, TypicalNames.BATCH });
    }

    private static CodeBlock parameterAssignment(
            final SqlConfiguration configuration,
            final String codeStatement,
            final Function<String, Object[]> parameterSetter) {
        final com.squareup.javapoet.CodeBlock.Builder builder = CodeBlock.builder();
        final List<SqlParameter> parameters = configuration.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            for (final SqlParameter parameter : configuration.getParameters()) {
                builder.beginControlFlow("for (final int $N : $N.get($S))", TypicalNames.JDBC_INDEX,
                        TypicalNames.INDEX, parameter.getName())
                        .add(CodeBlock.builder().addStatement(codeStatement,
                                parameterSetter.apply(parameter.getName())).build())
                        .endControlFlow();
            }
        }
        return builder.build();
    }

    public static CodeBlock maybeTry(final SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return startTryBlock();
        }
        return CodeBlock.builder().build();
    }

    public static CodeBlock endMaybeTry(final SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return endTryBlock();
        }
        return CodeBlock.builder().build();
    }

    public static CodeBlock startTryBlock() {
        return CodeBlock.builder().beginControlFlow("try").build();
    }

    public static CodeBlock endTryBlock() {
        return endTryBlock(1);
    }

    public static CodeBlock endTryBlock(final int flowsToClose) {
        final Builder builder = CodeBlock.builder();
        IntStream.range(0, flowsToClose).forEach(index -> builder.endControlFlow());
        return builder.build();
    }

    public static CodeBlock ifHasNext() {
        return CodeBlock.builder()
                .beginControlFlow("if ($N.next())", TypicalNames.STATE)
                .build();
    }

    public static CodeBlock returnAsList(final TypeName listOfResults, final String converterAlias) {
        return prepareReturnList(listOfResults, converterAlias)
                .addStatement("return $N", TypicalNames.LIST)
                .build();
    }

    public static CodeBlock returnAsStream(final TypeName listOfResults, final String converterAlias) {
        return prepareReturnList(listOfResults, converterAlias)
                .addStatement("return $N.stream()", TypicalNames.LIST)
                .build();
    }

    private static Builder prepareReturnList(final TypeName listOfResults, final String converterAlias) {
        return CodeBlock.builder()
                .addStatement("final $T $N = new $T<>()", listOfResults, TypicalNames.LIST, ArrayList.class)
                .add(TypicalCodeBlocks.whileHasNext())
                .addStatement("$N.add($N.asUserType($N))", TypicalNames.LIST, converterAlias, TypicalNames.STATE)
                .endControlFlow();
    }

    public static CodeBlock whileHasNext() {
        return CodeBlock.builder()
                .beginControlFlow("while ($N.next())", TypicalNames.STATE)
                .build();
    }

    public static CodeBlock endWhile() {
        return CodeBlock.builder()
                .endControlFlow()
                .build();
    }

    public static CodeBlock nextElse() {
        return CodeBlock.builder()
                .nextControlFlow("else")
                .build();
    }

    public static CodeBlock endIf() {
        return CodeBlock.builder().endControlFlow().build();
    }

    public static CodeBlock maybeCatchAndRethrow(final SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return catchAndRethrow();
        }
        return CodeBlock.builder().build();
    }

    public static CodeBlock catchAndRethrow() {
        return catchAndDo("throw new $T($N)", RuntimeException.class, TypicalNames.EXCEPTION);
    }

    public static CodeBlock catchAndDo(final String format, final Object... args) {
        return CodeBlock.builder()
                .beginControlFlow("catch (final $T $N)", SQLException.class, TypicalNames.EXCEPTION)
                .addStatement(format, args)
                .endControlFlow().build();
    }

    public CodeBlock pickVendorQuery(final List<SqlStatement> sqlStatements) {
        final Builder builder = CodeBlock.builder();
        if (sqlStatements.size() > 1) {
            builder.addStatement("final $T $N = $N.getMetaData().getDatabaseProductName()",
                    String.class, TypicalNames.DATABASE_PRODUCT_NAME, TypicalNames.CONNECTION)
                    .add(logging.vendorDetected())
                    .addStatement("$T $N = null", String.class, TypicalNames.QUERY)
                    .addStatement("$T $N = null", TypicalTypes.MAP_OF_STRING_AND_NUMBERS, TypicalNames.INDEX)
                    .beginControlFlow("switch ($N)", TypicalNames.DATABASE_PRODUCT_NAME);
            sqlStatements.stream()
                    .map(SqlStatement::getConfiguration)
                    .filter(config -> Objects.nonNull(config.getVendor()))
                    .forEach(config -> {
                        final String fieldName = TypicalFields.constantSqlStatementFieldName(config);
                        builder.add("case $S:\n", config.getVendor())
                                .addStatement("$N = $N", TypicalNames.QUERY, fieldName)
                                .add(logging.vendorQueryPicked(fieldName));
                        if (config.hasParameters()) {
                            final String indexName = TypicalFields.constantSqlStatementParameterIndexFieldName(config);
                            builder.addStatement("$N = $N", TypicalNames.INDEX, indexName)
                                    .add(logging.vendorIndexPicked(indexName));
                            ;
                        }
                        builder.addStatement("break");
                    });
            sqlStatements.stream()
                    .map(SqlStatement::getConfiguration)
                    .filter(config -> Objects.isNull(config.getVendor()))
                    .limit(1)
                    .forEach(config -> {
                        final String fieldName = TypicalFields.constantSqlStatementFieldName(config);
                        builder.add("default:\n")
                                .addStatement("$N = $N", TypicalNames.QUERY, fieldName)
                                .add(logging.vendorQueryPicked(fieldName));
                        if (config.hasParameters()) {
                            final String indexName = TypicalFields.constantSqlStatementParameterIndexFieldName(config);
                            builder.addStatement("$N = $N", TypicalNames.INDEX, indexName)
                                    .add(logging.vendorIndexPicked(indexName));
                        }
                        builder.addStatement("break");
                    });
            builder.endControlFlow();
        } else {
            final SqlConfiguration configuration = sqlStatements.get(0).getConfiguration();
            final String fieldName = TypicalFields.constantSqlStatementFieldName(configuration);
            builder.addStatement("final $T $N = $N", String.class, TypicalNames.QUERY, fieldName)
                    .add(logging.queryPicked(fieldName));
            if (configuration.hasParameters()) {
                final String indexFieldName = TypicalFields.constantSqlStatementParameterIndexFieldName(configuration);
                builder.addStatement("final $T $N = $N", TypicalTypes.MAP_OF_STRING_AND_NUMBERS, TypicalNames.INDEX,
                        indexFieldName)
                        .add(logging.indexPicked(indexFieldName));
            }
        }
        return builder.build();
    }

    public static CodeBlock returnTrue() {
        return CodeBlock.builder().addStatement("return $L", true).build();
    }

    public static CodeBlock returnFalse() {
        return CodeBlock.builder().addStatement("return $L", false).build();
    }

    public static CodeBlock closeResultSet() {
        return close(TypicalNames.RESULT_SET);
    }

    public static CodeBlock closePrepareStatement() {
        return close(TypicalNames.STATEMENT);
    }

    public static CodeBlock closeConnection() {
        return close(TypicalNames.CONNECTION);
    }

    public static CodeBlock closeState() {
        return close(TypicalNames.STATE);
    }

    private static CodeBlock close(final String resource) {
        return CodeBlock.builder().addStatement("$N.close()", resource).build();
    }

    public static Iterable<TypeName> sqlException(final SqlConfiguration configuration) {
        if (!configuration.isMethodCatchAndRethrow()) {
            return Arrays.asList(ClassName.get(SQLException.class));
        }
        return Collections.emptyList();
    }

    private final PluginConfig     pluginConfig;
    private final LoggingGenerator logging;

    @Inject
    public TypicalCodeBlocks(
            final PluginConfig pluginConfig,
            final JdkLoggingGenerator logging) {
        this.pluginConfig = pluginConfig;
        this.logging = logging;
    }

    public CodeBlock newResultState() {
        return CodeBlock.builder()
                .addStatement("final $T $N = new $T($N, $N, $N)", pluginConfig.getResultStateClass(),
                        TypicalNames.STATE, pluginConfig.getResultStateClass(), TypicalNames.RESULT_SET,
                        TypicalNames.META_DATA, TypicalNames.COLUMN_COUNT)
                .build();
    }

    public CodeBlock newFlowState() {
        return CodeBlock.builder()
                .addStatement("return new $T($N, $N, $N, $N, $N)", pluginConfig.getFlowStateClass(),
                        TypicalNames.CONNECTION, TypicalNames.STATEMENT, TypicalNames.RESULT_SET,
                        TypicalNames.META_DATA, TypicalNames.COLUMN_COUNT)
                .build();
    }

    public CodeBlock logExecutedQuery(final SqlConfiguration configuration) {
        final Builder builder = CodeBlock.builder();
        if (LoggingAPI.NONE != pluginConfig.getLoggingApi()) {
            builder.beginControlFlow("if ($L)", logging.shouldLogLow());
            builder.add("final $T $N = $N", String.class, TypicalNames.EXECUTED_QUERY, TypicalNames.QUERY);
            configuration.getParameters().stream()
                    .forEach(parameter -> {
                        if (TypicalTypes.guessTypeName(parameter.getType()).isPrimitive()) {
                            builder
                                    .add("\n        .replace($S, $T.valueOf($N))", ":" + parameter.getName(),
                                            String.class, parameter.getName());
                        } else {
                            builder
                                    .add("\n        .replace($S, $N == null ? $S : $N.toString())",
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

}
