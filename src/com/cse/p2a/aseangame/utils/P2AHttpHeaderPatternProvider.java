/**
 *  Provides header pattern for service requests
 */
package com.cse.p2a.aseangame.utils;

/**
 * @author DHanh
 * 
 */
public class P2AHttpHeaderPatternProvider {

	public static final String CONTENT_TYPE_VALUE = "application/json;charset=utf-8";

	public static final int GET_QUESTION = 2;

	public static final int LOGIN_REQUEST = 1;

	public static P2AHttpHeaderPatternProvider instanceOfCommitScoreHeader(
			String token, String countryId, String totalScore,
			String finishDate, String finishTime) {
		return new P2AHttpHeaderPatternProvider(token, countryId, totalScore,
				finishDate, finishTime);
	}

	public static P2AHttpHeaderPatternProvider instanceOfGetDataHeader(
			String token) {
		return new P2AHttpHeaderPatternProvider(token);
	}

	public static P2AHttpHeaderPatternProvider instanceOfGetQuestionHeader(
			String token, String id) {
		return new P2AHttpHeaderPatternProvider(token, id, GET_QUESTION);
	}

	public static P2AHttpHeaderPatternProvider instanceOfLoginHeader(
			String password, String username) {
		return new P2AHttpHeaderPatternProvider(password, username,
				LOGIN_REQUEST);
	}

	private final P2AHttpHeaderConstants httpHeaderConstants;

	public P2AHttpHeaderPatternProvider(String token) {
		httpHeaderConstants = new P2AHttpHeaderConstants();
		httpHeaderConstants.getHeaderParams().put(P2AHttpHeaderConstants.TOKEN,
				token);
	}

	public P2AHttpHeaderPatternProvider(String agr1, String agr2,
			int requestFunction) {
		httpHeaderConstants = new P2AHttpHeaderConstants();

		if (requestFunction == LOGIN_REQUEST) {
			final String authorization = GeneralHelper.encryptMD5(agr1);
			httpHeaderConstants.getHeaderParams().put(
					P2AHttpHeaderConstants.AUTHORIZATION, authorization);
			httpHeaderConstants.getHeaderParams().put(
					P2AHttpHeaderConstants.ACCESS_KEY, agr2);
		} else if (requestFunction == GET_QUESTION) {
			httpHeaderConstants.getHeaderParams().put(
					P2AHttpHeaderConstants.TOKEN, agr1);
			httpHeaderConstants.getHeaderParams().put(
					P2AHttpHeaderConstants.QUESTION_ID, agr2);
		}

	}

	public P2AHttpHeaderPatternProvider(String token, String countryId,
			String totalScore, String finishDate, String finishTime) {
		httpHeaderConstants = new P2AHttpHeaderConstants();
		httpHeaderConstants.getHeaderParams().put(P2AHttpHeaderConstants.TOKEN,
				token);
		httpHeaderConstants.getHeaderParams().put(
				P2AHttpHeaderConstants.COUNTRY_ID, countryId);
		httpHeaderConstants.getHeaderParams().put(
				P2AHttpHeaderConstants.TOTAL_SCORE, totalScore);
		httpHeaderConstants.getHeaderParams().put(
				P2AHttpHeaderConstants.FINISH_DATE, finishDate);
		httpHeaderConstants.getHeaderParams().put(
				P2AHttpHeaderConstants.FINISH_TIME, finishDate);
	}

	public P2AHttpHeaderConstants getHttpHeaderConstants() {
		return httpHeaderConstants;
	}

}
