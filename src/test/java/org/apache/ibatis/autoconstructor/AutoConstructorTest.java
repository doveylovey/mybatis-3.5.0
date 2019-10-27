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
package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AutoConstructorTest {
    // 全局变量 sqlSessionFactory，用于获取 sqlSession 对象以便操作数据库
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        // 指定 mybatis-config.xml 配置文件位置
        String resource = "org/apache/ibatis/autoconstructor/mybatis-config.xml";
        // 以Reader对象形式返回类路径上的资源。使用 try-with-resource 语法可以优雅的关闭资源，jdk7开始新增的语法
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            // 根据 mybatis-config.xml 配置文件创建 sqlSessionFactory 对象。注：SqlSessionFactory 创建时实际上返回的是一个 DefaultSqlSessionFactory 对象
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        // 以下操作主要是为了创建表及添加数据
        Configuration configuration = sqlSessionFactory.getConfiguration();
        Environment environment = configuration.getEnvironment();
        DataSource dataSource = environment.getDataSource();
        // 初始化表并添加数据
        BaseDataTest.runScript(dataSource, "org/apache/ibatis/autoconstructor/CreateDB-mysql.sql");
        // populate in-memory database
        // BaseDataTest.runScript(dataSource, "org/apache/ibatis/autoconstructor/CreateDB.sql");
    }

    @Test
    public void fullyPopulatedSubject() {
        // 通过 SqlSessionFactory 获取 SqlSession 对象
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
            final Object subject = mapper.getSubject(1);
            System.out.println("查询结果：" + subject);
            assertNotNull(subject);
        }
    }

    @Test
    public void primitiveSubjects() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
            List<PrimitiveSubject> subjects = mapper.getSubjects();
            System.out.println("查询结果：");
            subjects.forEach(System.out::println);
            //assertThrows(PersistenceException.class, () -> mapper.getSubjects());
        }
    }

    @Test
    public void annotatedSubject() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
            verifySubjects(mapper.getAnnotatedSubjects());
        }
    }

    @Test
    public void badSubject() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
            //assertThrows(PersistenceException.class, () -> mapper.getBadSubjects());
        }
    }

    @Test
    public void extensiveSubject() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
            List<ExtensiveSubject> extensiveSubjectList = mapper.getExtensiveSubject();
            extensiveSubjectList.forEach(System.out::println);
            verifySubjects(extensiveSubjectList);
        }
    }

    private void verifySubjects(final List<?> subjects) {
        assertNotNull(subjects);
        Assertions.assertThat(subjects.size()).isEqualTo(3);
    }
}
