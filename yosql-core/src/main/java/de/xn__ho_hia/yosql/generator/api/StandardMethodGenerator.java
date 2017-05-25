/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package de.xn__ho_hia.yosql.generator.api;

import java.util.List;

import com.squareup.javapoet.MethodSpec;

import de.xn__ho_hia.yosql.model.SqlConfiguration;
import de.xn__ho_hia.yosql.model.SqlStatement;

/**
 * Generates 'standard' methods - implementation decides whatever that means.
 */
public interface StandardMethodGenerator {

    /**
     * Generates code the execute a "standard" read against the database using the most common way the configured API
     * executes reads. The <code>javax.sql</code> package for example calls
     * {@link java.sql.PreparedStatement#executeQuery()}.
     *
     * @param configuration
     *            The configuration for the generated method.
     * @param vendorStatements
     *            The vendor statements for the generated method.
     * @return A method specification for a standard reading method.
     */
    MethodSpec standardReadMethod(
            SqlConfiguration configuration,
            List<SqlStatement> vendorStatements);

    /**
     * Generates code the execute a "standard" write against the database using the most common way the configured API
     * executes writes. The <code>javax.sql</code> package for example calls
     * {@link java.sql.PreparedStatement#executeUpdate()}.
     *
     * @param configuration
     *            The configuration for the generated method.
     * @param vendorStatements
     *            The vendor statements for the generated method.
     * @return A method specification for a standard writing method.
     */
    MethodSpec standardWriteMethod(
            SqlConfiguration configuration,
            List<SqlStatement> vendorStatements);

    /**
     * Generates code the execute a "standard" call against the database using the most common way the configured API
     * executes calls database functions. The <code>javax.sql</code> package for example calls
     * {@link java.sql.CallableStatement#executeQuery()}.
     *
     * @param configuration
     *            The configuration for the generated method.
     * @param vendorStatements
     *            The vendor statements for the generated method.
     * @return A method specification for a standard calling method.
     */
    MethodSpec standardCallMethod(
            SqlConfiguration configuration,
            List<SqlStatement> vendorStatements);

}
