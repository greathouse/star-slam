create table Project (
	ID varchar(50) not null,
	NAME varchar(500) not null,
	ROOT_PATH varchar(100) not null,
	FILE_GLOB varchar(100) not null
);
create primary key on project (id);
create unique index project_name_uidx on project (name);

create table Scan ( 
	ID varchar(50) not null,
	PROJECT_ID varchar(50) not null,
	CREATED BIGINT not null,
	COMPLETED BIGINT,
	ROOT_PATH varchar(100) not null,
	PRODUCTION_DATE bigint,
	PROCESSING_TIME BIGINT,
	STATUS int not null
);
create primary key on Scan (id);
alter table Scan add foreign key (project_id) references project(id);

create table ScannedFile (
	ID varchar(50) not null,
	SCAN_ID varchar(50) not null,
	FILENAME varchar(100) not null,
	RELATIVE_PATH varchar(500) not null,
	FULLPATH varchar(500) not null,
	IS_NEW boolean not null,
	HAS_CHANGED boolean not null,
	DATA clob,
	SCANNER varchar(50) not null,
	MD5 varchar(32) not null
);
create primary key on ScannedFile (id);
alter table ScannedFile add foreign key (scan_id) references Scan(id);

--down
drop table ScannedFile;
drop table Scan;
drop table Project;