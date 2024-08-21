package com.gagan.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import oracle.iam.platform.OIMClient;

public class Platform {

    private static final Logger LOG = Logger.getLogger(Platform.class.getName());

    // Method to get the Operational Data Source (database connection)
    public static OperationalDS getOperationalDS() {
        return new OperationalDS();
    }

    // Method to get OIMClient connection
    public static OIMClient getOIMConnection() {
        LOG.info("Start :: getOIMConnection()");
        OIMClient oimClient = null;
        String authwlLocation = "#"; // Path to authwl.conf file
        String t3Url = "#"; // OIM server URL (e.g., "t3://localhost:14000")
        String username = "#"; // Username for OIM login
        String password = "#"; // Password for OIM login

        // Set system properties
        System.setProperty("java.security.auth.login.config", authwlLocation);
        System.setProperty("OIM.AppServerType", "wls");
        System.setProperty("APPSERVER_TYPE", "wls");

        Hashtable<String, String> oimenv = new Hashtable<>();
        oimenv.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
        oimenv.put(OIMClient.JAVA_NAMING_PROVIDER_URL, t3Url);

        try {
            oimClient = new OIMClient(oimenv);
            oimClient.login(username, password.toCharArray());
            LOG.info("OIMClient connected successfully.");
        } catch (LoginException e) {
            LOG.log(Level.SEVERE, "LoginException occurred while connecting to OIM: ", e);
        }

        LOG.info("End :: getOIMClientConnection()");
        return oimClient;
    }

    // Method to get the OIM service with a dynamic class parameter
    public static <T> T getService(Class<T> serviceClass) {
        OIMClient oimClient = getOIMConnection();
        if (oimClient != null) {
            try {
                return serviceClass.cast(oimClient.getService(serviceClass));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error obtaining service: ", e);
            }
        }
        return null;
    }

    // Simulates the Operational Data Source class

    public static class OperationalDS {

        // Method to get a database connection
        public Connection getConnection() {
            LOG.info("Start :: getConnection()");
            Connection connection = null;
            String url = "#"; // Database URL
            String username = "#"; // Database username
            String password = "#"; // Database password

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(url, username, password);
                LOG.log(Level.INFO, "Database Connected Successfully");
            } catch (ClassNotFoundException e) {
                LOG.log(Level.SEVERE, "Oracle JDBC Driver not found", e);
            } catch (SQLException e) {
                LOG.log(Level.SEVERE, "SQL Exception occurred while connecting to database", e);
            }

            LOG.info("End :: getConnection()");
            return connection;
        }
    }
}