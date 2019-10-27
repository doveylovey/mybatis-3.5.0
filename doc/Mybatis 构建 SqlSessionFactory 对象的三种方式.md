Mybatis 构建 SqlSessionFactory 对象的三种方式
============================================

### 方式一(Reader字符流)：
- public SqlSessionFactory build(Reader reader)

- public SqlSessionFactory build(Reader reader, String environment);

- public SqlSessionFactory build(Reader reader, Properties properties);

- public SqlSessionFactory build(Reader reader, String environment, Properties properties);

  使用示例：
  ```
  String resource = "org/apache/ibatis/autoconstructor/mybatis-config.xml";
  Reader reader = Resources.getResourceAsReader(resource);
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
  ```
### 方式二(InputStream字节流)：
- public SqlSessionFactory build(InputStream inputStream);

- public SqlSessionFactory build(InputStream inputStream, String environment);

- public SqlSessionFactory build(InputStream inputStream, Properties properties);

- public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties);

  使用示例：
  ```
  String resource = "org/mybatis/example/mybatis-config.xml";
  InputStream inputStream = Resources.getResourceAsStream(resource);
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream) ;
  ```
### 方式三(Configuration类)：
- public SqlSessionFactory build(Configuration config);

  使用示例(配合阿里的 Druid 数据库连接池)：
  ```
  DruidDataSource dataSource = new DruidDataSource();
  dataSource.setUrl("");
  dataSource.setUsername("");
  dataSource.setPassword("");
  TransactionFactory transactionFactory = new JdbcTransactionFactory();
  Environment environment = new Environment("dev", transactionFactory, dataSource);
  Configuration configuration = new Configuration(environment);
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration) ;
  ```
