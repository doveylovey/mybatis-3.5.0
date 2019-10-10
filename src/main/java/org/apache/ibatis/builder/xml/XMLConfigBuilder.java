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

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * 根据全局配置文件 mybatis-config.xml 的流文件解析其中的各个节点，然后创建一个 Configuration 对象，并将 xml 中的节点属性赋值给 Configuration 对象
 *
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLConfigBuilder extends BaseBuilder {
    private boolean parsed;// mybatis 配置文件解析标识(因为 Configuration 是全局变量，只需解析创建一次即可)：true 表示已经解析过，false 表示没有解析过
    private final XPathParser parser;
    private String environment;// 环境参数
    private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public XMLConfigBuilder(Reader reader) {
        this(reader, null, null);
    }

    public XMLConfigBuilder(Reader reader, String environment) {
        this(reader, environment, null);
    }

    public XMLConfigBuilder(Reader reader, String environment, Properties props) {
        this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    public XMLConfigBuilder(InputStream inputStream) {
        this(inputStream, null, null);
    }

    public XMLConfigBuilder(InputStream inputStream, String environment) {
        this(inputStream, environment, null);
    }

    public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
        this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
        super(new Configuration());// 调用父类的构造方法
        ErrorContext.instance().resource("SQL Mapper Configuration");
        this.configuration.setVariables(props);
        this.parsed = false;// 设置 parsed 的初始值为 false，目的是只让 xml 文件加载一次
        this.environment = environment;
        this.parser = parser;
    }

    /**
     * 解析 mybatis 全局配置文件，并封装成 Configuration 对象
     *
     * @return
     */
    public Configuration parse() {
        if (parsed) {// 判断 Configuration 是否解析过，Configuration 是全局变量，只需解析创建一次即可。parsed 为 true 代表已经解析过一次了
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;// 标志 xml 文件已经被加载过
        // parser.evalNode("/configuration") 从 mybatis 全局配置文件的根路径开始解析，将解析到的信息保存到 Configuration 对象中
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    /**
     * 解析 mybatis 配置文件中的各个节点，并将节点信息保存在 Configuration 对象中
     *
     * @param root
     */
    private void parseConfiguration(XNode root) {
        try {
            //issue #117 read properties first
            propertiesElement(root.evalNode("properties"));// 解析配置文件中的 properties(属性) 节点
            Properties settings = settingsAsProperties(root.evalNode("settings"));// 解析配置文件中的 settings(设置) 节点
            loadCustomVfs(settings);// 指定 VFS 的实现
            loadCustomLogImpl(settings);// 指定 MyBatis 所用日志的具体实现，未指定时将自动查找
            typeAliasesElement(root.evalNode("typeAliases"));// 解析配置文件中的 typeAliases(类型别名) 节点
            pluginElement(root.evalNode("plugins"));// 解析配置文件中的 plugins(插件) 节点
            objectFactoryElement(root.evalNode("objectFactory"));// 解析配置文件中的 objectFactory(对象工厂) 节点
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            reflectorFactoryElement(root.evalNode("reflectorFactory"));
            settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631
            environmentsElement(root.evalNode("environments"));// 解析配置文件中的 environments(环境配置) 节点
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));// 解析配置文件中的 databaseIdProvider(数据库厂商标识) 节点
            typeHandlerElement(root.evalNode("typeHandlers"));// 解析配置文件中的 typeHandlers(类型处理器) 节点
            mapperElement(root.evalNode("mappers"));// 解析配置文件中的 mappers(映射器) 节点
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    /**
     * 解析配置文件中的 settings(设置) 节点，并将节点信息保存在 Properties 对象中
     *
     * @param context
     * @return
     */
    private Properties settingsAsProperties(XNode context) {
        if (context == null) {
            return new Properties();
        }
        // 获取子节点的 name 值和 value 值，并将其保存在 Properties 对象中
        Properties props = context.getChildrenAsProperties();
        // Check that all settings are known to the configuration class
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    /**
     * 解析配置文件中 settings(设置) 节点指定的 MyBatis 所用日志的具体实现，未指定时将自动查找
     *
     * @param props
     * @throws ClassNotFoundException
     */
    private void loadCustomVfs(Properties props) throws ClassNotFoundException {
        String value = props.getProperty("vfsImpl");
        if (value != null) {
            String[] clazzes = value.split(",");
            for (String clazz : clazzes) {
                if (!clazz.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends VFS> vfsImpl = (Class<? extends VFS>) Resources.classForName(clazz);
                    configuration.setVfsImpl(vfsImpl);
                }
            }
        }
    }

    /**
     * 解析配置文件中 settings(设置) 节点指定的 VFS 实现
     *
     * @param props
     */
    private void loadCustomLogImpl(Properties props) {
        Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
        configuration.setLogImpl(logImpl);
    }

    /**
     * 解析 <typeAliases> 标签：可以配置多个 package 和多个 typeAlias 一起，也可以单个配置。
     * 如果有注解(@Alias)定义则取注解名称，否则取类名小写作为Key，自定义的Key也是取类小写，否则异常。
     * 注意：如果配置了相同的Key(alias)，但存储的类型不一样也会抛异常。
     *
     * @param parent
     */
    private void typeAliasesElement(XNode parent) {
        if (parent != null) {
            // getChildren()：获取当前节点的所有子节点信息，返回 List<XNode> 对象
            for (XNode child : parent.getChildren()) {
                // <typeAliases> 标签下只可以定义 <package> 和 <typeAlias> 两种标签
                if ("package".equals(child.getName())) {// 解析 <package> 标签
                    String typeAliasPackage = child.getStringAttribute("name");
                    configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
                } else {// 解析 <typeAlias> 标签
                    // 先解析 <typeAlias> 中的 alias 属性
                    String alias = child.getStringAttribute("alias");
                    // 再解析 <typeAlias> 中的 type 属性。注意：alias 属性也可以不定义
                    String type = child.getStringAttribute("type");
                    try {
                        Class<?> clazz = Resources.classForName(type);
                        if (alias == null) {// <typeAlias> 中未定义 alias 属性时
                            typeAliasRegistry.registerAlias(clazz);
                        } else {// <typeAlias> 中定义了 alias 属性时
                            typeAliasRegistry.registerAlias(alias, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
                    }
                }
            }
        }
    }

    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
            // getChildren()：获取当前节点的所有子节点信息，返回 List<XNode> 对象
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
                interceptorInstance.setProperties(properties);
                configuration.addInterceptor(interceptorInstance);
            }
        }
    }

    private void objectFactoryElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties properties = context.getChildrenAsProperties();
            ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
            factory.setProperties(properties);
            configuration.setObjectFactory(factory);
        }
    }

    private void objectWrapperFactoryElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
            configuration.setObjectWrapperFactory(factory);
        }
    }

    private void reflectorFactoryElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            ReflectorFactory factory = (ReflectorFactory) resolveClass(type).newInstance();
            configuration.setReflectorFactory(factory);
        }
    }

    /**
     * 解析配置文件中的 properties 节点，并将节点信息保存在 Configuration 对象中
     *
     * @param context
     * @throws Exception
     */
    private void propertiesElement(XNode context) throws Exception {
        if (context != null) {
            Properties defaults = context.getChildrenAsProperties();
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            if (resource != null && url != null) {
                throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
            }
            if (resource != null) {
                defaults.putAll(Resources.getResourceAsProperties(resource));
            } else if (url != null) {
                defaults.putAll(Resources.getUrlAsProperties(url));
            }
            Properties vars = configuration.getVariables();
            if (vars != null) {
                defaults.putAll(vars);
            }
            parser.setVariables(defaults);
            configuration.setVariables(defaults);
        }
    }

    private void settingsElement(Properties props) {
        configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
        configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
        configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
        configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
        configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
        configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
        configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
        configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
        configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
        configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
        configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
        configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
        configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
        configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
        configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
        configuration.setDefaultEnumTypeHandler(resolveClass(props.getProperty("defaultEnumTypeHandler")));
        configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
        configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
        configuration.setReturnInstanceForEmptyRow(booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
        configuration.setLogPrefix(props.getProperty("logPrefix"));
        configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
    }

    private void environmentsElement(XNode context) throws Exception {
        if (context != null) {
            if (environment == null) {
                environment = context.getStringAttribute("default");
            }
            // 注：context.getChildren()作用是获取当前节点的所有子节点信息，返回 List<XNode> 对象
            for (XNode child : context.getChildren()) {
                String id = child.getStringAttribute("id");
                if (isSpecifiedEnvironment(id)) {
                    TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));// 解析配置文件中的 transactionManager(事务管理器) 节点
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));// 解析配置文件中的 dataSource(数据源) 节点
                    DataSource dataSource = dsFactory.getDataSource();
                    Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource);
                    configuration.setEnvironment(environmentBuilder.build());
                }
            }
        }
    }

    private void databaseIdProviderElement(XNode context) throws Exception {
        DatabaseIdProvider databaseIdProvider = null;
        if (context != null) {
            String type = context.getStringAttribute("type");
            // awful patch to keep backward compatibility
            if ("VENDOR".equals(type)) {
                type = "DB_VENDOR";
            }
            Properties properties = context.getChildrenAsProperties();
            databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
            databaseIdProvider.setProperties(properties);
        }
        Environment environment = configuration.getEnvironment();
        if (environment != null && databaseIdProvider != null) {
            String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
            configuration.setDatabaseId(databaseId);
        }
    }

    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

    private void typeHandlerElement(XNode parent) {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    String typeHandlerPackage = child.getStringAttribute("name");
                    typeHandlerRegistry.register(typeHandlerPackage);
                } else {
                    String javaTypeName = child.getStringAttribute("javaType");
                    String jdbcTypeName = child.getStringAttribute("jdbcType");
                    String handlerTypeName = child.getStringAttribute("handler");
                    Class<?> javaTypeClass = resolveClass(javaTypeName);
                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                    Class<?> typeHandlerClass = resolveClass(handlerTypeName);
                    if (javaTypeClass != null) {
                        if (jdbcType == null) {
                            typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
                        } else {
                            typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
                        }
                    } else {
                        typeHandlerRegistry.register(typeHandlerClass);
                    }
                }
            }
        }
    }

    // 解析 mapper 映射文件
    private void mapperElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    String mapperPackage = child.getStringAttribute("name");
                    configuration.addMappers(mapperPackage);
                } else {
                    String resource = child.getStringAttribute("resource");
                    String url = child.getStringAttribute("url");
                    String mapperClass = child.getStringAttribute("class");
                    if (resource != null && url == null && mapperClass == null) {
                        ErrorContext.instance().resource(resource);
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        ErrorContext.instance().resource(url);
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        Class<?> mapperInterface = Resources.classForName(mapperClass);
                        configuration.addMapper(mapperInterface);
                    } else {
                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        } else if (id == null) {
            throw new BuilderException("Environment requires an id attribute.");
        } else if (environment.equals(id)) {
            return true;
        }
        return false;
    }

}
