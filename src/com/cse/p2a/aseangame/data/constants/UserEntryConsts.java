/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 *
 */
public class UserEntryConsts implements BaseColumns{

	public static final String COL_USER_ACTIVE = "_user_active";
	
	public static final String COL_USER_BIRTHDAY = "_user_birthday";
	
	public static final String COL_USER_COUNTRY_NAME = "_user_country_name";
	
	public static final String COL_USER_CREATED_DATE = "_user_created_date";
	
	public static final String COL_USER_EMAIL = "_user_email";
	
	public static final String COL_USER_FIRST_NAME = "_user_first_name";
	
	public static final String COL_USER_GROUP_ID = "_user_group_id";
	
	public static final String COL_USER_IMG_URL = "_user_img_url";
	
	public static final String COL_USER_INSTITUTE_NAME = "_user_institute_name";
	
	public static final String COL_USER_LAST_NAME = "_user_last_name";
	
	public static final String COL_USER_MIDDLE_NAME = "_user_middle_name";
	
	public static final String COL_USER_NAME = "_user_name";
	
	public static final String COL_USER_PASSWORD = "_user_password";
	
	public static final String COL_USER_POINT = "_user_point";
	
	public static final String COL_USER_TITLE = "_user_title";
	
	public static final String COL_USER_TOKEN = "_user_token";
	
	public static final String COL_USER_TYPE = "_user_type";
	
	public static final String NAME_OF_TABLE = "user";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_OF_TABLE + " ("
			+ UserEntryConsts._ID + CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_ACTIVE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_BIRTHDAY + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_COUNTRY_NAME + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_CREATED_DATE + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_EMAIL + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_FIRST_NAME + CommonConsts.TEXT_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_IMG_URL + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_INSTITUTE_NAME + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_GROUP_ID + CommonConsts.INTEGER_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_LAST_NAME + CommonConsts.TEXT_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_MIDDLE_NAME + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_NAME + CommonConsts.TEXT_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_PASSWORD + CommonConsts.TEXT_TYPE + CommonConsts.NOT_NULL + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_POINT + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_TITLE + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_TOKEN + CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ UserEntryConsts.COL_USER_TYPE + CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ CommonConsts.FOREIGN_KEY + "(" + UserEntryConsts.COL_USER_GROUP_ID + ")"
			+ CommonConsts.REFERENCES + GroupEntryConsts.NAME_OF_TABLE + "(" + GroupEntryConsts._ID + "))";
			
	public UserEntryConsts(){}
			
			
}
