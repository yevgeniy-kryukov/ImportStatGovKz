CREATE TABLE z.h_taxation_type (
    id integer NOT NULL,
    code character varying(256),
    name_ru character varying(1024),
    name_kz character varying(1024),
    is_active boolean DEFAULT true,
	constraint h_taxation_type_pk primary key (id)
);

create sequence z.h_taxation_type_seq;