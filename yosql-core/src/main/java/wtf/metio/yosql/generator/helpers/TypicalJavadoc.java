/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql.generator.helpers;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import de.xn__ho_hia.javapoet.TypeGuesser;
import wtf.metio.yosql.model.ResultRowConverter;
import wtf.metio.yosql.model.SqlConfiguration;
import wtf.metio.yosql.model.SqlStatement;

import java.util.List;
import java.util.Objects;

/**
 * Typical Javadocs comments in the domain of yosql.
 */
public final class TypicalJavadoc {

    private TypicalJavadoc() {
        // utility class
    }

    /**
     * Creates the typical javadoc documentation for generated methods.
     *
     * @param statements
     *            The vendor statements of the method.
     * @return The javadoc for a single method based on the given statements.
     */
    public static CodeBlock methodJavadoc(final List<SqlStatement> statements) {
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
        builder.add("\n<p>Generated based on the following file(s):</p>\n")
                .add("<ul>\n");
        statements.stream()
                .map(SqlStatement::getSourcePath)
                .distinct()
                .forEach(path -> builder.add("<li>$L</li>\n", path));
        builder.add("</ul>\n");
        statements.stream()
                .map(SqlStatement::getConfiguration)
                .map(SqlConfiguration::getResultRowConverter)
                .filter(Objects::nonNull)
                .map(ResultRowConverter::getResultType)
                .filter(Objects::nonNull)
                .filter(type -> !type.startsWith("java"))
                .map(type -> type.substring(0, type.contains("<") ? type.indexOf("<") : type.length()))
                .distinct()
                .map(TypeGuesser::guessTypeName)
                .filter(type -> !type.isPrimitive())
                .filter(type -> !type.isBoxedPrimitive())
                .forEach(type -> builder.add("\n@see $S", type));
        return builder.build();
    }

    /**
     * Creates the typical javadoc documentation for generated repositories.
     *
     * @param statements
     *            The vendor statements of the repository.
     * @return The class javadoc for a repository.
     */
    public static CodeBlock repositoryJavadoc(final List<SqlStatement> statements) {
        final Builder builder = CodeBlock.builder()
                .add("Generated based on the following file(s):\n")
                .add("<ul>\n");
        statements.stream()
                .map(SqlStatement::getSourcePath)
                .distinct()
                .forEach(path -> builder.add("<li>$L</li>\n", path));
        builder.add("</ul>\n");
        return builder.build();
    }

}