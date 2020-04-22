package org.study.test.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BuySellDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键编号
     */
    private Long code;

    /**
     * 卖出商户编号
     */
    private Long rentMerchantCode;

    /**
     * 卖出商户地址
     */
    private String rentMerchantAddress;

    /**
     * 卖出商户名称
     */
    private String rentMerchantName;

    /**
     * 买入商户编号
     */
    private Long returnMerchantCode;

    /**
     * 买入商户地址
     */
    private String returnMerchantAddress;

    /**
     * 买入商户名称
     */
    private String returnMerchantName;

    /**
     * 消费订单号
     */
    private String consumerOrderNo;

    /**
     * 创建日期
     */
    private Date gmtCreate;

    /**
     * 修改日期
     */
    private Date gmtModified;
}