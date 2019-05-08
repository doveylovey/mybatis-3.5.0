/**
 * Copyright 2009-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.submitted.call_setters_on_nulls_again;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MyBatisTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        // create an SqlSessionFactory
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/call_setters_on_nulls_again/mybatis-config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Test
    public void test() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ParentBean parentBean = session.selectOne("test");
            Assertions.assertEquals("p1", parentBean.getName());
        }
    }

}
