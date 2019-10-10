/**
 * Copyright 2009-2018 the original author or authors.
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
package org.apache.ibatis;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据初始化：按需建表、添加数据
 */
public abstract class BaseDataTest {
    // Derby数据库(Apache Derby是一个完全用java编写的数据库)
    public static final String BLOG_PROPERTIES = "org/apache/ibatis/databases/blog/blog-derby.properties";
    public static final String BLOG_DDL = "org/apache/ibatis/databases/blog/blog-derby-schema.sql";
    public static final String BLOG_DATA = "org/apache/ibatis/databases/blog/blog-derby-dataload.sql";
    // HSQLDB数据库(HyperSQL DataBase是一个开放源代码的JAVA数据库,具有标准的SQL语法和JAVA接口,它可以自由使用和分发,非常简洁和快速)
    public static final String JPETSTORE_PROPERTIES = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb.properties";
    public static final String JPETSTORE_DDL = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-schema.sql";
    public static final String JPETSTORE_DATA = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-dataload.sql";
    // mysql数据库
    public static final String MYSQL_BLOG_PROPERTIES = "org/apache/ibatis/databases/mysql/mysql-blog-jdbc.properties";
    public static final String MYSQL_BLOG_DDL = "org/apache/ibatis/databases/mysql/mysql-blog-schema.sql";
    public static final String MYSQL_BLOG_DATA = "org/apache/ibatis/databases/mysql/mysql-blog-dataload.sql";
    public static final String MYSQL_JPETSTORE_PROPERTIES = "org/apache/ibatis/databases/mysql/mysql-jpetstore-jdbc.properties";
    public static final String MYSQL_JPETSTORE_DDL = "org/apache/ibatis/databases/mysql/mysql-jpetstore-schema.sql";
    public static final String MYSQL_JPETSTORE_DATA = "org/apache/ibatis/databases/mysql/mysql-jpetstore-dataload.sql";

    /**
     * 创建数据源(不使用连接池)
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public static UnpooledDataSource createUnpooledDataSource(String resource) throws IOException {
        Properties props = Resources.getResourceAsProperties(resource);
        UnpooledDataSource ds = new UnpooledDataSource();
        ds.setDriver(props.getProperty("driver"));
        ds.setUrl(props.getProperty("url"));
        ds.setUsername(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        return ds;
    }

    /**
     * 创建数据源(使用连接池)
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public static PooledDataSource createPooledDataSource(String resource) throws IOException {
        Properties props = Resources.getResourceAsProperties(resource);
        PooledDataSource ds = new PooledDataSource();
        ds.setDriver(props.getProperty("driver"));
        ds.setUrl(props.getProperty("url"));
        ds.setUsername(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        return ds;
    }

    /**
     * 执行数据库脚本文件
     *
     * @param ds       数据源
     * @param resource 脚本文件路径
     * @throws IOException
     * @throws SQLException
     */
    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try (Connection connection = ds.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runScript(runner, resource);
        }
    }

    /**
     * 执行数据库脚本文件
     *
     * @param runner   脚本执行器
     * @param resource 脚本文件路径
     * @throws IOException
     * @throws SQLException
     */
    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            runner.runScript(reader);
        }
    }

    /**
     * 根据脚本文件创建数据源
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static DataSource createBlogDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(BLOG_PROPERTIES);
        runScript(ds, BLOG_DDL);
        runScript(ds, BLOG_DATA);
        return ds;
    }

    /**
     * 根据脚本文件创建数据源
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static DataSource createJPetstoreDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
        runScript(ds, JPETSTORE_DDL);
        runScript(ds, JPETSTORE_DATA);
        return ds;
    }

    /**
     * 根据脚本文件创建数据源
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static DataSource createMysqlBlogDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(MYSQL_BLOG_PROPERTIES);
        runScript(ds, MYSQL_BLOG_DDL);
        runScript(ds, MYSQL_BLOG_DATA);
        return ds;
    }

    /**
     * 根据脚本文件创建数据源
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static DataSource createMysqlJPetstoreDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(MYSQL_JPETSTORE_PROPERTIES);
        runScript(ds, MYSQL_JPETSTORE_DDL);
        runScript(ds, MYSQL_JPETSTORE_DATA);
        return ds;
    }
}
