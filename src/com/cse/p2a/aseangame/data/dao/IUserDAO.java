/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.User;

/**
 * @author HanhDTRAN
 */
public interface IUserDAO {

    long insertUser(User newUser);

    long updateUser(User updatedUser);

    boolean isDuplicatedUser(User mUser);

    User getAnonymous();

    User findP2AUser(int userId);

    User findUserLogged(String username, String password);
}
