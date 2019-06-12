/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql.benchmark.parse_file;

import wtf.metio.yosql.benchmark.DaggerYoSqlBenchmarkComponent;

/**
 * JMH based micro benchmark for the DefaultSqlFileParser running against all .sql files.
 */
public class AlternativeSqlFileParserParseAllFileBenchmark extends AbstractParseAllFileBenchmark {

    @Override
    public void setUpParser() throws Exception {
        // DaggerYoSqlWithAlternativeParserComponent
        parser = DaggerYoSqlBenchmarkComponent.builder().build().sqlFileParser();
    }

}