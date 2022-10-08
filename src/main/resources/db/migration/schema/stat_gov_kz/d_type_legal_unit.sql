create table if not exists stat_gov_kz.d_type_legal_unit (
    id integer not null,
	name_kz character varying (255) not null,
    name_ru character varying (255) not null,
    is_updated boolean default true not null,
    constraint d_type_legal_unit_pk primary key (id)
);
comment on table stat_gov_kz.d_type_legal_unit is 'Справочник "Тип правовой единицы"';
comment on column stat_gov_kz.d_type_legal_unit.id is 'Идентификатор';
comment on column stat_gov_kz.d_type_legal_unit.name_kz is 'Наименование на казахском';
comment on column stat_gov_kz.d_type_legal_unit.name_ru is 'Наименование на русском';
comment on column stat_gov_kz.d_type_legal_unit.is_updated is 'Признак загрузки (обновления) данных правовых единиц (юр.лиц) по коду типа правовой ед.';

insert into stat_gov_kz.d_type_legal_unit (id, name_kz, name_ru, is_updated)
values  (742679, 'Заңды тұлға', 'Юридическое лицо', false),
		(742680, 'Филиал', 'Филиал', false),
		(742681, 'Жеке кәсіпкерлік түріндегі дара кәсіпкерлік субьектісі', 'Субъект индивидуального предпринимательства в виде личного предпринимательства', true),
		(742684, 'Шетелдік заңды тұлғаның филиалы', 'Филиал иностранного юридического лица', false),
		(742687, 'Бірлескен кәсіпкерлік түріндегі дара кәсіпкерлік субъектісі', 'Субъект индивидуального предпринимательства в виде совместного предпринимательства', true);