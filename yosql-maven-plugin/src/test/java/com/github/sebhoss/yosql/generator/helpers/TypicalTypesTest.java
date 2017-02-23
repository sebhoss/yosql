/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package com.github.sebhoss.yosql.generator.helpers;

import com.squareup.javapoet.TypeName;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 *
 */
@SuppressWarnings({ "nls", "static-method" })
public class TypicalTypesTest {

    /**
     *
     */
    @Test
    public void shouldGuessArrayType() {
        // given
        final String givenType = "java.lang.Object[]";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

    /**
     *
     */
    @Test
    public void shouldGuessArrayTypeOfPrimitive() {
        // given
        final String givenType = "int[]";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

    /**
     *
     */
    @Test
    public void shouldGuessTypeOfGenericList() {
        // given
        final String givenType = "java.util.List<java.lang.Object>";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

    /**
     *
     */
    @Test
    public void shouldGuessTypeOfGenericListOfGenericType() {
        // given
        final String givenType = "java.util.List<java.lang.Object<java.lang.Integer>>";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

    /**
     *
     */
    @Test
    public void shouldGuessTypeOfGenericMap() {
        // given
        final String givenType = "java.util.Map<java.lang.String, java.lang.Object>";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

    /**
     *
     */
    @Test
    @Ignore
    public void shouldGuessTypeOfGenericMapOfGenericMap() {
        // given
        final String givenType = "java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Object>>";

        // when
        final TypeName guessedType = TypicalTypes.guessTypeName(givenType);

        // then
        Assert.assertEquals(givenType, guessedType.toString());
    }

}
