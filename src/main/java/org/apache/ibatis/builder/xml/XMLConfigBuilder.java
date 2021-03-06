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
    /**
     * mybatis 配置文件解析标识(因为 Configuration 是全局变量，只需解析创建一次即可)：true 表示已经解析过，false 表示没有解析过
     */
    private boolean parsed;
    private final XPathParser parser;
    /**
     * 环境参数
     */
    private String environment;
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
        // 调用父类的构造方法
        super(new Configuration());
        ErrorContext.instance().resource("SQL Mapper Configuration");
        this.configuration.setVariables(props);
        // 设置 parsed 的初始值为 false，目的是只让 xml 文件加载一次
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;
    }

    /**
     * 通过 XMLConfigBuilder 解析 mybatis 配置文件中的各个节点，并将节点信息封装成 Configuration 对象
     *
     * @return
     */
    public Configuration parse() {
        if (parsed) {
            // 判断 Configuration 是否解析过，Configuration 是全局变量，只需解析创建一次即可。parsed 为 true 代表已经解析过一次了
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        // 标志 xml 文件已经被加载过
        parsed = true;
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
            // 解析配置文件中的 properties(属性) 节点
            propertiesElement(root.evalNode("properties"));
            // 解析配置文件中的 settings(设置) 节点
            Properties settings = settingsAsProperties(root.evalNode("settings"));
            // 指定 VFS 的实现
            loadCustomVfs(settings);
            // 指定 MyBatis 所用日志的具体实现，未指定时将自动查找
            loadCustomLogImpl(settings);
            // 解析配置文件中的 typeAliases(类型别名) 节点
            typeAliasesElement(root.evalNode("typeAliases"));
            // 解析配置文件中的 plugins(插件) 节点
            pluginElement(root.evalNode("plugins"));
            // 解析配置文件中的 objectFactory(对象工厂) 节点
            objectFactoryElement(root.evalNode("objectFactory"));
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            reflectorFactoryElement(root.evalNode("reflectorFactory"));
            // 处理　mybatis 配置文件中 <settings> 下的每一个 <setting>　子节点，如果没有显示设置值则赋默认值
            settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631
            // 解析配置文件中的 environments(环境配置) 节点
            environmentsElement(root.evalNode("environments"));
            // 解析配置文件中的 databaseIdProvider(数据库厂商标识) 节点
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            // 解析配置文件中的 typeHandlers(类型处理器) 节点
            typeHandlerElement(root.evalNode("typeHandlers"));
            // 解析配置文件中的 mappers(映射器) 节点
            mapperElement(root.evalNode("mappers"));
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
        // 检查所有 <settings> 是否已经存在于配置类中
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    /**
     * 解析配置文件中 settings(设置) 节点指定的 VFS 实现。注：VFS 是单例模式的。
     * VFS 指的是虚拟文件系统，主要是通过程序能够方便读取本地文件系统、FTP文件系统等系统中的文件资源。
     * Mybatis 中提供了 VFS 这个配置，目的是通过该配置来加载自定义的虚拟文件系统应用程序。
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
     * 解析配置文件中 settings(设置) 节点指定的 MyBatis 所用日志的具体实现，未指定时将自动查找
     *
     * @param props
     */
    private void loadCustomLogImpl(Properties props) {
        Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
        configuration.setLogImpl(logImpl);
    }

    /**
     * 类型别名解析，解析 <typeAliases> 标签：可以配置多个 package 和多个 typeAlias 一起，也可以单个配置。
     * 如果有注解(@Alias)定义则取注解名称，否则(即：未显示配置别名，也没有注解标示别名)取类名小写作为 Key，自定义的 Key 也是取类小写，否则异常。
     * 注意：如果配置了相同的 Key(alias)，但存储的类型不一样也会抛异常。
     *
     * @param parent
     */
    private void typeAliasesElement(XNode parent) {
        if (parent != null) {
            // getChildren()：获取当前节点的所有子节点信息，返回 List<XNode> 对象
            for (XNode child : parent.getChildren()) {
                // <typeAliases> 标签下只可以定义 <package> 和 <typeAlias> 两种标签
                if ("package".equals(child.getName())) {
                    // 解析 <package> 标签
                    String typeAliasPackage = child.getStringAttribute("name");
                    configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
                } else {
                    // 解析 <typeAlias> 标签
                    // 先解析 <typeAlias> 中的 alias 属性
                    String alias = child.getStringAttribute("alias");
                    // 再解析 <typeAlias> 中的 type 属性。注意：alias 属性也可以不定义
                    String type = child.getStringAttribute("type");
                    try {
                        Class<?> clazz = Resources.classForName(type);
                        if (alias == null) {
                            // <typeAlias> 中未定义 alias 属性时
                            typeAliasRegistry.registerAlias(clazz);
                        } else {
                            // <typeAlias> 中定义了 alias 属性时
                            typeAliasRegistry.registerAlias(alias, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
                    }
                }
            }
        }
    }

    /**
     * 插件注册：通过 interceptor 标签解析出拦截器类，然后将其实例化并保存到 Configuration 类的 InterceptorChain 中，以备后用。
     *
     * @param parent
     * @throws Exception
     */
    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
            // getChildren()：获取当前节点的所有子节点信息，返回 List<XNode> 对象
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                // 实例化拦截器类
                Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
                interceptorInstance.setProperties(properties);
                // 将实例化的拦截器类放到 configuration 中的 interceptorChain 中
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
     * 解析配置文件中的 properties 节点，并将节点信息保存在 Configuration 对象中。
     * 【注】如果属性在不只一个地方进行了配置，那么 MyBatis 将按照下面的顺序来加载：
     * 1、首先读取在 properties 元素体内指定的属性(即 <properties> 的子节点 <property>)。
     * 2、然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性。
     * 3、最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。
     * 即：通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的是 properties 属性中指定的属性。
     *
     * @param context
     * @throws Exception
     */
    private void propertiesElement(XNode context) throws Exception {
        if (context != null) {
            // 获取 <properties> 节点的所有子节点 <property>，并用所有子节点的 name 和 value 保存到 Properties 对象中
            Properties defaults = context.getChildrenAsProperties();
            // 分别获取 mybatis 配置文件中 <properties> 节点的 resource 属性和 url 属性
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            // resource 属性和 url 属性不能同时存在，否则将抛出无法解析的异常
            if (resource != null && url != null) {
                throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
            }
            if (resource != null) {
                // 用从 resource 指定的资源中读取的属性值去覆盖旧值。因：Properties 继承自 Hashtable，故"键相同则值覆盖"
                defaults.putAll(Resources.getResourceAsProperties(resource));
            } else if (url != null) {
                // 用从 url 指定的资源中读取的属性值去覆盖旧值。因：Properties 继承自 Hashtable，故"键相同则值覆盖"
                defaults.putAll(Resources.getUrlAsProperties(url));
            }
            // 获取方法参数中指定的所有属性值
            Properties vars = configuration.getVariables();
            if (vars != null) {
                // 用从方法参数中指定的所有属性值去覆盖旧值。因：Properties 继承自 Hashtable，故"键相同则值覆盖"
                defaults.putAll(vars);
            }
            parser.setVariables(defaults);
            // 将最终的属性值放回 Configuration 对象的 variables 中
            configuration.setVariables(defaults);
        }
    }

    /**
     * 处理　mybatis 配置文件中 <settings> 下的每一个 <setting>　子节点，如果没有显示设置值则赋默认值
     *
     * @param props
     */
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
                    // 解析配置文件中的 transactionManager(事务管理器) 节点
                    TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                    // 解析配置文件中的 dataSource(数据源) 节点
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
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

    /**
     * 解析配置文件中的 typeHandlers(类型处理器) 节点
     *
     * @param parent
     */
    private void typeHandlerElement(XNode parent) {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    // 若 <typeHandlers> 的子节点为 <package>，则获取其 name 属性值，然后自动扫描指定包下的自定义 TypeHandler
                    String typeHandlerPackage = child.getStringAttribute("name");
                    typeHandlerRegistry.register(typeHandlerPackage);
                } else {
                    // 若 <typeHandlers> 的子节点为 <typeHandler>， 则可以指定 javaType 属性和jdbcType 属性中的一个或两个同时指定
                    // javaType 是指定 java 类型(Java数据类型：如String)
                    String javaTypeName = child.getStringAttribute("javaType");
                    // jdbcType 是指定 jdbc 类型(数据库类型：如varchar)
                    String jdbcTypeName = child.getStringAttribute("jdbcType");
                    // handler 就是我们自定义的 TypeHandler 的全限定名
                    String handlerTypeName = child.getStringAttribute("handler");
                    // resolveClass() 就是 BaseBuilder 类里面处理别名的方法
                    Class<?> javaTypeClass = resolveClass(javaTypeName);
                    // JdbcType 是一个枚举类型，resolveJdbcType() 用于获取枚举类型的值
                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                    // resolveClass() 就是 BaseBuilder 类里面处理别名的方法
                    Class<?> typeHandlerClass = resolveClass(handlerTypeName);
                    // 注册 typeHandler，typeHandler 通过 TypeHandlerRegistry 这个类管理
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

    /**
     * 解析 mapper 映射文件。在 MyBatis 中，共有四种加载映射文件或信息的方式：
     * 第一种：从文件系统中加载映射文件。
     * 第二种：通过 URL 的方式加载和解析映射文件。
     * 第三种：通过 mapper 接口加载映射信息，映射信息可以配置在注解中，也可以配置在映射文件中。
     * 第四种：通过包扫描的方式获取到某个包下的所有类，并使用第三种方式为每个类解析映射信息。
     * <p>
     * 解析 mapper 映射文件过程一共对应4种情况：
     * 1)、若子节点为 <package>，则扫描这个包下所有 mapper 接口和对应注解，最后调用 MapperRegistry 的 addMapper() 方法添加。
     * 2)、若子节点为 <mapper>，则分三种情况：
     * 2-1)、若 resource 属性不为空，则从 classpath 加载。
     * 2-2)、若 url 属性不为空，则从 URL 加载。
     * 2-3)、若 class 属性不为空，则调用 Configuration 的 addMapper()，但最后还是会调用 MapperRegistry 的 addMapper()，扫描接口和接口上的注解。
     * 这四种情况可归结为2种方式：使用 mapper.xml 生成代理类；使用接口和注解生成代理类。
     *
     * @param parent
     * @throws Exception
     */
    private void mapperElement(XNode parent) throws Exception {
        if (parent != null) {
            // 开始遍历 mybatis 配置文件中 <mappers> 节点的所有子节点
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    // 如果子节点是配置的 package，那么进行包自动扫描处理
                    // 获取 <package> 节点中的 name 属性
                    String mapperPackage = child.getStringAttribute("name");
                    // 从指定包中查找所有的 mapper 接口，并根据 mapper 接口解析映射配置
                    configuration.addMappers(mapperPackage);
                } else {
                    // 注：这里永远先执行 else，因为 mybatis 配置文件中 <mappers> 节点的 <mapper> 子节点必须在 <package> 子节点之前。
                    // 如果子节点配置的是mapper(包含三种属性 resource、url、class，但最多只能配置一种)，则分别处理。
                    // 获取 resource/url/class 等属性
                    String resource = child.getStringAttribute("resource");
                    String url = child.getStringAttribute("url");
                    String mapperClass = child.getStringAttribute("class");
                    if (resource != null && url == null && mapperClass == null) {
                        // 处理 mapper 的 resource 属性：resource 不为空，且其他两者为空，则从指定路径中加载配置
                        // 将文件路径保存到 ErrorContext 上下文中
                        ErrorContext.instance().resource(resource);
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        // 创建 XMLMapperBuilder 对象
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        // 解析映射文件
                        mapperParser.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        // 处理 mapper 的 url 属性：url 不为空，且其他两者为空，则通过 url 加载配置
                        // 将文件路径保存到 ErrorContext 上下文中
                        ErrorContext.instance().resource(url);
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        // 解析映射文件
                        mapperParser.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        // 处理 mapper 的 class 属性：class 不为空，且其他两者为空，则通过 class 解析映射配置
                        Class<?> mapperInterface = Resources.classForName(mapperClass);
                        configuration.addMapper(mapperInterface);
                    } else {
                        // mapper 节点除了 resource、url、class 三种属性就抛异常
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
