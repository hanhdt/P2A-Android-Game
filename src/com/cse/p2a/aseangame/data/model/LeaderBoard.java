package com.cse.p2a.aseangame.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafe on 3/14/14.
 */
public class LeaderBoard {
    private String loggedName;
    private String nameOfInstitute;
    private int totalScore;

    public LeaderBoard(String loggedName, String nameOfInstitute, int totalScore) {
        this.loggedName = loggedName;
        this.nameOfInstitute = nameOfInstitute;
        this.totalScore = totalScore;
    }

    public String getLoggedName() {
        return loggedName;
    }

    public void setLoggedName(String loggedName) {
        this.loggedName = loggedName;
    }

    public String getNameOfInstitute() {
        return nameOfInstitute;
    }

    public void setNameOfInstitute(String nameOfInstitute) {
        this.nameOfInstitute = nameOfInstitute;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public static List<LeaderBoard> parserLeaderBoardJson(String leaderBoardJson) {
        List<LeaderBoard> leaders = new ArrayList<LeaderBoard>();
        try {
            JSONObject countryObject = new JSONObject(leaderBoardJson);
            JSONArray leaderArray = countryObject.getJSONArray("Country");
            JSONObject leaderObject;
            LeaderBoard mLeader;
            for (int i = 0; i < leaderArray.length(); i++) {
                leaderObject = leaderArray.getJSONObject(i);
                mLeader = new LeaderBoard(leaderObject.getString("login_name"), leaderObject.getString("name_institute"),
                        leaderObject.getInt("total_score"));
                leaders.add(mLeader);
                leaderObject = null;
                mLeader = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return leaders;
    }
}
