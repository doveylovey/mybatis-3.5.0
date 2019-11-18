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
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.*;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * 该类也继承自 BaseBuilder，和 {@link XMLConfigBuilder} 类不同的是：该类是解析 mapper 文件的，而 XMLConfigBuilder 类是解析 config 文件的，很显然该类也必须有一个 XpathParser 对象负责解析 mapper 文件
 *
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLMapperBuilder extends BaseBuilder {
    private final XPathParser parser;
    private final MapperBuilderAssistant builderAssistant;
    private final Map<String, XNode> sqlFragments;
    private final String resource;

    @Deprecated
    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
        this(reader, configuration, resource, sqlFragments);
        this.builderAssistant.setCurrentNamespace(namespace);
    }

    @Deprecated
    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration, resource, sqlFragments);
    }

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
        this(inputStream, configuration, resource, sqlFragments);
        this.builderAssistant.setCurrentNamespace(namespace);
    }

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration, resource, sqlFragments);
    }

    private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        // 首先调用父类构造方法，目的是初始化 TypeAliasRegistry 和 TypeHandlerRegistry
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = parser;
        this.sqlFragments = sqlFragments;
        this.resource = resource;
    }

    /**
     * 解析具体的 mapper 映射文件
     */
    public void parse() {
        // 先判断 *Mapper.xml 映射文件是否已被加载解析过
        if (!configuration.isResourceLoaded(resource)) {
            // 【重点关注】解析 *Mapper.xml 文件中 <mapper> 节点内容。
            configurationElement(parser.evalNode("/mapper"));
            // 将该资源路径添加到"已解析资源集合"(即：Set<String> loadedResources)集合中，表示其已经被加载解析过了
            configuration.addLoadedResource(resource);
            // 【重点关注】解析命名空间：即通过 *Mapper.xml 文件的命名空间绑定 *Mapper.java 接口。
            bindMapperForNamespace();
        }
        parsePendingResultMaps();
        parsePendingCacheRefs();
        parsePendingStatements();
    }

    public XNode getSqlFragment(String refid) {
        return sqlFragments.get(refid);
    }

    /**
     * 从 *Mapper.xml 文件内容中的 <mapper> 节点开始解析 mapper 文件。
     * 注：在 MyBatis 映射文件中，可以配置多种节点。如 <cache>、<resultMap>、<sql> 以及 <select | insert | update | delete> 等。
     *
     * @param context
     */
    private void configurationElement(XNode context) {
        try {
            // 获取 *Mapper.xml 文件的命名空间。注：<mapper> 标签的 namespace 属性不能为空，否则就会抛出异常
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }
            // 设置命名空间到 builderAssistant 中
            builderAssistant.setCurrentNamespace(namespace);
            // 解析 <mapper> 节点的 <cache-ref> 子节点
            cacheRefElement(context.evalNode("cache-ref"));
            // 解析 <mapper> 节点的 <cache> 子节点
            cacheElement(context.evalNode("cache"));
            // 解析 <mapper> 节点的 <parameterMap> 子节点
            parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            // 解析 <mapper> 节点的 <resultMap> 子节点
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            // 解析 <mapper> 节点的 <sql> 子节点
            sqlElement(context.evalNodes("/mapper/sql"));
            // 解析 <mapper> 节点的 <select>、<insert>、<update>、<delete> 子节点
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }

    /**
     * 解析 mapper 节点的 select、insert、update、delete 子节点。
     * <select>、<insert>、<update> 以及 <delete> 等节点统称为 SQL 语句节点，其解析过程在 buildStatementFromContext() 方法中
     *
     * @param list
     */
    private void buildStatementFromContext(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            // 调用重载方法构建 Statement
            buildStatementFromContext(list, configuration.getDatabaseId());
        }
        buildStatementFromContext(list, null);
    }

    private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
        for (XNode context : list) {
            // 创建 XMLStatementBuilder 建造类
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
            try {
                // 解析 sql 节点，将其封装到 Statement 对象中，并将解析结果存储到 configuration 的 mappedStatements 集合中
                statementParser.parseStatementNode();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteStatement(statementParser);
            }
        }
    }

    private void parsePendingResultMaps() {
        Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
        synchronized (incompleteResultMaps) {
            Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolve();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // ResultMap is still missing a resource...
                }
            }
        }
    }

    private void parsePendingCacheRefs() {
        Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
        synchronized (incompleteCacheRefs) {
            Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolveCacheRef();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Cache ref is still missing a resource...
                }
            }
        }
    }

    private void parsePendingStatements() {
        Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
        synchronized (incompleteStatements) {
            Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().parseStatementNode();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Statement is still missing a resource...
                }
            }
        }
    }

    /**
     * 解析 mapper 节点的 cache-ref 子节点
     *
     * @param context
     */
    private void cacheRefElement(XNode context) {
        if (context != null) {
            configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
            CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, context.getStringAttribute("namespace"));
            try {
                cacheRefResolver.resolveCacheRef();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteCacheRef(cacheRefResolver);
            }
        }
    }

    /**
     * 解析 mapper 节点的 cache 子节点。MyBatis 提供了两种级别的缓存，其中：
     * 一级缓存是 SqlSession 级别的，默认为开启状态。
     * 二级缓存配置在映射文件中，使用者需要显示配置才能开启。如1：<cache/>
     * 也可以使用第三方缓存，如2：<cache type="org.mybatis.caches.redis.RedisCache"/>
     * 其中有一些属性可以选择，如3：<cache eviction="LRU"  flushInterval="60000"  size="512" readOnly="true"/>
     * 对示例配置3的解释：根据数据的历史访问记录来进行淘汰数据(其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”)、缓存的容量为512个对象引用、缓存每隔60秒刷新一次、缓存返回的对象是写安全的(即在外部修改对象不会影响到缓存内部存储对象)
     *
     * @param context
     */
    private void cacheElement(XNode context) {
        // 缓存配置的解析逻辑如下
        if (context != null) {
            // 获取 type 属性，如果 type 没有指定就用默认的 PERPETUAL (早已经注册过的别名的PerpetualCache)
            String type = context.getStringAttribute("type", "PERPETUAL");
            // 根据 type 从早已经注册的别名中获取对应的 Class，PERPETUAL 对应的 Class 是 PerpetualCache.class
            // 如果写了 type 属性，如 type="org.mybatis.caches.redis.RedisCache" 将会得到 RedisCache.class
            Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
            // 获取缓存的数据淘汰方式，默认为 LRU (早已经注册过的别名的 LruCache)，即最近最少使用到的先淘汰
            String eviction = context.getStringAttribute("eviction", "LRU");
            // 别名解析：根据别名获取其对应的 Class 对象。
            // 如果没有设置 type 属性，则这里传过去的就是 PERPETUAL，通过别名解析后获取到的就是 PerpetualCache.class
            // 如果是设置了自定义的 type 属性，则在别名缓存中是获取不到的，会直接通过类加载去加载自定义的 type，如 RedisCache.class
            Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
            Long flushInterval = context.getLongAttribute("flushInterval");
            Integer size = context.getIntAttribute("size");
            boolean readWrite = !context.getBooleanAttribute("readOnly", false);
            boolean blocking = context.getBooleanAttribute("blocking", false);
            // 获取节点 <cache> 的所有子节点配置
            Properties props = context.getChildrenAsProperties();
            // 构建缓存对象：缓存的构建封装在 BuilderAssistant 类的 useNewCache 方法中
            builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
        }
    }

    /**
     * 解析 mapper 节点的 parameterMap 子节点
     *
     * @param list
     */
    private void parameterMapElement(List<XNode> list) {
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            String type = parameterMapNode.getStringAttribute("type");
            Class<?> parameterClass = resolveClass(type);
            List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
            List<ParameterMapping> parameterMappings = new ArrayList<>();
            for (XNode parameterNode : parameterNodes) {
                String property = parameterNode.getStringAttribute("property");
                String javaType = parameterNode.getStringAttribute("javaType");
                String jdbcType = parameterNode.getStringAttribute("jdbcType");
                String resultMap = parameterNode.getStringAttribute("resultMap");
                String mode = parameterNode.getStringAttribute("mode");
                String typeHandler = parameterNode.getStringAttribute("typeHandler");
                Integer numericScale = parameterNode.getIntAttribute("numericScale");
                ParameterMode modeEnum = resolveParameterMode(mode);
                Class<?> javaTypeClass = resolveClass(javaType);
                JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
                Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
                ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property, javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
                parameterMappings.add(parameterMapping);
            }
            builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
        }
    }

    /**
     * 解析 mapper 节点的 resultMap 子节点。
     * resultMap 主要用于映射结果。通过 resultMap 和自动映射，可以让 MyBatis 帮助我们完成 ResultSet → Object 的映射。
     *
     * @param list
     * @throws Exception
     */
    private void resultMapElements(List<XNode> list) throws Exception {
        // 遍历 <resultMap> 节点列表
        for (XNode resultMapNode : list) {
            try {
                resultMapElement(resultMapNode);
            } catch (IncompleteElementException e) {
                // ignore, it will be retried
            }
        }
    }

    private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
        return resultMapElement(resultMapNode, Collections.<ResultMapping>emptyList(), null);
    }

    private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) throws Exception {
        ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
        // 获取 id 属性值
        String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
        // 获取 type 属性值
        String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType", resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
        // 获取 extends 属性值
        String extend = resultMapNode.getStringAttribute("extends");
        // 获取 autoMapping 属性值
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
        // 获取 type 属性对应的类型：通过别名解析得到
        Class<?> typeClass = resolveClass(type);
        if (typeClass == null) {
            typeClass = inheritEnclosingType(resultMapNode, enclosingType);
        }
        Discriminator discriminator = null;
        //创建 ResultMapping 集合，对应 resultMap 子节点的 id 和 result 节点
        List<ResultMapping> resultMappings = new ArrayList<>();
        resultMappings.addAll(additionalResultMappings);
        // 获取 <resultMap> 的所有子节点
        List<XNode> resultChildren = resultMapNode.getChildren();
        // 遍历 <resultMap> 的所有子节点
        for (XNode resultChild : resultChildren) {
            if ("constructor".equals(resultChild.getName())) {
                processConstructorElement(resultChild, typeClass, resultMappings);
            } else if ("discriminator".equals(resultChild.getName())) {
                discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
            } else {
                List<ResultFlag> flags = new ArrayList<>();
                if ("id".equals(resultChild.getName())) {
                    // 添加 ID 到 flags 集合中
                    flags.add(ResultFlag.ID);
                }
                // 解析 id 和 result 节点，将 id 或 result 节点生成相应的 ResultMapping，将 ResultMapping 添加到 resultMappings 集合中
                resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
            }
        }
        // 创建 ResultMapResolver 对象
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
        try {
            // 根据前面获取到的信息构建 ResultMap 对象
            return resultMapResolver.resolve();
        } catch (IncompleteElementException e) {
            configuration.addIncompleteResultMap(resultMapResolver);
            throw e;
        }
    }

    protected Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
        if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            String property = resultMapNode.getStringAttribute("property");
            if (property != null && enclosingType != null) {
                MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
                return metaResultType.getSetterType(property);
            }
        } else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            return enclosingType;
        }
        return null;
    }

    private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
        List<XNode> argChildren = resultChild.getChildren();
        for (XNode argChild : argChildren) {
            List<ResultFlag> flags = new ArrayList<>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if ("idArg".equals(argChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
        }
    }

    private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String typeHandler = context.getStringAttribute("typeHandler");
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        Map<String, String> discriminatorMap = new HashMap<>();
        for (XNode caseChild : context.getChildren()) {
            String value = caseChild.getStringAttribute("value");
            String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(caseChild, resultMappings, resultType));
            discriminatorMap.put(value, resultMap);
        }
        return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
    }

    /**
     * 解析 mapper 节点的 sql 子节点。
     * <sql> 节点用来定义一些可重用的 SQL 语句片段(如：表名或表的列名等)。在映射文件中，我们可以通过 <include> 节点引用 <sql> 节点定义的内容。
     *
     * @param list
     */
    private void sqlElement(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            // 调用 sqlElement 解析 <sql> 节点
            sqlElement(list, configuration.getDatabaseId());
        }
        // 再次调用 sqlElement，不同的是，这次调用，该方法的第二个参数为 null
        sqlElement(list, null);
    }

    private void sqlElement(List<XNode> list, String requiredDatabaseId) {
        for (XNode context : list) {
            // 获取 id 和 databaseId 属性
            String databaseId = context.getStringAttribute("databaseId");
            String id = context.getStringAttribute("id");
            // 重写 id，让它的格式变为：id = currentNamespace + "." + id
            id = builderAssistant.applyCurrentNamespace(id, false);
            // 检测当前 databaseId 和 requiredDatabaseId 是否一致
            if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
                // 将 <id, XNode> 键值对缓存到 XMLMapperBuilder 对象的 sqlFragments 属性中，以供后面的 sql 语句使用
                sqlFragments.put(id, context);
            }
        }
    }

    private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
        if (requiredDatabaseId != null) {
            if (!requiredDatabaseId.equals(databaseId)) {
                return false;
            }
        } else {
            if (databaseId != null) {
                return false;
            }
            // skip this fragment if there is a previous one with a not null databaseId
            if (this.sqlFragments.containsKey(id)) {
                XNode context = this.sqlFragments.get(id);
                if (context.getStringAttribute("databaseId") != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 解析 id 和 result 节点：在 <resultMap> 节点中，其子节点 <id> 和 <result> 都是比较常见的常规配置
     *
     * @param context
     * @param resultType
     * @param flags
     * @return
     * @throws Exception
     */
    private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) throws Exception {
        String property;
        // 根据节点类型获取 name 或 property 属性
        if (flags.contains(ResultFlag.CONSTRUCTOR)) {
            property = context.getStringAttribute("name");
        } else {
            property = context.getStringAttribute("property");
        }
        // 获取节点的各种其他属性
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String nestedSelect = context.getStringAttribute("select");
        /*
         * 解析 resultMap 属性，该属性出现在 <association> 和 <collection> 节点中。
         * 若这两个节点不包含 resultMap 属性，则调用 processNestedResultMappings() 方法，递归调用 resultMapElement 解析 <association> 和 <collection> 的嵌套节点，生成 resultMap，并返回 resultMap.getId();
         * 如果包含resultMap属性，则直接获取其属性值，这个属性值对应一个 resultMap 节点。
         */
        String nestedResultMap = context.getStringAttribute("resultMap", processNestedResultMappings(context, Collections.<ResultMapping>emptyList(), resultType));
        String notNullColumn = context.getStringAttribute("notNullColumn");
        String columnPrefix = context.getStringAttribute("columnPrefix");
        String typeHandler = context.getStringAttribute("typeHandler");
        String resultSet = context.getStringAttribute("resultSet");
        String foreignColumn = context.getStringAttribute("foreignColumn");
        boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        // 构建 ResultMapping 对象
        return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
    }

    /**
     * 该方法用于解析 <association> 和 <collection> 节点中的子节点，并返回 ResultMap.id
     *
     * @param context
     * @param resultMappings
     * @param enclosingType
     * @return
     * @throws Exception
     */
    private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType) throws Exception {
        if ("association".equals(context.getName()) || "collection".equals(context.getName()) || "case".equals(context.getName())) {
            if (context.getStringAttribute("select") == null) {
                validateCollection(context, enclosingType);
                ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
                return resultMap.getId();
            }
        }
        return null;
    }

    protected void validateCollection(XNode context, Class<?> enclosingType) {
        if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null && context.getStringAttribute("resultType") == null) {
            MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
            String property = context.getStringAttribute("property");
            if (!metaResultType.hasSetter(property)) {
                throw new BuilderException("Ambiguous collection type for property '" + property + "'. You must specify 'resultType' or 'resultMap'.");
            }
        }
    }

    /**
     * Mapper 接口绑定：映射文件解析完成后就需要通过命名空间将 mapper 映射文件和 mapper 接口绑定。
     * 如果当前命名空间是接口的全限定名，则为当前命名空间绑定相应的 Mapper 代理。
     */
    private void bindMapperForNamespace() {
        // 获取映射文件的命名空间
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace != null) {
            Class<?> boundType = null;
            try {
                // 根据命名空间解析 mapper 类型，即通过类名加载类
                boundType = Resources.classForName(namespace);
            } catch (ClassNotFoundException e) {
                //ignore, bound type is not required
            }
            if (boundType != null) {
                // 检测当前 mapper 类是否被绑定过
                if (!configuration.hasMapper(boundType)) {
                    // Spring may not know the real resource name so we set a flag
                    // to prevent loading again this resource from the mapper interface
                    // look at MapperAnnotationBuilder#loadXmlResource
                    configuration.addLoadedResource("namespace:" + namespace);
                    // 绑定 mapper 接口
                    configuration.addMapper(boundType);
                }
            }
        }
    }
}
