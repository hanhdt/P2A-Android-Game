/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 * 
 */
public class GroupEntryConsts implements BaseColumns {

	public static final String COL_GROUP_COUNTRY_ID = "_group_country_id";

	public static final String COL_GROUP_CATEGORY_ID = "_group_category_id";

	public static final String COL_GROUP_WEIGHT = "_group_weight";

	public static final String COL_GROUP_NAME = "_group_name";

	public static final String LOG_TAG = GroupEntryConsts.class
			.getCanonicalName();

	public static final String NAME_OF_TABLE = "group_country_category";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE "
			+ NAME_OF_TABLE + " (" + GroupEntryConsts._ID
			+ CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT
			+ CommonConsts.COMMA_SEP + GroupEntryConsts.COL_GROUP_NAME
			+ CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ GroupEntryConsts.COL_GROUP_WEIGHT + CommonConsts.INTEGER_TYPE
			+ CommonConsts.COMMA_SEP + GroupEntryConsts.COL_GROUP_COUNTRY_ID
			+ CommonConsts.INTEGER_TYPE + CommonConsts.COMMA_SEP
			+ GroupEntryConsts.COL_GROUP_CATEGORY_ID
			+ CommonConsts.INTEGER_TYPE + ")";

	public GroupEntryConsts() {
	}
}
