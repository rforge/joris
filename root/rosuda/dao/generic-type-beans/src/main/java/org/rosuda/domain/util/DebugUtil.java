package org.rosuda.domain.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugUtil.class);

    public static void debugSchema(final DataSource dataSource) {
	try {
	    LOGGER.info("-----------------------");
	    LOGGER.info("--- Database Tables ---");
	    LOGGER.info("-----------------------");
	    final Connection con = dataSource.getConnection();
	    final DatabaseMetaData metaData = con.getMetaData();
	    ResultSet allTables = metaData.getTables(null, null, null, new String[] { "TABLE" });
	    while (allTables.next()) {
		String table_name = allTables.getString("TABLE_NAME");
		LOGGER.debug("Table Name: " + table_name);
		LOGGER.debug("Table Type: " + allTables.getString("TABLE_TYPE"));
		LOGGER.debug("Indexes: ");
		// Get a list of all the indexes for this table
		ResultSet indexList = metaData.getColumns(null, null, table_name, null);
		while (indexList.next()) {
		    LOGGER.info(table_name + "." + indexList.getString("COLUMN_NAME"));
		    LOGGER.debug(" Index Name: " + indexList.getString("INDEX_NAME"));
		    LOGGER.debug(" Column Name:"+indexList.getString("COLUMN_NAME"));
		}
		indexList.close();
	    }
	    allTables.close();
	    LOGGER.info("-----------------------");
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

}
