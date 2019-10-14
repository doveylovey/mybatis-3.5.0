DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `suppid` int(11)    NOT NULL AUTO_INCREMENT,
  `name`   varchar(80)         DEFAULT NULL,
  `status` varchar(2) NOT NULL,
  `addr1`  varchar(80)         DEFAULT NULL,
  `addr2`  varchar(80)         DEFAULT NULL,
  `city`   varchar(80)         DEFAULT NULL,
  `state`  varchar(80)         DEFAULT NULL,
  `zip`    varchar(5)          DEFAULT NULL,
  `phone`  varchar(80)         DEFAULT NULL,
  PRIMARY KEY (`suppid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `signon`;
CREATE TABLE `signon` (
  `username` varchar(25) NOT NULL,
  `password` varchar(25) NOT NULL,
  PRIMARY KEY (`username`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `userid`    varchar(80) NOT NULL,
  `email`     varchar(80) NOT NULL,
  `firstname` varchar(80) NOT NULL,
  `lastname`  varchar(80) NOT NULL,
  `status`    varchar(2)  DEFAULT NULL,
  `addr1`     varchar(80) NOT NULL,
  `addr2`     varchar(40) DEFAULT NULL,
  `city`      varchar(80) NOT NULL,
  `state`     varchar(80) NOT NULL,
  `zip`       varchar(20) NOT NULL,
  `country`   varchar(20) NOT NULL,
  `phone`     varchar(80) NOT NULL,
  PRIMARY KEY (`userid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `profile`;
CREATE TABLE `profile` (
  `userid`      varchar(80) NOT NULL,
  `langpref`    varchar(80) NOT NULL,
  `favcategory` varchar(30) DEFAULT NULL,
  `mylistopt`   int(11)     DEFAULT NULL,
  `banneropt`   int(11)     DEFAULT NULL,
  PRIMARY KEY (`userid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `bannerdata`;
CREATE TABLE `bannerdata` (
  `favcategory` varchar(80) NOT NULL,
  `bannername`  varchar(255) DEFAULT NULL,
  PRIMARY KEY (`favcategory`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `orderid`         int(11)        NOT NULL,
  `userid`          varchar(80)    NOT NULL,
  `orderdate`       date           NOT NULL,
  `shipaddr1`       varchar(80)    NOT NULL,
  `shipaddr2`       varchar(80) DEFAULT NULL,
  `shipcity`        varchar(80)    NOT NULL,
  `shipstate`       varchar(80)    NOT NULL,
  `shipzip`         varchar(20)    NOT NULL,
  `shipcountry`     varchar(20)    NOT NULL,
  `billaddr1`       varchar(80)    NOT NULL,
  `billaddr2`       varchar(80) DEFAULT NULL,
  `billcity`        varchar(80)    NOT NULL,
  `billstate`       varchar(80)    NOT NULL,
  `billzip`         varchar(20)    NOT NULL,
  `billcountry`     varchar(20)    NOT NULL,
  `courier`         varchar(80)    NOT NULL,
  `totalprice`      decimal(10, 2) NOT NULL,
  `billtofirstname` varchar(80)    NOT NULL,
  `billtolastname`  varchar(80)    NOT NULL,
  `shiptofirstname` varchar(80)    NOT NULL,
  `shiptolastname`  varchar(80)    NOT NULL,
  `creditcard`      varchar(80)    NOT NULL,
  `exprdate`        varchar(7)     NOT NULL,
  `cardtype`        varchar(80)    NOT NULL,
  `locale`          varchar(80)    NOT NULL,
  PRIMARY KEY (`orderid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `orderstatus`;
CREATE TABLE `orderstatus` (
  `orderid`   int(11)    NOT NULL,
  `linenum`   int(11)    NOT NULL,
  `timestamp` date       NOT NULL,
  `status`    varchar(2) NOT NULL,
  PRIMARY KEY (`orderid`, `linenum`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `lineitem`;
CREATE TABLE `lineitem` (
  `orderid`   int(11)        NOT NULL,
  `linenum`   int(11)        NOT NULL,
  `itemid`    varchar(10)    NOT NULL,
  `quantity`  int(11)        NOT NULL,
  `unitprice` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`orderid`, `linenum`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS category;
CREATE TABLE `category` (
  `catid` varchar(10) NOT NULL,
  `NAME`  varchar(80)  DEFAULT NULL,
  `descn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`catid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `productid` varchar(10) NOT NULL,
  `category`  varchar(10) NOT NULL,
  `NAME`      varchar(80)  DEFAULT NULL,
  `descn`     varchar(255) DEFAULT NULL,
  PRIMARY KEY (`productid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
CREATE INDEX productCat
  ON product (`category`);
CREATE INDEX productName
  ON product (`name`);

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `itemid`    varchar(10) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `listprice` decimal(10, 2) DEFAULT NULL,
  `unitcost`  decimal(10, 2) DEFAULT NULL,
  `supplier`  int(11)        DEFAULT NULL,
  `status`    varchar(2)     DEFAULT NULL,
  `attr1`     varchar(80)    DEFAULT NULL,
  `attr2`     varchar(80)    DEFAULT NULL,
  `attr3`     varchar(80)    DEFAULT NULL,
  `attr4`     varchar(80)    DEFAULT NULL,
  `attr5`     varchar(80)    DEFAULT NULL,
  PRIMARY KEY (`itemid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
CREATE INDEX itemProd
  ON item (`productid`);

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `itemid` varchar(10) NOT NULL,
  `qty`    int(11)     NOT NULL,
  PRIMARY KEY (`itemid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence` (
  `name`   varchar(30) NOT NULL,
  `nextid` int(11)     NOT NULL,
  PRIMARY KEY (`name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

