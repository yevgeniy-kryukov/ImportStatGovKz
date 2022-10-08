CREATE TABLE z.h_district (
    id integer NOT NULL,
    code character varying(256) NOT NULL,
    name_ru character varying(1024) NOT NULL,
    name_kz character varying(1024) NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    modification_date timestamp without time zone NOT NULL,
    kato character varying(9),
    new_code character varying(256),
	constraint h_district_pk primary key (id)
);

create sequence z.h_district_seq;