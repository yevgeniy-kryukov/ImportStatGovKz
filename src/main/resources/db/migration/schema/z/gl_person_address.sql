CREATE TABLE z.gl_person_address (
    id bigint NOT NULL,
    address_line character varying(2048),
    is_deleted numeric DEFAULT 0,
    h_country_id integer,
    h_district_id integer,
    h_region_id integer,
    city character varying(256),
    region character varying(256),
    district character varying(256),
    street character varying(256),
    building character varying(256),
    corpus character varying(256),
    flat character varying(256),
    foreign_district_name character varying(128),
    foreign_region_name character varying(128),
    ar_code character varying(32),
	constraint gl_person_address_pk primary key (id),
	constraint gl_person_address_h_country_id_fk foreign key (h_country_id) references z.h_country (id),
	constraint gl_person_address_h_district_id_fk foreign key (h_district_id) references z.h_district (id),
	constraint gl_person_address_h_region_id_fk foreign key (h_region_id) references z.h_region (id)
)
WITH (autovacuum_enabled='false', toast.autovacuum_enabled='false');

create sequence z.gl_person_address_seq;