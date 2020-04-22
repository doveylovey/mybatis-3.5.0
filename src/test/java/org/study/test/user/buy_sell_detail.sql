CREATE TABLE `buy_sell_detail`(
  `code`                    bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键编号',
  `rent_merchant_code`      bigint(20)          NOT NULL DEFAULT '0' COMMENT '卖出商户编号',
  `rent_merchant_address`   varchar(255)        NOT NULL COMMENT '卖出商户地址',
  `rent_merchant_name`      varchar(255)        NOT NULL COMMENT '卖出商户名称',
  `return_merchant_code`    bigint(20)          NOT NULL DEFAULT '0' COMMENT '买入商户编号',
  `return_merchant_address` varchar(255)        NOT NULL COMMENT '买入商户地址',
  `return_merchant_name`    varchar(255)        NOT NULL COMMENT '买入商户名称',
  `consumer_order_no`       varchar(32)         NOT NULL COMMENT '消费订单号',
  `gmt_create`              datetime            NOT NULL COMMENT '创建日期',
  `gmt_modified`            datetime            NOT NULL COMMENT '修改日期',
  PRIMARY KEY (`code`) USING BTREE,
  UNIQUE KEY `idx_ consumer_order` (`consumer_order_no`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='买卖明细表';