/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql.benchmark.full_lifecycle;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import wtf.metio.yosql.YoSql;
import wtf.metio.yosql.generator.api.RepositoryGenerator;
import wtf.metio.yosql.generator.api.UtilitiesGenerator;
import wtf.metio.yosql.model.CodeGenerationException;
import wtf.metio.yosql.model.ExecutionErrors;
import wtf.metio.yosql.model.SqlStatement;
import wtf.metio.yosql.parser.SqlFileParser;
import wtf.metio.yosql.parser.SqlFileResolver;
import wtf.metio.yosql.utils.Timer;

/**
 * Alternative implementation of {@link YoSql} used for benchmarking.
 */
final class AlternativeYoSqlImplementation implements YoSql {

    private final SqlFileResolver     fileResolver;
    private final SqlFileParser       sqlFileParser;
    private final RepositoryGenerator repositoryGenerator;
    private final UtilitiesGenerator  utilsGenerator;
    private final ExecutionErrors     errors;
    private final Timer               timer;

    @Inject
    AlternativeYoSqlImplementation(
            final SqlFileResolver fileResolver,
            final SqlFileParser sqlFileParser,
            final RepositoryGenerator repositoryGenerator,
            final UtilitiesGenerator utilsGenerator,
            final ExecutionErrors errors,
            final Timer timer) {
        this.fileResolver = fileResolver;
        this.sqlFileParser = sqlFileParser;
        this.repositoryGenerator = repositoryGenerator;
        this.utilsGenerator = utilsGenerator;
        this.errors = errors;
        this.timer = timer;
    }

    @Override
    public void generateCode() {
        try {
            final Executor executor = Executors.newWorkStealingPool();
            supplyAsync(() -> parseFiles(), executor)
                    .thenApplyAsync(this::generateRepositories, executor)
                    .thenAcceptAsync(this::generateUtilities, executor)
                    .join();
        } catch (final Throwable exception) {
            errors.add(exception);
        }
        if (errors.hasErrors()) {
            errors.throwWith(new CodeGenerationException("Error during code generation"));
        }
    }

    private List<SqlStatement> parseFiles() {
        return timer.timed("parse files", //$NON-NLS-1$
                () -> fileResolver.resolveFiles()
                        // .parallel()
                        .flatMap(sqlFileParser::parse)
                        .collect(toList()));
    }

    private List<SqlStatement> generateRepositories(final List<SqlStatement> statements) {
        timer.timed("generate repositories", () -> statements.stream() //$NON-NLS-1$
                .collect(groupingBy(SqlStatement::getRepository))
                .entrySet()
                .parallelStream()
                .forEach(repository -> repositoryGenerator.generateRepository(repository.getKey(),
                        repository.getValue())));
        return statements;
    }

    private void generateUtilities(final List<SqlStatement> statements) {
        timer.timed("generate utilities", () -> utilsGenerator.generateUtilities(statements)); //$NON-NLS-1$
    }

}