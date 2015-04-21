package com.harrywu.springweb.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by sadupa on 8/6/14.
 */
public interface HibernateSessionFactory {

    Session getSession();
    SessionFactory getSessionFactory();
}
