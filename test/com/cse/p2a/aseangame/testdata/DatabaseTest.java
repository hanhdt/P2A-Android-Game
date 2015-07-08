/**
 *
 */
package com.cse.p2a.aseangame.testdata;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;

import java.util.List;

/**
 * @author Administrator
 */
public class DatabaseTest extends AndroidTestCase {

    private P2AGameDbHelper myDb;

    @Override
    protected void setUp() throws Exception {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        myDb = new P2AGameDbHelper(context);
    }

    public void testGetTables() {
        List<String> tables = myDb.getAllEntries();
        assertEquals(10, tables.size());
    }

    @Override
    protected void tearDown() throws Exception {
        myDb.close();
        super.tearDown();
    }
}
