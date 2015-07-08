/**
 * This class describes what is manipulate activities with SQLite database.
 * Also, it will be implemented general methods for interacting database.
 */
package com.cse.p2a.aseangame.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.constants.AnswerEntryConsts;
import com.cse.p2a.aseangame.data.constants.CategoryEntryConsts;
import com.cse.p2a.aseangame.data.constants.CommonConsts;
import com.cse.p2a.aseangame.data.constants.CountryEntryConsts;
import com.cse.p2a.aseangame.data.constants.GroupEntryConsts;
import com.cse.p2a.aseangame.data.constants.LevelEntryConsts;
import com.cse.p2a.aseangame.data.constants.QuestionEntryConsts;
import com.cse.p2a.aseangame.data.constants.SessionDetailEntryConsts;
import com.cse.p2a.aseangame.data.constants.SessionEntryConsts;
import com.cse.p2a.aseangame.data.constants.TopResultEntryConsts;
import com.cse.p2a.aseangame.data.constants.UserEntryConsts;
import com.cse.p2a.aseangame.data.dao.IGeneralDAO;
import com.cse.p2a.aseangame.data.model.Answer;
import com.cse.p2a.aseangame.data.model.Question;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hanh D. TRAN
 */
public class P2AGameDbHelper extends SQLiteOpenHelper implements IGeneralDAO {

