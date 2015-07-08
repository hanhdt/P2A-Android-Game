/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class AnswerEntryConsts implements BaseColumns {
	
	public static final String COL_ANSWER_CONTENT = "_answer_content";
	
	public static final String COL_ANSWER_QUESTION_ID = "_answer_question_id";
	
	public static final String COL_ANSWER_CORRECT = "_answer_is_correct";
	
	public static final String NAME_OF_TABLE = "answer";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ AnswerEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP		
			+ AnswerEntryConsts.COL_ANSWER_CONTENT + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ AnswerEntryConsts.COL_ANSWER_QUESTION_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ AnswerEntryConsts.COL_ANSWER_CORRECT + CommonConsts.INTEGER_TYPE + ")";
	
	
	public AnswerEntryConsts(){}
}
