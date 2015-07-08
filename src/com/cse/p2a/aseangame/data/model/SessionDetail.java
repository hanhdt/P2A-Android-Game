/**
 * 
 */
package com.cse.p2a.aseangame.data.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author DHanh
 * 
 */
public class SessionDetail {
	private int _session_detail_id;
	private int _session_detail_answer_id_1;
	private int _session_detail_answer_id_2;
	private int _session_detail_answer_id_3;
	private int _session_detail_answer_id_4;
	private long _session_detail_create_date;
	private int _session_detail_order_number;
	private int _session_detail_question_id;
	private float _session_detail_score;
	private int _session_detail_session_id;

	public SessionDetail() {
	}

	public SessionDetail(int _session_detail_id,
			int _session_detail_answer_id_1, int _session_detail_answer_id_2,
			int _session_detail_answer_id_3, int _session_detail_answer_id_4,
			long _session_detail_create_date, int _session_detail_order_number,
			int _session_detail_question_id, float _session_detail_score,
			int _session_detail_session_id) {
		super();
		this._session_detail_answer_id_1 = _session_detail_answer_id_1;
		this._session_detail_answer_id_2 = _session_detail_answer_id_2;
		this._session_detail_answer_id_3 = _session_detail_answer_id_3;
		this._session_detail_answer_id_4 = _session_detail_answer_id_4;
		this._session_detail_create_date = _session_detail_create_date;
		this._session_detail_id = _session_detail_id;
		this._session_detail_order_number = _session_detail_order_number;
		this._session_detail_question_id = _session_detail_question_id;
		this._session_detail_score = _session_detail_score;
		this._session_detail_session_id = _session_detail_session_id;
	}

	public int get_session_detail_answer_id_1() {
		return _session_detail_answer_id_1;
	}

	public int get_session_detail_answer_id_2() {
		return _session_detail_answer_id_2;
	}

	public int get_session_detail_answer_id_3() {
		return _session_detail_answer_id_3;
	}

	public int get_session_detail_answer_id_4() {
		return _session_detail_answer_id_4;
	}

	public long get_session_detail_create_date() {
		return _session_detail_create_date;
	}

	public int get_session_detail_id() {
		return _session_detail_id;
	}

	public int get_session_detail_order_number() {
		return _session_detail_order_number;
	}

	public int get_session_detail_question_id() {
		return _session_detail_question_id;
	}

	public float get_session_detail_score() {
		return _session_detail_score;
	}

	public int get_session_detail_session_id() {
		return _session_detail_session_id;
	}

	public void set_session_detail_answer_id_1(int _session_detail_answer_id_1) {
		this._session_detail_answer_id_1 = _session_detail_answer_id_1;
	}

	public void set_session_detail_answer_id_2(int _session_detail_answer_id_2) {
		this._session_detail_answer_id_2 = _session_detail_answer_id_2;
	}

	public void set_session_detail_answer_id_3(int _session_detail_answer_id_3) {
		this._session_detail_answer_id_3 = _session_detail_answer_id_3;
	}

	public void set_session_detail_answer_id_4(int _session_detail_answer_id_4) {
		this._session_detail_answer_id_4 = _session_detail_answer_id_4;
	}

	public void set_session_detail_create_date(long _session_detail_create_date) {
		this._session_detail_create_date = _session_detail_create_date;
	}

	public void set_session_detail_id(int _session_detail_id) {
		this._session_detail_id = _session_detail_id;
	}

	public void set_session_detail_order_number(int _session_detail_order_number) {
		this._session_detail_order_number = _session_detail_order_number;
	}

	public void set_session_detail_question_id(int _session_detail_question_id) {
		this._session_detail_question_id = _session_detail_question_id;
	}

	public void set_session_detail_score(float _session_detail_score) {
		this._session_detail_score = _session_detail_score;
	}

	public void set_session_detail_session_id(int _session_detail_session_id) {
		this._session_detail_session_id = _session_detail_session_id;
	}

	public static List<SessionDetail> loadSessionDetailList(
			ArrayList<ArrayList<String>> records) {
		List<SessionDetail> sesstionDetails = new ArrayList<SessionDetail>();
		Iterator<ArrayList<String>> itRecords = records.iterator();
		while (itRecords.hasNext()) {
			ArrayList<String> row = itRecords.next();
			SessionDetail sessionDetail = new SessionDetail(
					Integer.parseInt(row.get(0)), Integer.parseInt(row.get(1)),
					Integer.parseInt(row.get(2)), Integer.parseInt(row.get(3)),
					Integer.parseInt(row.get(4)), Long.parseLong(row.get(5)),
					Integer.parseInt(row.get(6)), Integer.parseInt(row.get(7)),
					Float.parseFloat(row.get(8)), Integer.parseInt(row.get(9)));
			sesstionDetails.add(sessionDetail);
		}
		return sesstionDetails;
	}
}
