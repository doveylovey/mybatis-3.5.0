package org.study.test.china;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 中国省份信息
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2019年10月19日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
public class Province implements Serializable {
    private static final long serialVersionUID = -5783634600754275360L;

    /**
     * 省份代码
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省会城市
     */
    private String provinceCapital;
    /**
     * 省份简称
     */
    private String provinceZhShort;
    /**
     * 省份英文名称
     */
    private String provinceEnglish;
    /**
     * 省份英文简称
     */
    private String provinceEnShort;
}
