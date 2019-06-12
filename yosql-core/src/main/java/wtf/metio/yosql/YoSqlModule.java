/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql;

import dagger.Provides;
import dagger.Module;
import wtf.metio.yosql.dagger.Delegating;
import wtf.metio.yosql.generator.api.RepositoryGenerator;
import wtf.metio.yosql.generator.api.TypeWriter;
import wtf.metio.yosql.generator.api.UtilitiesGenerator;
import wtf.metio.yosql.model.ExecutionConfiguration;
import wtf.metio.yosql.model.ExecutionErrors;
import wtf.metio.yosql.model.Translator;
import wtf.metio.yosql.parser.SqlFileParser;
import wtf.metio.yosql.parser.SqlFileResolver;
import wtf.metio.yosql.utils.Timer;

/**
 * Dagger2 module for YoSql.
 */
@Module
public class YoSqlModule {

    @Provides
    YoSql provideYoSql(
            final SqlFileResolver fileResolver,
            final SqlFileParser sqlFileParser,
            final @Delegating RepositoryGenerator repositoryGenerator,
            final UtilitiesGenerator utilsGenerator,
            final ExecutionErrors errors,
            final Timer timer,
            final TypeWriter typeWriter,
            final Translator messages,
            final ExecutionConfiguration configuration) {
        return new YoSqlImplementation(
            fileResolver,
            sqlFileParser,
            repositoryGenerator,
            utilsGenerator,
            errors,
            timer,
            typeWriter,
            messages,
            configuration);
    }

}