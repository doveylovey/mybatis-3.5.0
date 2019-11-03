基于MyBatis-3.5.0的源码分析
=====================================
### **声明**
* 由于开发工具和环境配置的不同，我对项目进行了少许改动，但基本上都是一些环境配置和测试类的改动。
* 此项目仅供我个人学习研究使用，其他人请务必参考原作者代码，以免误人子弟。原作者代码地址已在下方给出。

### **源码地址**
* [GitHub源码](https://github.com/mybatis/mybatis-3/tree/mybatis-3.5.0)：https://github.com/mybatis/mybatis-3/tree/mybatis-3.5.0

### **部分参考资料(列表不分先后)**
* 徐郡明编著.MyBatis技术内幕[M].电子工业出版社:2017-06-01
* ErrorContext类分析参考：https://www.jianshu.com/p/901e37d05853
* BaseBuilder类分析参考：https://www.cnblogs.com/zhengzuozhanglina/p/11291553.html
* mapper.xml映射文件解析参考：https://www.lagou.com/lgeduarticle/53182.html



