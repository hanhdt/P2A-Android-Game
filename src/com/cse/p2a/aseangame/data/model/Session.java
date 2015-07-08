/**
 *
 */
package com.cse.p2a.aseangame.data.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author DHanh
 */
public class Session {
    public static final String LOG_TAG = Country.class.getCanonicalName();
    private int _session_id;
    private int _session_country_id;
    private int _session_user_id;
    private String _session_token;
    private long _session_begin_date;
    private long _session_end_date;
    private long _session_spend_time;
    private int _session_finish;
    private long _session_total_score;
    private int _session_num_lastest_correct;

    public Session() {
    }

    public Session(int _session_id, long _session_begin_date,
                   int _session_country_id, long _session_end_date,
                   int _session_finish, int _session_num_lastest_correct,
                   long _session_spend_time, String _session_token,
                   long _session_total_score, int _session_user_id) {
        super();
        this._session_id = _session_id;
        this._session_begin_date = _session_begin_date;
        this._session_country_id = _session_country_id;
        this._session_end_date = _session_end_date;
        this._session_finish = _session_finish;
        this._session_num_lastest_correct = _session_num_lastest_correct;
        this._session_spend_time = _session_spend_time;
        this._session_token = _session_token;
        this._session_total_score = _session_total_score;
        this._session_user_id = _session_user_id;
    }

    public static List<Session> loadSessionList(ArrayList<ArrayList<String>> records) {
        List<Session> sessions = new ArrayList<Session>();
        Iterator<ArrayList<String>> itRecords = records.iterator();
        while (itRecords.hasNext()) {
            ArrayList<String> row = itRecords.next();
            Session session = new Session(Integer.parseInt(row.get(0)),
                    Long.parseLong(row.get(1)),
                    Integer.parseInt(row.get(2)),
                    Long.parseLong(row.get(3)),
                    Integer.parseInt(row.get(4)),
                    Integer.parseInt(row.get(5)),
                    Long.parseLong(row.get(6)),
                    row.get(7),
                    Long.parseLong(row.get(8)),
                    Integer.parseInt(row.get(9)));
            sessions.add(session);
        }
        return sessions;
    }

    public int get_session_id() {
        return _session_id;
    }

    public void set_session_id(int _session_id) {
        this._session_id = _session_id;
    }

    public int get_session_country_id() {
        return _session_country_id;
    }

    public void set_session_country_id(int _session_country_id) {
        this._session_country_id = _session_country_id;
    }

    public int get_session_user_id() {
        return _session_user_id;
    }

    public void set_session_user_id(int _session_user_id) {
        this._session_user_id = _session_user_id;
    }

    public String get_session_token() {
        return _session_token;
    }

    public void set_session_token(String _session_token) {
        this._session_token = _session_token;
    }

    public long get_session_begin_date() {
        return _session_begin_date;
    }

    public void set_session_begin_date(long _session_begin_date) {
        this._session_begin_date = _session_begin_date;
    }

    public long get_session_end_date() {
        return _session_end_date;
    }

    public void set_session_end_date(long _session_end_date) {
        this._session_end_date = _session_end_date;
    }

    public long get_session_spend_time() {
        return _session_spend_time;
    }

    public void set_session_spend_time(long _session_spend_time) {
        this._session_spend_time = _session_spend_time;
    }

    public int get_session_finish() {
        return _session_finish;
    }

    public void set_session_finish(int _session_finish) {
        this._session_finish = _session_finish;
    }

    public long get_session_total_score() {
        return _session_total_score;
    }

    public void set_session_total_score(long _session_total_score) {
        this._session_total_score = _session_total_score;
    }

    public int get_session_num_lastest_correct() {
        return _session_num_lastest_correct;
    }

    public void set_session_num_lastest_correct(int _session_num_lastest_correct) {
        this._session_num_lastest_correct = _session_num_lastest_correct;
    }

}
