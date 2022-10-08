CREATE TABLE z.h_db_source (
    id integer NOT NULL,
    code character varying(256),
    name_ru character varying(1024),
    name_kz character varying(1024),
    is_active boolean DEFAULT true,
    gl_order_index integer DEFAULT 0 NOT NULL,
	constraint h_db_source_pk primary key (id)
);

COMMENT ON COLUMN z.h_db_source.gl_order_index IS 'Приоритет источника при получении глобального идентификатора, источники со значением <= 0 не учитываются';

insert into z.h_db_source (id, code, name_ru, name_kz) values (1, '1', 'ГБД ФЛ', 'ГБД ФЛ'),
															  (2, '2', 'ГБД ЮЛ', 'ГБД ЮЛ'),
															  (69, '69', 'stat.gov.kz', 'stat.gov.kz');
															  
create sequence z.h_db_source_seq;

select setval('z.h_db_source_seq', 69);