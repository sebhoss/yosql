/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package com.github.sebhoss.yosql.plugin;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Preconditions for plugin execution.
 */
@Named
@Singleton
public class PluginPreconditions {

    private final PluginErrors pluginErrors;

    /**
     * @param pluginErrors
     *            The plugin error handler to use.
     */
    @Inject
    public PluginPreconditions(final PluginErrors pluginErrors) {
        this.pluginErrors = pluginErrors;
    }

    /**
     * Asserts that a single directory is writable. In order to be writable, the directory has to:
     * <ul>
     * <li>exist</li>
     * <li>be a directory (not a file)</li>
     * <li>be writable by the current process</li>
     * </ul>
     *
     * @param directory
     *            The directory to check
     * @throws MojoExecutionException
     *             In case the directory is somehow not writeable.
     */
    @SuppressWarnings("nls")
    public void assertDirectoryIsWriteable(final File directory) throws MojoExecutionException {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                pluginErrors.buildError("Could not create [%s]. Check the permissions.", directory);
            }
        }
        if (!directory.isDirectory()) {
            pluginErrors.buildError("[%s] is not a directory.", directory);
        }
        if (!directory.canWrite()) {
            pluginErrors.buildError("Don't have permission to write to [%s].", directory);
        }
    }

}
