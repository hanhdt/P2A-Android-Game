package com.cse.p2a.aseangame.data.adapter;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.AnswerEntryConsts;
import com.cse.p2a.aseangame.data.constants.CountryEntryConsts;
import com.cse.p2a.aseangame.data.constants.GroupEntryConsts;
import com.cse.p2a.aseangame.data.constants.LevelEntryConsts;
import com.cse.p2a.aseangame.data.constants.QuestionEntryConsts;
import com.cse.p2a.aseangame.data.constants.SessionDetailEntryConsts;
import com.cse.p2a.aseangame.data.constants.SessionEntryConsts;
import com.cse.p2a.aseangame.data.model.Answer;
import com.cse.p2a.aseangame.data.model.Question;
import com.cse.p2a.aseangame.data.model.Session;
import com.cse.p2a.aseangame.data.model.SessionDetail;

import java.util.ArrayList;
import java.util.Collections;

public class P2AGameDbAdapter {
    private static P2AGameDbHelper dbHelper = P2AGameDbHelper.getInstance(P2AContext.getContext());
    private static String[] levelColumn = {LevelEntryConsts._ID, LevelEntryConsts.COL_LEVEL_NAME};

    public static ArrayList<Question> getRandomQuestion(int _id_country) {
        dbHelper.openDataBase();
        final ArrayList<Question> tbtQuestions = new ArrayList<Question>();

        final String query = "SELECT " + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts._ID + " , " + QuestionEntryConsts.NAME_OF_TABLE + "."
                + QuestionEntryConsts.COL_QUESTION_GROUP_ID + " , " + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts.COL_QUESTION_CONTENT
                + " , " + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts.COL_QUESTION_LEVEL_ID + " " + "FROM "
                + CountryEntryConsts.NAME_OF_TABLE + " INNER JOIN " + GroupEntryConsts.NAME_OF_TABLE + " ON " + CountryEntryConsts.NAME_OF_TABLE + "."
                + CountryEntryConsts._ID + " = " + GroupEntryConsts.NAME_OF_TABLE + "." + GroupEntryConsts.COL_GROUP_COUNTRY_ID + " INNER JOIN "
                + QuestionEntryConsts.NAME_OF_TABLE + " ON " + GroupEntryConsts.NAME_OF_TABLE + "." + GroupEntryConsts._ID + " = "
                + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts.COL_QUESTION_GROUP_ID + " INNER JOIN " + LevelEntryConsts.NAME_OF_TABLE
                + " ON " + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts.COL_QUESTION_LEVEL_ID + " = " + LevelEntryConsts.NAME_OF_TABLE + "."
                + LevelEntryConsts._ID + " WHERE " + QuestionEntryConsts.NAME_OF_TABLE + "." + QuestionEntryConsts.COL_QUESTION_LEVEL_ID + "=? AND "
                + CountryEntryConsts.NAME_OF_TABLE + "." + CountryEntryConsts._ID + " = ? " + " ORDER BY RANDOM() LIMIT ";

        ArrayList<ArrayList<String>> levelArray = dbHelper.selectRecordsFromDBList(LevelEntryConsts.NAME_OF_TABLE, levelColumn, null, null, null, null, null);
        for (ArrayList<String> level : levelArray) {
            String[] selectionArgs = {level.get(0), _id_country + ""};
            ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query + "10", selectionArgs);
            Collections.shuffle(tempArray);
            for (ArrayList<String> arrayList : tempArray) {
                Question que = new Question();
                que.set_question_id(Integer.parseInt(arrayList.get(0)));
                que.set_group_id(Integer.parseInt(arrayList.get(1)));
                que.set_question_content(arrayList.get(2));
                que.set_level_id(Integer.parseInt(arrayList.get(3)));
//                que.setAnswers(AnswerDAO.getInstance().getAnswersOfQuestion(Integer.parseInt(arrayList.get(0))));
                tbtQuestions.add(que);
            }
            if (tempArray.size() < 10) {

                String[] selectionAsian = {level.get(0), "999"};
                ArrayList<ArrayList<String>> tempArrayAsian = dbHelper.selectRecordsFromDBList(query
                        + (10 - tempArray.size()), selectionAsian);
                Collections.shuffle(tempArrayAsian);
                for (ArrayList<String> arrayList : tempArrayAsian) {
                    Question que = new Question();
                    que.set_question_id(Integer.parseInt(arrayList.get(0)));
                    que.set_group_id(Integer.parseInt(arrayList.get(1)));
                    que.set_question_content(arrayList.get(2));
                    que.set_level_id(Integer.parseInt(arrayList.get(3)));
                    tbtQuestions.add(que);
                }

                int i = 0;
                while (tempArray.size() + tempArrayAsian.size() + i < 10) {
                    Collections.shuffle(tempArray);
                    for (ArrayList<String> arrayList : tempArray) {
                        if (i < 10 - tempArray.size() - tempArrayAsian.size()) {
                            Question que = new Question();
                            que.set_question_id(Integer.parseInt(arrayList.get(0)));
                            que.set_group_id(Integer.parseInt(arrayList.get(1)));
                            que.set_question_content(arrayList.get(2));
                            que.set_level_id(Integer.parseInt(arrayList.get(3)));
                            tbtQuestions.add(que);
                            i++;
                        } else
                            break;
                    }
                }
            }
        }
        dbHelper.close();
        return tbtQuestions;
    }

    public static ArrayList<Answer> getAnswer(int _id_question) {
        dbHelper.openDataBase();
        ArrayList<Answer> tbtAnswers = new ArrayList<Answer>();
        String query = "SELECT * FROM " + AnswerEntryConsts.NAME_OF_TABLE + " WHERE "
                + AnswerEntryConsts.COL_ANSWER_QUESTION_ID + " = ?";
        String[] selectionArgs = {_id_question + ""};
        ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query, selectionArgs);
        for (ArrayList<String> arrayList : tempArray) {
            Answer ans = new Answer();
            ans.set_answer_id(Integer.parseInt(arrayList.get(0)));
            ans.set_answer_content(arrayList.get(1));
            ans.set_question_id(Integer.parseInt(arrayList.get(2)));
            ans.set_is_true(Integer.parseInt(arrayList.get(3)) == 1 ? true : false);
            tbtAnswers.add(ans);
        }
        dbHelper.close();
        return tbtAnswers;
    }

    public static int[] getPoint(int _id_level) {
        dbHelper.openDataBase();
        int points[] = new int[4];
        String query = "SELECT * FROM " + LevelEntryConsts.NAME_OF_TABLE + " WHERE "
                + LevelEntryConsts._ID + " = ? ";
        String[] selectionArgs = {_id_level + ""};
        ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query, selectionArgs);
        if (tempArray != null) {
            points[0] = Integer.parseInt(tempArray.get(0).get(1));
            points[1] = Integer.parseInt(tempArray.get(0).get(2));
            points[2] = Integer.parseInt(tempArray.get(0).get(3));
            points[3] = Integer.parseInt(tempArray.get(0).get(4));
            return points;
        }
        dbHelper.close();
        return null;
    }

    public static Question getQuestion(int id_question) {
        dbHelper.openDataBase();
        Question question = new Question();
        String query = "SELECT * FROM " + QuestionEntryConsts.NAME_OF_TABLE + " WHERE "
                + QuestionEntryConsts._ID + " = ? ";
        String[] selectionArgs = {id_question + ""};
        ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query, selectionArgs);
        if (tempArray != null) {
            question.set_question_id(id_question);         // id
            question.set_question_content(tempArray.get(0).get(2)); // question_group_id
            question.set_level_id(Integer.parseInt(tempArray.get(0).get(3))); // question_level_id
            question.set_group_id(Integer.parseInt(tempArray.get(0).get(1)));
        }
        dbHelper.close();
        return question;
    }

    public static Session getSession(int id_session) {
        dbHelper.openDataBase();
        Session session = new Session();
        String query = "SELECT * FROM " + SessionEntryConsts.NAME_OF_TABLE + " WHERE "
                + SessionEntryConsts._ID + " = ? ";
        String[] selectionArgs = {id_session + ""};
        ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query, selectionArgs);
        if (tempArray != null) {
            session.set_session_id(id_session);
            session.set_session_begin_date(Long.parseLong(tempArray.get(0).get(5)));
            session.set_session_country_id(Integer.parseInt(tempArray.get(0).get(1)));
            session.set_session_end_date(Long.parseLong(tempArray.get(0).get(6)));
            session.set_session_finish(Integer.parseInt(tempArray.get(0).get(2)));
            session.set_session_num_lastest_correct(Integer.parseInt(tempArray.get(0).get(3)));
            session.set_session_spend_time(Long.parseLong(tempArray.get(0).get(7)));
            session.set_session_token(tempArray.get(0).get(9));
            session.set_session_total_score(Long.parseLong(tempArray.get(0).get(8)));
            session.set_session_user_id(Integer.parseInt(tempArray.get(0).get(4)));
        }
        dbHelper.close();
        return session;
    }

    public static ArrayList<SessionDetail> getSessionDetails(int id_session) {
        dbHelper.openDataBase();
        ArrayList<SessionDetail> sessionDetails = new ArrayList<SessionDetail>();
        String query = "SELECT * FROM " + SessionDetailEntryConsts.NAME_OF_TABLE + " WHERE "
                + SessionDetailEntryConsts.COL_SESSION_DETAIL_SESSION_ID + " = ? ";
        String[] selectionArgs = {id_session + ""};
        ArrayList<ArrayList<String>> tempArray = dbHelper.selectRecordsFromDBList(query, selectionArgs);
        for (ArrayList<String> arrayList : tempArray) {
            SessionDetail sessionDetail = new SessionDetail();
            sessionDetail.set_session_detail_id(Integer.parseInt(arrayList.get(0)));
            sessionDetail.set_session_detail_answer_id_1(Integer.parseInt(arrayList.get(1)));
            sessionDetail.set_session_detail_answer_id_2(Integer.parseInt(arrayList.get(2)));
            sessionDetail.set_session_detail_answer_id_3(Integer.parseInt(arrayList.get(3)));
            sessionDetail.set_session_detail_answer_id_4(Integer.parseInt(arrayList.get(4)));
            sessionDetail.set_session_detail_create_date(Long.parseLong(arrayList.get(5)));
            sessionDetail.set_session_detail_order_number(Integer.parseInt(arrayList.get(6)));
            sessionDetail.set_session_detail_question_id(Integer.parseInt(arrayList.get(8)));
            sessionDetail.set_session_detail_score(Float.parseFloat(arrayList.get(7)));
            sessionDetail.set_session_detail_session_id(id_session);
            sessionDetails.add(sessionDetail);
        }
        dbHelper.close();
        return sessionDetails;
    }
}
