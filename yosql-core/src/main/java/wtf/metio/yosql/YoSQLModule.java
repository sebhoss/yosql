/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql;

import dagger.Module;
import dagger.Provides;
import wtf.metio.yosql.files.FileParser;
import wtf.metio.yosql.generator.api.CodeGenerator;
import wtf.metio.yosql.orchestration.Orchestrator;

import javax.inject.Singleton;

/**
 * Dagger module for YoSQL itself.
 */
@Module
public final class YoSQLModule {

    @Provides
    @Singleton
    YoSQL provideYoSql(
            final Orchestrator orchestrator,
            final FileParser files,
            final CodeGenerator codeGenerator) {
        return new OrchestratedYoSQL(orchestrator, files, codeGenerator);
    }

}
