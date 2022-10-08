CREATE TABLE z.h_address_status (
    id integer NOT NULL,
    code character varying(2) NOT NULL,
    name_ru character varying(64) NOT NULL,
    name_kz character varying(64) NOT NULL,
    modification_date timestamp without time zone DEFAULT current_timestamp NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
	constraint h_address_status_pk primary key (id)
);

COMMENT ON TABLE z.h_address_status IS 'Справочник статусов адресов';

create sequence z.h_address_status_seq;