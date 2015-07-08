/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.constants.UserEntryConsts;
import com.cse.p2a.aseangame.data.model.User;
import libs.BCrypt;

/**
 * @author HanhDTRAN
 */
public class UserDAO implements IUserDAO {

    private static UserDAO instance = null;

    protected final Context ctxApp;

    protected final P2AGameDbHelper p2aDb;

    public static final UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public UserDAO() {
        ctxApp = P2AContext.getContext();
        p2aDb = P2AGameDbHelper.getInstance(ctxApp);
    }


    @Override
    public long insertUser(User newUser) {
        long newUserId = -1;
        try {
            final ContentValues values = new ContentValues();
            values.put(UserEntryConsts.COL_USER_ACTIVE, newUser.get_user_active());
            values.put(UserEntryConsts.COL_USER_BIRTHDAY, newUser.get_user_birthday());
            values.put(UserEntryConsts.COL_USER_COUNTRY_NAME, newUser.get_user_country_name());
            values.put(UserEntryConsts.COL_USER_CREATED_DATE, newUser.get_user_created_date());
            values.put(UserEntryConsts.COL_USER_EMAIL, newUser.get_user_email());
            values.put(UserEntryConsts.COL_USER_FIRST_NAME, newUser.get_user_first_name());
            values.put(UserEntryConsts.COL_USER_GROUP_ID, newUser.get_user_group_id());
            values.put(UserEntryConsts.COL_USER_IMG_URL, newUser.get_user_img_url());
            values.put(UserEntryConsts.COL_USER_INSTITUTE_NAME, newUser.get_user_institute_name());
            values.put(UserEntryConsts.COL_USER_LAST_NAME, newUser.get_user_last_name());
            values.put(UserEntryConsts.COL_USER_MIDDLE_NAME, newUser.get_user_middle_name());
            values.put(UserEntryConsts.COL_USER_NAME, newUser.get_user_name());
            values.put(UserEntryConsts.COL_USER_PASSWORD, BCrypt.hashpw(newUser.get_user_password(), BCrypt.gensalt()));
            values.put(UserEntryConsts.COL_USER_POINT, newUser.get_user_point());
            values.put(UserEntryConsts.COL_USER_TITLE, newUser.get_user_title());
            values.put(UserEntryConsts.COL_USER_TYPE, newUser.get_user_type());
            values.put(UserEntryConsts.COL_USER_TOKEN, newUser.get_user_token());

            p2aDb.openDataBase();
            newUserId = p2aDb.insertRecordsInDB(UserEntryConsts.NAME_OF_TABLE, null, values);
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return newUserId;
    }

    @Override
    public boolean isDuplicatedUser(User mUser) {
        boolean flag = false;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB(UserEntryConsts.NAME_OF_TABLE,
                    new String[]{
                            UserEntryConsts.COL_USER_NAME,
                            UserEntryConsts.COL_USER_PASSWORD},
                    null, null, null, null, null);
            if (cur.moveToFirst()) {
                do {
                    if (!cur.getString(0).equals("anonymous")) {
                        if (mUser.get_user_name().equals(cur.getString(0))
                                && BCrypt.checkpw(mUser.get_user_password(), cur.getString(1))) {
                            flag = true;
                            break;
                        }
                    }
                } while (cur.moveToNext());
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return flag;
    }

    @Override
    public User getAnonymous() {
        User user = null;
        try {
            p2aDb.openDataBase();
            Cursor cur = p2aDb.selectRecordsFromDB(UserEntryConsts.NAME_OF_TABLE, new String[]{
                    UserEntryConsts._ID,
                    UserEntryConsts.COL_USER_ACTIVE,
                    UserEntryConsts.COL_USER_BIRTHDAY,
                    UserEntryConsts.COL_USER_COUNTRY_NAME,
                    UserEntryConsts.COL_USER_CREATED_DATE,
                    UserEntryConsts.COL_USER_EMAIL,
                    UserEntryConsts.COL_USER_FIRST_NAME,
                    UserEntryConsts.COL_USER_GROUP_ID,
                    UserEntryConsts.COL_USER_IMG_URL,
                    UserEntryConsts.COL_USER_INSTITUTE_NAME,
                    UserEntryConsts.COL_USER_LAST_NAME,
                    UserEntryConsts.COL_USER_MIDDLE_NAME,
                    UserEntryConsts.COL_USER_NAME,
                    UserEntryConsts.COL_USER_PASSWORD,
                    UserEntryConsts.COL_USER_POINT,
                    UserEntryConsts.COL_USER_TITLE,
                    UserEntryConsts.COL_USER_TOKEN,
                    UserEntryConsts.COL_USER_TYPE
            }, UserEntryConsts._ID + "=?", new String[]{"1"}, null, null, null);
            if (cur != null) {
                cur.moveToFirst();
                user = new User();
                user.set_user_id(cur.getInt(0));
                user.set_user_active(cur.getInt(1));
                user.set_user_birthday(cur.getString(2));
                user.set_user_country_name(cur.getString(3));
                user.set_user_created_date(cur.getString(4));
                user.set_user_email(cur.getString(5));
                user.set_user_first_name(cur.getString(6));
                user.set_user_group_id(cur.getInt(7));
                user.set_user_img_url(cur.getString(8));
                user.set_user_institute_name(cur.getString(9));
                user.set_user_last_name(cur.getString(10));
                user.set_user_middle_name(cur.getString(11));
                user.set_user_name(cur.getString(12));
                user.set_user_password(cur.getString(13));
                user.set_user_point(cur.getFloat(14));
                user.set_user_title(cur.getString(15));
                user.set_user_token(cur.getString(16));
                user.set_user_type(cur.getInt(17));
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return user;
    }

    @Override
    public User findP2AUser(int userId) {
        User user = null;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(UserEntryConsts.NAME_OF_TABLE, new String[]{
                    UserEntryConsts._ID,
                    UserEntryConsts.COL_USER_ACTIVE,
                    UserEntryConsts.COL_USER_BIRTHDAY,
                    UserEntryConsts.COL_USER_COUNTRY_NAME,
                    UserEntryConsts.COL_USER_CREATED_DATE,
                    UserEntryConsts.COL_USER_EMAIL,
                    UserEntryConsts.COL_USER_FIRST_NAME,
                    UserEntryConsts.COL_USER_GROUP_ID,
                    UserEntryConsts.COL_USER_IMG_URL,
                    UserEntryConsts.COL_USER_INSTITUTE_NAME,
                    UserEntryConsts.COL_USER_LAST_NAME,
                    UserEntryConsts.COL_USER_MIDDLE_NAME,
                    UserEntryConsts.COL_USER_NAME,
                    UserEntryConsts.COL_USER_PASSWORD,
                    UserEntryConsts.COL_USER_POINT,
                    UserEntryConsts.COL_USER_TITLE,
                    UserEntryConsts.COL_USER_TOKEN,
                    UserEntryConsts.COL_USER_TYPE
            }, UserEntryConsts._ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.set_user_id(cursor.getInt(0));
                user.set_user_active(cursor.getInt(1));
                user.set_user_birthday(cursor.getString(2));
                user.set_user_country_name(cursor.getString(3));
                user.set_user_created_date(cursor.getString(4));
                user.set_user_email(cursor.getString(5));
                user.set_user_first_name(cursor.getString(6));
                user.set_user_group_id(cursor.getInt(7));
                user.set_user_img_url(cursor.getString(8));
                user.set_user_institute_name(cursor.getString(9));
                user.set_user_last_name(cursor.getString(10));
                user.set_user_middle_name(cursor.getString(11));
                user.set_user_name(cursor.getString(12));
                user.set_user_password(cursor.getString(13));
                user.set_user_point(cursor.getFloat(14));
                user.set_user_title(cursor.getString(15));
                user.set_user_token(cursor.getString(16));
                user.set_user_type(cursor.getInt(17));
            }
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return user;
    }

    @Override
    public User findUserLogged(String username, String password) {
        User user = null;
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(UserEntryConsts.NAME_OF_TABLE, new String[]{
                    UserEntryConsts._ID,
                    UserEntryConsts.COL_USER_ACTIVE,
                    UserEntryConsts.COL_USER_BIRTHDAY,
                    UserEntryConsts.COL_USER_COUNTRY_NAME,
                    UserEntryConsts.COL_USER_CREATED_DATE,
                    UserEntryConsts.COL_USER_EMAIL,
                    UserEntryConsts.COL_USER_FIRST_NAME,
                    UserEntryConsts.COL_USER_GROUP_ID,
                    UserEntryConsts.COL_USER_IMG_URL,
                    UserEntryConsts.COL_USER_INSTITUTE_NAME,
                    UserEntryConsts.COL_USER_LAST_NAME,
                    UserEntryConsts.COL_USER_MIDDLE_NAME,
                    UserEntryConsts.COL_USER_NAME,
                    UserEntryConsts.COL_USER_PASSWORD,
                    UserEntryConsts.COL_USER_POINT,
                    UserEntryConsts.COL_USER_TITLE,
                    UserEntryConsts.COL_USER_TOKEN,
                    UserEntryConsts.COL_USER_TYPE
            }, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    if (!cursor.getString(12).equals("anonymous") || !cursor.getString(13).equals("anonymous")) {
                        if (username.equals(cursor.getString(12)) &&
                                BCrypt.checkpw(password, cursor.getString(13))) {
                            user = new User();
                            user.set_user_id(cursor.getInt(0));
                            user.set_user_active(cursor.getInt(1));
                            user.set_user_birthday(cursor.getString(2));
                            user.set_user_country_name(cursor.getString(3));
                            user.set_user_created_date(cursor.getString(4));
                            user.set_user_email(cursor.getString(5));
                            user.set_user_first_name(cursor.getString(6));
                            user.set_user_group_id(cursor.getInt(7));
                            user.set_user_img_url(cursor.getString(8));
                            user.set_user_institute_name(cursor.getString(9));
                            user.set_user_last_name(cursor.getString(10));
                            user.set_user_middle_name(cursor.getString(11));
                            user.set_user_name(username);
                            user.set_user_password(password);
                            user.set_user_point(cursor.getFloat(14));
                            user.set_user_title(cursor.getString(15));
                            user.set_user_token(cursor.getString(16));
                            user.set_user_type(cursor.getInt(17));
                            break;
                        }
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqle) {
            Log.e(UserDAO.class.getCanonicalName(), sqle.getMessage());
        } finally {
            p2aDb.close();
        }

        return user;
    }

    @Override
    public long updateUser(User updatedUser) {
        long newUserId = -1;
        try {
            final ContentValues values = new ContentValues();
            values.put(UserEntryConsts.COL_USER_ACTIVE, updatedUser.get_user_active());
            values.put(UserEntryConsts.COL_USER_BIRTHDAY, updatedUser.get_user_birthday());
            values.put(UserEntryConsts.COL_USER_COUNTRY_NAME, updatedUser.get_user_country_name());
            values.put(UserEntryConsts.COL_USER_CREATED_DATE, updatedUser.get_user_created_date());
            values.put(UserEntryConsts.COL_USER_EMAIL, updatedUser.get_user_email());
            values.put(UserEntryConsts.COL_USER_FIRST_NAME, updatedUser.get_user_first_name());
            values.put(UserEntryConsts.COL_USER_GROUP_ID, updatedUser.get_user_group_id());
            values.put(UserEntryConsts.COL_USER_IMG_URL, updatedUser.get_user_img_url());
            values.put(UserEntryConsts.COL_USER_INSTITUTE_NAME, updatedUser.get_user_institute_name());
            values.put(UserEntryConsts.COL_USER_LAST_NAME, updatedUser.get_user_last_name());
            values.put(UserEntryConsts.COL_USER_MIDDLE_NAME, updatedUser.get_user_middle_name());
            values.put(UserEntryConsts.COL_USER_NAME, updatedUser.get_user_name());
            values.put(UserEntryConsts.COL_USER_PASSWORD, BCrypt.hashpw(updatedUser.get_user_password(), BCrypt.gensalt()));
            values.put(UserEntryConsts.COL_USER_POINT, updatedUser.get_user_point());
            values.put(UserEntryConsts.COL_USER_TITLE, updatedUser.get_user_title());
            values.put(UserEntryConsts.COL_USER_TYPE, updatedUser.get_user_type());
            values.put(UserEntryConsts.COL_USER_TOKEN, updatedUser.get_user_token());

            p2aDb.openDataBase();
            newUserId = p2aDb.updateRecordsInDB(UserEntryConsts.NAME_OF_TABLE, values,
                    UserEntryConsts.COL_USER_NAME + "=?", new String[]{updatedUser.get_user_name()});
        } catch (SQLiteException sqle) {
            sqle.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return newUserId;
    }
}
