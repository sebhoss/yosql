package com.github.sebhoss.yosql.generator;

import com.github.sebhoss.yosql.SqlStatementConfiguration;

public class TypicalFields {

    public static final String constantSqlStatementFieldName(final SqlStatementConfiguration configuration) {
        return configuration.getName().replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase() + getVendor(configuration);
    }

    public static final String constantRawSqlStatementFieldName(final SqlStatementConfiguration configuration) {
        return constantSqlStatementFieldName(configuration) + "_RAW" + getVendor(configuration);
    }

    public static final String constantSqlStatementParameterIndexFieldName(
            final SqlStatementConfiguration configuration) {
        return constantSqlStatementFieldName(configuration) + "_PARAMETER_INDEX";
    }

    private static final String getVendor(final SqlStatementConfiguration configuration) {
        return configuration.getVendor() == null ? "" : "_" + configuration.getVendor().replace(" ", "_").toUpperCase();
    }

}
