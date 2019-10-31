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
package org.apache.ibatis.builder;

import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 该抽象类是 mybatis 配置文件、Mapper 映射文件等解析器的基类。
 * <p>
 * 该类虽为抽象类，但并未声明相关的抽象方法，其所有子类都可以使用 BaseBuilder 类提供的属性和方法，BaseBuilder 类的每个子类在使用构造器实例化时必定会调用 BaseBuilder 类的构造方法，因为 BaseBuilder 类内部维护着三个属性 Configuration、TypeAliasRegistry、TypeHandlerRegistry。
 * <p>
 * Configuration 内部维护着各种各样的对象实例，TypeAliasRegistry 和 TypeHandlerRegistry 就是其中两个。事实上，BaseBuilder 的这两个实例就是从 Configuration 中获取的。为什么要这样做呢？因为在编写 mybatis 配置文件时可能需要自定义参数别名和类型处理器，在解析配置文件时必须将自定义的别名处理器重新放入 Configuration 中。
 *
 * @author Clinton Begin
 */
public abstract class BaseBuilder {
    /**
     * 配置类
     */
    protected final Configuration configuration;
    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry;
    /**
     * 类型处理器注册器
     */
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        // 从 Configuration 中获取类型别名注册器 typeAliasRegistry
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        // 从 Configuration 中获取类型处理器注册器 typeHandlerRegistry
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 获取正则表达式对象：如果regex表达式为null，则使用默认值defaultValue
     *
     * @param regex
     * @param defaultValue
     * @return
     */
    protected Pattern parseExpression(String regex, String defaultValue) {
        return Pattern.compile(regex == null ? defaultValue : regex);
    }

    /**
     * 获取value的boolean值：如果为null，则使用默认值defaultValue
     *
     * @param value
     * @param defaultValue
     * @return
     */
    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    /**
     * 获取value的integer值：如果为null，则使用默认值defaultValue
     *
     * @param value
     * @param defaultValue
     * @return
     */
    protected Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    /**
     * 获取value的Set集合(将value用‘,’分割为数组再转为HashSet)：如果为null，则使用默认值defaultValue
     *
     * @param value
     * @param defaultValue
     * @return
     */
    protected Set<String> stringSetValueOf(String value, String defaultValue) {
        value = value == null ? defaultValue : value;
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    /**
     * 根据别名查询对应的JDBC数据类型：JdbcType 是 mybatis 对 {@link java.sql.Types} 类包装后的一个枚举类。详情看{@link org.apache.ibatis.type.JdbcType}
     *
     * @param alias
     * @return
     */
    protected JdbcType resolveJdbcType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return JdbcType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    /**
     * 根据别名获取对应的结果：详情看{@link org.apache.ibatis.mapping.ResultSetType}
     * ResultSetType 类是对 {@link java.sql.ResultSet} 的包装，java.sql.ResultSet 提供了如下三个值：
     * ResultSet.TYPE_FORWARD_ONLY：结果集的游标只能向下滚动。
     * ResultSet.TYPE_SCROLL_INSENSITIVE：结果集的游标可以上下移动，当数据库变化时，当前结果集不变。
     * ResultSet.TYPE_SCROLL_SENSITIVE：返回可滚动的结果集，当数据库变化时，当前结果集同步改变。
     *
     * @param alias
     * @return
     */
    protected ResultSetType resolveResultSetType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return ResultSetType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
        }
    }

    /**
     * 根据别名获取 ParameterMode 类型，可选值为：IN、OUT、INOUT。详情看{@link org.apache.ibatis.mapping.ParameterMode}
     *
     * @param alias
     * @return
     */
    protected ParameterMode resolveParameterMode(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return ParameterMode.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
        }
    }

    /**
     * 根据别名创建实例对象：如果别名为null，则直接返回null对象，否则根据别名所找到的类进行实例化
     *
     * @param alias
     * @return
     */
    protected Object createInstance(String alias) {
        Class<?> clazz = resolveClass(alias);
        if (clazz == null) {
            return null;
        }
        try {
            return resolveClass(alias).newInstance();
        } catch (Exception e) {
            throw new BuilderException("Error creating instance. Cause: " + e, e);
        }
    }

    protected <T> Class<? extends T> resolveClass(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    /**
     * 根据java类解和类型处理器别名析出类型处理器。与别名处理器类似
     * <p>
     * 首先判断类型处理器别名是否为 null，再去别名注册器中寻找别名对应的类类型，使用类类型在类型处理注册器中寻找类类型对应的实例，如果不存在该实例，则采用反射对其进行实例化
     *
     * @param javaType
     * @param typeHandlerAlias
     * @return
     */
    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
        // 判断类型处理器别名是否为 null，如果为 null 则直接返回 null，否则在别名注册器中寻找已经注册的类类型
        if (typeHandlerAlias == null) {
            return null;
        }
        Class<?> type = resolveClass(typeHandlerAlias);
        // 如果找到的类类型不为 null 且不属于 TypeHandler 类型，则抛 BuilderException 异常
        if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
            throw new BuilderException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
        }
        @SuppressWarnings("unchecked")
        // already verified it is a TypeHandler
        // 将找到的类类型强转为 TypeHandler 类型
        Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
        return resolveTypeHandler(javaType, typeHandlerType);
    }

    /**
     * 使用类类型在类型处理注册器中寻找类类型对应的实例，如果不存在该实例，则采用反射对其进行实例化
     *
     * @param javaType
     * @param typeHandlerType
     * @return
     */
    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        // javaType ignored for injected handlers see issue #746 for full detail
        // 在类型处理器中根据类类型寻找已经注册的类型处理器实例
        TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
        if (handler == null) {
            // not in registry, create a new one
            // 如果找不到对应的类型处理器实例，则采用反射进行实例化
            handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
        }
        return handler;
    }

    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
