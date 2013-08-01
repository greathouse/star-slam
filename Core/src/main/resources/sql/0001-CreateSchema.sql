create table Project (
	ID varchar(50) not null,
	NAME varchar(500) not null,
	CREATED BIGINT not null,
	ROOT_PATH varchar(100) not null
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

--down
drop table Scan;
drop table Project;