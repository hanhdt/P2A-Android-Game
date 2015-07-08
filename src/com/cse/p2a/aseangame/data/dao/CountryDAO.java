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
import com.cse.p2a.aseangame.data.constants.CommonConsts;
import com.cse.p2a.aseangame.data.constants.CountryEntryConsts;
import com.cse.p2a.aseangame.data.model.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hanh D. TRAN
 */
public class CountryDAO implements ICountryDAO {

    private static CountryDAO instance = null;

    protected final Context ctxApp;

    public static CountryDAO getInstance() {
        if (instance == null) {
            instance = new CountryDAO();
        }

        return instance;
    }

    protected final P2AGameDbHelper p2aDb;

    public CountryDAO() {
        ctxApp = P2AContext.getContext().getApplicationContext();
        p2aDb = P2AGameDbHelper.getInstance(ctxApp);
    }

    /**
     * @see com.cse.p2a.aseangame.data.dao.ICountryDAO#getCountryList()
     */
    @Override
    public List<Country> getCountryList() {
        List<Country> countries = new ArrayList<Country>();
        try {
            p2aDb.openDataBase();

            ArrayList<ArrayList<String>> records = p2aDb
                    .selectRecordsFromDBList(
                            CommonConsts
                                    .getSqlSelectAllRecordInTable(CountryEntryConsts.NAME_OF_TABLE),
                            null);

            countries = Country.loadCountryList(records);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return countries;
    }

    /**
     * @see com.cse.p2a.aseangame.data.dao.ICountryDAO#insertCountry(com.cse.p2a.aseangame.data.model.Country)
     */
    @Override
    public long insertCountry(Country newCountry) {

        long n = 0;
        final ContentValues values = new ContentValues();
        values.put(CountryEntryConsts.COL_COUNTRY_NAME,
                newCountry.get_country_name());
        Log.d(getClass().getCanonicalName(),
                "Performing insert country: " + newCountry.get_country_name());
        try {
            p2aDb.openDataBase();
            n = p2aDb.insertRecordsInDB(CountryEntryConsts.NAME_OF_TABLE, null,
                    values);

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return n;
    }

    @Override
    public int updateCountry(Country updatedCountry) {
        int affected = 0;
        final ContentValues values = new ContentValues();
        values.put(CountryEntryConsts._ID, updatedCountry.get_country_id());
        values.put(CountryEntryConsts.COL_COUNTRY_NAME,
                updatedCountry.get_country_name());
        Log.d(getClass().getCanonicalName(),
                "Performing update country: " + updatedCountry.get_country_id()
                        + ";" + updatedCountry.get_country_name());
        try {
            p2aDb.openDataBase();
            affected = p2aDb.updateRecordsInDB(
                    CountryEntryConsts.NAME_OF_TABLE, values,
                    CountryEntryConsts._ID + "=?",
                    new String[]{values.get(CountryEntryConsts._ID)
                            .toString()});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }
        return affected;
    }

    @Override
    public Country findCountryByName(String name) {
        final Country foundCountry = new Country();
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(CountryEntryConsts.NAME_OF_TABLE, new String[]{CountryEntryConsts._ID,
                    CountryEntryConsts.COL_COUNTRY_NAME}, CountryEntryConsts.COL_COUNTRY_NAME + "=?", new String[]{name},
                    null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            foundCountry.set_country_id(cursor.getInt(0)); // Get country id
            foundCountry.set_country_name(cursor.getString(1)); // Get country name
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return foundCountry;
    }

    @Override
    public Country findCountryById(String id) {
        final Country foundCountry = new Country();
        try {
            p2aDb.openDataBase();
            Cursor cursor = p2aDb.selectRecordsFromDB(CountryEntryConsts.NAME_OF_TABLE, new String[]{CountryEntryConsts._ID,
                    CountryEntryConsts.COL_COUNTRY_NAME}, CountryEntryConsts._ID + "=?", new String[]{id},
                    null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            foundCountry.set_country_id(cursor.getInt(0)); // Get country id
            foundCountry.set_country_name(cursor.getString(1)); // Get country name
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            p2aDb.close();
        }

        return foundCountry;

    }
}
