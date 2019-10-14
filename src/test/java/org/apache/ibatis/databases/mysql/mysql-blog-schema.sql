DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `id`                INT(11)      NOT NULL AUTO_INCREMENT,
  `username`          VARCHAR(255) NOT NULL,
  `password`          VARCHAR(255) NOT NULL,
  `email`             VARCHAR(255) NOT NULL,
  `bio`               LONGTEXT,
  `favourite_section` VARCHAR(25)           DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id`        INT(11) NOT NULL AUTO_INCREMENT,
  `author_id` INT(11) NOT NULL,
  `title`     VARCHAR(255)     DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id`         INT(11)      NOT NULL AUTO_INCREMENT,
  `blog_id`    INT(11)               DEFAULT NULL,
  `author_id`  INT(11)      NOT NULL,
  `created_on` DATETIME     NOT NULL,
  `section`    VARCHAR(25)  NOT NULL,
  `subject`    VARCHAR(255) NOT NULL,
  `body`       LONGTEXT     NOT NULL,
  `draft`      INT(11)      NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id`   INT(11)      NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag` (
  `post_id` INT(11) NOT NULL,
  `tag_id`  INT(11) NOT NULL,
  PRIMARY KEY (`post_id`, `tag_id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id`      INT(11)      NOT NULL AUTO_INCREMENT,
  `post_id` INT(11)      NOT NULL,
  `name`    VARCHAR(255) NOT NULL,
  `comment` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `node`;
CREATE TABLE `node` (
  `id`        INT(11) NOT NULL,
  `parent_id` INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;