package org.study.test.china;

import lombok.Data;

import java.util.List;

/**
 * @Description: 中国省份信息(包含其所有城市信息)
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
public class ProvinceExtends extends Province {
    /**
     * 该省份的城市列表
     */
    private List<City> cityList;
}
