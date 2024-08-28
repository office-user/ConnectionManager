package com.gagan.oim.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Logger;

import Thor.API.Operations.tcLookupOperationsIntf;
//import com.gagan.config.Platform;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.platform.Platform;

public class Main {

    // Store the class name in a variable for logging
    private static final String CLASS_NAME = Main.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);

    public static void main(String[] args) {
        String methodName = "main";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        // Test OIM Connection
        testOIMConnection();

        // Test getting a service (UserManager in this example)
        testGetService();

        // Test database connection
        testDatabaseConnection();

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
    }

    /**
     * Method to test the OIMClient connection using the Platform class.
     */
    private static void testOIMConnection() {
        String methodName = "testOIMConnection";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        // Attempt to retrieve the tcLookupOperationsIntf service
        try {
            tcLookupOperationsIntf lookupService = Platform.getService(tcLookupOperationsIntf.class);
            if (lookupService != null) {
                LOG.info(CLASS_NAME + "tcLookupOperationsIntf service obtained successfully.");
                // You can now use lookupService to perform operations
            } else {
                LOG.severe(CLASS_NAME + "Failed to obtain tcLookupOperationsIntf service.");
            }
        } catch (Exception e) {
            LOG.severe(CLASS_NAME + "Exception while obtaining tcLookupOperationsIntf service: " + e.getMessage());
        }

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
    }

    /**
     * Method to test getting a service using the Platform class.
     * In this example, we attempt to get the UserManager service.
     */
    private static void testGetService() {
        String methodName = "testGetService";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        // Attempt to retrieve the UserManager service
        try {
            UserManager userManagerService = Platform.getService(UserManager.class);
            if (userManagerService != null) {
                LOG.info(CLASS_NAME + "UserManager service obtained successfully.");
                // You can now use userManagerService to perform operations
            } else {
                LOG.severe(CLASS_NAME + "Failed to obtain UserManager service.");
            }
        } catch (Exception e) {
            LOG.severe(CLASS_NAME + "Exception while obtaining UserManager service: " + e.getMessage());
        }

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
    }

    /**
     * Method to test the database connection using the Platform class.
     */
    private static void testDatabaseConnection() {
        String methodName = "testDatabaseConnection";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        Connection connection = null;
        try {
            // Attempt to get a database connection
            connection = Platform.getOperationalDS().getConnection();
            if (connection != null) {
                LOG.info(CLASS_NAME + "Database connection established successfully.");

                // Example query to test the connection
                String query = "SELECT 1 FROM DUAL"; // Modify this query as needed
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    while (rs.next()) {
                        int result = rs.getInt(1);
                        LOG.info(CLASS_NAME + "Database test query result: " + result);
                    }
                } catch (SQLException e) {
                    LOG.severe(CLASS_NAME + "SQL Exception while executing test query: " + e.getMessage());
                }
            } else {
                LOG.severe(CLASS_NAME + "Failed to establish database connection.");
            }
        } catch (Exception e) {
            LOG.severe(CLASS_NAME + "SQL Exception while obtaining database connection: " + e.getMessage());
        } finally {
            // Ensure the connection is closed properly
            if (connection != null) {
                try {
                    connection.close();
                    LOG.info(CLASS_NAME + "Database connection closed successfully.");
                } catch (SQLException e) {
                    LOG.severe(CLASS_NAME + "SQL Exception while closing database connection: " + e.getMessage());
                }
            }
        }

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
    }
}
