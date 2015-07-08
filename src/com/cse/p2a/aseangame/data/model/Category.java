/**
 * 
 */
package com.cse.p2a.aseangame.data.model;

/**
 * @author DHanh
 *
 */
public class Category {
	
	public static final String LOG_TAG = Country.class.getCanonicalName();
	
	private int _category_id;
	
	private String _category_name;
	
	public Category() {}

	public Category(int _category_id, String _category_name) {
		super();
		this._category_id = _category_id;
		this._category_name = _category_name;
	}

	public int get_category_id() {
		return _category_id;
	}

	public void set_category_id(int _category_id) {
		this._category_id = _category_id;
	}

	public String get_category_name() {
		return _category_name;
	}

	public void set_category_name(String _category_name) {
		this._category_name = _category_name;
	}
	
	
}
