package com.harrywu.springweb.dao;

import com.harrywu.springweb.model.User;
import org.springframework.stereotype.Repository;

/**
 * Created by sadupa on 8/18/14.
 */
public interface UserDao {

    boolean addUser(User user);

    User getUser(long id);

    boolean updateUser(User user);

    boolean deleteUser(User user);


}
