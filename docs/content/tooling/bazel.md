---
title: Bazel
date: 2019-06-16T18:22:51+02:00
menu:
  main:
    parent: Tooling
categories:
  - Tooling
tags:
  - Bazel
---

1) Add git repository to your `WORKSPACE`:

```
git_repository(
    name = "yosql",
    remote = "https://github.com/sebhoss/yosql.git",
    tag = "0.0.1-bazel",
)
```

2) Write .sql files in a directory of your choice (e.g. `persistence`)

```
project/
├── WORKSPACE
└── persistence/
    ├── BUILD
    └── user/
        ├── findUser.sql
        └── addUser.sql
    └── item/
        ├── queryAllItems.sql
        └── createItemTable.sql
```

3) Declare a `genrule` in one of your BUILD files:

```
filegroup(
  name = "your-sql-files",
  srcs = glob(["persistence/**/*.sql"]),
)

genrule(
  name = "yosql-example",
  srcs = [":your-sql-files"],
  outs = [
    "com/example/persistence/UserRepository.java",
    "com/example/persistence/ItemRepository.java",
    "com/example/persistence/util/ResultRow.java",
    "com/example/persistence/util/ResultState.java",
    "com/example/persistence/util/FlowState.java",
    "com/example/persistence/converter/ToResultRowConverter.java",
  ],
  cmd = """
    $(location @yosql//yosql-cli) \
      --inputBaseDirectory persistence \
      --outputBaseDirectory $(@D) \
      --logLevel off
  """,
  tools = ["@yosql//yosql-cli"],
)
```

4) Depend on the generated sources by using `:yosql-example` in the `srcs` of another rule.
