package com.github.sebhoss.yosql;

import java.io.File;
import java.util.function.Supplier;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.logging.Log;

@Named
@Singleton
public class PluginRuntimeConfig {

    private FileSet       sqlFiles;
    private File          outputBaseDirectory;
    private String        repositorySqlStatements;
    private boolean       generateBatchApi;
    private boolean       generateRxJavaApi;
    private String        methodBatchPrefix;
    private boolean       generateStreamEagerApi;
    private boolean       generateStreamLazyApi;
    private String        methodStreamPrefix;
    private String        methodStreamSuffix;
    private String        methodRxJavaPrefix;
    private String        methodRxJavaSuffix;
    private String        repositoryNameSuffix;
    private String        methodLazyName;
    private String        methodEagerName;
    private boolean       generateStandardApi;
    private String        utilityPackageName;
    private String        basePackageName;
    private Supplier<Log> logSupplier;
    private String        methodBatchSuffix;
    private String[]      allowedWritePrefixes;
    private String[]      allowedReadPrefixes;
    private boolean       validateMethodNamePrefixes;

    /**
     * @return the outputBaseDirectory
     */
    public File getOutputBaseDirectory() {
        return outputBaseDirectory;
    }

    /**
     * @param outputBaseDirectory
     *            the outputBaseDirectory to set
     */
    public void setOutputBaseDirectory(final File outputBaseDirectory) {
        this.outputBaseDirectory = outputBaseDirectory;
    }

    /**
     * @return How SQL statements are to be included in generated repositories.
     */
    public String getRepositorySqlStatements() {
        return repositorySqlStatements;
    }

    /**
     * @param repositorySqlStatements
     *            How SQL statements are to be included in generated
     *            repositories.
     */
    public void setRepositorySqlStatements(final String repositorySqlStatements) {
        this.repositorySqlStatements = repositorySqlStatements;
    }

    /**
     * @return the generateBatchApi
     */
    public boolean isGenerateBatchApi() {
        return generateBatchApi;
    }

    /**
     * @param generateBatchApi
     *            the generateBatchApi to set
     */
    public void setGenerateBatchApi(final boolean generateBatchApi) {
        this.generateBatchApi = generateBatchApi;
    }

    /**
     * @return the generateRxJavaApi
     */
    public boolean isGenerateRxJavaApi() {
        return generateRxJavaApi;
    }

    /**
     * @param generateRxJavaApi
     *            the generateRxJavaApi to set
     */
    public void setGenerateRxJavaApi(final boolean generateRxJavaApi) {
        this.generateRxJavaApi = generateRxJavaApi;
    }

    /**
     * @return the batch prefix
     */
    public String getMethodBatchPrefix() {
        return methodBatchPrefix;
    }

    /**
     * @param methodBatchPrefix
     *            the batch prefix to set
     */
    public void setMethodBatchPrefix(final String methodBatchPrefix) {
        this.methodBatchPrefix = methodBatchPrefix;
    }

    /**
     * @return the method stream prefix
     */
    public String getMethodStreamPrefix() {
        return methodStreamPrefix;
    }

    /**
     * @param methodStreamPrefix
     *            the method stream prefix to set
     */
    public void setMethodStreamPrefix(final String methodStreamPrefix) {
        this.methodStreamPrefix = methodStreamPrefix;
    }

    /**
     * @return the method stream suffix
     */
    public String getMethodStreamSuffix() {
        return methodStreamSuffix;
    }

    /**
     * @param methodStreamSuffix
     *            the method stream suffix to set
     */
    public void setMethodStreamSuffix(final String methodStreamSuffix) {
        this.methodStreamSuffix = methodStreamSuffix;
    }

    /**
     * @return the RxJava prefix
     */
    public String getMethodRxJavaPrefix() {
        return methodRxJavaPrefix;
    }

    /**
     * @param methodRxJavaPrefix
     *            the RxJava prefix to set
     */
    public void setMethodRxJavaPrefix(final String methodRxJavaPrefix) {
        this.methodRxJavaPrefix = methodRxJavaPrefix;
    }

    /**
     * @return the method RxJava suffix
     */
    public String getMethodRxJavaSuffix() {
        return methodRxJavaSuffix;
    }

    /**
     * @param methodRxJavaSuffix
     *            the method RxJava suffix to set
     */
    public void setMethodRxJavaSuffix(final String methodRxJavaSuffix) {
        this.methodRxJavaSuffix = methodRxJavaSuffix;
    }

