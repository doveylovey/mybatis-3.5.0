以 Java 中 List<String> 类型的数据转为 varchar 类型存入数据库为例说明自定义 TypeHandler 的使用
=========================================================================================

### 自定义 TypeHandler 一般有两种方式，两种方法大同小异
- 1、继承 BaseTypeHandler<T> 抽象类(通过源码可以知道该类也实现了 TypeHandler<T> 接口)。示例代码如下：
```java
// 此处如果不使用该注解指定 javaType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 javaType="java.util.List"
@MappedTypes(List.class)
// 此处如果不使用该注解指定 jdbcType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 jdbcType="VARCHAR"
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (!Objects.isNull(parameter)) {
            for (String hobby : parameter) {
                sb.append(hobby).append(",");
            }
        }
        String hobbyString = sb.toString();
        ps.setString(i, hobbyString.substring(0, hobbyString.length() - 1));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = rs.getString(columnName);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = rs.getString(columnIndex);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = cs.getString(columnIndex);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }
}
```

- 2、实现 TypeHandler<T> 接口。示例代码如下：
```java
// 此处如果不使用该注解指定 javaType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 javaType="java.util.List"
@MappedTypes(List.class)
// 此处如果不使用该注解指定 jdbcType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 jdbcType="VARCHAR"
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyTypeHandler implements TypeHandler<List<String>> {
    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (!Objects.isNull(parameter)) {
            for (String hobby : parameter) {
                sb.append(hobby).append(",");
            }
        }
        String hobbyString = sb.toString();
        ps.setString(i, hobbyString.substring(0, hobbyString.length() - 1));
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = rs.getString(columnName);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = rs.getString(columnIndex);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        List<String> hobbyList = new ArrayList<>();
        // 从数据库中得到的值
        String hobbyString = cs.getString(columnIndex);
        if (!Objects.isNull(hobbyString)) {
            hobbyList = Arrays.asList(hobbyString.split(","));
        }
        return hobbyList;
    }
}
```

### 使用自定义 TypeHandler
- 在 mybatis-config.xml 文件中配置自定义的 TypeHandler，示例代码如下：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeHandlers>
        <typeHandler handler="org.study.test.user.HobbyTypeHandler1" javaType="java.util.List" jdbcType="VARCHAR"/>
    </typeHandlers>

    <environments default="dev">
        <environment id="dev">
            <transactionManager type="JDBC"/>
            <dataSource type="UNPOOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis-source?serverTimezone=Asia/Shanghai&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="org/study/test/user/UserMapper.xml"/>
    </mappers>
</configuration>
```

- 在 XxxMapper.xml 文件中需要使用自定义 TypeHandler 的字段上使用自定义的 MyTypeHandler 类来处理类型转换，示例代码如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.study.test.user.UserMapper">
    <resultMap id="BaseResultMap" type="org.study.test.user.User">
        <id column="id" jdbcType="BIGINT" property="id"/>
        <result column="name" jdbcType="VARCHAR" property="name"/>
        <result column="password" jdbcType="VARCHAR" property="password"/>
        <result column="birthday" jdbcType="DATE" property="birthday"/>
        <result column="gender" jdbcType="BOOLEAN" property="gender"/>
        <result column="email" jdbcType="VARCHAR" property="email"/>
        <!-- <result column="hobby" jdbcType="VARCHAR" property="hobby"/> -->
        <result column="hobby" jdbcType="VARCHAR" property="hobby" typeHandler="org.study.test.user.HobbyTypeHandler1"/>
        <result column="gmt_create" jdbcType="TIMESTAMP" property="gmtCreate"/>
        <result column="gmt_update" jdbcType="TIMESTAMP" property="gmtUpdate"/>
    </resultMap>

    <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="BaseResultMap">
        select id, `name`, `password`, birthday, gender, email, hobby, gmt_create, gmt_update
        from t_user
        where id = #{id,jdbcType=BIGINT}
    </select>

    <insert id="insert" keyColumn="id" keyProperty="id" parameterType="org.study.test.user.User" useGeneratedKeys="true">
        insert into t_user (`name`, `password`, birthday, gender, email, hobby, gmt_create, gmt_update)
        values (#{name,jdbcType=VARCHAR}, #{password,jdbcType=VARCHAR}, #{birthday,jdbcType=DATE}, 
        #{gender,jdbcType=BOOLEAN}, #{email,jdbcType=VARCHAR}, 
        #{hobby,jdbcType=VARCHAR, typeHandler=org.study.test.user.HobbyTypeHandler1}, 
        #{gmtCreate,jdbcType=TIMESTAMP}, #{gmtUpdate,jdbcType=TIMESTAMP})
        </insert>
</mapper>
```

其实，自定义 TypeHandler 就是在往数据库中插入数据前，把数据处理成数据库需要的类型，然后再插入；在查询的时候，把从数据库中查询出来的数据转换成 Java 程序需要的类型。这个转换过程就是通过 TypeHandler 来实现的。
