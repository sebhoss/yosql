package com.github.sebhoss.yosql.generator;

import java.util.List;

import com.github.sebhoss.yosql.model.SqlStatement;
import com.squareup.javapoet.MethodSpec;

public interface MethodGenerator {

    Iterable<MethodSpec> asMethods(List<SqlStatement> sqlStatementsInRepository);

}
