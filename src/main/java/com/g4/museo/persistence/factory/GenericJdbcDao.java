package com.g4.museo.persistence.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class GenericJdbcDao {
    protected Log LOGGER = LogFactory.getLog(this.getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate ;

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplateRC ;

    public JdbcTemplate getJdbcTemplate()
    {
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate()
    {
        return namedParameterJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplateRC()
    {
        return namedParameterJdbcTemplateRC;
    }
}
