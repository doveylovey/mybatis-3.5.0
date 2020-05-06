package org.study.test.user;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 自定义类型处理器。在自定义 TypeHandler 时可以通过 @MappedJdbcTypes 指定 jdbcType，通过 @MappedTypes 指定 javaType，如果没有使用注解指定，那么就需要在配置文件中配置
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2020年03月16日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
// 此处如果不使用该注解指定 javaType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 javaType="java.util.List"
@MappedTypes(List.class)
// 此处如果不使用该注解指定 jdbcType，在 mybatis-config.xml 中注册该 typeHandler 的时候需要写明 jdbcType="VARCHAR"
@MappedJdbcTypes(JdbcType.VARCHAR)
public class HobbyTypeHandler2 implements TypeHandler<List<String>> {
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
