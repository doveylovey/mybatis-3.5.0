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
package org.apache.ibatis.executor;

/**
 * MyBatis 异常涵盖的信息总结为：异常是由谁在做什么的时候在哪个资源文件中发生的，执行的 SQL 是哪个，以及 Java 详细的异常信息。ErrorContext 类中有六个私有变量分别存储这些信息
 *
 * @author Clinton Begin
 */
public class ErrorContext {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    /**
     * 使用 ThreadLocal 管理 ErrorContext：保证了在多线程环境中，每个线程内部可以共用一份 ErrorContext，但多个线程持有的 ErrorContext 互不影响，保证了异常日志的正确输出。
     * 注：ThreadLocal 是本地线程存储，作用是为变量在每个线程中创建一个副本，每个线程内部都可以使用该副本，线程之间互不影响。
     */
    private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<>();

    private ErrorContext stored;
    /**
     * 存储异常存在于哪个资源文件中，如：The error may exist in mapper/AuthorMapper.xml
     */
    private String resource;
    /**
     * 存储异常是做什么操作时发生的，如：The error occurred while setting parameters
     */
    private String activity;
    /**
     * 存储哪个对象操作时发生异常，如：The error may involve defaultParameterMap
     */
    private String object;
    /**
     * 存储异常的概览信息，如：Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'id2' in 'field list'
     */
    private String message;
    /**
     * 存储发生异常的SQL语句，如：SQL: select id2, name, sex, phone from author where name = ?
     */
    private String sql;
    /**
     * 存储详细的Java异常日志，如：Cause: java.sql.SQLSyntaxErrorException: Unknown column 'id2' in 'field list' at
     * org.apache.ibatis.exceptions.ExceptionFactory.wrapException(ExceptionFactory.java:30) at
     * org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:150) at
     * org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:141) at
     * org.apache.ibatis.binding.MapperMethod.executeForMany(MapperMethod.java:139) at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:76)
     */
    private Throwable cause;

    /**
     * 私有构造方法
     */
    private ErrorContext() {
    }

    /**
     * 获取 ErrorContext 实例的静态公共方法：在同一线程中，ErrorContext 通过该方法提供给外界一个获取其唯一实例的方式。
     * 在调用 instance() 时，首先从 LOCAL 中获取，如果获取到了 ErrorContext 实例，则直接返回；否则调用其构造方法创建一个，并将其存入 LOCAL。
     *
     * @return
     */
    public static ErrorContext instance() {
        ErrorContext context = LOCAL.get();
        if (context == null) {
            context = new ErrorContext();
            LOCAL.set(context);
        }
        return context;
    }

    /**
     * stored 变量充当一个中介：在调用 store() 时将当前 ErrorContext 保存下来，在调用 recall() 时将该 ErrorContext 实例传递给 LOCAL。
     *
     * @return
     */
    public ErrorContext store() {
        ErrorContext newContext = new ErrorContext();
        newContext.stored = this;
        LOCAL.set(newContext);
        return LOCAL.get();
    }

    public ErrorContext recall() {
        if (stored != null) {
            LOCAL.set(stored);
            stored = null;
        }
        return LOCAL.get();
    }

    public ErrorContext resource(String resource) {
        this.resource = resource;
        return this;
    }

    public ErrorContext activity(String activity) {
        this.activity = activity;
        return this;
    }

    public ErrorContext object(String object) {
        this.object = object;
        return this;
    }

    public ErrorContext message(String message) {
        this.message = message;
        return this;
    }

    public ErrorContext sql(String sql) {
        this.sql = sql;
        return this;
    }

    public ErrorContext cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    /**
     * 该方法用来重置变量(即为变量赋 null 值，并清空 LOCAL)，以便 gc 的执行
     *
     * @return
     */
    public ErrorContext reset() {
        resource = null;
        activity = null;
        object = null;
        message = null;
        sql = null;
        cause = null;
        LOCAL.remove();
        return this;
    }

    /**
     * 该方法用来拼接异常信息，以便打印时使用
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder description = new StringBuilder();

        // message
        if (this.message != null) {
            description.append(LINE_SEPARATOR);
            description.append("### ");
            description.append(this.message);
        }

        // resource
        if (resource != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error may exist in ");
            description.append(resource);
        }

        // object
        if (object != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error may involve ");
            description.append(object);
        }

        // activity
        if (activity != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error occurred while ");
            description.append(activity);
        }

        // activity
        if (sql != null) {
            description.append(LINE_SEPARATOR);
            description.append("### SQL: ");
            description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
        }

        // cause
        if (cause != null) {
            description.append(LINE_SEPARATOR);
            description.append("### Cause: ");
            description.append(cause.toString());
        }
        return description.toString();
    }
}
