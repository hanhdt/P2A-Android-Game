package com.cse.p2a.aseangame.data.model;

public class Level {
	private int _level_id;
	private String _level_name;
	private float _attempt1_score;
	private float _attempt2_score;
	private float _attempt3_score;
	private float _attempt4_score;
	private String _description;

	public int get_level_id() {
		return _level_id;
	}

	public void set_level_id(int _level_id) {
		this._level_id = _level_id;
	}

	public String get_level_name() {
		return _level_name;
	}

	public void set_level_name(String _level_name) {
		this._level_name = _level_name;
	}

	public float get_attempt1_score() {
		return _attempt1_score;
	}

	public void set_attempt1_score(float _attempt1_score) {
		this._attempt1_score = _attempt1_score;
	}

	public float get_attempt2_score() {
		return _attempt2_score;
	}

	public void set_attempt2_score(float _attempt2_score) {
		this._attempt2_score = _attempt2_score;
	}

	public float get_attempt3_score() {
		return _attempt3_score;
	}

	public void set_attempt3_score(float _attempt3_score) {
		this._attempt3_score = _attempt3_score;
	}

	public float get_attempt4_score() {
		return _attempt4_score;
	}

	public void set_attempt4_score(float _attempt4_score) {
		this._attempt4_score = _attempt4_score;
	}

	public String get_description() {
		return _description;
	}

	public void set_description(String _description) {
		this._description = _description;
	}

}
