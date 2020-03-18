package org.study.test.user;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class UserTests {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        String resource = "org/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                String tableName = "t_user";
                int tableCount = userMapper.findTable(tableName);
                if (tableCount > 0) {
                    System.out.println("表【" + tableName + "】已存在，即将删除");
                    userMapper.dropTable(tableName);
                }
                userMapper.createTable(tableName);
            }
        }
    }

    @Test
    public void testUser01() {
        // 注意：通过这种方式打开的 SqlSession，autoCommit 默认为 false，需要手动提交事务，即 sqlSession.commit();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = User.builder()
                    .name("MyBatis自定义类型处理器-手动提交事务")
                    .password("123456")
                    .birthday(LocalDate.now())
                    .gender(true)
                    .email("typehandler@gmail.com")
                    .hobby(Arrays.asList("MyBatis", "Spring", "Spring Boot", "Spring Cloud"))
                    .gmtCreate(LocalDateTime.now())
                    .gmtUpdate(LocalDateTime.now())
                    .build();
            String result = userMapper.insert(user) > 0 ? "成功" : "失败";
            System.out.println("新增结果：" + result);
            sqlSession.commit();// MyBatis 默认不是自动提交事务的，需要手动提交
            user = userMapper.selectByPrimaryKey(user.getId());
            System.out.println("查询结果：" + user);
        }
    }

    @Test
    public void testUser02() {
        // 注意：通过这种方式打开的 SqlSession，autoCommit 为 true，会自动提交事务
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = User.builder()
                    .name("MyBatis自定义类型处理器-自动提交事务")
                    .password("123456")
                    .birthday(LocalDate.now())
                    .gender(true)
                    .email("typehandler@gmail.com")
                    .hobby(Arrays.asList("MyBatis", "Spring", "Spring Boot", "Spring Cloud"))
                    .gmtCreate(LocalDateTime.now())
                    .gmtUpdate(LocalDateTime.now())
                    .build();
            String result = userMapper.insert(user) > 0 ? "成功" : "失败";
            System.out.println("新增结果：" + result);
            user = userMapper.selectByPrimaryKey(user.getId());
            System.out.println("查询结果：" + user);
        }
    }

    @Test
    public void testUser03() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectByPrimaryKey(1L);
            System.out.println("查询结果：" + user);
        }
    }
}
