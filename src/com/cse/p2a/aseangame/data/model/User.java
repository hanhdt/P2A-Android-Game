/**
 *
 */
package com.cse.p2a.aseangame.data.model;

import android.util.Log;

import com.cse.p2a.aseangame.utils.GeneralHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @author HanhDTRAN
 */
public class User {
    public static final String TAG = User.class.getSimpleName();

    private int _user_id;
    private int _user_active;
    private String _user_birthday;
    private String _user_country_name;
    private String _user_created_date;
    private String _user_email;
    private String _user_first_name;
    private int _user_group_id;
    private String _user_img_url;
    private String _user_institute_name;
    private String _user_last_name;
    private String _user_middle_name;
    private String _user_name;
    private String _user_password;
    private float _user_point;
    private String _user_title;
    private int _user_type;
    private String _user_token;

    public User() {
    }

    /**
     * This function provides to parse an User object to JSON Obj
     *
     * @param userJson
     * @return
     */
    public static User parseUserJson(String userJson) {
        final User mUser = new User();
        try {

            JSONObject userJsonObject = new JSONObject(userJson);
            JSONArray userJsonArray = userJsonObject.getJSONArray("User");
            JSONObject user = userJsonArray.getJSONObject(0);
            mUser.set_user_active(1);
            mUser.set_user_birthday("");
            mUser.set_user_country_name(user.getString("country_name"));
            mUser.set_user_created_date(new Date().toString());
            mUser.set_user_email("");
            mUser.set_user_first_name(user.getString("first_name"));
            mUser.set_user_group_id(1);
            mUser.set_user_id(user.getInt("user_id"));
            mUser.set_user_img_url("");
            mUser.set_user_institute_name(user.getString("institute_name"));
            mUser.set_user_last_name(user.getString("last_name"));
            mUser.set_user_middle_name(user.getString("middle_name"));
            try {
                mUser.set_user_name(GeneralHelper.HexMD5ForString("fake"));
                mUser.set_user_password(GeneralHelper.HexMD5ForString("fake"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mUser.set_user_title("student");
            mUser.set_user_type(1);
            mUser.set_user_token(user.getString("token"));
        } catch (JSONException e) {
            Log.e(User.TAG, e.getLocalizedMessage() + " - login response string: " + userJson);
            e.printStackTrace();
        }

        return mUser;
    }

    /**
     * @return the _user_id
     */
    public int get_user_id() {
        return _user_id;
    }

    /**
     * @param _user_id the _user_id to set
     */
    public void set_user_id(int _user_id) {
        this._user_id = _user_id;
    }

    /**
     * @return the _user_active
     */
    public int get_user_active() {
        return _user_active;
    }

    /**
     * @param _user_active the _user_active to set
     */
    public void set_user_active(int _user_active) {
        this._user_active = _user_active;
    }

    /**
     * @return the _user_birthday
     */
    public String get_user_birthday() {
        return _user_birthday;
    }

    /**
     * @param _user_birthday the _user_birthday to set
     */
    public void set_user_birthday(String _user_birthday) {
        this._user_birthday = _user_birthday;
    }

    /**
     * @return the _user_country_name
     */
    public String get_user_country_name() {
        return _user_country_name;
    }

    /**
     * @param _user_country_name the _user_country_name to set
     */
    public void set_user_country_name(String _user_country_name) {
        this._user_country_name = _user_country_name;
    }

    /**
     * @return the _user_created_date
     */
    public String get_user_created_date() {
        return _user_created_date;
    }

    /**
     * @param _user_created_date the _user_created_date to set
     */
    public void set_user_created_date(String _user_created_date) {
        this._user_created_date = _user_created_date;
    }

    /**
     * @return the _user_email
     */
    public String get_user_email() {
        return _user_email;
    }

    /**
     * @param _user_email the _user_email to set
     */
    public void set_user_email(String _user_email) {
        this._user_email = _user_email;
    }

    /**
     * @return the _user_first_name
     */
    public String get_user_first_name() {
        return _user_first_name;
    }

    /**
     * @param _user_first_name the _user_first_name to set
     */
    public void set_user_first_name(String _user_first_name) {
        this._user_first_name = _user_first_name;
    }

    /**
     * @return the _user_group_id
     */
    public int get_user_group_id() {
        return _user_group_id;
    }

    /**
     * @param _user_group_id the _user_group_id to set
     */
    public void set_user_group_id(int _user_group_id) {
        this._user_group_id = _user_group_id;
    }

    /**
     * @return the _user_img_url
     */
    public String get_user_img_url() {
        return _user_img_url;
    }

    /**
     * @param _user_img_url the _user_img_url to set
     */
    public void set_user_img_url(String _user_img_url) {
        this._user_img_url = _user_img_url;
    }

    /**
     * @return the _user_institute_name
     */
    public String get_user_institute_name() {
        return _user_institute_name;
    }

    /**
     * @param _user_institute_name the _user_institute_name to set
     */
    public void set_user_institute_name(String _user_institute_name) {
        this._user_institute_name = _user_institute_name;
    }

    /**
     * @return the _user_last_name
     */
    public String get_user_last_name() {
        return _user_last_name;
    }

    /**
     * @param _user_last_name the _user_last_name to set
     */
    public void set_user_last_name(String _user_last_name) {
        this._user_last_name = _user_last_name;
    }

    /**
     * @return the _user_middle_name
     */
    public String get_user_middle_name() {
        return _user_middle_name;
    }

    /**
     * @param _user_middle_name the _user_middle_name to set
     */
    public void set_user_middle_name(String _user_middle_name) {
        this._user_middle_name = _user_middle_name;
    }

    /**
     * @return the _user_name
     */
    public String get_user_name() {
        return _user_name;
    }

    /**
     * @param _user_name the _user_name to set
     */
    public void set_user_name(String _user_name) {
        this._user_name = _user_name;
    }

    /**
     * @return the _user_password
     */
    public String get_user_password() {
        return _user_password;
    }

    /**
     * @param _user_password the _user_password to set
     */
    public void set_user_password(String _user_password) {
        this._user_password = _user_password;
    }

    /**
     * @return the _user_point
     */
    public float get_user_point() {
        return _user_point;
    }

    /**
     * @param _user_point the _user_point to set
     */
    public void set_user_point(float _user_point) {
        this._user_point = _user_point;
    }

    /**
     * @return the _user_title
     */
    public String get_user_title() {
        return _user_title;
    }

    /**
     * @param _user_title the _user_title to set
     */
    public void set_user_title(String _user_title) {
        this._user_title = _user_title;
    }

    /**
     * @return the _user_type
     */
    public int get_user_type() {
        return _user_type;
    }

    /**
     * @param _user_type the _user_type to set
     */
    public void set_user_type(int _user_type) {
        this._user_type = _user_type;
    }

    /**
     * @return the _user_token
     */
    public String get_user_token() {
        return _user_token;
    }

    /**
     * @param _user_token the _user_token to set
     */
    public void set_user_token(String _user_token) {
        this._user_token = _user_token;
    }

}
