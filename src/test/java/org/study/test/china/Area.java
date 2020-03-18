package org.study.test.china;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 中国地区信息
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
public class Area implements Serializable {
    private static final long serialVersionUID = 6715596678009811683L;

    /**
     * 区域代码
     */
    private String areaCode;
    /**
     * 城市代码
     */
    private String cityCode;
    /**
     * 区域名称
     */
    private String areaName;
}
