/**
 * 
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.Answer;

import java.util.List;

/**
 * @author HanhDTRAN
 *
 */
public interface IAnswerDAO {
	
	long insertAnswer(final Answer newAnswer);

    List<Answer> getAnswersOfQuestion(int questionId);
}
