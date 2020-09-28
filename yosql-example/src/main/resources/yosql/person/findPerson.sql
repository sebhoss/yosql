/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

--
-- name: findPerson
-- vendor: Microsoft SQL Server
-- #methodUknownVendorBehavior: *execute*|ignore|fail
-- parameters:
--   - name: name
--     type: java.lang.String
--
select  *
from    persons
where   name = :name
;

--
-- name: findPerson
-- vendor: H2
-- #methodUknownVendorBehavior: *execute*|ignore|fail|none
--
select  *
from    persons
where   name = :name
;

--
-- name: findPerson
-- #methodUknownVendorBehavior: *execute*|ignore|fail|none
--
select  *
from    persons
where   name = :name
;
