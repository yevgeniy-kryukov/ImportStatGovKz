alter table stat_gov_kz.g_legal add column leader_lname character varying(255);
alter table stat_gov_kz.g_legal add column leader_fname character varying(255);
alter table stat_gov_kz.g_legal add column leader_mname character varying(255);

COMMENT ON COLUMN stat_gov_kz.g_legal.leader_lname IS 'Фамилия руководителя';
COMMENT ON COLUMN stat_gov_kz.g_legal.leader_fname IS 'Имя руководителя';
COMMENT ON COLUMN stat_gov_kz.g_legal.leader_mname IS 'Отчество руководителя';