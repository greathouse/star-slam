create table Project (
	ID varchar(50) not null,
	NAME varchar(500) not null,
	CREATED BIGINT not null
);
create primary key on project (id);
create unique index project_name_uidx on project (name);

create table Scan (
	ID varchar(50) not null,
	PROJECT_ID varchar(50) not null,
	CREATED BIGINT not null,
	DIRECTORY varchar(100) not null,
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
	NAME varchar(500),
	MD5 varchar(32),
	IS_NEW boolean,
	HAS_CHANGED boolean
)

--down
drop table ConfigFile;
drop table Scan;
drop table Project;