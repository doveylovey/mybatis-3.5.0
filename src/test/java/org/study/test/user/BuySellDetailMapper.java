package org.study.test.user;

public interface BuySellDetailMapper {
    /**
     * delete by primary key
     *
     * @param code primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long code);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(BuySellDetail record);

    int insertOrUpdate(BuySellDetail record);

    int insertOrUpdateSelective(BuySellDetail record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(BuySellDetail record);

    /**
     * select by primary key
     *
     * @param code primary key
     * @return object by primary key
     */
    BuySellDetail selectByPrimaryKey(Long code);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(BuySellDetail record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(BuySellDetail record);
}