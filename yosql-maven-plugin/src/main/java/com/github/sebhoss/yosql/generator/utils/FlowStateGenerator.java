/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package com.github.sebhoss.yosql.generator.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.sebhoss.yosql.generator.AnnotationGenerator;
import com.github.sebhoss.yosql.generator.TypeWriter;
import com.github.sebhoss.yosql.generator.helpers.TypicalCodeBlocks;
import com.github.sebhoss.yosql.generator.helpers.TypicalMethods;
import com.github.sebhoss.yosql.generator.helpers.TypicalModifiers;
import com.github.sebhoss.yosql.generator.helpers.TypicalNames;
import com.github.sebhoss.yosql.generator.helpers.TypicalParameters;
import com.github.sebhoss.yosql.generator.helpers.TypicalTypes;
import com.github.sebhoss.yosql.plugin.PluginConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

@Named
@Singleton
public class FlowStateGenerator {

    public static final String        FLOW_STATE_CLASS_NAME = "FlowState";

    private final AnnotationGenerator annotations;
    private final TypeWriter          typeWriter;
    private final PluginConfig        pluginConfig;

    @Inject
    public FlowStateGenerator(
            final AnnotationGenerator annotations,
            final TypeWriter typeWriter,
            final PluginConfig pluginConfig) {
        this.annotations = annotations;
        this.typeWriter = typeWriter;
        this.pluginConfig = pluginConfig;
    }

    public void generateFlowStateClass() {
        final String packageName = pluginConfig.getBasePackageName() + "." + pluginConfig.getUtilityPackageName();
        final ClassName superclass = ClassName.get(packageName, ResultStateGenerator.RESULT_STATE_CLASS_NAME);
        final TypeSpec type = TypicalTypes.publicClass(FLOW_STATE_CLASS_NAME)
                .superclass(superclass)
                .addFields(fields())
                .addMethods(methods())
                .addAnnotations(annotations.generatedClass(FlowStateGenerator.class))
                .build();
        typeWriter.writeType(pluginConfig.getOutputBaseDirectory().toPath(), packageName, type);
    }

    private Iterable<FieldSpec> fields() {
        final List<FieldSpec> fields = new ArrayList<>();
        fields.add(connectionField());
        fields.add(preparedStatementField());
        return fields;
    }

    private FieldSpec connectionField() {
        return FieldSpec.builder(Connection.class, TypicalNames.CONNECTION)
                .addModifiers(TypicalModifiers.PRIVATE_FIELD)
                .build();
    }

    private FieldSpec preparedStatementField() {
        return FieldSpec.builder(PreparedStatement.class, TypicalNames.STATEMENT)
                .addModifiers(TypicalModifiers.PRIVATE_FIELD)
                .build();
    }

    private Iterable<MethodSpec> methods() {
        final List<MethodSpec> fields = new ArrayList<>();
        fields.add(constructor());
        fields.add(close());
        return fields;
    }

    private MethodSpec constructor() {
        return TypicalMethods.constructor()
                .addParameter(TypicalParameters.connection())
                .addParameter(TypicalParameters.preparedStatement())
                .addParameter(TypicalParameters.resultSet())
                .addParameter(TypicalParameters.metaData())
                .addParameter(TypicalParameters.columnCount())
                .addStatement("super($N, $N, $N)", TypicalNames.RESULT_SET, TypicalNames.META_DATA,
                        TypicalNames.COLUMN_COUNT)
                .addCode(TypicalCodeBlocks.setFieldToSelf(TypicalNames.CONNECTION))
                .addCode(TypicalCodeBlocks.setFieldToSelf(TypicalNames.STATEMENT))
                .build();
    }

    private MethodSpec close() {
        return TypicalMethods.publicMethod("close")
                .returns(void.class)
                .addException(SQLException.class)
                .addStatement("$N.close()", TypicalNames.RESULT_SET)
                .addStatement("$N.close()", TypicalNames.STATEMENT)
                .addStatement("$N.close()", TypicalNames.CONNECTION)
                .build();
    }

}
