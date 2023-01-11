CREATE TABLE IF not exists address (
  address_id bigint auto_increment not null primary key,
  location_id bigint not null,
  address1 varchar(1024) not null,
  address2 varchar(1024),
  city varchar(255) ,
  state varchar(255),
  zip_code varchar(255),
  country varchar(255)
);

CREATE TABLE IF not exists location(
location_id bigint auto_increment not null primary key,
accessibility varchar(1024),
adminEmails varchar(255),
alternateName varchar(255),
description text ,
email varchar(255),
languages varchar(255),
latitude varchar(255),
longitude varchar(255),
name varchar(255),
shortDesc varchar(1024),
transportation varchar(1024),
website varchar(1024),
is_virtual bit(1)
);

ALTER TABLE address
ADD CONSTRAINT Fk_addressLocation
FOREIGN KEY (location_id) REFERENCES location(location_id);