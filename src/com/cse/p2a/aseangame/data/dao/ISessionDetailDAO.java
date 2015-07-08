/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.SessionDetail;

import java.util.List;

/**
 * @author Vuong
 */
public interface ISessionDetailDAO {

    /**
     * @param newSessionDetail
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    long insertSessionDetail(SessionDetail newSessionDetail);

    /**
     * Get all countries in the database
     *
     * @return country list
     */
    List<SessionDetail> getSessionDetailList();

    int updateSessionDetail(SessionDetail updatedSessionDetail);

    int deleteSessionDetail(int session_id);
}
