DROP TABLE IF EXISTS App;
CREATE TABLE App (
  id int(11) NOT NULL AUTO_INCREMENT ,
  name varchar(50) NOT NULL ,
  modify_time timestamp NOT NULL,
  PRIMARY KEY (id)
);
DROP TABLE IF EXISTS Person;
CREATE TABLE Person (
  id int(11) NOT NULL AUTO_INCREMENT ,
  title varchar(50) NOT NULL ,
  first_name varchar(50) NOT NULL ,
  last_name varchar(50) NOT NULL ,
  PRIMARY KEY (id)
);