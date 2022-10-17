create table if not exists stat_gov_kz.g_legal (
	id bigint not null,
	bin_iin character varying (12) not null,
	full_name_kz character varying (1000),
	full_name character varying (1000),
	date_reg timestamp without time zone,
	oked_main_code character varying (5) not null,
	oked_main_activity_name_kz character varying (1000),
	oked_main_activity_name character varying (1000),
	secondary_oked_code_list character varying (750),
	krp_code character varying (3) not null,
	krp_name_kz character varying (1000),
	krp_name character varying (1000),
	kato_code character varying (9),
	locality_name_kz character varying (1000),
	locality_name character varying (1000),
	legal_address character varying (1000),
	leader_name character varying (750),
	cut_id integer not null,
	type_legal_unit_id integer not null,
	leader_gl_person_id bigint,
	actualization_dt timestamp without time zone not null,
	is_actual boolean default true not null,
	constraint g_legal_pk primary key (id),
	constraint g_legal_bin_iin_uk unique (bin_iin),
	constraint g_legal_type_legal_unit_id_fk foreign key (type_legal_unit_id) references stat_gov_kz.d_type_legal_unit (id),
	constraint g_legal_cut_id_fk foreign key (cut_id) references stat_gov_kz.d_cut (id)
) WITH (autovacuum_enabled='false', toast.autovacuum_enabled='false');
comment on table stat_gov_kz.g_legal is 'Правовая единица (юр.лицо)';
comment on column stat_gov_kz.g_legal.id is 'Идентификатор';
comment on column stat_gov_kz.g_legal.bin_iin is 'БИН (ИИН)';
comment on column stat_gov_kz.g_legal.full_name_kz is 'Полное наименование (каз)';
comment on column stat_gov_kz.g_legal.full_name is 'Полное наименование ';
comment on column stat_gov_kz.g_legal.date_reg is 'Дата регистрации';
comment on column stat_gov_kz.g_legal.oked_main_code is 'ОКЭД';
comment on column stat_gov_kz.g_legal.oked_main_activity_name_kz is 'Наименование основного вида деятельности (каз)';
comment on column stat_gov_kz.g_legal.oked_main_activity_name is 'Наименование основного вида деятельности';
comment on column stat_gov_kz.g_legal.secondary_oked_code_list is 'Втор.ОКЭД';
comment on column stat_gov_kz.g_legal.krp_code is 'КРП';
comment on column stat_gov_kz.g_legal.krp_name_kz is 'Наименование КРП (каз)';
comment on column stat_gov_kz.g_legal.krp_name is 'Наименование КРП';
comment on column stat_gov_kz.g_legal.kato_code is 'КАТО';
comment on column stat_gov_kz.g_legal.locality_name_kz is 'Наименование населенного пункта (каз)';
comment on column stat_gov_kz.g_legal.locality_name is 'Наименование населенного пункта';
comment on column stat_gov_kz.g_legal.legal_address is 'Юридический адрес';
comment on column stat_gov_kz.g_legal.leader_name is 'ФИО руководителя';
comment on column stat_gov_kz.g_legal.cut_id is 'Идентификатор среза';
comment on column stat_gov_kz.g_legal.type_legal_unit_id is 'Идентификатор типа правовой единицы';
comment on column stat_gov_kz.g_legal.leader_gl_person_id is 'Идентификатор глобальной персоны';
comment on column stat_gov_kz.g_legal.actualization_dt is 'Дата, время актуализации';
comment on column stat_gov_kz.g_legal.is_actual is 'Признак актульности';

create sequence if not exists stat_gov_kz.g_legal_seq;