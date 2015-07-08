/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class QuestionEntryConsts implements BaseColumns{
	
	public static final String COL_QUESTION_GROUP_ID = "_question_group_id";
	
	public static final String COL_QUESTION_CONTENT = "_question_content";
	
	public static final String COL_QUESTION_LEVEL_ID = "_question_level_id";
	
	public static final String NAME_OF_TABLE = "question";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ QuestionEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY 
			+ CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ QuestionEntryConsts.COL_QUESTION_GROUP_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ QuestionEntryConsts.COL_QUESTION_CONTENT + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ QuestionEntryConsts.COL_QUESTION_LEVEL_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + ")";
	
	public QuestionEntryConsts(){}
			
}
