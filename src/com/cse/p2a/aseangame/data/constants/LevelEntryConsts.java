/**
 * 
 */
package com.cse.p2a.aseangame.data.constants;

import android.provider.BaseColumns;

/**
 * @author Hanh D. TRAN
 * 
 */
public class LevelEntryConsts implements BaseColumns {

	public static final String NAME_OF_TABLE = "level";

	public static final String COL_LEVEL_NAME = "_level_name";

	public static final String COL_SCORE_1 = "_level_score_1";

	public static final String COL_SCORE_2 = "_level_score_2";

	public static final String COL_SCORE_3 = "_level_score_3";

	public static final String COL_SCORE_4 = "_level_score_4";

	public static final String LOG_TAG = LevelEntryConsts.class
			.getCanonicalName();

	public static final String SQL_CREATE_TABLE = "CREATE TABLE "
			+ NAME_OF_TABLE + " (" + LevelEntryConsts._ID
			+ CommonConsts.INTEGER_TYPE + CommonConsts.PRIMARY_KEY + CommonConsts.AUTOINCREMENT
			+ CommonConsts.COMMA_SEP + LevelEntryConsts.COL_LEVEL_NAME
			+ CommonConsts.TEXT_TYPE + CommonConsts.COMMA_SEP
			+ LevelEntryConsts.COL_SCORE_1 + CommonConsts.NUMERIC_TYPE
			+ CommonConsts.COMMA_SEP + LevelEntryConsts.COL_SCORE_2
			+ CommonConsts.NUMERIC_TYPE + CommonConsts.COMMA_SEP
			+ LevelEntryConsts.COL_SCORE_3 + CommonConsts.NUMERIC_TYPE
			+ CommonConsts.COMMA_SEP + LevelEntryConsts.COL_SCORE_4
			+ CommonConsts.NUMERIC_TYPE + ")";

	public LevelEntryConsts() {
	}
}
