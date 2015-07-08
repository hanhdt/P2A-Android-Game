/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.Session;

import java.util.List;
import java.util.Set;


/**
 * @author Vuong
 * @contributor Hanh D TRAN
 */
public interface ISessionDAO {

    /**
     * @param newSession
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    long insertSession(Session newSession);

    /**
     * Get all countries in the database
     *
     * @return country list
     */
    List<Session> getSessionList();

    /**
     * @param updatedSession
     * @return return affected row number
     */
    int updateSession(Session updatedSession);

    /**
     * @param userId
     * @return the non-finished session of the user
     */
    Session findSessionByUserId(int userId, int countryId);

    /**
     * @param CountryId
     * @return the list of five sessions that have highest scores of all be found by country.
     */
    List<Session> find5HighestScoreSessionOfCountry(int CountryId);

    /**
     * @param id
     * @return the finished session
     */
    Session findFinishedSessionById(int id);

    /**
     * @param userId
     * @return the total score of the user
     */
    float computeTotalScoreOfUser(int userId);

    /**
     * @param userId
     * @return list of country Id
     */
    List<Integer> findCountriesNonFinish(int userId);

    /**
     * @param userId
     * @return Set of country Id of user's finished session.
     */
    Set<Integer> getFinishedCountryOfUser(int userId);

    /**
     * @param userId
     * @param countryId
     * @return return true if user's session got highest score for current country.
     */
    boolean isHighestScoreOfThisCountry(int userId, int countryId, int sessionId);

    /**
     * Find total score through all sessions of user.
     *
     * @param userId
     * @return long the total score of the user
     */
    long findTotalScoreOfUser(int userId);
}
