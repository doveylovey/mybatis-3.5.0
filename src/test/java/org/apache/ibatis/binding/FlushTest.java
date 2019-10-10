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

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Post;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FlushTest {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setup() throws Exception {
        DataSource dataSource1 = BaseDataTest.createBlogDataSource();

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
        configuration.setDefaultExecutorType(ExecutorType.BATCH);
        configuration.getTypeAliasRegistry().registerAlias(Post.class);
        configuration.getTypeAliasRegistry().registerAlias(Author.class);
        configuration.addMapper(BoundAuthorMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Test
    public void invokeFlushStatementsViaMapper() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoundAuthorMapper mapper = session.getMapper(BoundAuthorMapper.class);
            Author author = new Author(-1, "cbegin", "******", "cbegin@nowhere.com", "N/A", Section.NEWS);
            List<Integer> ids = new ArrayList<>();
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            // test
            List<BatchResult> results = mapper.flush();
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0).getUpdateCounts().length).isEqualTo(ids.size());
            for (int id : ids) {
                Author selectedAuthor = mapper.selectAuthor(id);
                System.out.println("=====> " + selectedAuthor);
                assertNotNull(selectedAuthor, id + " is not found.");
            }
            session.rollback();
        }
    }
}
