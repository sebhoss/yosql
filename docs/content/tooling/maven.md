---
title: Maven
date: 2019-06-16T18:23:40+02:00
menu:
  main:
    parent: Tooling
categories:
  - Tooling
tags:
  - Maven
---

## Getting Started

1) Add the plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>wtf.metio.yosql</groupId>
            <artifactId>yosql-maven-plugin</artifactId>
        </plugin>
        ...
    </plugins>
</build>
```

2) Add .sql files in *src/main/yosql* and write SQL statements into them.

```
<project_root>/
├── pom.xml
└── src/
    └── main/
        └── yosql/
            └── domainObject/
                ├── yourQuery.sql
                └── changeYourData.sql
            └── aggregateRoot/
                ├── anotherQuery.sql
                └── addData.sql
```

3) Execute the *yosql:generate* goal (or just run `mvn generate-sources`) to generate the Java code.

## Configuration

You can configure how YoSQL operates and how the generated code like by using the default Maven configuration 
mechanism (TODO: add link). Take a look at the [available configuration options](../configuration/) in order to see what can be 
configured.

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>wtf.metio.yosql</groupId>
            <artifactId>yosql-maven-plugin</artifactId>
            <configuration>
              <repositories>
                <basePackageName>your.domain.persistence</basePackageName>
              </repositories>
            </configuration>
        </plugin>
        ...
    </plugins>
</build>
```

### Multiple Configurations

In some cases it might be preferable to generate some repositories (TODO: link to glossary) with a specific set of configuration options while using another set for other repositories. There are several ways how this can be accomplished:

1. Place SQL files in different Maven modules.
2. Use a single module with multiple `execution` configurations.
3. Override configuration for individual SQL statements.

#### Multiple `execution`s

Make sure that multiple executions do not make use of the same .sql files. Otherwise, the executions will overwrite 
the generated code of each other. The last execution will win. Share configuration across all executions by using a single top level `configuration` block.

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>wtf.metio.yosql</groupId>
            <artifactId>yosql-maven-plugin</artifactId>
            <configuration>
              <repositories>
                <basePackageName>your.domain.persistence</basePackageName>
              </repositories>
            </configuration>
            <executions>
                <execution>
                    <id>config-a</id>
                    <configuration>
                      <files>
                        <inputBaseDirectory>src/main/database/reactive</inputBaseDirectory>
                      </files>
                      <repositories>
                        <generateRxJavaApi>true</generateRxJavaApi>
                      </repositories>
                    </configuration>
                </execution>
            </executions>
            <executions>
                <execution>
                    <id>config-b</id>
                    <configuration>
                      <files>
                        <inputBaseDirectory>src/main/database/synchronous</inputBaseDirectory>
                      </files>
                      <java>
                        <apiVersion>15</apiVersion>
                      </java>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```
