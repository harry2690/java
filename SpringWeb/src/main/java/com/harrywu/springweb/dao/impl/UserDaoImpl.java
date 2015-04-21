package com.harrywu.springweb.dao.impl;

import com.harrywu.springweb.dao.HibernateSessionFactory;
import com.harrywu.springweb.dao.UserDao;
import com.harrywu.springweb.model.User;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by sadupa on 8/18/14.
 */

@Repository
@Component("defaultDao")
public class UserDaoImpl implements UserDao {

    @Autowired
    HibernateSessionFactory hibernateSessionFactory;

    @Override
    public boolean addUser(User user) {
        Session session = hibernateSessionFactory.getSession();
        session.save(user);
        return true;
    }

    @Override
    public User getUser(long id) {
        Session session = hibernateSessionFactory.getSession();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq(hibernateSessionFactory.getSessionFactory().getClassMetadata(User.class).getIdentifierPropertyName(), id))
                .uniqueResult();

        return user;
    }

    @Override
    public boolean updateUser(User user) {
        Session session = hibernateSessionFactory.getSession();
        session.update(user);
        return true;
    }

    @Override
    public boolean deleteUser(User user) {
        Session session = hibernateSessionFactory.getSession();
        session.delete(user);
        return true;
    }
}
