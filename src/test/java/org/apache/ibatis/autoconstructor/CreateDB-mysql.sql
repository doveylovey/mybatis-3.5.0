-- 创建 subject 表并向其中插入数据
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject` (
  `id`     int(11)  NOT NULL AUTO_INCREMENT,
  `name`   varchar(20)       DEFAULT NULL,
  `age`    int(11)  NOT NULL,
  `height` int(11)           DEFAULT NULL,
  `weight` int(11)           DEFAULT NULL,
  `active` bit(1)            DEFAULT NULL,
  `dt`     datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO subject (`id`, `name`, `age`, `height`, `weight`, `active`, `dt`)
VALUES (1, 'a', 10, 100, 45, 1, CURRENT_TIMESTAMP);
INSERT INTO subject (`id`, `name`, `age`, `height`, `weight`, `active`, `dt`)
VALUES (2, 'b', 10, NULL, 45, 1, CURRENT_TIMESTAMP);
INSERT INTO subject (`id`, `name`, `age`, `height`, `weight`, `active`, `dt`)
VALUES (3, 'c', 10, NULL, NULL, 0, CURRENT_TIMESTAMP);

-- 创建 extensive_subject 表并向其中插入数据
DROP TABLE IF EXISTS `extensive_subject`;
CREATE TABLE `extensive_subject` (
  `aByte`      tinyint(4)   DEFAULT NULL,
  `aShort`     smallint(6)  DEFAULT NULL,
  `aChar`      char(1)      DEFAULT NULL,
  `anInt`      int(11)      DEFAULT NULL,
  `aLong`      bigint(20)   DEFAULT NULL,
  `aFloat`     float        DEFAULT NULL,
  `aDouble`    double       DEFAULT NULL,
  `aBoolean`   bit(1)       DEFAULT NULL,
  `aString`    varchar(255) DEFAULT NULL,
  `anEnum`     varchar(50)  DEFAULT NULL,
  `aClob`      longtext,
  `aBlob`      longblob,
  `aTimestamp` timestamp NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
INSERT INTO extensive_subject (`aByte`, `aShort`, `aChar`, `anInt`, `aLong`, `aFloat`, `aDouble`, `aBoolean`, `aString`, `anEnum`, `aClob`, `aBlob`, `aTimestamp`)
VALUES (1, 1, 'a', 1, 1, 1, 1.0, 1, 'a', 'AVALUE', 'ACLOB', 'aaaaaabbbbbb', CURRENT_TIMESTAMP);
INSERT INTO extensive_subject (`aByte`, `aShort`, `aChar`, `anInt`, `aLong`, `aFloat`, `aDouble`, `aBoolean`, `aString`, `anEnum`, `aClob`, `aBlob`, `aTimestamp`)
VALUES (2, 2, 'b', 2, 2, 2, 2.0, 0, 'b', 'BVALUE', 'BCLOB', '010101010101', CURRENT_TIMESTAMP);
INSERT INTO extensive_subject (`aByte`, `aShort`, `aChar`, `anInt`, `aLong`, `aFloat`, `aDouble`, `aBoolean`, `aString`, `anEnum`, `aClob`, `aBlob`, `aTimestamp`)
VALUES (3, 3, 'c', 3, 3, 3, 3.0, 1, 'c', 'CVALUE', 'CCLOB', '777d010078da', CURRENT_TIMESTAMP);
