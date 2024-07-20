alter table stat_gov_kz.d_situational_code add column is_in_group_active boolean not null default false;
alter table stat_gov_kz.d_situational_code add column is_in_group_registered boolean not null default true;

comment on column stat_gov_kz.d_situational_code.is_in_group_active is 'Входит в группу действующих (активных)';
comment on column stat_gov_kz.d_situational_code.is_in_group_registered is 'Входит в группу зарегистрированных';
