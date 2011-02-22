package org.rosuda.domain.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DebugUtil {

	public static void debugSchema(final DataSource dataSource) {
		try {
			System.out.println("-----------------------");
			System.out.println("--- Database Tables ---");
			System.out.println("-----------------------");
			final Connection con = dataSource.getConnection();
			final DatabaseMetaData metaData = con.getMetaData();
			ResultSet allTables = metaData.getTables(null,null,null, new String[]{ "TABLE" });
			while(allTables.next()) {
				String table_name = allTables.getString("TABLE_NAME");
				//System.out.println("Table Name: " + table_name);
				//System.out.println("Table Type: " + allTables.getString("TABLE_TYPE"));
				//System.out.println("Indexes: ");
				// Get a list of all the indexes for this table
				ResultSet indexList = metaData.getColumns(null,null,table_name,null);
				while(indexList.next()) {
					System.out.println(table_name+"."+indexList.getString("COLUMN_NAME"));
					//System.out.println(" Index Name: "+indexList.getString("INDEX_NAME"));
					//System.out.println(" Column Name:"+indexList.getString("COLUMN_NAME"));
				}
				indexList.close(); 
			}
			allTables.close();
			System.out.println("-----------------------");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
