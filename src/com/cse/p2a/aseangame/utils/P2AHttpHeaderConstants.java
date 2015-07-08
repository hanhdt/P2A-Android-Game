/**
 * 
 */
package com.cse.p2a.aseangame.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DHanh
 * 
 */
public class P2AHttpHeaderConstants {
	
	public static final String ACCESS_KEY = "AccessKeyId";
	
	public static final String AUTHORIZATION = "Authorization";

	public static final String CONTENT_TYPE = "Content-Type";
	
	public static final String COUNTRY_ID = "CountryId";
	
	public static final String FINISH_DATE = "FinishDate";
	
	public static final String FINISH_TIME = "FinishTime";
	
	public static final String QUESTION_ID = "Id";
	
	public static final String TOKEN = "Token";
	
	public static final String TOTAL_SCORE = "TotalScore";

	private Map<String, String> headerParams;

	public P2AHttpHeaderConstants() {
		headerParams = new HashMap<String, String>();
	}

	public HashMap<String, String> getHeaderParams() {
		return (HashMap<String, String>) headerParams;
	}
}
