package com.study.test;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 自定义类型处理器。在自定义 TypeHandler 时可以通过 @MappedJdbcTypes 指定 jdbcType，通过 @MappedTypes 指定 javaType，如果没有使用注解指定，那么就需要在配置文件中配置
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年11月01日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
//@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
// 此处如果不用注解指定 jdbcType，那么就可以在配置文件中通过 jdbcType 属性指定。同理，javaType 属性也可通过 @MappedTypes 注解来指定
public class MyExampleTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }
}
