CREATE FUNCTION etl_util_pkg.get_gl_person_id(p_iin character varying) RETURNS bigint
    LANGUAGE sql STRICT PARALLEL SAFE
    AS $$
    SELECT g.id
    FROM z.gl_person g
    WHERE g.iin = p_iin AND g.gl_person_id IS NULL AND g.h_db_source_id = 1 
    ORDER BY g.id DESC 
    LIMIT 1;
$$;