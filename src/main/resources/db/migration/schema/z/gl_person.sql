drop table if exists z.gl_person;

CREATE TABLE z.gl_person (
    id bigint not null,
    gl_person_id bigint,
    h_db_source_id integer not null,
    gl_person_address_id bigint,
    iin character varying(12),
    surname character varying(256),
    name character varying(256),
    secondname character varying(256),
    birth_date timestamp without time zone,
    register_date timestamp without time zone,
    rnn character varying(12),
    is_deleted boolean default false not null,
    h_country_id integer,
    is_resident boolean default true not null,
    reg_h_addr_stat_id integer,
    reg_h_addr_inva_id integer,
    reg_addr_begin_date timestamp without time zone,
    reg_addr_end_date timestamp without time zone,
    --constraint gl_person_pk primary key (id),
 	--constraint gl_person_gl_person_id_fk foreign key (gl_person_id) references z.gl_person (id),
	constraint gl_person_h_db_source_id_fk foreign key (h_db_source_id) references z.h_db_source (id),
	constraint gl_person_gl_person_address_id_fk foreign key (gl_person_address_id) references z.gl_person_address (id),
	constraint gl_person_h_country_id_fk foreign key (h_country_id) references z.h_country (id),
	constraint gl_person_reg_h_addr_stat_id_fk foreign key (reg_h_addr_stat_id) references z.h_address_status (id),
	constraint gl_person_reg_h_addr_inva_id_fk foreign key (reg_h_addr_inva_id) references z.h_address_invalidity (id)
)
PARTITION BY LIST (h_db_source_id);

CREATE INDEX gl_person_id_idx ON z.gl_person USING btree (id);
CREATE INDEX gl_person_iin_idx ON z.gl_person USING btree (iin, gl_person_id DESC, id DESC);
CREATE INDEX gl_person_h_db_source_id_ix ON z.gl_person USING btree (h_db_source_id);

COMMENT ON COLUMN z.gl_person.gl_person_address_id IS 'Идентификатор адреса постоянной регистрации';
COMMENT ON COLUMN z.gl_person.reg_h_addr_stat_id IS 'Идентификатор статуса адреса постоянной регистрации';
COMMENT ON COLUMN z.gl_person.reg_h_addr_inva_id IS 'Идентификатор причины недействительности адреса постоянной регистрации';
COMMENT ON COLUMN z.gl_person.reg_addr_begin_date IS 'Дата постоянной регистрации';
COMMENT ON COLUMN z.gl_person.reg_addr_end_date IS 'Дата снятия постоянной регистрации';

CREATE TABLE z.gl_person_part_1 PARTITION OF z.gl_person FOR VALUES IN (1) WITH (autovacuum_enabled='false', toast.autovacuum_enabled='false');
CREATE TABLE z.gl_person_part_2 PARTITION OF z.gl_person FOR VALUES IN (2) WITH (autovacuum_enabled='false', toast.autovacuum_enabled='false');
CREATE TABLE z.gl_person_part_69 PARTITION OF z.gl_person FOR VALUES IN (69) WITH (autovacuum_enabled='false', toast.autovacuum_enabled='false');

drop sequence if exists z.gl_person_seq;

create sequence z.gl_person_seq;
