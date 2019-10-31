/**
 * Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 无论是在预处理语句(PreparedStatement)中设置参数(将 Java 类型转换为 Jdbc 类型)时，还是从结果集中取出值(将 Jdbc 类型转换为 Java 类型)时，都会使用类型处理器来完成 Java 类型和 Jdbc 类型的相互转换。
 * Mybatis 默认为我们实现了许多 TypeHandler，当我们没有配置指定 TypeHandler 时，Mybatis 就会根据参数或返回结果集的不同来自动选择合适的 TypeHandler 进行处理。
 *
 * @author Clinton Begin
 */
public interface TypeHandler<T> {
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    T getResult(ResultSet rs, String columnName) throws SQLException;

    T getResult(ResultSet rs, int columnIndex) throws SQLException;

    T getResult(CallableStatement cs, int columnIndex) throws SQLException;
}
