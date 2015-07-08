package com.cse.p2a.aseangame.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Answer {

    private int _answer_id;
    private int _question_id;
    private String _answer_content;
    private Boolean _is_true;

    public int get_answer_id() {
        return _answer_id;
    }

    public void set_answer_id(int _answer_id) {
        this._answer_id = _answer_id;
    }

    public int get_question_id() {
        return _question_id;
    }

    public void set_question_id(int _question_id) {
        this._question_id = _question_id;
    }

    public String get_answer_content() {
        return _answer_content;
    }

    public void set_answer_content(String _answer_content) {
        this._answer_content = _answer_content;
    }

    public Boolean get_is_true() {
        return _is_true;
    }

    public void set_is_true(Boolean _is_true) {
        this._is_true = _is_true;
    }

    public static List<Answer> parserAnswerJson(String answersJSON, int questionId) {
        final List<Answer> answers = new ArrayList<Answer>();

        try {
            final JSONArray jsonAnswerArray = new JSONArray(answersJSON);
            for (int i = 0; i < jsonAnswerArray.length(); i++) {
                final JSONObject answerJsonObject = jsonAnswerArray.getJSONObject(i);
                Answer answer = new Answer();
                answer.set_question_id(questionId);
                answer.set_answer_id(answerJsonObject.getInt("choice_index"));
                answer.set_answer_content(answerJsonObject.getString("answer_text"));
                answer.set_is_true((answerJsonObject.getInt("correct_choice") == 1 ? true : false));
                answers.add(answer);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return answers;
    }
}
