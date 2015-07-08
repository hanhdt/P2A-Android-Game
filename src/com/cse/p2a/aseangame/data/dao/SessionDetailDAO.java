/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.CommonConsts;
import com.cse.p2a.aseangame.data.constants.SessionDetailEntryConsts;
import com.cse.p2a.aseangame.data.model.SessionDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vuong
 */
public class SessionDetailDAO implements ISessionDetailDAO {

    private static SessionDetailDAO instance = null;
    protected final P2AGameDbHelper p2aDb;

    public SessionDetailDAO() {
        p2aDb = P2AGameDbHelper.getInstance(P2AContext.getContext());
    }

    public static SessionDetailDAO getInstance() {
        if (instance == null) {
            instance = new SessionDetailDAO();
        }

        return instance;
    }

    @Override
    public List<SessionDetail> getSessionDetailList() {
        List<SessionDetail> sessions = new ArrayList<SessionDetail>();
        try {
            p2aDb.openDataBase();

            ArrayList<ArrayList<String>> records = p2aDb.selectRecordsFromDBList(CommonConsts.getSqlSelectAllRecordInTable(SessionDetailEntryConsts.NAME_OF_TABLE),
                    null);

            sessions = SessionDetail.loadSessionDetailList(records);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return sessions;
    }

    @Override
    public long insertSessionDetail(SessionDetail newSessionDetail) {
        long n = 0;
        final ContentValues values = new ContentValues();
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID1, newSessionDetail.get_session_detail_answer_id_1());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID2, newSessionDetail.get_session_detail_answer_id_2());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID3, newSessionDetail.get_session_detail_answer_id_3());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID4, newSessionDetail.get_session_detail_answer_id_4());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_CREATE_DATE, newSessionDetail.get_session_detail_create_date());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ORDER_NUMBER, newSessionDetail.get_session_detail_order_number());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_QUESTION_ID, newSessionDetail.get_session_detail_question_id());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_SCORE, newSessionDetail.get_session_detail_score());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID, newSessionDetail.get_session_detail_session_id());

        try {
            p2aDb.openDataBase();
            n = p2aDb.insertRecordsInDB(SessionDetailEntryConsts.NAME_OF_TABLE, null, values);

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return n;
    }

    @Override
    public int updateSessionDetail(SessionDetail updatedSessionDetail) {
        int affected = 0;
        final ContentValues values = new ContentValues();
        values.put(SessionDetailEntryConsts._ID, updatedSessionDetail.get_session_detail_id());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID1, updatedSessionDetail.get_session_detail_answer_id_1());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID2, updatedSessionDetail.get_session_detail_answer_id_2());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID3, updatedSessionDetail.get_session_detail_answer_id_3());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID4, updatedSessionDetail.get_session_detail_answer_id_4());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_CREATE_DATE, updatedSessionDetail.get_session_detail_create_date());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_ORDER_NUMBER, updatedSessionDetail.get_session_detail_order_number());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_QUESTION_ID, updatedSessionDetail.get_session_detail_question_id());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_SCORE, updatedSessionDetail.get_session_detail_score());
        values.put(SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID, updatedSessionDetail.get_session_detail_session_id());
//        Log.d(getClass().getCanonicalName(), "Performing update session: " + updatedSessionDetail.get_session_detail_id() + ";");
        try {
            p2aDb.openDataBase();
            affected = p2aDb.updateRecordsInDB(SessionDetailEntryConsts.NAME_OF_TABLE, values, SessionDetailEntryConsts._ID + "=?",
                    new String[]{values.get(SessionDetailEntryConsts._ID).toString()});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return affected;
    }

    @Override
    public int deleteSessionDetail(int session_id) {
        try {
            p2aDb.openDataBase();
            return p2aDb.deleteRecordInDB(SessionDetailEntryConsts.NAME_OF_TABLE, SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID + "=?", new String[]{session_id + ""});
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return -1;
    }


}
