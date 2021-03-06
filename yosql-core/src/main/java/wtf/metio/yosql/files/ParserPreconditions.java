/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.yosql.files;

import java.nio.file.Path;

/**
 * Preconditions that have to be matched for SQL parsing to work.
 */
public interface ParserPreconditions {

    /**
     * Asserts that a single directory is writable. In order to be writable, the directory has to:
     * <ul>
     * <li>exist</li>
     * <li>be a directory (not a file)</li>
     * <li>be writable by the current process</li>
     * </ul>
     *
     * @param directory The directory to check
     */
    void assertDirectoryIsWriteable(Path directory);

    /**
     * Asserts that a single directory is writable. In order to be writable, the directory has to:
     * <ul>
     * <li>exist</li>
     * <li>be a directory (not a file)</li>
     * <li>be writable by the current process</li>
     * </ul>
     *
     * @param directory The directory to check
     */
    void assertDirectoryIsReadable(Path directory);

}
