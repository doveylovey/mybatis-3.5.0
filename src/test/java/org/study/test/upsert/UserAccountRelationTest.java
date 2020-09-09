package org.study.test.upsert;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

public class UserAccountRelationTest {
    private static SqlSessionFactory sqlSessionFactory;
    LocalDateTime nowDateTime = LocalDateTime.now();

    @BeforeAll
    public static void setUp() throws Exception {
        String resource = "org/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
                System.out.println("开始：删除表 -> 创建表 -> 初始化表");
                mapper.deleteTable();
                mapper.createTable();
                int insertData = mapper.insertData();
                System.out.println("结束：删除表 -> 创建表 -> 初始化表，结果：" + insertData);
            }
        }
    }

    @Test
    public void testInsertIgnoreInto() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
            UserAccountRelation query = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(1)
                    .build();
            List<UserAccountRelation> oldRecord = mapper.selectBySelective(query);
            System.out.println("原数据：" + oldRecord);
            UserAccountRelation relation = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(1)
                    .gmtCreate(nowDateTime)
                    .gmtUpdate(nowDateTime)
                    .build();
            int insert = mapper.insertIgnoreInto(relation);
            System.out.println("使用 insert ignore into 的插入结果：" + insert);
            List<UserAccountRelation> newRecord = mapper.selectBySelective(query);
            System.out.println("新数据：" + newRecord);
        }
    }

    @Test
    public void testInsertOnDuplicateKeyUpdate() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
            UserAccountRelation query = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    //.status(1)
                    .build();
            List<UserAccountRelation> oldRecord = mapper.selectBySelective(query);
            System.out.println("原数据：" + oldRecord);
            UserAccountRelation relation = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(0)
                    .gmtCreate(nowDateTime)
                    .gmtUpdate(nowDateTime)
                    .build();
            int insert = mapper.insertOnDuplicateKeyUpdate(relation);
            System.out.println("使用 on duplicate key update 的插入结果：" + insert);
            List<UserAccountRelation> newRecord = mapper.selectBySelective(query);
            System.out.println("新数据：" + newRecord);
        }
    }

    @Test
    public void testInsertOnDuplicateKeyUpdate2() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
            UserAccountRelation query = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    //.status(1)
                    .build();
            List<UserAccountRelation> oldRecord = mapper.selectBySelective(query);
            System.out.println("原数据：" + oldRecord);
            UserAccountRelation relation = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(0)
                    .gmtCreate(nowDateTime)
                    .gmtUpdate(nowDateTime)
                    .build();
            int insert = mapper.insertOnDuplicateKeyUpdate2(relation);
            System.out.println("使用 on duplicate key update 的插入结果：" + insert);
            List<UserAccountRelation> newRecord = mapper.selectBySelective(query);
            System.out.println("新数据：" + newRecord);
        }
    }

    @Test
    public void testInsertReplaceInto() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
            UserAccountRelation query = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(1)
                    .build();
            List<UserAccountRelation> oldRecord = mapper.selectBySelective(query);
            System.out.println("原数据：" + oldRecord);
            UserAccountRelation relation = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(1)
                    .gmtCreate(nowDateTime)
                    .gmtUpdate(nowDateTime)
                    .build();
            int insert = mapper.insertReplaceInto(relation);
            System.out.println("使用 replace into 的插入结果：" + insert);
            List<UserAccountRelation> newRecord = mapper.selectBySelective(query);
            System.out.println("新数据：" + newRecord);
        }
    }

    @Test
    public void testInsertIfNotExists() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserAccountRelationMapper mapper = sqlSession.getMapper(UserAccountRelationMapper.class);
            UserAccountRelation query = UserAccountRelation.builder()
                    .userId(10086)
                    .accountId(1008611)
                    .status(1)
                    .build();
            List<UserAccountRelation> oldRecord = mapper.selectBySelective(query);
            System.out.println("原数据：" + oldRecord);
            UserAccountRelation relation = UserAccountRelation.builder()
                    .userId(1008611)
                    .accountId(1008611)
                    .status(1)
                    .gmtCreate(nowDateTime)
                    .gmtUpdate(nowDateTime)
                    .build();
            int insert = mapper.insertIfNotExists(relation);
            System.out.println("使用 insert if not exists 的插入结果：" + insert);
            List<UserAccountRelation> newRecord = mapper.selectBySelective(query);
            System.out.println("新数据：" + newRecord);
        }
    }
}
