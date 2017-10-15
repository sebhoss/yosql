/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package de.xn__ho_hia.yosql.generator.helpers;

import java.util.List;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;

import de.xn__ho_hia.yosql.model.SqlStatement;

/**
 * Typical Javadocs comments in the domain of yosql.
 */
public final class TypicalJavadoc {

    private TypicalJavadoc() {
        // utility class
    }

    /**
     * @param statements
     *            The vendor statements of the method.
     * @return The javadoc for a single method based on the given statements.
     */
    @SuppressWarnings("nls")
    public static CodeBlock javadoc(final List<SqlStatement> statements) {
        final Builder builder = CodeBlock.builder();
        if (statements.size() > 1) {
            builder.add("<p>Executes one of the following statements:</p>");
        } else {
            builder.add("<p>Executes the following statement:</p>");
        }
        for (final SqlStatement statement : statements) {
            if (statement.getConfiguration().getVendor() == null) {
                if (statements.size() > 1) {
                    builder.add("<p>Fallback:</p>");
                }
            } else {
                builder.add("<p>Vendor: $N", statement.getConfiguration().getVendor());
            }
            builder.add("\n<pre>\n$L</pre>", statement.getRawStatement());
        }
        builder.add("\n<p>Generated based on the following files:</p>\n")
                .add("<ul>\n");
        statements.stream()
                .map(SqlStatement::getSourcePath)
                .distinct()
                .forEach(path -> builder.add("<li>$L</li>\n", path));
        builder.add("</ul>\n");
        return builder.build();
    }

}