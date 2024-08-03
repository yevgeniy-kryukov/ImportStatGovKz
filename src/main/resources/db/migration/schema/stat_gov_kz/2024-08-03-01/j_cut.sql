create sequence stat_gov_kz.j_cut_seq;

create table stat_gov_kz.j_cut (
    id integer not null primary key,
    cut_id integer not null references stat_gov_kz.d_cut (id),
    type_legal_id integer not null references stat_gov_kz.d_type_legal_unit (id),
    oked_item_id integer not null,
    time_start timestamp without time zone not null default localtimestamp,
    time_end timestamp without time zone,
    unique (cut_id, type_legal_id, oked_item_id, time_start)
);

comment on table stat_gov_kz.j_cut is 'Журнал загрузки среза данных по ОКЭД';