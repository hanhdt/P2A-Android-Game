/**
 *
 */
package com.cse.p2a.aseangame.data.dao;

import com.cse.p2a.aseangame.data.model.Category;

import java.util.List;

/**
 * @author DHanh
 */
public interface ICategoryDAO {
    /**
     * Get all categories in the database
     *
     * @return country list
     */
    List<Category> getCategoryList();
}
