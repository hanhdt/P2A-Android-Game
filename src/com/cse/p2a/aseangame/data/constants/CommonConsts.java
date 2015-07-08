/**
 * This class provides common all data types, key words, and SQL query. 
 */
package com.cse.p2a.aseangame.data.constants;


/**
 * @author Hanh D. TRAN
 *
 */
public class CommonConsts {
	public static final String TEXT_TYPE = " TEXT ";
    public static final String INTEGER_TYPE = " INTEGER ";
    public static final String BLOB_TYPE = " BLOB ";
    public static final String NUMERIC_TYPE = " NUMERIC ";
    public static final String REAL_TYPE = " REAL ";
    public static final String COMMA_SEP = ",";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String FOREIGN_KEY = " FOREIGN KEY ";
    public static final String REFERENCES = " REFERENCES ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String UNIQUE = " UNIQUE ";
    public static final String AUTOINCREMENT = " AUTOINCREMENT ";
    
    public static final String COL_Z_ENT = "Z_ENT";
	
	public static final String COL_Z_OPT = "Z_OPT";
    
    public static String getSqlDeleteTable(String tableName){
    	return "DROP TABLE IF EXISTS " + tableName;
    }
    
    public static String getSqlSelectAllRecordInTable(final String tableName){
    	return "Select * FROM " + tableName;
    }
}
