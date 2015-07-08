/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class SessionEntryConsts implements BaseColumns {
	
	public static final String COL_SESSION_BEGINE_DATE = "_session_begin_date";
	
	public static final String COL_SESSION_COUNTRY_ID = "_session_country_id";
	
	public static final String COL_SESSION_END_DATE = "_session_end_date";
	
	public static final String COL_SESSION_FINISH = "_session_finish";
	
	public static final String COL_SESSION_NUM_LASTEST_CORRECT = "_session_num_lastest_correct";
	
	public static final String COL_SESSION_SPEND_TIME = "_session_spend_time";
	
	public static final String COL_SESSION_TOKEN = "_session_token";
	
	public static final String COL_SESSION_TOTAL_SCORE = "_session_total_score";
	
	public static final String COL_SESSION_USER_ID = "_session_user_id";	
	
	public static final String NAME_OF_TABLE = "session";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ SessionEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_BEGINE_DATE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_COUNTRY_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_END_DATE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_FINISH + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_NUM_LASTEST_CORRECT + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_TOTAL_SCORE + CommonConsts.NUMERIC_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_TOKEN + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_SPEND_TIME + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionEntryConsts.COL_SESSION_USER_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ CommonConsts.FOREIGN_KEY + "(" + SessionEntryConsts.COL_SESSION_COUNTRY_ID + ")"
			+ CommonConsts.REFERENCES + CountryEntryConsts.NAME_OF_TABLE + "(" + CountryEntryConsts._ID + "))";
	
	public SessionEntryConsts(){}	
}
