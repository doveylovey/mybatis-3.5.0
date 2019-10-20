package com.study.test;

import lombok.Data;

import java.util.List;

/**
 * @Description: 中国城市信息(包含其所有地区信息)
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
public class CityExtends extends City {
    /**
     * 该省份的区域列表
     */
    private List<Area> areaList;
}
