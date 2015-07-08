/**
 * 
 */
package com.cse.p2a.aseangame.data.model;

/**
 * @author DHanh
 *
 */
public class Group {
	private int _group_id;
	private String _group_name;
	private int _group_weight;
	private int _group_country_id;
	private int _group_category_id;
	
	public Group() {
	}

	public Group(int _group_id, String _group_name, int _group_weight,
			int _group_country_id, int _group_category_id) {
		super();
		this._group_id = _group_id;
		this._group_name = _group_name;
		this._group_weight = _group_weight;
		this._group_country_id = _group_country_id;
		this._group_category_id = _group_category_id;
	}

	public int get_group_id() {
		return _group_id;
	}

	public void set_group_id(int _group_id) {
		this._group_id = _group_id;
	}

	public String get_group_name() {
		return _group_name;
	}

	public void set_group_name(String _group_name) {
		this._group_name = _group_name;
	}

	public int get_group_weight() {
		return _group_weight;
	}

	public void set_group_weight(int _group_weight) {
		this._group_weight = _group_weight;
	}

	public int get_group_country_id() {
		return _group_country_id;
	}

	public void set_group_country_id(int _group_country_id) {
		this._group_country_id = _group_country_id;
	}

	public int get_group_category_id() {
		return _group_category_id;
	}

	public void set_group_category_id(int _group_category_id) {
		this._group_category_id = _group_category_id;
	}
}
