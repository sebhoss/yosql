/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package de.xn__ho_hia.yosql.generator.logging.log4j;

import java.util.Optional;

import javax.inject.Inject;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.xn__ho_hia.yosql.generator.helpers.TypicalFields;
import de.xn__ho_hia.yosql.generator.helpers.TypicalNames;
import de.xn__ho_hia.yosql.generator.logging.shared.AbstractLoggingGenerator;

@Log4j
@SuppressWarnings({ "nls", "javadoc" })
public final class Log4jLoggingGenerator extends AbstractLoggingGenerator {

    private final TypicalFields fields;

    @Inject
    public Log4jLoggingGenerator(final TypicalFields fields) {
        this.fields = fields;
    }

    @Override
    public Optional<FieldSpec> logger(final TypeName repoClass) {
        return Optional.of(fields.prepareConstant(getClass(), Logger.class, TypicalNames.LOGGER)
                .initializer("$T.getLogger($T.class)", LogManager.class, repoClass)
                .build());
    }

    @Override
    public CodeBlock shouldLog() {
        return CodeBlock.builder().add("$N.isEnabled($T.INFO)", TypicalNames.LOGGER, Level.class).build();
    }

    @Override
    public CodeBlock entering(final String repository, final String method) {
        return CodeBlock.builder()
                .addStatement("$N.debug(() -> $T.format($S, $S, $S))", TypicalNames.LOGGER, String.class,
                        "Entering [%s#%s]", repository, method)
                .build();
    }

}
