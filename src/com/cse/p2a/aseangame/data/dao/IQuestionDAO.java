/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.Question;

/**
 * @author HanhDTRAN
 */
public interface IQuestionDAO {

    long insertQuestion(final Question question);

    long getFirstQuestionId();

    int countQuestions();

    void insertMultipleAnswerOfQuestion(final Question question);
}
