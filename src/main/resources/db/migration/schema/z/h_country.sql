create table z.h_country (
    id integer not null,
    code character varying(256),
    name_ru character varying(1024),
    name_kz character varying(1024),
    is_active boolean default true,
    h_taxation_type_id integer,
    modification_date timestamp without time zone default localtimestamp,
	constraint h_country_pk primary key (id),
	constraint h_country_h_taxation_type_id_fk foreign key (h_taxation_type_id) references z.h_taxation_type (id)
);

insert into z.h_country (id, code, name_ru, name_kz) values (105, 'kz', 'Казахстан', 'Казахстан');

create sequence z.h_country_seq;

select setval('z.h_db_source_seq', 105);