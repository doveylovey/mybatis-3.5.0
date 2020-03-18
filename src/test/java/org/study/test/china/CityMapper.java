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
public interface CityMapper {
    /**
     * 根据cityCode查询
     *
     * @param cityCode
     * @return
     */
    City selectByCityCode(String cityCode);

    /**
     * 根据provinceCode查询
     *
     * @param provinceCode
     * @return
     */
    List<City> selectByProvinceCode1(String provinceCode);
    /**
     * 根据provinceCode查询
     *
     * @param provinceCode
     * @return
     */
    List<CityExtends> selectByProvinceCode2(String provinceCode);
}
