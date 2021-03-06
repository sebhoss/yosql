---
title: Write Sql Files
date: 2019-06-16T18:33:06+02:00
menu:
  main:
    parent: SQL
categories:
  - SQL
tags:
  - files
---

```
<project_root>/
└── src/
    └── main/
        └── yosql/
            └── user/
                └── getAllUsers.sql
```

The SQL statements in your .sql files are just plain SQL, e.g. you can save the following statement in a file called
`getAllUsers.sql`:

```sql
SELECT  *
FROM    users
```

YoSQL will use the name of the file (`getAllUsers`) as the generated method name and the name of the enclosing folder
as the prefix for the generated repository. The final result is a new Java class called `UserRepository` which has a
method called `getAllUsers`.

## Using parameters

YoSQL supports named parameters only. They must have the form `:parameter`.

```
<project_root>/
└── src/
    └── main/
        └── yosql/
            └── user/
                ├── findUser.sql
                └── getAllUsers.sql
```

The newly added `findUser.sql` file could look like the following and causes YoSQL to add another method named
`findUser` to the same `UserRepository` class.

```sql
SELECT  *
FROM    users
WHERE   id = :userId
```

The `findUser` method will expect one parameter (`userId`).
