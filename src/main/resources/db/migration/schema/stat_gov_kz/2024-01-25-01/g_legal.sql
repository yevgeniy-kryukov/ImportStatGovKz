-- Table: stat_gov_kz.g_legal

-- DROP TABLE IF EXISTS stat_gov_kz.g_legal;

CREATE TABLE IF NOT EXISTS stat_gov_kz.g_legal
(
    id bigint NOT NULL,
    bin_iin character varying(12) COLLATE pg_catalog."default" NOT NULL,
    full_name_kz character varying(1000) COLLATE pg_catalog."default",
    full_name character varying(1000) COLLATE pg_catalog."default",
    date_reg timestamp without time zone,
    oked_main_code character varying(5) COLLATE pg_catalog."default" NOT NULL,
    oked_main_activity_name_kz character varying(1000) COLLATE pg_catalog."default",
    oked_main_activity_name character varying(1000) COLLATE pg_catalog."default",
    secondary_oked_code_list character varying(750) COLLATE pg_catalog."default",
    krp_code character varying(3) COLLATE pg_catalog."default" NOT NULL,
    krp_name_kz character varying(1000) COLLATE pg_catalog."default",
    krp_name character varying(1000) COLLATE pg_catalog."default",
    kse_code character varying(10) COLLATE pg_catalog."default",
    kse_name_kz character varying(1000) COLLATE pg_catalog."default",
    kse_name character varying(1000) COLLATE pg_catalog."default",
    kfs_code character varying(2) COLLATE pg_catalog."default",
    kfs_name_kz character varying(1000) COLLATE pg_catalog."default",
    kfs_name character varying(1000) COLLATE pg_catalog."default",
    kato_code character varying(9) COLLATE pg_catalog."default",
    locality_name_kz character varying(1000) COLLATE pg_catalog."default",
    locality_name character varying(1000) COLLATE pg_catalog."default",
    legal_address character varying(1000) COLLATE pg_catalog."default",
    leader_name character varying(750) COLLATE pg_catalog."default",
    cut_id integer NOT NULL,
    type_legal_unit_id integer NOT NULL,
    leader_gl_person_id bigint,
    actualization_dt timestamp without time zone NOT NULL,
    is_actual boolean NOT NULL DEFAULT true,
    CONSTRAINT g_legal_pk PRIMARY KEY (id),
    CONSTRAINT g_legal_bin_iin_uk UNIQUE (bin_iin),
    CONSTRAINT g_legal_cut_id_fk FOREIGN KEY (cut_id)
        REFERENCES stat_gov_kz.d_cut (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT g_legal_type_legal_unit_id_fk FOREIGN KEY (type_legal_unit_id)
        REFERENCES stat_gov_kz.d_type_legal_unit (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

WITH (
    autovacuum_enabled = FALSE,
    toast.autovacuum_enabled = FALSE
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS stat_gov_kz.g_legal
    OWNER to postgres;

COMMENT ON TABLE stat_gov_kz.g_legal
    IS 'Правовая единица (юр.лицо)';

COMMENT ON COLUMN stat_gov_kz.g_legal.id
    IS 'Идентификатор';

COMMENT ON COLUMN stat_gov_kz.g_legal.bin_iin
    IS 'БИН (ИИН)';

COMMENT ON COLUMN stat_gov_kz.g_legal.full_name_kz
    IS 'Полное наименование (каз)';

COMMENT ON COLUMN stat_gov_kz.g_legal.full_name
    IS 'Полное наименование ';

COMMENT ON COLUMN stat_gov_kz.g_legal.date_reg
    IS 'Дата регистрации';

COMMENT ON COLUMN stat_gov_kz.g_legal.oked_main_code
    IS 'ОКЭД';

COMMENT ON COLUMN stat_gov_kz.g_legal.oked_main_activity_name_kz
    IS 'Наименование основного вида деятельности (каз)';

COMMENT ON COLUMN stat_gov_kz.g_legal.oked_main_activity_name
    IS 'Наименование основного вида деятельности';

COMMENT ON COLUMN stat_gov_kz.g_legal.secondary_oked_code_list
    IS 'Втор.ОКЭД';

COMMENT ON COLUMN stat_gov_kz.g_legal.krp_code
    IS 'КРП';

COMMENT ON COLUMN stat_gov_kz.g_legal.krp_name_kz
    IS 'Наименование КРП (каз)';

COMMENT ON COLUMN stat_gov_kz.g_legal.krp_name
    IS 'Наименование КРП';

COMMENT ON COLUMN stat_gov_kz.g_legal.kse_code
    IS 'КСЭ';

COMMENT ON COLUMN stat_gov_kz.g_legal.kse_name_kz
    IS 'ЭСЖ атауы';

COMMENT ON COLUMN stat_gov_kz.g_legal.kse_name
    IS 'Наименование КСЭ';

COMMENT ON COLUMN stat_gov_kz.g_legal.kfs_code
    IS 'КФС';

COMMENT ON COLUMN stat_gov_kz.g_legal.kfs_name_kz
    IS 'МНЖ атауы';

COMMENT ON COLUMN stat_gov_kz.g_legal.kfs_name
    IS 'Наименование КФС';

COMMENT ON COLUMN stat_gov_kz.g_legal.kato_code
    IS 'КАТО';

COMMENT ON COLUMN stat_gov_kz.g_legal.locality_name_kz
    IS 'Наименование населенного пункта (каз)';

COMMENT ON COLUMN stat_gov_kz.g_legal.locality_name
    IS 'Наименование населенного пункта';

COMMENT ON COLUMN stat_gov_kz.g_legal.legal_address
    IS 'Юридический адрес';

COMMENT ON COLUMN stat_gov_kz.g_legal.leader_name
    IS 'ФИО руководителя';

COMMENT ON COLUMN stat_gov_kz.g_legal.cut_id
    IS 'Идентификатор среза';

COMMENT ON COLUMN stat_gov_kz.g_legal.type_legal_unit_id
    IS 'Идентификатор типа правовой единицы';

COMMENT ON COLUMN stat_gov_kz.g_legal.leader_gl_person_id
    IS 'Идентификатор глобальной персоны';

COMMENT ON COLUMN stat_gov_kz.g_legal.actualization_dt
    IS 'Дата, время актуализации';

COMMENT ON COLUMN stat_gov_kz.g_legal.is_actual
    IS 'Признак актульности';