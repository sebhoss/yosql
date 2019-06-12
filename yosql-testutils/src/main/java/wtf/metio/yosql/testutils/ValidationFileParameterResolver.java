/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql.testutils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit5 {@link ParameterResolver} for {@link ValidationFile}s.
 */
public final class ValidationFileParameterResolver implements ParameterResolver {

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        final Optional<Class<?>> testClass = extensionContext.getTestClass();
        final Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testClass.isPresent() && testMethod.isPresent()) {
            final String fileName = testClass.get().getSimpleName() + "#" + testMethod.get().getName() + ".txt";
            return new TxtValidationFile(fileName);
        }
        throw new IllegalStateException("No test class or test method found.");
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        final Parameter parameter = parameterContext.getParameter();
        final Class<?> type = parameter.getType();
        return ValidationFile.class.isAssignableFrom(type);
    }

    private static final class TxtValidationFile implements ValidationFile {

        private final String fileName;

        public TxtValidationFile(final String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String read(final Charset charset) {
            final Path pathToValidationFile = lookupFile();
            return read(pathToValidationFile, charset);
        }

        private Path lookupFile() {
            final String resourceDir = "src/test/resources/";
            String path = System.getenv("TEST_SRCDIR");
            if (path == null) {
                path = resourceDir;
            } else {
                path = Paths.get(path, "/__main__/yosql-core/" + resourceDir).toString();
            }
            return Paths.get(path, fileName);
        }

        private static String read(final Path pathToValidationFile, final Charset charset) {
            try {
                final byte[] readAllBytes = Files.readAllBytes(pathToValidationFile);
                return new String(readAllBytes, charset);
            } catch (final IOException exception) {
                throw new IllegalArgumentException(exception);
            }
        }

    }

}