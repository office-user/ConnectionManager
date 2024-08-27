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

    // Configuration constants - replace '#' with actual values
    private static final String AUTH_WL_LOCATION = "#";
    private static final String T3_URL = "#";
    private static final String OIM_USERNAME = "#";
    private static final String OIM_PASSWORD = "#";
    private static final String DB_URL = "#";
    private static final String DB_USERNAME = "#";
    private static final String DB_PASSWORD = "#";

    private Platform() {
    }

    private static String logMessage(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        return caller.getClassName() + "." + caller.getMethodName() + "() - " + message;
    }

    // Method to establish a connection with OIM (Oracle Identity Manager)
    public static OIMClient getOIMConnection() throws LoginException, Exception {
        LOG.info(logMessage("Start"));

        System.setProperty("java.security.auth.login.config", AUTH_WL_LOCATION);
        System.setProperty("OIM.AppServerType", "wls");
        System.setProperty("APPSERVER_TYPE", "wls");

        Hashtable<String, String> oimenv = new Hashtable<>();
        oimenv.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
        oimenv.put(OIMClient.JAVA_NAMING_PROVIDER_URL, T3_URL);

        OIMClient oimClient = new OIMClient(oimenv);
        oimClient.login(OIM_USERNAME, OIM_PASSWORD.toCharArray());
        LOG.info(logMessage("OIMClient connected successfully"));

        // LOG.info(logMessage("End"));
        return oimClient;
    }

    // Generic method to get an OIM service of a specified type
    public static <T> T getService(OIMClient oimClient, Class<T> serviceClass) {
        LOG.info(logMessage("Start"));
        if (oimClient != null) {
            try {
                T service = serviceClass.cast(oimClient.getService(serviceClass));
                LOG.info(logMessage("Service obtained successfully"));
                return service;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, logMessage("Error obtaining service"), e);
            }
        }
        // LOG.info(logMessage("End"));
        return null;
    }

    // Method to get a database connection
    public static Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        LOG.info(logMessage("Start"));

        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

        if (connection != null) {
            LOG.info(logMessage("Database Connected Successfully"));
        }

        // LOG.info(logMessage("End"));
        return connection;
    }
}