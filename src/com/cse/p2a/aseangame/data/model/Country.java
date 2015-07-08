/**
 *
 */
package com.cse.p2a.aseangame.data.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Administrator
 */
@SuppressLint("ParcelCreator")
public class Country implements Parcelable {

    public static final String LOG_TAG = Country.class.getCanonicalName();

    private int _country_id;

    private String _country_name;

    public Country() {
    }


    public Country(int _country_id, String _country_name) {
        super();
        this._country_id = _country_id;
        this._country_name = _country_name;
    }

    /**
     * Called from the constructor to create this
     * object from a parcel.
     *
     * @param in
     */
    public Country(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        this._country_id = Integer.parseInt(data[0]);
        this._country_name = data[1];
    }


    public int get_country_id() {
        return _country_id;
    }


    public void set_country_id(int _country_id) {
        this._country_id = _country_id;
    }


    public String get_country_name() {
        return _country_name;
    }


    public void set_country_name(String _country_name) {
        this._country_name = _country_name;
    }

    /**
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * We just need to write each field into the
     * parcel. When we read from parcel, they
     * will come back in the same order
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeStringArray(new String[]{String.valueOf(this._country_id)
                , this._country_name});
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public static List<Country> loadCountryList(ArrayList<ArrayList<String>> records) {
        List<Country> countries = new ArrayList<Country>();
        Iterator<ArrayList<String>> itRecords = records.iterator();
        while (itRecords.hasNext()) {
            ArrayList<String> row = itRecords.next();
            Country country = new Country(Integer.parseInt(row.get(0)), row.get(1));
            countries.add(country);
        }
        return countries;
    }
}
