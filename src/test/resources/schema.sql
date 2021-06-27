drop table if exists USERS;
drop table if exists country;

create table USERS(
  ID int not null,
  NAME varchar(100) not null,
  country int,
  PRIMARY KEY ( ID )
);

CREATE TABLE country (
  id   INTEGER      NOT NULL,
  name VARCHAR(128) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE usr_country (
  id   INTEGER NOT NULL,
  CNTRY_ID INTEGER NOT NULL,
  CNTRY_NAME VARCHAR(128) NOT NULL,
  USR_NAME VARCHAR(128) NOT NULL,
  PRIMARY KEY (id)
);