package de.xn__ho_hia.yosql.generator.logging;

import java.util.Optional;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import de.xn__ho_hia.yosql.generator.api.LoggingGenerator;
import de.xn__ho_hia.yosql.model.ExecutionConfiguration;

/**
 * Delegates its work to the configured logging generator.
 */
final class DelegatingLoggingGenerator implements LoggingGenerator {

    private final ExecutionConfiguration config;
    private final LoggingGenerator       jdkLoggingGenerator;
    private final LoggingGenerator       log4jLoggingGenerator;
    private final LoggingGenerator       noOpLoggingGenerator;
    private final LoggingGenerator       slf4jLoggingGenerator;

    DelegatingLoggingGenerator(
            final ExecutionConfiguration config,
            final LoggingGenerator jdkLoggingGenerator,
            final LoggingGenerator log4jLoggingGenerator,
            final LoggingGenerator noOpLoggingGenerator,
            final LoggingGenerator slf4jLoggingGenerator) {
        this.config = config;
        this.jdkLoggingGenerator = jdkLoggingGenerator;
        this.log4jLoggingGenerator = log4jLoggingGenerator;
        this.noOpLoggingGenerator = noOpLoggingGenerator;
        this.slf4jLoggingGenerator = slf4jLoggingGenerator;

    }

    private LoggingGenerator log() {
        switch (config.loggingApi()) {
            case JDK:
                return jdkLoggingGenerator;
            case LOG4J:
                return log4jLoggingGenerator;
            case SLF4J:
                return slf4jLoggingGenerator;
            case NONE:
            default:
                return noOpLoggingGenerator;
        }
    }

    @Override
    public CodeBlock queryPicked(final String fieldName) {
        return log().queryPicked(fieldName);
    }

    @Override
    public CodeBlock indexPicked(final String fieldName) {
        return log().indexPicked(fieldName);
    }

    @Override
    public CodeBlock vendorQueryPicked(final String fieldName) {
        return log().vendorQueryPicked(fieldName);
    }

    @Override
    public CodeBlock vendorIndexPicked(final String fieldName) {
        return log().vendorIndexPicked(fieldName);
    }

    @Override
    public CodeBlock vendorDetected() {
        return log().vendorDetected();
    }

    @Override
    public CodeBlock executingQuery() {
        return log().executingQuery();
    }

    @Override
    public CodeBlock shouldLog() {
        return log().shouldLog();
    }

    @Override
    public boolean isEnabled() {
        return log().isEnabled();
    }

    @Override
    public Optional<FieldSpec> logger(final TypeName repoClass) {
        return log().logger(repoClass);
    }

    @Override
    public CodeBlock entering(final String repository, final String method) {
        return log().entering(repository, method);
    }

}
