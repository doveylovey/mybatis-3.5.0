package org.study.test.upsert;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作用描述：TODO
 *
 * @author doveylovey
 * @version v1.0.0
 * @email 1135782208@qq.com
 * @date 2020年09月09日
 */
public interface UserAccountRelationMapper {
    /**
     * 如果表已经存在则删除
     */
    void deleteTable();

    /**
     * 创建表
     */
    void createTable();

    /**
     * 向表中初始化一条数据
     */
    int insertData();

    /**
     * 在 MySQL 中避免重复插入数据的方式一：insert ignore into
     * 即插入数据时，如果数据存在，则忽略此次插入，前提条件是插入的数据字段设置了主键或唯一索引。
     * 请参考 SQL 语句：当插入本条数据时，MySQL 首先检索已有数据(即 idx_user_account 索引)，如果存在，则忽略本次插入操作，否则正常插入数据。
     *
     * @param record
     * @return
     */
    int insertIgnoreInto(UserAccountRelation record);

    /**
     * 在 MySQL 中避免重复插入数据的方式二：on duplicate key update
     * 即插入数据时，如果数据存在，则执行更新操作，前提条件是插入的数据字段设置了主键或唯一索引。
     * 请参考 SQL 语句：当插入本条记录时，MySQL 首先检索已有数据(即 idx_user_account 索引)，如果存在，则执行 update 更新操作，否则正常插入数据。
     *
     * @param record
     * @return
     */
    int insertOnDuplicateKeyUpdate(UserAccountRelation record);

    /**
     * 在 MySQL 中避免重复插入数据的方式二：on duplicate key update
     * 即插入数据时，如果数据存在，则执行更新操作，前提条件是插入的数据字段设置了主键或唯一索引。
     * 请参考 SQL 语句：当插入本条记录时，MySQL 首先检索已有数据(即 idx_user_account 索引)，如果存在，则执行 update 更新操作，否则正常插入数据。
     *
     * @param record
     * @return
     */
    int insertOnDuplicateKeyUpdate2(UserAccountRelation record);

    /**
     * 在 MySQL 中避免重复插入数据的方式三：replace into
     * 即插入数据时，如果数据存在，则先删除再插入，前提条件是插入的数据字段设置了主键或唯一索引。
     * 请参考 SQL 语句：当插入本条记录时，MySQL 首先检索已有数据(即 idx_user_account 索引)，如果存在，则先删除然后再插入数据，否则直接插入数据。
     *
     * @param record
     * @return
     */
    int insertReplaceInto(UserAccountRelation record);

    /**
     * 在 MySQL 中避免重复插入数据的方式四：insert if not exists，即 insert into … select … where not exist …
     * 这种方式适用于将要插入数据的表中没有主键或唯一索引，当插入一条数据时，MySQL 首先检索数据库中是否存在这条数据，如果存在，则忽略本次插入操作，否则正常插入数据。
     *
     * @param record
     * @return
     */
    int insertIfNotExists(UserAccountRelation record);

    /**
     * 插入一条记录：未设置值的字段用 null 填充
     *
     * @param record
     * @return
     */
    int insert(UserAccountRelation record);

    /**
     * 插入一条记录：：未设置值的字段用数据库默认值填充
     *
     * @param record
     * @return
     */
    int insertSelective(UserAccountRelation record);

    /**
     * 根据主键删除一条记录
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 根据主键查询一条记录
     *
     * @param id
     * @return
     */
    UserAccountRelation selectByPrimaryKey(Integer id);

    /**
     * 查询表中所有数据
     *
     * @return
     */
    List<UserAccountRelation> selectAll();

    /**
     * 按条件查询数据
     *
     * @param condition
     * @return
     */
    List<UserAccountRelation> selectBySelective(UserAccountRelation condition);

    /**
     * 根据主键更新一条记录的某些字段值
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserAccountRelation record);

    /**
     * 根据主键更新一条记录的所有字段值
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(UserAccountRelation record);

    /**
     * 批量插入数据
     *
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<UserAccountRelation> list);
}