/*
 * Copyright (C) 2004 - 2014 Brian McCallister
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skife.jdbi.v2;

import org.junit.Assert;
import org.junit.Test;
import org.skife.jdbi.v2.util.IntegerColumnMapper;

import java.util.Arrays;
import java.util.List;

public class TestPreparedBatchGenerateKeys {

    @Test
    public void testBatchInsertWithKeyGeneration() throws Exception {

        DBI dbi = new DBI("jdbc:hsqldb:mem:jdbi-batch-keys-test", "sa", "");

        Handle h = dbi.open();

        h.execute("create table something (id integer not null generated by default as identity (start with 10000), name varchar(50) )");

        PreparedBatch batch = h.prepareBatch("insert into something (name) values (?)");
        batch.add("Brian");
        batch.add("Thom");
        List<Integer> ids = batch.executeAndGenerateKeys(IntegerColumnMapper.WRAPPER).list();
        Assert.assertEquals(Arrays.asList(10000, 10001), ids);

        List<Something> somethings = h.createQuery("select id, name from something")
                .map(Something.class)
                .list();
        h.close();

        Assert.assertEquals(Arrays.asList(new Something(10000, "Brian"), new Something(10001, "Thom")), somethings);
    }
}
