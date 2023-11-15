package com.java.kaggle_project.db;

import com.java.kaggle_project.common.KaggleCommon;

import java.sql.*;
import java.util.Map;

public class InsertDatasetInfo {
    public void insertDatasetInfoIntoAzure(String connection_url, String azure_username, String azure_password,
                                           Map<String,String> userMap) throws SQLException,NullPointerException {
        Connection connection = null;
        PreparedStatement insertPreparedStatement=null;
        PreparedStatement maxStatement=null;
        int dataset_id = 0;
        try {
            // Connect to azure sql database.

            connection = DriverManager.getConnection(connection_url, azure_username, azure_password);
            try {
                maxStatement = connection.prepareStatement("SELECT max(dataset_id) as m FROM dbo.kaggle;");
                ResultSet maxDataset = maxStatement.executeQuery();
                while (maxDataset.next()) {
                    dataset_id = maxDataset.getInt("m") + 1;
                }
            }
            finally{
                if (maxStatement != null) {
                    maxStatement.close();
                }
            }
            try {
                // Insert the dataset info to azure sql database.
                String insertStatement = "insert into dbo.kaggle values(" + dataset_id + ",'" + userMap.get(KaggleCommon.DATASET_NAME) + "','"
                        + userMap.get(KaggleCommon.URL) + "','" + userMap.get(KaggleCommon.CATEGORY) + "','"
                        + userMap.get(KaggleCommon.DATASET_LOCATION) + "')";
                insertPreparedStatement = connection.prepareStatement(insertStatement);
                insertPreparedStatement.executeUpdate();
            }
            finally{
                if (insertPreparedStatement != null) {
                    insertPreparedStatement.close();
                }
            }
        } catch (Exception e) {
            //
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}