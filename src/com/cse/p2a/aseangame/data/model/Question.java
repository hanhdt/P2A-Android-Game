package com.cse.p2a.aseangame.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int _question_id;
    private int _group_id;
    private int _level_id;
    private String _question_content;
    private List<Answer> answers;

    public Question() {
        answers = new ArrayList<Answer>();
    }

    public static List<Question> parserQuestionsJson(String questionJson) {
        final List<Question> questions = new ArrayList<Question>();

        try {
            final JSONObject jsonObject = new JSONObject(questionJson);
            final JSONArray questionJsonArray = jsonObject.getJSONArray("Question");
            Question question;
            List<Answer> answersOfQuestion;
            for (int i = 0; i < questionJsonArray.length(); i++) {
                question = new Question();
                final JSONObject questionObject = questionJsonArray.getJSONObject(i);
                question.set_question_id(questionObject.getInt("question_id"));
                question.set_group_id(questionObject.getInt("group_id"));
                question.set_level_id(questionObject.getInt("level_id"));
                question.set_question_content(questionObject.getString("question_text"));
                final JSONArray answersArray = questionObject.getJSONArray("Answer");
                answersOfQuestion = Answer.parserAnswerJson(answersArray.toString(), question.get_question_id());
                question.setAnswers(answersOfQuestion);
                questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public int get_question_id() {
        return _question_id;
    }

    public void set_question_id(int _question_id) {
        this._question_id = _question_id;
    }

    public int get_group_id() {
        return _group_id;
    }

    public void set_group_id(int _group_id) {
        this._group_id = _group_id;
    }

    public int get_level_id() {
        return _level_id;
    }

    public void set_level_id(int _level_id) {
        this._level_id = _level_id;
    }

    public String get_question_content() {
        return _question_content;
    }

    public void set_question_content(String _question_content) {
        this._question_content = _question_content;
    }

    /**
     * @return the answers
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }


}
