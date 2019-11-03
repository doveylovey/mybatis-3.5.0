/**
 * Copyright 2009-2019 the original author or authors.
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

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 * 类型别名注册器
 *
 * @author Clinton Begin
 */
public class TypeAliasRegistry {
    /**
     * 核心：别名仅仅通过一个HashMap来实现，key为别名，value是别名对应的类型(class对象)
     */
    private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    /**
     * 初始化的时候默认注册的类型别名
     */
    public TypeAliasRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    /**
     * 处理别名：直接从保存有别名的 HashMap 中取出即可。
     * <p>
     * 该方法首先根据别名去已注册的别名集合 TYPE_ALIASES 中查找对应的类，如果不存在则使用 Resources.classForName(string) 获取对应的类。
     * <p>
     * 注：Resources.classForName() 的效果和 Class.forName() 效果一样，都用于根据类路径返回类对象
     *
     * @param string
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String string) {
        try {
            if (string == null) {
                return null;
            }
            // issue #748
            // 将别名转换成小写
            String key = string.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (TYPE_ALIASES.containsKey(key)) {
                // 从别名缓存中获取别名对应的 Class 对象
                value = (Class<T>) TYPE_ALIASES.get(key);
            } else {
                // 若从别名缓存中获取不到 Class 对象，则通过类加载去获取 Class 对象
                value = (Class<T>) Resources.classForName(string);
            }
            return value;
        } catch (ClassNotFoundException e) {
            // throws class cast exception as well if types cannot be assigned
            throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
        }
    }

    public void registerAliases(String packageName) {
        registerAliases(packageName, Object.class);
    }

    /**
     * 解析 <typeAliases> 标签下的 <package> 标签
     *
     * @param packageName
     * @param superType
     */
    public void registerAliases(String packageName, Class<?> superType) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        // 根据路径 packageName 寻找它下面的 .class 文件
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        // 拿到所有的 .class 文件对应的类的 Class
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        // 然后遍历所有的 Class
        for (Class<?> type : typeSet) {
            // Ignore inner classes and interfaces (including package-info.java)
            // Skip also inner classes. See issue #6
            // 必须不是匿名类、必须不是接口、必须不是成员类
            if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                registerAlias(type);
            }
        }
    }

    /**
     * 注册类型别名：仅当指定实体类型(未指定类型别名)时调用
     *
     * @param type
     */
    public void registerAlias(Class<?> type) {
        // simpleName是指移除了包名的简单类名，如 com.study.Test 通过 getSimpleName() 获取的就是 Test
        String alias = type.getSimpleName();
        // 获取类上的 @Alias 注解，如果 @Alias 注解中有 value 属性且指定了值，那么优先取这个值作为 Class 的别名
        Alias aliasAnnotation = type.getAnnotation(Alias.class);
        if (aliasAnnotation != null) {
            alias = aliasAnnotation.value();
        }
        // 注册别名
        registerAlias(alias, type);
    }

    /**
     * 注册类型别名：指定实体类别名、指定实体类型时调用。实质是向保存别名的 HashMap 新增键值对
     *
     * @param alias
     * @param value
     */
    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new TypeException("The parameter alias cannot be null");
        }
        // issue #748
        // 将 alias 全部转小写
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (TYPE_ALIASES.containsKey(key) && TYPE_ALIASES.get(key) != null && !TYPE_ALIASES.get(key).equals(value)) {
            throw new TypeException("The alias '" + alias + "' is already mapped to the value '" + TYPE_ALIASES.get(key).getName() + "'.");
        }
        // 将 alias 和 Class 对象放到 TYPE_ALIASES 中，TYPE_ALIASES 是一个 HashMap
        TYPE_ALIASES.put(key, value);
    }

    public void registerAlias(String alias, String value) {
        try {
            registerAlias(alias, Resources.classForName(value));
        } catch (ClassNotFoundException e) {
            throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + e, e);
        }
    }

    /**
     * 获取保存别名的 HashMap。
     * 注：Configuration 对象持有对 TypeAliasRegistry 的引用，因此可以通过 Configuration 对象获取
     *
     * @since 3.2.2
     */
    public Map<String, Class<?>> getTypeAliases() {
        return Collections.unmodifiableMap(TYPE_ALIASES);
    }
}
