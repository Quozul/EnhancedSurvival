create database if not exists `enhanced_survival`;

use `enhanced_survival`;

create table if not exists `user` (
	uuid char(36) primary key,
    warns integer default 0,
    mute_end timestamp default NOW(),

    team integer default 0 references `team`(team_id)
);

create table if not exists `team` (
	team_id integer auto_increment primary key
);

create table if not exists `chunk` (
	x integer,
    y integer,
    protected boolean default false,

    user char(36) not null references `user`(uuid),
    team integer default 0 references `team`(team_id),

    primary key (x, y)
);