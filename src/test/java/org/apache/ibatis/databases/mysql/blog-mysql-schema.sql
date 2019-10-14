--
--    Copyright 2009-2016 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS blog;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS node;

CREATE TABLE comment
(
    id      INT          NOT NULL AUTO_INCREMENT,
    post_id INT          NOT NULL,
    name    VARCHAR(255) NOT NULL,
    comment VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE post_tag
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE tag
(
    id   INT          NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE blog
(
    id        INT NOT NULL AUTO_INCREMENT,
    author_id INT NOT NULL,
    title     VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE post
(
    id         INT          NOT NULL AUTO_INCREMENT,
    blog_id    INT,
    author_id  INT          NOT NULL,
    created_on TIMESTAMP    NOT NULL,
    section    VARCHAR(25)  NOT NULL,
    subject    VARCHAR(255) NOT NULL,
    body       LONGTEXT     NOT NULL,
    draft      INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (blog_id) REFERENCES blog (id)
);

CREATE TABLE author
(
    id                INT          NOT NULL AUTO_INCREMENT,
    username          VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    bio               LONGTEXT,
    favourite_section VARCHAR(25),
    PRIMARY KEY (id)
) AUTO_INCREMENT = 1000;

CREATE TABLE node
(
    id        INT NOT NULL,
    parent_id INT,
    PRIMARY KEY (id)
);

DROP PROCEDURE IF EXISTS selectTwoSetsOfAuthors;
CREATE PROCEDURE selectTwoSetsOfAuthors
    (DP1 INTEGER, DP2 INTEGER) PARAMETER STYLE JAVA
LANGUAGE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 2
EXTERNAL NAME 'org.apache.ibatis.databases.blog.StoredProcedures.selectTwoSetsOfTwoAuthors';

DROP PROCEDURE IF EXISTS insertAuthor;
CREATE PROCEDURE insertAuthor
    (DP1 INTEGER, DP2 VARCHAR(255), DP3 VARCHAR(255), DP4 VARCHAR(255)) PARAMETER STYLE JAVA
LANGUAGE JAVA
EXTERNAL NAME 'org.apache.ibatis.databases.blog.StoredProcedures.insertAuthor';


DROP PROCEDURE IF EXISTS selectAuthorViaOutParams;
CREATE PROCEDURE selectAuthorViaOutParams
    (ID INTEGER, OUT USERNAME VARCHAR(255), OUT PASSWORD VARCHAR(255), OUT EMAIL VARCHAR(255), OUT BIO VARCHAR(255)) PARAMETER STYLE JAVA
LANGUAGE JAVA
EXTERNAL NAME 'org.apache.ibatis.databases.blog.StoredProcedures.selectAuthorViaOutParams';
