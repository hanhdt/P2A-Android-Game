/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class CategoryEntryConsts implements BaseColumns {
	
	static final String COL_CATEGORY_NAME = "_category_name";
	
	public static final String LOG_TAG = CategoryEntryConsts.class.getCanonicalName();
	
	public static final String NAME_OF_TABLE = "category";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ CategoryEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ CategoryEntryConsts.COL_CATEGORY_NAME + CommonConsts.TEXT_TYPE + ")";
	
	public CategoryEntryConsts() {}
	
}
