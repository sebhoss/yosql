package com.github.sebhoss.yosql.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

import com.github.sebhoss.yosql.generator.jdbc.BatchMethodAPI;
import com.github.sebhoss.yosql.generator.jdbc.Java8MethodAPI;
import com.github.sebhoss.yosql.generator.jdbc.RxJava2MethodAPI;
import com.github.sebhoss.yosql.generator.jdbc.StandardMethodAPI;
import com.github.sebhoss.yosql.generator.utils.FlowStateGenerator;
import com.github.sebhoss.yosql.generator.utils.ResultRowGenerator;
import com.github.sebhoss.yosql.generator.utils.ResultStateGenerator;
import com.github.sebhoss.yosql.generator.utils.ToResultRowConverterGenerator;
import com.github.sebhoss.yosql.helpers.TypicalAnnotations;
import com.github.sebhoss.yosql.helpers.TypicalCodeBlocks;
import com.github.sebhoss.yosql.helpers.TypicalFields;
import com.github.sebhoss.yosql.helpers.TypicalMethods;
import com.github.sebhoss.yosql.helpers.TypicalModifiers;
import com.github.sebhoss.yosql.helpers.TypicalNames;
import com.github.sebhoss.yosql.helpers.TypicalParameters;
import com.github.sebhoss.yosql.helpers.TypicalTypes;
import com.github.sebhoss.yosql.model.ResultRowConverter;
import com.github.sebhoss.yosql.model.SqlParameter;
import com.github.sebhoss.yosql.model.SqlStatement;
import com.github.sebhoss.yosql.model.SqlStatementConfiguration;
import com.github.sebhoss.yosql.model.SqlStatementType;
import com.github.sebhoss.yosql.plugin.PluginErrors;
import com.github.sebhoss.yosql.plugin.PluginRuntimeConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

@Named
@Singleton
public class DefaultCodeGenerator implements CodeGenerator {

    private final TypeWriter                    typeWriter;
    private final FlowStateGenerator            flowStateGenerator;
    private final PluginRuntimeConfig           runtimeConfig;
    private final ResultStateGenerator          resultStateGenerator;
    private final ToResultRowConverterGenerator toResultRowConverterGenerator;
    private final ResultRowGenerator            resultRowGenerator;
    private final RxJava2MethodAPI              rxJava2Api;
    private final Java8MethodAPI                java8Api;
    private final BatchMethodAPI                batchApi;
    private final StandardMethodAPI             standardApi;

    @Inject
    public DefaultCodeGenerator(
            final TypeWriter typeWriter,
            final FlowStateGenerator flowStateGenerator,
            final ResultStateGenerator resultStateGenerator,
            final ToResultRowConverterGenerator toResultRowConverterGenerator,
            final ResultRowGenerator resultRowGenerator,
            final PluginErrors pluginErrors,
            final PluginRuntimeConfig runtimeConfig,
            final RxJava2MethodAPI rxJava2Api,
            final Java8MethodAPI java8Api,
            final BatchMethodAPI batchApi,
            final StandardMethodAPI standardApi) {
        this.typeWriter = typeWriter;
        this.flowStateGenerator = flowStateGenerator;
        this.resultStateGenerator = resultStateGenerator;
        this.toResultRowConverterGenerator = toResultRowConverterGenerator;
        this.resultRowGenerator = resultRowGenerator;
        this.runtimeConfig = runtimeConfig;
        this.rxJava2Api = rxJava2Api;
        this.java8Api = java8Api;
        this.batchApi = batchApi;
        this.standardApi = standardApi;
    }

    @Override
    public void generateUtilities(final List<SqlStatement> allStatements) {
        if (allStatements.stream()
                .map(SqlStatement::getConfiguration)
                .anyMatch(config -> SqlStatementType.READING == config.getType())) {
            resultStateGenerator.generateResultStateClass();
        }
        if (allStatements.stream()
                .map(SqlStatement::getConfiguration)
                .filter(config -> SqlStatementType.READING == config.getType())
                .anyMatch(config -> config.isMethodRxJavaApi())) {
            flowStateGenerator.generateFlowStateClass();
        }
        if (resultConverters(allStatements)
                .anyMatch(converter -> converter.getConverterType().endsWith(
                        ToResultRowConverterGenerator.TO_RESULT_ROW_CONVERTER_CLASS_NAME))) {
            toResultRowConverterGenerator.generateToResultRowConverterClass();
            resultRowGenerator.generateResultRowClass();
        }
    }

    /**
     * Generates a single repository.
     *
     * @param repositoryName
     *            The fully-qualified name of the repository to generate.
     * @param sqlStatements
     *            The SQL statements to be included in the repository.
     */
    @Override
    public void generateRepository(final String repositoryName,
            final List<SqlStatement> sqlStatements) {
        final String className = TypicalNames.getClassName(repositoryName);
        final String packageName = TypicalNames.getPackageName(repositoryName);
        final TypeSpec repository = TypeSpec.classBuilder(className)
                .addModifiers(TypicalModifiers.PUBLIC_CLASS)
                .addFields(asFields(sqlStatements))
                .addMethods(asMethods(sqlStatements))
                .addAnnotation(TypicalAnnotations.generatedAnnotation(DefaultCodeGenerator.class))
                .addStaticBlock(staticInitializer(sqlStatements))
                .build();
        typeWriter.writeType(runtimeConfig.getOutputBaseDirectory().toPath(), packageName, repository);
    }

