-- View: stat_gov_kz.oked_list

-- DROP VIEW stat_gov_kz.oked_list;

CREATE OR REPLACE VIEW stat_gov_kz.oked_list
 AS
 SELECT oked.id,
    oked.code,
    oked.name_ru,
    oked.item_id
   FROM stat_gov_kz.oked
  WHERE length(oked.code::text) = 1 AND oked.end_date IS NULL AND upper(oked.name_ru::text) <> 'ВСЕГО'::text
  ORDER BY oked.code;

ALTER TABLE stat_gov_kz.oked_list
    OWNER TO postgres;

