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