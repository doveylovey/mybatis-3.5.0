package org.study.test.user;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

/**
 * PageHelper 分页插件的使用示例
 * <p>
 * 参考 https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md
 */
public class UserPageTests {
    private Integer pageNum = 3;
    private Integer pageSize = 2;

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        String resource = "org/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    /**
     * RowBounds 实现分页
     */
    @Test
    public void testUser01() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // 注意 new RowBounds(offset, limit) 的参数
            RowBounds rowBounds = new RowBounds((pageNum - 1) * pageSize, pageSize);
            List<User> userList = userMapper.selectList(where, rowBounds);
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            System.out.println("RowBounds 实现分页：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * Mapper 接口方式的调用，推荐这种使用方式
     */
    @Test
    public void testUser02() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // 使用 PageHelper 分页插件
            PageHelper.startPage(pageNum, pageSize);
            List<User> userList = userMapper.selectList(where);
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            System.out.println("Mapper 接口方式的调用：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * Mapper 接口方式的调用，推荐这种使用方式
     */
    @Test
    public void testUser03() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // 使用 PageHelper 分页插件
            PageHelper.offsetPage((pageNum - 1) * pageSize, pageSize);
            List<User> userList = userMapper.selectList(where);
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            System.out.println("Mapper 接口方式的调用：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * 参数方法调用
     */
    @Test
    public void testUser04() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // 这种分页需要配置 supportMethodsArguments=true，见配置文件
            List<User> userList = userMapper.selectListByMethodsArguments(where, pageNum, pageSize);
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            System.out.println("参数方法调用：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * 参数对象
     */
    @Test
    public void testUser05() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().pageNum(pageNum).pageSize(pageSize).build();
            // 如果 pageNum 和 pageSize 存在于 User 对象中，只要参数有值，也会被分页
            // 即当 user 中的 pageNum != null 并且 pageSize != null 时，会自动分页
            // 不需要在 xml 处理后 pageNum、pageSize 参数，但需注意分页的参数名和 params 配置的名字一致，见配置文件
            List<User> userList = userMapper.selectList(where);
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            System.out.println("参数对象：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * ISelect 接口方式
     */
    @Test
    public void testUser06() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // jdk6、jdk7 用法，创建接口
            Page<User> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(new ISelect() {
                @Override
                public void doSelect() {
                    userMapper.selectList(where);
                }
            });
            // jdk8 lambda 用法
            Page<User> page8 = PageHelper.startPage(pageNum, pageSize).doSelectPage(() -> userMapper.selectList(where));
            System.out.println("ISelect 接口方式：" + page);
            page.getResult().forEach(System.out::println);
        }
    }

    /**
     * 直接返回 PageInfo，注意 doSelectPageInfo() 方法和 doSelectPage() 方法
     */
    @Test
    public void testUser07() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().build();
            // jdk6、jdk7 用法
            PageInfo<User> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    userMapper.selectList(where);
                }
            });
            // jdk8 lambda 用法
            PageInfo<User> pageInfo8 = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> userMapper.selectList(where));
            System.out.println("直接返回 PageInfo：" + pageInfo);
            pageInfo.getList().forEach(System.out::println);
        }
    }

    /**
     * count 查询：返回一个查询语句的 count 数
     */
    @Test
    public void testUser08() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            final UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 构建查询条件
            User where = User.builder().gender(true).build();
            // jdk6、jdk7 用法
            long total = PageHelper.count(new ISelect() {
                @Override
                public void doSelect() {
                    userMapper.selectList(where);
                }
            });
            // jdk8 lambda 用法
            long total8 = PageHelper.count(() -> userMapper.selectList(where));
            System.out.println("count 查询：" + total);
        }
    }
}
