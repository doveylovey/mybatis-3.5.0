MyBatis主要的类概述
============================================
### MyBatis主要的类
- Configuration：MyBatis 所有的配置信息都维持在 Configuration 对象中。
- SqlSession：作为 MyBatis 工作的主要顶层 API，表示和数据库交互的会话，完成数据库必要的增删改查功能。
- Executor：MyBatis 执行器，是 MyBatis 调度的核心，负责 SQL 语句的生成和查询缓存的维护。
- StatementHandler：封装了 JDBC Statement 操作，负责对 JDBC statement 的操作，如设置参数、将 Statement 结果集转换成 List 集合等。
- ParameterHandler：负责将用户传递的参数转换成 JDBC Statement 所需要的参数。
- ResultSetHandler：负责将 JDBC 返回的 ResultSet 结果集对象转换成 List 类型的集合对象。
- TypeHandler：负责 Java 数据类型和 Jdbc 数据类型之间的映射和转换。
- MappedStatement：该类维护了一个 <select|update|delete|insert> 节点的封装。
- SqlSource：负责根据用户传递的 parameterObject 动态地生成 SQL 语句，将信息封装到 BoundSql 对象中并返回。
- BoundSql：表示动态生成的 SQL 语句以及相应的参数信息。

以上几个类在 SQL 操作中都会涉及，在 SQL 操作中重点关注：SQL 参数什么时候写入？结果集怎么转换为 Java 对象？这两个过程正好对应的类是 PreparedStatementHandler 和 ResultSetHandler 类。

