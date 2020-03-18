package org.study.test.user;

import org.apache.ibatis.annotations.Param;

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
}
