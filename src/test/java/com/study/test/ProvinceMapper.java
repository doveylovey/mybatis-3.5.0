package com.study.test;

/**
 * @Description: 该接口作用描述
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
public interface ProvinceMapper {
    /**
     * 根据provinceCode查询
     *
     * @param provinceCode
     * @return
     */
    Province selectByProvinceCode1(String provinceCode);

    /**
     * 根据provinceCode查询
     *
     * @param provinceCode
     * @return
     */
    ProvinceExtends selectByProvinceCode2(String provinceCode);
}
