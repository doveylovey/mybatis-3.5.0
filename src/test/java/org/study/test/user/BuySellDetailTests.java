package org.study.test.user;

import org.study.test.util.SnowFlakeIdWorker;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.Date;

public class BuySellDetailTests {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        String resource = "org/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            /*try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                String tableName = "t_user";
                int tableCount = userMapper.findTable(tableName);
                if (tableCount > 0) {
                    System.out.println("表【" + tableName + "】已存在，即将删除");
                    userMapper.dropTable(tableName);
                }
                userMapper.createTable(tableName);
            }*/
        }
    }

    @Test
    public void testUser01() {
        // 注意：通过这种方式打开的 SqlSession，autoCommit 默认为 false，需要手动提交事务，即 sqlSession.commit();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final BuySellDetailMapper buySellDetailMapper = sqlSession.getMapper(BuySellDetailMapper.class);
            Date nowDate = new Date();
            BuySellDetail buySellDetail = BuySellDetail.builder()
                    .rentMerchantCode(SnowFlakeIdWorker.genId())
                    .rentMerchantName("租借商户名称22222")
                    .rentMerchantAddress("租借商户地址")
                    .returnMerchantCode(SnowFlakeIdWorker.genId())
                    .returnMerchantName("归还商户名称")
                    .returnMerchantAddress("归还商户地址")
                    .consumerOrderNo(String.valueOf(SnowFlakeIdWorker.genId()))
                    .gmtCreate(nowDate)
                    .gmtModified(nowDate)
                    .build();
            int insertOrUpdate = buySellDetailMapper.insertOrUpdate(buySellDetail);
            System.out.println(insertOrUpdate > 0 ? "成功" : "失败");
            System.out.println(buySellDetail);
        }
    }

    @Test
    public void testUser02() {
        // 注意：通过这种方式打开的 SqlSession，autoCommit 为 true，会自动提交事务
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final BuySellDetailMapper buySellDetailMapper = sqlSession.getMapper(BuySellDetailMapper.class);
            Date nowDate = new Date();
            BuySellDetail buySellDetail = BuySellDetail.builder()
                    .rentMerchantCode(SnowFlakeIdWorker.genId())
                    .rentMerchantName("xg-租借商户名称")
                    .rentMerchantAddress("xg-租借商户地址")
                    .returnMerchantCode(SnowFlakeIdWorker.genId())
                    .returnMerchantName("xg-归还商户名称")
                    .returnMerchantAddress("xg-归还商户地址")
                    .consumerOrderNo("702483150251819010")
                    .gmtCreate(nowDate)
                    .gmtModified(nowDate)
                    .build();
            int insertOrUpdate = buySellDetailMapper.insertOrUpdate(buySellDetail);
            System.out.println(insertOrUpdate > 0 ? "成功" : "失败");
            System.out.println(buySellDetail);
        }
    }
}
