/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class TopResultEntryConsts implements BaseColumns {

	static final String COL_TOPRESULT_BEGINE_DATE = "_topresult_begine_date";
	
	static final String COL_TOPRESULT_COUNTRY_ID = "_topresult_country_id";
	
	static final String COL_TOPRESULT_DEVICE = "_topresult_device";
	
	static final String COL_TOPRESULT_END_DATE = "_topresult_end_date";
	
	static final String COL_TOPRESULT_POINT = "_topresult_point";
	
	static final String COL_TOPRESULT_SPEND_TIME = "_toperesult_spend_time";
	
	public static final String NAME_OF_TABLE = "top_result";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ TopResultEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_BEGINE_DATE + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_COUNTRY_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_DEVICE + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_END_DATE + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_POINT + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ TopResultEntryConsts.COL_TOPRESULT_SPEND_TIME + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ CommonConsts.FOREIGN_KEY + "(" + TopResultEntryConsts.COL_TOPRESULT_COUNTRY_ID + ")"
			+ CommonConsts.REFERENCES + CountryEntryConsts.NAME_OF_TABLE + " (" + CountryEntryConsts._ID + "))";
	public TopResultEntryConsts(){}
}
