/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.CommonConsts;
import com.cse.p2a.aseangame.data.constants.SessionEntryConsts;
import com.cse.p2a.aseangame.data.model.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vuong
 */
public class SessionDAO implements ISessionDAO {

    private static SessionDAO instance = null;
    protected final P2AGameDbHelper p2aDb;

    public SessionDAO() {
        p2aDb = P2AGameDbHelper.getInstance(P2AContext.getContext());
    }

    public static SessionDAO getInstance() {
        if (instance == null) {
            instance = new SessionDAO();
        }

        return instance;
    }

    /**
     * @see com.cse.p2a.aseangame.data.dao.ICountryDAO#getCountryList()
     */
    @Override
    public List<Session> getSessionList() {
        List<Session> sessions = new ArrayList<Session>();
        try {
            p2aDb.openDataBase();

            ArrayList<ArrayList<String>> records = p2aDb.selectRecordsFromDBList(CommonConsts.getSqlSelectAllRecordInTable(SessionEntryConsts.NAME_OF_TABLE),
                    null);

            sessions = Session.loadSessionList(records);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return sessions;
    }

    @Override
    public long insertSession(Session newSession) {
        long n = 0;
        final ContentValues values = new ContentValues();
        values.put(SessionEntryConsts.COL_SESSION_BEGINE_DATE, newSession.get_session_begin_date());
        values.put(SessionEntryConsts.COL_SESSION_COUNTRY_ID, newSession.get_session_country_id());
        values.put(SessionEntryConsts.COL_SESSION_END_DATE, newSession.get_session_end_date());
        values.put(SessionEntryConsts.COL_SESSION_FINISH, newSession.get_session_finish());
        values.put(SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT, newSession.get_session_num_lastest_correct());
        values.put(SessionEntryConsts.COL_SESSION_SPEND_TIME, newSession.get_session_spend_time());
        values.put(SessionEntryConsts.COL_SESSION_TOKEN, newSession.get_session_token());
        values.put(SessionEntryConsts.COL_SESSION_TOTAL_SCORE, newSession.get_session_total_score());
        values.put(SessionEntryConsts.COL_SESSION_USER_ID, newSession.get_session_user_id());


        try {
            p2aDb.openDataBase();
            n = p2aDb.insertRecordsInDB(SessionEntryConsts.NAME_OF_TABLE, null, values);

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return n;
    }

    @Override
    public int updateSession(Session updatedSession) {
        int affected = 0;
        final ContentValues values = new ContentValues();
        values.put(SessionEntryConsts._ID, updatedSession.get_session_id());
        values.put(SessionEntryConsts.COL_SESSION_BEGINE_DATE, updatedSession.get_session_begin_date());
        values.put(SessionEntryConsts.COL_SESSION_COUNTRY_ID, updatedSession.get_session_country_id());
        values.put(SessionEntryConsts.COL_SESSION_END_DATE, updatedSession.get_session_end_date());
        values.put(SessionEntryConsts.COL_SESSION_FINISH, updatedSession.get_session_finish());
        values.put(SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT, updatedSession.get_session_num_lastest_correct());
        values.put(SessionEntryConsts.COL_SESSION_SPEND_TIME, updatedSession.get_session_spend_time());
        values.put(SessionEntryConsts.COL_SESSION_TOKEN, updatedSession.get_session_token());
        values.put(SessionEntryConsts.COL_SESSION_TOTAL_SCORE, updatedSession.get_session_total_score());
        values.put(SessionEntryConsts.COL_SESSION_USER_ID, updatedSession.get_session_user_id());
        try {
            p2aDb.openDataBase();
            affected = p2aDb.updateRecordsInDB(SessionEntryConsts.NAME_OF_TABLE, values, SessionEntryConsts._ID + "=?",
                    new String[]{values.get(SessionEntryConsts._ID).toString()});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return affected;
    }

    /**
     * @param countryId
     * @return the list of five sessions that have highest scores of all be found by country.
     */
    @Override
    public List<Session> find5HighestScoreSessionOfCountry(int countryId) {
        List<Session> sessions = null;
        try {
            p2aDb.openDataBase();

            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_BEGINE_DATE,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_END_DATE,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT,
                    SessionEntryConsts.COL_SESSION_SPEND_TIME,
                    SessionEntryConsts.COL_SESSION_TOKEN,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_COUNTRY_ID + "=? AND " + SessionEntryConsts.COL_SESSION_FINISH + "=1",
                    new String[]{String.valueOf(countryId)}, null, null, SessionEntryConsts.COL_SESSION_TOTAL_SCORE + " DESC LIMIT 5");

            if (cursor != null && cursor.moveToFirst()) {
                sessions = new ArrayList<Session>();
                do {
                    Session session = new Session(cursor.getInt(0),
                            cursor.getLong(1),
                            cursor.getInt(2),
                            cursor.getLong(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getLong(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getInt(9));
                    sessions.add(session);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return sessions;
    }

    /**
     * @param id
     * @return the finished session
     */
    @Override
    public Session findFinishedSessionById(int id) {
        Session session = null;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_BEGINE_DATE,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_END_DATE,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT,
                    SessionEntryConsts.COL_SESSION_SPEND_TIME,
                    SessionEntryConsts.COL_SESSION_TOKEN,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts._ID + "=? AND " + SessionEntryConsts.COL_SESSION_FINISH + "=1",
                    new String[]{String.valueOf(id)}, null, null, SessionEntryConsts.COL_SESSION_TOTAL_SCORE + " DESC LIMIT 5");
            if (cur != null) {
                cur.moveToFirst();
                session = new Session();
                session.set_session_id(cur.getInt(0));
                session.set_session_begin_date(cur.getLong(1));
                session.set_session_country_id(cur.getInt(2));
                session.set_session_end_date(cur.getLong(3));
                session.set_session_finish(cur.getInt(4));
                session.set_session_num_lastest_correct(cur.getInt(5));
                session.set_session_spend_time(cur.getLong(6));
                session.set_session_token(cur.getString(7));
                session.set_session_total_score(cur.getInt(8));
                session.set_session_user_id(cur.getInt(9));
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
            Log.e(SessionDAO.class.getCanonicalName(), sqle.getMessage());
        } finally {
            p2aDb.close();
        }
        return session;
    }

    @Override
    public float computeTotalScoreOfUser(int userId) {
        float totalScore = 0;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_USER_ID + "=? AND " + SessionEntryConsts.COL_SESSION_FINISH + "=1",
                    new String[]{String.valueOf(userId)}, null, null, SessionEntryConsts.COL_SESSION_TOTAL_SCORE);
            if (cursor != null) {
                cursor.moveToLast();
                totalScore = cursor.getLong(3);
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
            Log.e(SessionDAO.class.getCanonicalName(), sqle.getMessage());
        } finally {
            p2aDb.close();
        }

        return totalScore;
    }

    /**
     * @param userId
     * @return list of country id
     */
    @Override
    public List<Integer> findCountriesNonFinish(int userId) {
        List<Integer> countriesID = null;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_FINISH + "=0 AND " + SessionEntryConsts.COL_SESSION_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                countriesID = new ArrayList<Integer>();
                do {
                    countriesID.add(cursor.getInt(1));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return countriesID;
    }

    /**
     * @param userId
     * @param countryId
     * @return return true if user got highest score for current country.
     */
    @Override
    public boolean isHighestScoreOfThisCountry(int userId, int countryId, int sessionId) {
        boolean flag = false;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_USER_ID,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE
            }, SessionEntryConsts.COL_SESSION_USER_ID + "=? AND " + SessionEntryConsts.COL_SESSION_COUNTRY_ID + "=? AND "
                    + SessionEntryConsts.COL_SESSION_FINISH + "=1",
                    new String[]{String.valueOf(userId), String.valueOf(countryId)}, null, null, SessionEntryConsts.COL_SESSION_TOTAL_SCORE + " DESC ");
            if (cursor != null && cursor.moveToFirst()) {
                if (cursor.getInt(0) == sessionId) {
                    flag = true;
                }
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return flag;
    }

    /**
     * @param userId
     * @return Set of country Id of user's finished session.
     */
    @Override
    public Set<Integer> getFinishedCountryOfUser(int userId) {
        Set<Integer> countrySet = null;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_USER_ID + "=? AND " + SessionEntryConsts.COL_SESSION_FINISH + "=1",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                countrySet = new HashSet<Integer>();
                do {
                    countrySet.add(cursor.getInt(1));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return countrySet;
    }

    /**
     * @param userId
     * @return the existed session of the user
     */
    @Override
    public Session findSessionByUserId(int userId, int countryId) {
        Session session = new Session();
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_BEGINE_DATE,
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID,
                    SessionEntryConsts.COL_SESSION_END_DATE,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT,
                    SessionEntryConsts.COL_SESSION_SPEND_TIME,
                    SessionEntryConsts.COL_SESSION_TOKEN,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_USER_ID + "=? AND " + SessionEntryConsts.COL_SESSION_FINISH + "=0 AND " +
                    SessionEntryConsts.COL_SESSION_COUNTRY_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(countryId)}, null, null, null);
            if (cursor.moveToFirst()) {
                session.set_session_id(cursor.getInt(0));
                session.set_session_begin_date(cursor.getLong(1));
                session.set_session_country_id(cursor.getInt(2));
                session.set_session_end_date(cursor.getLong(3));
                session.set_session_finish(cursor.getInt(4));
                session.set_session_num_lastest_correct(cursor.getInt(5));
                session.set_session_spend_time(cursor.getLong(6));
                session.set_session_token(cursor.getString(7));
                session.set_session_total_score(cursor.getInt(8));
                session.set_session_user_id(cursor.getInt(9));
            } else {
                session = null;
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return session;
    }

    /**
     * Find total score of all sessions of the user.
     *
     * @param userId
     * @return long the total score of the user
     */
    @Override
    public long findTotalScoreOfUser(int userId) {
        long totalScore = 0;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB(SessionEntryConsts.NAME_OF_TABLE, new String[]{
                    SessionEntryConsts._ID,
                    SessionEntryConsts.COL_SESSION_FINISH,
                    SessionEntryConsts.COL_SESSION_TOTAL_SCORE,
                    SessionEntryConsts.COL_SESSION_USER_ID
            }, SessionEntryConsts.COL_SESSION_FINISH + "=1 AND " + SessionEntryConsts.COL_SESSION_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    totalScore += cur.getLong(2);
                } while (cur.moveToNext());
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return totalScore;
    }
}
