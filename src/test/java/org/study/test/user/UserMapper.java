package org.study.test.user;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Description: 该接口作用描述
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2020年03月16日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
public interface UserMapper {
    /**
     * 查询表是否存在
     *
     * @param tableName 表名称
     * @return
     */
    int findTable(@Param("tableName") String tableName);

    /**
     * 删除表
     *
     * @param tableName 表名称
     */
    void dropTable(@Param("tableName") String tableName);

    /**
     * 创建表
     *
     * @param tableName 表名称
     */
    void createTable(@Param("tableName") String tableName);

    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return delete count
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(User record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(User record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    User selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(User record);

    /**
     * 根据条件查询列表
     *
     * @param user 查询条件
     * @return
     */
    List<User> selectList(User user);

    /**
     * 根据条件查询列表：使用 RowBounds 分页
     *
     * @param user 查询条件
     * @return
     */
    List<User> selectList(User user, RowBounds rowBounds);

    /**
     * 根据条件查询列表：会分页，且不需要在 xml 中处理 pageNum、pageSize 参数，但需要配置 supportMethodsArguments=true，见配置文件
     *
     * @param user     查询条件
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return
     */
    List<User> selectListByMethodsArguments(@Param("user") User user, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}
