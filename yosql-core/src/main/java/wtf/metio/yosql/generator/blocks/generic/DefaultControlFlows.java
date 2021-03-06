/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.yosql.generator.blocks.generic;

import com.squareup.javapoet.CodeBlock;
import wtf.metio.yosql.model.sql.SqlConfiguration;

import java.sql.SQLException;
import java.util.stream.IntStream;

import static wtf.metio.yosql.generator.blocks.generic.CodeBlocks.code;

final class DefaultControlFlows implements ControlFlows {

    private final Variables variables;
    private final Names names;

    DefaultControlFlows(
            final Variables variables,
            final Names names) {
        this.variables = variables;
        this.names = names;
    }

    @Override
    public CodeBlock tryWithResource(final CodeBlock resources) {
        return CodeBlock.builder()
                .beginControlFlow("try ($L)", resources)
                .build();
    }

    @Override
    public CodeBlock catchAndDo(final CodeBlock statement) {
        final var catchDeclaration = variables.variable(names.exception(), SQLException.class);
        return CodeBlock.builder()
                .beginControlFlow("catch ($L)", catchDeclaration)
                .addStatement(statement)
                .endControlFlow()
                .build();
    }

    @Override
    public CodeBlock catchAndRethrow() {
        return catchAndDo(code("throw new $T($N)", RuntimeException.class, names.exception()));
    }

    @Override
    public CodeBlock maybeCatchAndRethrow(final SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return catchAndRethrow();
        }
        return CodeBlock.builder().build();
    }

    @Override
    public CodeBlock forLoop(final CodeBlock init, final CodeBlock runner) {
        return CodeBlock.builder()
                .beginControlFlow("for ($L)", init)
                .add(runner)
                .endControlFlow()
                .build();
    }

    @Override
    public CodeBlock startTryBlock() {
        return CodeBlock.builder().beginControlFlow("try").build();
    }

    @Override
    public CodeBlock endTryBlock() {
        return endTryBlock(1);
    }

    @Override
    public CodeBlock endTryBlock(int flowsToClose) {
        final var builder = CodeBlock.builder();
        IntStream.range(0, flowsToClose).forEach(index -> builder.endControlFlow());
        return builder.build();
    }

    @Override
    public CodeBlock maybeTry(SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return startTryBlock();
        }
        return CodeBlock.builder().build();
    }

    @Override
    public CodeBlock endMaybeTry(final SqlConfiguration configuration) {
        if (configuration.isMethodCatchAndRethrow()) {
            return endTryBlock();
        }
        return CodeBlock.builder().build();
    }

    @Override
    public CodeBlock ifHasNext() {
        return CodeBlock.builder()
                .beginControlFlow("if ($N.next())", names.state())
                .build();
    }

    @Override
    public CodeBlock endIf() {
        return CodeBlock.builder().endControlFlow().build();
    }

    @Override
    public CodeBlock whileHasNext() {
        return CodeBlock.builder()
                .beginControlFlow("while ($N.next())", names.state())
                .build();
    }

    @Override
    public CodeBlock nextElse() {
        return CodeBlock.builder()
                .nextControlFlow("else")
                .build();
    }

}
