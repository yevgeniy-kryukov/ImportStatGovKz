CREATE FUNCTION etl_util_pkg.create_person_gl(p_iin character varying,
										p_surname character varying, 
										p_name character varying, 
										p_secondname character varying, 
										p_birth_date timestamp without time zone,
										p_rnn character varying,
										p_h_country_id integer,
										p_h_db_source_id integer) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
    gl_id numeric;
BEGIN
	insert into z.gl_person (
		id,
		h_db_source_id,
		iin,
		surname,
		name,
		secondname,
		birth_date,
		rnn,
		h_country_id
	) values (
		nextval('z.gl_person_seq'),
		p_h_db_source_id,
		p_iin,
		p_surname,
		p_name,
		p_secondname,
		p_birth_date,
		p_rnn,
		p_h_country_id
	)
	returning id into gl_id;
	
    RETURN gl_id;
END;
$$;