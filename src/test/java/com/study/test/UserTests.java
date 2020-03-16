package com.study.test;

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
        String resource = "com/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Test
    public void testUser01() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user1 = User.builder()
                    .name("name")
                    .password("password")
                    .birthday(LocalDate.now())
                    .gender(true)
                    .email("email")
                    .hobby(Arrays.asList("JAVA", "PHP", "PYTHON", ".NET", "AI"))
                    .gmtCreate(LocalDateTime.now())
                    .gmtUpdate(LocalDateTime.now())
                    .build();
            userMapper.insert(user1);
            final User user = userMapper.selectByPrimaryKey(1L);
            System.out.println("查询结果：" + user);
        }
    }
}
