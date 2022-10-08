create table if not exists stat_gov_kz.d_situational_code (
    id integer not null,
	name_kz character varying (255) not null,
    name_ru character varying (255) not null,
    is_updated boolean default true not null,
    constraint d_situational_code_pk primary key (id)
);
comment on table stat_gov_kz.d_situational_code is 'Справочник "Ситуационный код"';
comment on column stat_gov_kz.d_situational_code.id is 'Идентификатор';
comment on column stat_gov_kz.d_situational_code.name_kz is 'Наименование на казахском';
comment on column stat_gov_kz.d_situational_code.name_ru is 'Наименование на русском';
comment on column stat_gov_kz.d_situational_code.is_updated is 'Признак загрузки (обновления) данных правовых единиц (юр.лиц) по сит.коду';

insert into stat_gov_kz.d_situational_code (id, name_kz, name_ru)
values  (39354, 'Қайта тіркелген', 'Вновь зарегистрированное'),
		(39355, 'Белсенді', 'Активное'),
		(39356, 'Қызметін уақытша  тоқтатқандар', 'Временно приостановившее деятельность'),
		(39358, 'Ақпарат жоқ немесе әрекетсіз', 'Нет информации или бездействующий'),
		(534829, 'Қызметін тоқтатқандар', 'Приостановившее деятельность'),
		(39359, 'Таратылу үдерісінде', 'В процессе ликвидации');
		