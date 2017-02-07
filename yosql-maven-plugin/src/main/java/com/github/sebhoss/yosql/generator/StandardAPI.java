package com.github.sebhoss.yosql.generator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.sebhoss.yosql.ResultRowConverter;
import com.github.sebhoss.yosql.SqlStatement;
import com.github.sebhoss.yosql.SqlStatementConfiguration;
import com.github.sebhoss.yosql.helpers.TypicalCodeBlocks;
import com.github.sebhoss.yosql.helpers.TypicalMethods;
import com.github.sebhoss.yosql.helpers.TypicalTypes;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

@Named
@Singleton
public class StandardAPI {

    private final TypicalCodeBlocks codeBlocks;

    @Inject
    public StandardAPI(final TypicalCodeBlocks codeBlocks) {
        this.codeBlocks = codeBlocks;
    }

    public MethodSpec standardReadApi(
            final String methodName,
            final List<SqlStatement> sqlStatements,
            final SqlStatementConfiguration configuration) {
        final ResultRowConverter converter = configuration.getResultConverter();
        final ClassName resultType = ClassName.bestGuess(converter.getResultType());
        final ParameterizedTypeName listOfResults = ParameterizedTypeName.get(TypicalTypes.LIST, resultType);
        return TypicalMethods.publicMethod(methodName)
                .returns(listOfResults)
                .addParameters(configuration.getParameterSpecs())
                .addExceptions(TypicalCodeBlocks.sqlException(configuration))
                .addCode(TypicalCodeBlocks.tryConnect())
                .addCode(TypicalCodeBlocks.pickVendorQuery(sqlStatements))
                .addCode(TypicalCodeBlocks.tryPrepare())
                .addCode(TypicalCodeBlocks.setParameters(configuration))
                .addCode(TypicalCodeBlocks.tryExecute())
                .addCode(TypicalCodeBlocks.getMetaData())
                .addCode(TypicalCodeBlocks.getColumnCount())
                .addCode(codeBlocks.newResultState())
                .addCode(TypicalCodeBlocks.returnAsList(listOfResults, converter.getAlias()))
                .addCode(TypicalCodeBlocks.endTryBlock(3))
                .addCode(TypicalCodeBlocks.maybeCatchAndRethrow(configuration))
                .build();
    }

    public MethodSpec standardWriteApi(final String methodName, final List<SqlStatement> sqlStatements) {
        final SqlStatementConfiguration configuration = SqlStatementConfiguration.merge(sqlStatements);
        return TypicalMethods.publicMethod(configuration.getName())
                .returns(int.class)
                .addExceptions(TypicalCodeBlocks.sqlException(configuration))
                .addParameters(configuration.getParameterSpecs())
                .addCode(TypicalCodeBlocks.tryConnect())
                .addCode(TypicalCodeBlocks.pickVendorQuery(sqlStatements))
                .addCode(TypicalCodeBlocks.tryPrepare())
                .addCode(TypicalCodeBlocks.setParameters(configuration))
                .addCode(TypicalCodeBlocks.executeUpdate())
                .addCode(TypicalCodeBlocks.endTryBlock(2))
                .addCode(TypicalCodeBlocks.maybeCatchAndRethrow(configuration))
                .build();
    }

}
