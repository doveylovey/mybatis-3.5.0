package org.study.test.china;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 中国城市信息
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
public class City implements Serializable {
    private static final long serialVersionUID = -387448585589425700L;

    /**
     * 城市代码
     */
    private String cityCode;
    /**
     * 省份代码
     */
    private String provinceCode;
    /**
     * 城市名称
     */
    private String cityName;
}
