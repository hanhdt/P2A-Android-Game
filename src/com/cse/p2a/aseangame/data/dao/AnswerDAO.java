/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.AnswerEntryConsts;
import com.cse.p2a.aseangame.data.model.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HanhDTRAN
 */
public class AnswerDAO implements IAnswerDAO {

    private static AnswerDAO instance = null;

    protected final Context ctxApp;

    protected final P2AGameDbHelper p2aDb;

    public static final AnswerDAO getInstance() {
        if (instance == null) {
            instance = new AnswerDAO();
        }
        return instance;
    }

    public AnswerDAO() {
        ctxApp = P2AContext.getContext();
        p2aDb = P2AGameDbHelper.getInstance(ctxApp);
    }

    @Override
    public long insertAnswer(Answer newAnswer) {

        long n = -1;
        final ContentValues values = new ContentValues();
        values.put(AnswerEntryConsts.COL_ANSWER_CONTENT, newAnswer.get_answer_content());
        values.put(AnswerEntryConsts.COL_ANSWER_CORRECT, (newAnswer.get_is_true() ? 1 : 0));
        values.put(AnswerEntryConsts.COL_ANSWER_QUESTION_ID, newAnswer.get_question_id());
        try {
            p2aDb.openDataBase();
            n = p2aDb.insertRecordsInDB(AnswerEntryConsts.NAME_OF_TABLE, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return n;
    }

    @Override
    public List<Answer> getAnswersOfQuestion(int questionId) {
        List<Answer> answers = null;
        try {
            p2aDb.openDataBase();
            //Log.d(AnswerDAO.class.getCanonicalName(),"Loading answers of question:" + questionId);
            ArrayList<ArrayList<String>> results = p2aDb.selectRecordsFromDBList(AnswerEntryConsts.NAME_OF_TABLE, new String[]{
                    AnswerEntryConsts._ID,
                    AnswerEntryConsts.COL_ANSWER_CONTENT,
                    AnswerEntryConsts.COL_ANSWER_QUESTION_ID,
                    AnswerEntryConsts.COL_ANSWER_CORRECT
            }, AnswerEntryConsts.COL_ANSWER_QUESTION_ID + "=?", new String[]{String.valueOf(questionId)}, null, null, null);
            answers = new ArrayList<Answer>();
            if (!results.isEmpty()) {
                for (int i = 0; i < results.size(); i++) {
                    ArrayList<String> row = results.get(i);
                    Answer answer = new Answer();
                    answer.set_answer_id(Integer.parseInt(row.get(0)));
                    answer.set_answer_content(row.get(1));
                    answer.set_is_true(row.get(3).equals("1") ? true : false);
                    answer.set_question_id(questionId);
                    answers.add(answer);
                    answer = null;
                }
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return answers;
    }

}
