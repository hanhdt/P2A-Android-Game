/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.Country;

import java.util.List;

/**
 * @author Hanh D. TRAN
 */
public interface ICountryDAO {

    /**
     * @param newCountry
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    long insertCountry(Country newCountry);

    /**
     * Get all countries in the database
     *
     * @return country list
     */
    List<Country> getCountryList();

    int updateCountry(Country updatedCountry);

    Country findCountryByName(String name);

    Country findCountryById(String id);
}
