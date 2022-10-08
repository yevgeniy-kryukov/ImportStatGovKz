create table if not exists stat_gov_kz.j_loader (
    id integer not null,
	cut_id integer not null,
    started timestamp without time zone not null default localtimestamp,
	finished timestamp without time zone,
	error_text character varying (1000),
    constraint j_loader_pk primary key (id),
	constraint j_loader_cut_id_fk foreign key (cut_id) references stat_gov_kz.d_cut (id)
);
comment on table stat_gov_kz.j_loader is 'Журнал загрузок данных';
comment on column stat_gov_kz.j_loader.id is 'Идентификатор';
comment on column stat_gov_kz.j_loader.cut_id is 'Идентификатор среза';
comment on column stat_gov_kz.j_loader.started is 'Дата, время старта загрузки';
comment on column stat_gov_kz.j_loader.finished is 'Дата, время завершения загрузки';
comment on column stat_gov_kz.j_loader.error_text is 'Текст ошибки';

create sequence stat_gov_kz.j_loader_seq;