    private static CodeBlock staticInitializer(final List<SqlStatement> sqlStatements) {
        final CodeBlock.Builder builder = CodeBlock.builder();
        sqlStatements.stream()
                .map(SqlStatement::getConfiguration)
                .filter(SqlStatementConfiguration::hasParameters)
                .forEach(config -> {
                    config.getParameters().stream()
                            .filter(SqlParameter::hasIndices)
                            .forEach(param -> builder.addStatement("$N.put($S, $L)",
                                    TypicalFields.constantSqlStatementParameterIndexFieldName(config),
                                    param.getName(),
                                    indexArray(param)));
                });
        return builder.build();
    }

    private static String indexArray(final SqlParameter param) {
        return IntStream.of(param.getIndices())
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "new int[] { ", " }"));
    }

    private Iterable<FieldSpec> asFields(final List<SqlStatement> sqlStatements) {
        final Stream<FieldSpec> constants = Stream.concat(
                sqlStatements.stream()
                        .map(DefaultCodeGenerator::asConstantSqlField),
                sqlStatements.stream()
                        .filter(stmt -> !stmt.getConfiguration().getParameters().isEmpty())
                        .map(DefaultCodeGenerator::asConstantSqlParameterIndexField));
        final Stream<FieldSpec> fields = Stream.concat(Stream.of(asDataSourceField()),
                converterFields(sqlStatements));
        return Stream.concat(constants, fields)
                .collect(Collectors.toList());
    }

    private static FieldSpec asConstantSqlField(final SqlStatement sqlStatement) {
        final SqlStatementConfiguration configuration = sqlStatement.getConfiguration();
        return FieldSpec.builder(String.class, TypicalFields.constantSqlStatementFieldName(configuration))
                .addModifiers(TypicalModifiers.CONSTANT_FIELD)
                .initializer("$S", TypicalParameters.replaceNamedParameters(sqlStatement))
                .build();
    }

    private static FieldSpec asConstantSqlParameterIndexField(final SqlStatement sqlStatement) {
        final SqlStatementConfiguration configuration = sqlStatement.getConfiguration();
        return FieldSpec.builder(TypicalTypes.MAP_OF_STRING_AND_NUMBERS,
                TypicalFields.constantSqlStatementParameterIndexFieldName(configuration))
                .addModifiers(TypicalModifiers.CONSTANT_FIELD)
                .initializer("new $T<>($L)", HashMap.class, sqlStatement.getConfiguration().getParameters().size())
                .build();
    }

    private static FieldSpec asDataSourceField() {
        return FieldSpec.builder(DataSource.class, TypicalNames.DATA_SOURCE)
                .addModifiers(TypicalModifiers.PRIVATE_FIELD)
                .build();
    }

    private Stream<FieldSpec> converterFields(final List<SqlStatement> sqlStatements) {
        return resultConverters(sqlStatements)
                .map(converter -> {
                    final ClassName converterClass = ClassName.bestGuess(converter.getConverterType());
                    return FieldSpec.builder(converterClass, converter.getAlias())
                            .addModifiers(TypicalModifiers.PRIVATE_FIELD)
                            .build();
                });
    }

    private Iterable<MethodSpec> asMethods(final List<SqlStatement> sqlStatements) {
        final List<MethodSpec> methods = new ArrayList<>(sqlStatements.size());

        methods.add(constructor(sqlStatements));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodStandardApi())
                .filter(statement -> SqlStatementType.READING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(standardApi.standardReadApi(methodName, statements)));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodStandardApi())
                .filter(statement -> SqlStatementType.WRITING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(standardApi.standardWriteApi(methodName, statements)));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodBatchApi())
                .filter(statement -> statement.getConfiguration().hasParameters())
                .filter(statement -> SqlStatementType.WRITING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(batchApi.generateMethod(methodName, statements)));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodEagerStreamApi())
                .filter(statement -> SqlStatementType.READING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(java8Api.streamEagerApi(statements)));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodStreamLazyApi())
                .filter(statement -> SqlStatementType.READING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(java8Api.streamLazyApi(statements)));
        sqlStatements.stream()
                .filter(statement -> statement.getConfiguration().isMethodRxJavaApi())
                .filter(statement -> SqlStatementType.READING == statement.getConfiguration().getType())
                .collect(Collectors.groupingBy(statement -> statement.getConfiguration().getName()))
                .forEach((methodName, statements) -> methods.add(rxJava2Api.rxJava2Method(statements)));

        return methods;
    }

    private MethodSpec constructor(final List<SqlStatement> sqlStatements) {
        final Builder builder = CodeBlock.builder();
        resultConverters(sqlStatements).forEach(converter -> {
            final ClassName converterClass = ClassName.bestGuess(converter.getConverterType());
            builder.addStatement("this.$N = new $T()", converter.getAlias(), converterClass);
        });

        return TypicalMethods.constructor()
                .addParameter(TypicalParameters.dataSource())
                .addCode(TypicalCodeBlocks.setFieldToSelf(TypicalNames.DATA_SOURCE))
                .addCode(builder.build())
                .build();
    }

    private Stream<ResultRowConverter> resultConverters(final List<SqlStatement> sqlStatements) {
        return sqlStatements.stream()
                .map(SqlStatement::getConfiguration)
                .filter(config -> SqlStatementType.READING == config.getType())
                .map(SqlStatementConfiguration::getResultConverter)
                .filter(Objects::nonNull)
                .distinct();
    }

}