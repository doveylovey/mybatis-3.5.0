package com.study.test;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 自定义类型处理器，处理 String 数组与 List<String> 的相互转化
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2020年03月16日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
public class HobbyTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder hobbyString = new StringBuilder();
        if (!Objects.isNull(parameter)) {
            for (String hobby : parameter) {
                hobbyString.append(hobby).append(",");
            }
        }
        ps.setString(i, hobbyString.toString());
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
