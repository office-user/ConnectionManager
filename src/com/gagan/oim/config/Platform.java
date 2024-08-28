package com.gagan.oim.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import oracle.iam.platform.OIMClient;


/**
 * Platform class provides methods to establish database and OIMClient connections.
 * It also loads configuration properties from a file if available.
 */
public class Platform {

    // Logger instance for logging information and errors
    private static final Logger LOG = Logger.getLogger(Platform.class.getName());
    private static String CLASS_NAME = Platform.class.getName();

    // Default configuration values for database and OIM connections
    private static final String DEFAULT_DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String DEFAULT_DB_USERNAME = "admin";
    private static final String DEFAULT_DB_PASSWORD = "password";
    private static final String DEFAULT_AUTHWL_LOCATION = "path/to/authwl.conf";
    private static final String DEFAULT_T3_URL = "t3://localhost:14000";
    private static final String DEFAULT_OIM_USERNAME = "admin";
    private static final String DEFAULT_OIM_PASSWORD = "password";

    // Properties object to load configuration values from a properties file
    private static Properties properties = new Properties();

    // Static block to initialize properties from a file
    static {
        try (InputStream input = Platform.class.getClassLoader().getResourceAsStream("config.properties")) {
            // Load properties file if it exists
            if (input != null) {
                properties.load(input);
            } else {
                LOG.warning("config.properties file not found in classpath.");
            }
        } catch (Exception e) {
            // Log any errors encountered while loading properties
            LOG.log(Level.SEVERE, "Error loading properties file", e);
        }
    }

    /**
     * Retrieves the database connection settings from properties or uses default values.
     *
     * @param key          The property key to look up.
     * @param defaultValue The default value to return if the key is not found.
     * @return The property value or default value if the key is not present.
     */
    private static String getPropertyOrDefault(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Creates and returns a new instance of OperationalDS for database connections.
     *
     * @return A new OperationalDS instance.
     */
    public static OperationalDS getOperationalDS() {
        String methodName = "getOperationalDS";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        OperationalDS operationalDS = new OperationalDS();

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
        return operationalDS;
    }

    /**
     * Establishes and returns an OIMClient connection using configuration values from properties or defaults.
     *
     * @return The OIMClient instance if successful, otherwise null.
     */
    public static OIMClient getOIMConnection() {
        String methodName = "getOIMConnection";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        OIMClient oimClient = null;

        // Retrieve configuration values for OIM connection
        String authwlLocation = getPropertyOrDefault("authwl.location", DEFAULT_AUTHWL_LOCATION);
        String t3Url = getPropertyOrDefault("oim.server.url", DEFAULT_T3_URL);
        String username = getPropertyOrDefault("oim.username", DEFAULT_OIM_USERNAME);
        String password = getPropertyOrDefault("oim.password", DEFAULT_OIM_PASSWORD);

        // Set system properties required for OIM connection
        System.setProperty("java.security.auth.login.config", authwlLocation);
        System.setProperty("OIM.AppServerType", "wls");
        System.setProperty("APPSERVER_TYPE", "wls");

        // Set environment variables needed for OIMClient
        Hashtable<String, String> oimenv = new Hashtable<>();
        oimenv.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
        oimenv.put(OIMClient.JAVA_NAMING_PROVIDER_URL, t3Url);

        try {
            // Initialize OIMClient with the environment settings
            oimClient = new OIMClient(oimenv);
            // Attempt to login to the OIM server using the provided credentials
            oimClient.login(username, password.toCharArray());
            LOG.info("OIMClient connected successfully.");
        } catch (LoginException e) {
            // Log any login exceptions that occur
            LOG.log(Level.SEVERE, "LoginException occurred while connecting to OIM: ", e);
        }

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
        return oimClient;
    }

    /**
     * Retrieves a specific OIM service instance based on the service class provided.
     *
     * @param serviceClass The class of the OIM service to retrieve.
     * @param <T>          The type of the OIM service.
     * @return The service instance if successful, otherwise null.
     */
    public static <T> T getService(Class<T> serviceClass) {
        String methodName = "getService";
        LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

        OIMClient oimClient = getOIMConnection();
        T service = null;
        if (oimClient != null) {
            try {
                // Cast and return the service instance from OIMClient
                service = serviceClass.cast(oimClient.getService(serviceClass));
                LOG.info("Service obtained successfully for: " + serviceClass.getName());
            } catch (Exception e) {
                // Log any exceptions that occur while obtaining the service
                LOG.log(Level.SEVERE, "Error obtaining service: ", e);
            }
        } else {
            LOG.severe("OIMClient connection is null. Cannot obtain service.");
        }

        LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
        return service;
    }

    /**
     * Inner class to simulate Operational Data Source (database connection).
     */
    public static class OperationalDS {


        private static String CLASS_NAME = OperationalDS.class.getName();

        /**
         * Establishes and returns a database connection using configuration values from properties or defaults.
         *
         * @return The database connection if successful, otherwise null.
         */
        public Connection getConnection() {
            String methodName = "getConnection";
            LOG.info("Entering :: " + CLASS_NAME + "::" + methodName);

            Connection connection = null;

            // Retrieve database configuration values from properties or use defaults
            String url = getPropertyOrDefault("db.url", DEFAULT_DB_URL);
            String username = getPropertyOrDefault("db.username", DEFAULT_DB_USERNAME);
            String password = getPropertyOrDefault("db.password", DEFAULT_DB_PASSWORD);

            try {
                // Load the Oracle JDBC driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                // Establish the database connection using provided URL, username, and password
                connection = DriverManager.getConnection(url, username, password);
                LOG.info("Database Connected Successfully");
            } catch (ClassNotFoundException e) {
                // Log if the JDBC driver class is not found
                LOG.log(Level.SEVERE, "Oracle JDBC Driver not found", e);
            } catch (SQLException e) {
                // Log any SQL exceptions that occur during connection
                LOG.log(Level.SEVERE, "SQL Exception occurred while connecting to database", e);
            }

            LOG.info("Exiting :: " + CLASS_NAME + "::" + methodName);
            return connection;
        }
    }
}