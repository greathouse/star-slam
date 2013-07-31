create table Project (
	ID varchar(50) not null,
	NAME varchar(500) not null,
	CREATED BIGINT not null,
	ROOT_PATH varchar(100) not null,
	CONFIG_FILE_PATTERN varchar(100),
	SQL_FILE_DIRECTORY varchar(100)
);
create primary key on project (id);
create unique index project_name_uidx on project (name);

create table Scan (
	ID varchar(50) not null,
	PROJECT_ID varchar(50) not null,
	CREATED BIGINT not null,
	ROOT_PATH varchar(100) not null,
	CONFIG_FILE_PATTERN varchar(100),
	SQL_FILE_DIRECTORY varchar(100),
	DEPLOY_TIME bigint not null
);
create primary key on Scan (id);
alter table Scan add foreign key (project_id) references project(id);

create table ConfigFile (
	ID varchar(50) not null,
	SCAN_ID varchar(50) not null,
	CREATED bigint not null,
	NAME varchar(500) not null,
	MD5 varchar(32) not null,
	IS_NEW boolean not null,
	HAS_CHANGED boolean not null
);
create primary key on ConfigFile (id);
alter table ConfigFile add foreign key (scan_id) references scan(id);

create table SqlFile (
	ID varchar(50) not null,
	SCAN_ID varchar(50) not null,
	CREATED bigint not null,
	NAME varchar(500) not null,
	MD5 varchar(32) not null,
	IS_NEW boolean not null
);
create primary key on SqlFile (id);
alter table SqlFile add foreign key (scan_id) references scan(id);



--down
drop table ScanConfig;
drop table ConfigFile;
drop table Scan;
drop table Project;