    public static final String DATABASE_NAME = "agame.db";
    public static final int DATABASE_VERSION = 1;
    public static final String LOG_TAG = P2AGameDbHelper.class.getCanonicalName();
    @SuppressLint("SdCardPath")
    private static String DATABASE_PATH = "/data/data/"
            + P2AContext.getContext().getPackageName() + "/databases/";
    private static P2AGameDbHelper mInstance = null;
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    @SuppressLint("SdCardPath")
    public P2AGameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public static P2AGameDbHelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mInstance == null) {

            mInstance = new P2AGameDbHelper(ctx.getApplicationContext());
            try {

                mInstance.createDataBase();

            } catch (IOException ioe) {

                throw new Error("Unable to create database");

            }

            try {
                mInstance.openDataBase();

            } catch (SQLException sqle) {

                throw sqle;

            } finally {
                mInstance.close();
            }

        }

        return mInstance;
    }

    /*
     * Solution that resolve for getting error sqlite3_open_v2("...', &handle,
     * 6, NULL) open failed There's another way to resolve this problem: File
     * database = getApplicationContext().getDatabasePath("databasename.db");
     */
    private boolean checkDatabaseFixed() {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Database doesn't exist");
        }
        return checkdb;
    }

    /*
     * Close database
     *
     * @see android.database.sqlite.SQLiteOpenHelper#close()
     */
    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    /*
     * We overwrite the current database with our database.
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[4094];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private void createDataBase() throws IOException {
        // boolean dbExist = checkDataBase();
        boolean dbExist = checkDatabaseFixed();
        if (dbExist) {
            // do nothing - database already exist
        } else {
            /*
             * By calling following method:
			 * 1. An empty database will be created
			 * into the default system path of your application. 
			 * 2. Then we overwrite that database with our database.
			 */
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * This method provide the way that allow to delete a record in the
     * database. And convenience method for deleting rows in the database.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#deleteRecordInDB(java.lang.String,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public int deleteRecordInDB(String tableName, String whereClause,
                                String[] whereArgs) {
        try {
            return myDataBase.delete(tableName, whereClause, whereArgs);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage(), e.fillInStackTrace());
        }
        return -1;
    }

    @Override
    public List<String> getAllEntries() {
        Cursor cur = selectRecordsFromDB("SELECT * FROM p2a.sqlite_master WHERE type = 'table'", null);
        List<String> table = new ArrayList<String>();
        while (cur.moveToNext()) {
            table.add(cur.getString(0));
        }
        return table;
    }

    /**
     * Convenience method for inserting a row into the database.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#insertRecordsInDB(java.lang.String,
     * java.lang.String, android.content.ContentValues)
     */
    @Override
    public long insertRecordsInDB(String tableName, String nullColumnHack,
                                  ContentValues initialValues) {
        long n = -1;
        try {
            myDataBase.beginTransaction();
            n = myDataBase.insert(tableName, nullColumnHack, initialValues);
            myDataBase.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage(), e.fillInStackTrace());
        } finally {
            myDataBase.endTransaction();
        }
        return n;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AnswerEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(CategoryEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(CountryEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(GroupEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(LevelEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(QuestionEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(SessionEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(SessionDetailEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(TopResultEntryConsts.SQL_CREATE_TABLE);
        db.execSQL(UserEntryConsts.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CommonConsts.getSqlDeleteTable(AnswerEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(CategoryEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(CountryEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(GroupEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(LevelEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(QuestionEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(SessionEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(SessionDetailEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(TopResultEntryConsts.NAME_OF_TABLE));
        db.execSQL(CommonConsts.getSqlDeleteTable(UserEntryConsts.NAME_OF_TABLE));
        onCreate(db);
    }

    public void dropTablesToGetNewQuestions() {
        try {
            openDataBase();
            myDataBase.execSQL(CommonConsts.getSqlDeleteTable(QuestionEntryConsts.NAME_OF_TABLE));
            myDataBase.execSQL(CommonConsts.getSqlDeleteTable(AnswerEntryConsts.NAME_OF_TABLE));
            //myDataBase.execSQL(CommonConsts.getSqlDeleteTable(SessionEntryConsts.NAME_OF_TABLE));
            myDataBase.execSQL(CommonConsts.getSqlDeleteTable(SessionDetailEntryConsts.NAME_OF_TABLE));
            myDataBase.delete(SessionEntryConsts.NAME_OF_TABLE, SessionEntryConsts.COL_SESSION_FINISH + " = ?", new String[]{"0"});
            myDataBase.execSQL(QuestionEntryConsts.SQL_CREATE_TABLE);
            myDataBase.execSQL(AnswerEntryConsts.SQL_CREATE_TABLE);
            //myDataBase.execSQL(SessionEntryConsts.SQL_CREATE_TABLE);
            myDataBase.execSQL(SessionDetailEntryConsts.SQL_CREATE_TABLE);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            myDataBase.close();
        }
    }

    /**
     * Open the database
     *
     * @throws SQLException
     */
    public void openDataBase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        try {
            myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Runs the provided SQL and returns a Cursor over the result set.
     */
    @Override
    public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {

        return myDataBase.rawQuery(query, selectionArgs);
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#selectRecordsFromDB(java.lang.String,
     * java.lang.String[], java.lang.String, java.lang.String[],
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
                                      String whereClause, String[] whereArgs, String groupBy,
                                      String having, String orderBy) {

        return myDataBase.query(tableName, tableColumns, whereClause,
                whereArgs, groupBy, having, orderBy);
    }

    /**
     * Getting values of the record through sequence fields put in ArrayList.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#selectRecordsFromDBList(java.lang.String,
     * java.lang.String[])
     */
    @Override
    public ArrayList<ArrayList<String>> selectRecordsFromDBList(String query,
                                                                String[] selectionArgs) {

        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();

        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = myDataBase.rawQuery(query, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                list = new ArrayList<String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    list.add(cursor.getString(i));
                }
                retList.add(list);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return retList;
    }

    /**
     * Getting values of the record through sequence fields put in ArrayList.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#selectRecordsFromDBList(java.lang.String,
     * java.lang.String[], java.lang.String, java.lang.String[],
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ArrayList<ArrayList<String>> selectRecordsFromDBList(
            String tableName, String[] tableColumns, String whereClause,
            String[] whereArgs, String groupBy, String having, String orderBy) {
        ArrayList<ArrayList<String>> rowList = new ArrayList<ArrayList<String>>();
        ArrayList<String> row = new ArrayList<String>();

        Cursor cur = myDataBase.query(tableName, tableColumns, whereClause,
                whereArgs, groupBy, having, orderBy);
        if (cur != null) {
            try {
                cur.moveToFirst();
                do {
                    row = new ArrayList<String>();
                    for (int i = 0; i < cur.getColumnCount(); i++)
                        row.add(cur.getString(i));
                    rowList.add(row);
                } while (cur.moveToNext());

            } catch (SQLiteException e) {
                Log.e(LOG_TAG, e.getMessage(), e.fillInStackTrace());
            } finally {
                cur.close();
            }
        }
        return rowList;
    }

    /**
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#updateRecordInDB(java.lang.String,
     * android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean updateRecordInDB(String tableName,
                                    ContentValues initialValues, String whereClause, String[] whereArgs) {

        try {
            return myDataBase.update(tableName, initialValues, whereClause, whereArgs) > 0;

        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage(), e.fillInStackTrace());
        }
        return false;
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @see com.cse.p2a.aseangame.data.dao.IGeneralDAO#updateRecordsInDB(java.lang.String,
     * android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int updateRecordsInDB(String tableName, ContentValues initialValues,
                                 String whereClause, String[] whereArgs) {
        try {
            return myDataBase.update(tableName, initialValues, whereClause,
                    whereArgs);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage(), e.fillInStackTrace());
        }
        return -1;
    }

    public void insertMultipleAnswerOfQuestion(Question question) {
        try {
            myDataBase.beginTransaction();
            // insert question
            myDataBase.execSQL("INSERT INTO " + QuestionEntryConsts.NAME_OF_TABLE
                            + "(" + QuestionEntryConsts._ID + "," + QuestionEntryConsts.COL_QUESTION_CONTENT
                            + "," + QuestionEntryConsts.COL_QUESTION_GROUP_ID + ","
                            + QuestionEntryConsts.COL_QUESTION_LEVEL_ID + ") VALUES (?,?,?,?)",
                    new Object[]{question.get_question_id(), question.get_question_content(),
                            question.get_group_id(), question.get_level_id()}
            );
            // insert answers of question above.
            List<Answer> insertedAnswers = question.getAnswers();
            for (Answer answer : insertedAnswers) {
                myDataBase.execSQL("INSERT INTO " + AnswerEntryConsts.NAME_OF_TABLE
                                + "(" + AnswerEntryConsts.COL_ANSWER_CONTENT + ","
                                + AnswerEntryConsts.COL_ANSWER_CORRECT + "," + AnswerEntryConsts.COL_ANSWER_QUESTION_ID
                                + ") VALUES (?,?,?,?)",
                        new Object[]{answer.get_answer_content(), answer.get_is_true(), answer.get_question_id()}
                );
            }
            //p2aDb.getWritableDatabase().endTransaction();
            myDataBase.setTransactionSuccessful();
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        }
    }
}
