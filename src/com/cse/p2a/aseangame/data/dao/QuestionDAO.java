/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.QuestionEntryConsts;
import com.cse.p2a.aseangame.data.model.Question;

/**
 * @author HanhDTRAN
 */
public class QuestionDAO implements IQuestionDAO {

    private static QuestionDAO instance = null;

    protected final Context ctxApp;

    protected final P2AGameDbHelper p2aDb;

    public QuestionDAO() {
        ctxApp = P2AContext.getContext();
        p2aDb = P2AGameDbHelper.getInstance(ctxApp);
    }

    public static final QuestionDAO getInstance() {
        if (instance == null) {
            instance = new QuestionDAO();
        }
        return instance;
    }

    @Override
    public long insertQuestion(Question question) {
        long affected = -1;

        try {
            final ContentValues values = new ContentValues();
            values.put(QuestionEntryConsts._ID, question.get_question_id());
            values.put(QuestionEntryConsts.COL_QUESTION_CONTENT, question.get_question_content());
            values.put(QuestionEntryConsts.COL_QUESTION_GROUP_ID, question.get_group_id());
            values.put(QuestionEntryConsts.COL_QUESTION_LEVEL_ID, question.get_level_id());
            p2aDb.openDataBase();
            // insert question first
            affected = p2aDb.insertRecordsInDB(QuestionEntryConsts.NAME_OF_TABLE, null, values);
            //Log.w(QuestionDAO.class.getCanonicalName(), "Inserted question: " + affected);
//			if(affected != -1){
//				// insert answers of question above.
//				AnswerDAO answerDAO = AnswerDAO.getInstance();
//				List<Answer> insertedAnswers = question.getAnswers();
//				for(Answer answer: insertedAnswers){
//					long insertedAnswer = answerDAO.insertAnswer(answer);					
//					Log.w(QuestionDAO.class.getCanonicalName(), "Inserted answer: " + insertedAnswer + ";" + answer.get_question_id());
//				}
//			}					
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return affected;
    }

    @Override
    public long getFirstQuestionId() {
        long result = -1;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB(QuestionEntryConsts.NAME_OF_TABLE,
                    new String[]{QuestionEntryConsts._ID}, null, null, null, null, null);
            if (cur != null) {
                cur.moveToFirst();
                result = cur.getLong(0); // Get first question id
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();

        } finally {
            p2aDb.close();
        }
        return result;
    }

    @Override
    public int countQuestions() {
        int row = 0;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB("SELECT count(*) FROM " + QuestionEntryConsts.NAME_OF_TABLE + ";", null);
            if (cur != null) {
                cur.moveToFirst();
                row = cur.getInt(0);
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();

        } finally {
            p2aDb.close();
        }
        return row;
    }

    @Override
    public void insertMultipleAnswerOfQuestion(Question question) {
        try {
            p2aDb.openDataBase();
            p2aDb.insertMultipleAnswerOfQuestion(question);
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
    }
}
