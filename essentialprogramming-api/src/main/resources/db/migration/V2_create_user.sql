DROP TABLE IF EXISTS `User`;
create table if not exists public."user" (
  userid smallint NOT NULL GENERATED ALWAYS AS IDENTITY primary key,
  UserName varchar(50) NOT NULL,
  FirstName varchar(255)  NOT NULL,
  LastName varchar(255) NOT NULL,
  Email varchar(50) NOT NULL,
  Phone varchar(20) NOT NULL,
  DefaultLanguageId smallint DEFAULT NULL,
  Validated boolean NOT NULL,
  UserKey varchar(64) DEFAULT NULL,
  HistoryUserId smallint DEFAULT NULL,
  Active boolean NOT NULL ,
  Deleted boolean NOT NULL ,
  CreatedDate Timestamp DEFAULT NULL,
  ModifiedDate timestamp DEFAULT NULL,
  CreatedBy smallint DEFAULT NULL,
  ModifiedBy smallint DEFAULT NULL,
  Logkey char(38) DEFAULT NULL,
  Password varchar(200) DEFAULT NULL
);