    /**
     * @return the repositoryNameSuffix
     */
    public String getRepositoryNameSuffix() {
        return repositoryNameSuffix;
    }

    /**
     * @param repositoryNameSuffix
     *            the repositoryNameSuffix to set
     */
    public void setRepositoryNameSuffix(final String repositoryNameSuffix) {
        this.repositoryNameSuffix = repositoryNameSuffix;
    }

    /**
     * @return the generateStreamEagerApi
     */
    public boolean isGenerateStreamEagerApi() {
        return generateStreamEagerApi;
    }

    /**
     * @param generateStreamEagerApi
     *            the generateStreamEagerApi to set
     */
    public void setGenerateStreamEagerApi(final boolean generateStreamEagerApi) {
        this.generateStreamEagerApi = generateStreamEagerApi;
    }

    /**
     * @return the generateStreamLazyApi
     */
    public boolean isGenerateStreamLazyApi() {
        return generateStreamLazyApi;
    }

    /**
     * @param generateStreamLazyApi
     *            the generateStreamLazyApi to set
     */
    public void setGenerateStreamLazyApi(final boolean generateStreamLazyApi) {
        this.generateStreamLazyApi = generateStreamLazyApi;
    }

    /**
     * @return the method lazy name
     */
    public String getMethodLazyName() {
        return methodLazyName;
    }

    /**
     * @param methodLazyName
     *            the method lazy name to set
     */
    public void setMethodLazyName(final String methodLazyName) {
        this.methodLazyName = methodLazyName;
    }

    /**
     * @return the method eager name
     */
    public String getMethodEagerName() {
        return methodEagerName;
    }

    /**
     * @param methodEagerName
     *            the method eager name to set
     */
    public void setMethodEagerName(final String methodEagerName) {
        this.methodEagerName = methodEagerName;
    }

    /**
     * @return the sqlFiles
     */
    public FileSet getSqlFiles() {
        return sqlFiles;
    }

    public void setGenerateStandardApi(final boolean generateStandardApi) {
        this.generateStandardApi = generateStandardApi;
    }

    /**
     * @return the generateStandardApi
     */
    public boolean isGenerateStandardApi() {
        return generateStandardApi;
    }

    /**
     * @return the utilityPackageName
     */
    public String getUtilityPackageName() {
        return utilityPackageName;
    }

    /**
     * @param utilityPackageName
     *            the utilityPackageName to set
     */
    public void setUtilityPackageName(final String utilityPackageName) {
        this.utilityPackageName = utilityPackageName;
    }

    /**
     * @return the basePackageName
     */
    public String getBasePackageName() {
        return basePackageName;
    }

    /**
     * @param basePackageName
     *            the basePackageName to set
     */
    public void setBasePackageName(final String basePackageName) {
        this.basePackageName = basePackageName;
    }

    public void setLogger(final Supplier<Log> logSupplier) {
        this.logSupplier = logSupplier;

    }

    public Log getLogger() {
        return logSupplier.get();
    }

    public void setMethodBatchSuffix(final String methodBatchSuffix) {
        this.methodBatchSuffix = methodBatchSuffix;
    }

    public String getMethodBatchSuffix() {
        return methodBatchSuffix;
    }

    /**
     * @return the allowedWritePrefixes
     */
    public String[] getAllowedWritePrefixes() {
        return allowedWritePrefixes;
    }

    /**
     * @param allowedWritePrefixes
     *            the allowedWritePrefixes to set
     */
    public void setAllowedWritePrefixes(final String[] allowedWritePrefixes) {
        this.allowedWritePrefixes = allowedWritePrefixes;
    }

    /**
     * @return the allowedReadPrefixes
     */
    public String[] getAllowedReadPrefixes() {
        return allowedReadPrefixes;
    }

    /**
     * @param allowedReadPrefixes
     *            the allowedReadPrefixes to set
     */
    public void setAllowedReadPrefixes(final String[] allowedReadPrefixes) {
        this.allowedReadPrefixes = allowedReadPrefixes;
    }

    /**
     * @return the validateMethodNamePrefixes
     */
    public boolean isValidateMethodNamePrefixes() {
        return validateMethodNamePrefixes;
    }

    /**
     * @param validateMethodNamePrefixes
     *            the validateMethodNamePrefixes to set
     */
    public void setValidateMethodNamePrefixes(final boolean validateMethodNamePrefixes) {
        this.validateMethodNamePrefixes = validateMethodNamePrefixes;
    }
}
