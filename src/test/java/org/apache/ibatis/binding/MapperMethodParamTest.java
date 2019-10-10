/**
 * Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.binding;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperMethodParamTest {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setup() throws Exception {
//        DataSource dataSource1 = BaseDataTest.createUnpooledDataSource(BaseDataTest.BLOG_PROPERTIES);
//        BaseDataTest.runScript(dataSource1, "org/apache/ibatis/binding/paramtest-schema.sql");

        String resource = "org/apache/ibatis/databases/mysql/mysql-jdbc.properties";
        Properties props = Resources.getResourceAsProperties(resource);
        UnpooledDataSource dataSource = new UnpooledDataSource();
        dataSource.setDriver(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(Mapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Test
    public void parameterNameIsSizeAndTypeIsLong() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Mapper mapper = session.getMapper(Mapper.class);
            long maxValue = Long.MAX_VALUE;
            mapper.insert("foo", maxValue);
            long size = mapper.selectSize("foo");
            System.out.println("结果：" + size);
            assertThat(size).isEqualTo(maxValue);
        }
    }

    @Test
    public void parameterNameIsSizeUsingHashMap() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            HashMap<String, Object> params = new HashMap<>();
            long maxValue = Long.MAX_VALUE;
            params.put("id", "foo");
            params.put("size", maxValue);
            Mapper mapper = session.getMapper(Mapper.class);
            mapper.insertUsingHashMap(params);
            long size = mapper.selectSize("foo");
            System.out.println("结果：" + size);
            assertThat(size).isEqualTo(maxValue);
        }
    }

    interface Mapper {
        @Insert("insert into param_test (id, size) values(#{id}, #{size})")
        void insert(@Param("id") String id, @Param("size") long size);

        @Insert("insert into param_test (id, size) values(#{id}, #{size})")
        void insertUsingHashMap(HashMap<String, Object> params);

        @Select("select size from param_test where id = #{id}")
        long selectSize(@Param("id") String id);
    }
}
