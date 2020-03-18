package org.study.test.china;

import java.util.List;

/**
 * @Description: 该接口作用描述
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
public interface AreaMapper {
    /**
     * 根据areaCode查询
     *
     * @param areaCode
     * @return
     */
    Area selectByAreaCode(String areaCode);

    /**
     * 根据cityCode查询
     *
     * @param cityCode
     * @return
     */
    List<Area> selectByCityCode(String cityCode);
}
