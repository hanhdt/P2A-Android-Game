/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class CountryEntryConsts implements BaseColumns{
	
	public static final String COL_COUNTRY_NAME = "_country_name";
	
	public static final String LOG_TAG = CountryEntryConsts.class.getCanonicalName();
	
	public static final String NAME_OF_TABLE = "country";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ CountryEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ CountryEntryConsts.COL_COUNTRY_NAME + CommonConsts.TEXT_TYPE + ")";
	public CountryEntryConsts() {}
}
