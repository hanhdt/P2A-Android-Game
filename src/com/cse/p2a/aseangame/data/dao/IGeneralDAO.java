/**
 * This interface describe common methods that interact to database.
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hanh D. TRAN
 */
public interface IGeneralDAO {

    /**
     * @param tableName
     * @param whereClause
     * @param whereArgs
     *
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise, and -1 for throwing any errors. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    public int deleteRecordInDB(String tableName, String whereClause,
                                String[] whereArgs);

    /**
     * @param tableName
     * @param nullColumnHack
     * @param initialValues
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertRecordsInDB(String tableName, String nullColumnHack,
                                  ContentValues initialValues);

    /**
     * @param query
     * @param selectionArgs
     *
     * @return A Cursor object, which is positioned before the first entry. Note
     * that Cursors are not synchronized, see the documentation for more
     * details.
     */
    public Cursor selectRecordsFromDB(String query, String[] selectionArgs);

    /**
     * @param tableName
     * @param tableColumns
     * @param whereClase
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param orderBy
     *
     * @return A Cursor object, which is positioned before the first entry. Note
     * that Cursors are not synchronized, see the documentation for more
     * details.
     */
    public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
                                      String whereClase, String whereArgs[], String groupBy,
                                      String having, String orderBy);

    /**
     * @param query
     * @param selectionArgs
     *
     * @return A result list, which is rows in the database.
     */
    public ArrayList<ArrayList<String>> selectRecordsFromDBList(String query,
                                                                String[] selectionArgs);

    /**
     * @param tableName
     * @param tableColumns
     * @param whereClase
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param orderBy
     *
     * @return
     */
    public ArrayList<ArrayList<String>> selectRecordsFromDBList(
            String tableName, String[] tableColumns, String whereClase,
            String whereArgs[], String groupBy, String having, String orderBy);

    /**
     * @param tableName
     * @param initialValues
     * @param whereClause
     * @param whereArgs
     *
     * @return
     */
    public boolean updateRecordInDB(String tableName,
                                    ContentValues initialValues, String whereClause, String whereArgs[]);

    /**
     * @param tableName
     * @param initialValues
     * @param whereClause
     * @param whereArgs
     *
     * @return the number of rows affected
     */
    public int updateRecordsInDB(String tableName, ContentValues initialValues,
                                 String whereClause, String whereArgs[]);

    public List<String> getAllEntries();

}
