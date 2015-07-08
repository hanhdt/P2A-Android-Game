/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class SessionDetailEntryConsts implements BaseColumns {

	public static final String COL_SESSION_DETAIL_ANSWER_ID1 = "_session_detail_answer_id1";
	
	public static final String COL_SESSION_DETAIL_ANSWER_ID2 = "_session_detail_answer_id2";
	
	public static final String COL_SESSION_DETAIL_ANSWER_ID3 = "_session_detail_answer_id3";
	
	public static final String COL_SESSION_DETAIL_ANSWER_ID4 = "_session_detail_answer_id4";
	
	public static final String COL_SESSION_DETAIL_CREATE_DATE = "_session_detail_create_date";
	
	public static final String COL_SESSION_DETAIL_ORDER_NUMBER = "_session_detail_order_number";
	
	public static final String COL_SESSION_DETAIL_QUESTION_ID = "_session_detail_question_id";
	
	public static final String COL_SESSION_DETAIL_SCORE = "_session_detail_score";
	
	public static final String COL_SESSION_DETAIL_SESSION_ID = "_session_detail_session_id";
	
	public static final String NAME_OF_TABLE = "session_detail";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ SessionDetailEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY 
			+ CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID1 + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID2 + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID3 + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_ANSWER_ID4 + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_CREATE_DATE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_ORDER_NUMBER + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_SCORE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_QUESTION_ID + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ CommonConsts.FOREIGN_KEY + "(" + SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID + ")"
			+ CommonConsts.REFERENCES + SessionEntryConsts.NAME_OF_TABLE + " (" + SessionEntryConsts._ID + ")" + CommonConsts.COMMA_SEP
			+ CommonConsts.FOREIGN_KEY + "(" + SessionDetailEntryConsts.COL_SESSION_DETAIL_QUESTION_ID + ")"
			+ CommonConsts.REFERENCES + QuestionEntryConsts.NAME_OF_TABLE + " (" + QuestionEntryConsts._ID + "))";
	public SessionDetailEntryConsts(){}
	
}
