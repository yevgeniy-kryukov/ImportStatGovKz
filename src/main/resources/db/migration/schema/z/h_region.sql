CREATE TABLE z.h_region (
    id integer NOT NULL,
    code character varying(256),
    name_ru character varying(1024),
    name_kz character varying(1024),
    is_active boolean DEFAULT true,
    kato_code character varying(25),
    modification_date timestamp without time zone,
	constraint h_region_pk primary key (id)
);

create sequence z.h_region_seq;