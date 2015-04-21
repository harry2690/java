package com.harrywu.springweb.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

public class HibernateAwareObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = -2516238454841556952L;

    public HibernateAwareObjectMapper() {
        registerModule(new Hibernate4Module());
    }
}
