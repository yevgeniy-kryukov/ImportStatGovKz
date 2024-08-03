CREATE TABLE IF NOT EXISTS stat_gov_kz.d_kato
(
    id integer NOT NULL,
    code character varying(9),
    name character varying(255) NOT NULL,
    CONSTRAINT d_kato_pk PRIMARY KEY (id)
);

comment on table stat_gov_kz.d_kato is 'Справочник КАТО';

INSERT INTO stat_gov_kz.d_kato VALUES (741880, NULL, 'РЕСПУБЛИКА КАЗАХСТАН');
INSERT INTO stat_gov_kz.d_kato VALUES (77208141, '100000000', 'ОБЛАСТЬ АБАЙ');
INSERT INTO stat_gov_kz.d_kato VALUES (247783, '110000000', 'АКМОЛИНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (248875, '150000000', 'АКТЮБИНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (250502, '190000000', 'АЛМАТИНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (252311, '230000000', 'АТЫРАУСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (253160, '270000000', 'ЗАПАДНО-КАЗАХСТАНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (255577, '310000000', 'ЖАМБЫЛСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (77208139, '330000000', 'ОБЛАСТЬ ЖЕТІСУ');
INSERT INTO stat_gov_kz.d_kato VALUES (256619, '350000000', 'КАРАГАНДИНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (258742, '390000000', 'КОСТАНАЙСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (260099, '430000000', 'КЫЗЫЛОРДИНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (260907, '470000000', 'МАНГИСТАУСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (263009, '550000000', 'ПАВЛОДАРСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (264023, '590000000', 'СЕВЕРО-КАЗАХСТАНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (20243032, '610000000', 'ТУРКЕСТАНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (77208140, '620000000', 'ОБЛАСТЬ ҰЛЫТАУ');
INSERT INTO stat_gov_kz.d_kato VALUES (264990, '630000000', 'ВОСТОЧНО-КАЗАХСТАНСКАЯ ОБЛАСТЬ');
INSERT INTO stat_gov_kz.d_kato VALUES (268012, '710000000', 'Г.АСТАНА');
INSERT INTO stat_gov_kz.d_kato VALUES (268020, '750000000', 'Г.АЛМАТЫ');
INSERT INTO stat_gov_kz.d_kato VALUES (20242100, '790000000', 'Г.ШЫМКЕНТ');