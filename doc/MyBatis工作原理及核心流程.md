MyBatis 工作原理及核心流程
======================

### JDBC 中的四个核心对象
- DriverManager：用于注册数据库连接的对象。
- Connection：与数据库连接的对象。
- Statement/PrepareStatement：操作数据库SQL语句的对象。
- ResultSet：结果集或一张虚拟表。

### MyBatis 中的四个核心对象
- SqlSession 对象：该对象中包含了执行 SQL 语句的所有方法。类似于 JDBC 里面的 Connection。
- Executor 接口：根据 SqlSession 传递的参数动态地生成需要执行的 SQL 语句，同时负责查询缓存的维护。类似于 JDBC 里面的 Statement/PrepareStatement。
- MappedStatement 对象：该对象是对映射 SQL 的封装，用于存储要映射的 SQL 语句的 id、参数等信息。
- ResultHandler 对象：用于对返回的结果进行处理，最终得到自己想要的数据格式或类型，可以自定义返回类型。

### MyBatis 工作原理图
![MyBatis工作原理图](../doc/MyBatis工作原理图.png)

### MyBatis 核心流程
MyBatis 工作原理图中的流程就是 MyBatis 内部核心流程，每一步流程的说明如下：
- (1)、读取 MyBatis 配置文件：mybatis-config.xml 为 MyBatis 的全局配置文件，用于配置数据库连接等信息。
- (2)、加载 SQL 映射文件：这些文件中配置了操作数据库的 SQL 语句，需要在 MyBatis 配置文件中加载这些 SQL 映射文件(可以加载多个，通常每个文件对应数据库中的一张表)。
- (3)、构造会话工厂：通过 MyBatis 的环境配置信息构建会话工厂 SqlSessionFactory。
- (4)、创建会话对象：由会话工厂创建 SqlSession 对象，该对象中包含了执行 SQL 语句的所有方法。
- (5)、Executor 执行器：MyBatis 底层定义了一个 Executor 接口来操作数据库，它根据 SqlSession 传递的参数动态地生成需要执行的 SQL 语句，同时负责查询缓存的维护。
- (6)、MappedStatement 对象：在 Executor 接口的执行方法中有一个 MappedStatement 类型的参数，该参数是对映射信息的封装，用于存储要映射的 SQL 语句的 id、参数等信息。
- (7)、输入参数映射：输入参数可以是 Map、List 等集合类型，也可以是基本数据类型和 POJO 类型，输入参数映射过程类似于 JDBC 对 preparedStatement 对象设置参数的过程。
- (8)、输出结果映射：输出结果可以是 Map、List 等集合类型，也可以是基本数据类型和 POJO 类型，输出结果映射过程类似于 JDBC 对结果集的解析过程。
