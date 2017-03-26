/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package de.xn__ho_hia.yosql.generator;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import de.xn__ho_hia.yosql.model.ExecutionErrors;

/**
 * Writes a single {@link TypeSpec type} into a directory.
 */
public class TypeWriter {

    private final ExecutionErrors errors;

    /**
     * @param errors
     *            The errors to use.
     */
    @Inject
    public TypeWriter(final ExecutionErrors errors) {
        this.errors = errors;
    }

    /**
     * @param baseDirectory
     *            The base directory to use.
     * @param packageName
     *            The package name to use.
     * @param typeSpec
     *            The type specification to write.
     */
    public void writeType(
            final Path baseDirectory,
            final String packageName,
            final TypeSpec typeSpec) {
        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(baseDirectory);
            // log().info(String.format("Generated [%s.%s]", packageName, typeSpec.name));
        } catch (final IOException exception) {
            errors.add(exception);
            // log().error(String.format("Could not write [%s.%s] into [%s]",
            // packageName, typeSpec.name, baseDirectory));
        }
    }

}
