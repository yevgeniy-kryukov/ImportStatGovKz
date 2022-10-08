create table if not exists  stat_gov_kz.d_cut (
    id integer not null,
    name character varying (255) not null,
    constraint d_cut_pk primary key (id)
);
comment on table stat_gov_kz.d_cut is 'Справочник "Срез"';
comment on column stat_gov_kz.d_cut.id is 'Идентификатор';
comment on column stat_gov_kz.d_cut.name is 'Наименование';