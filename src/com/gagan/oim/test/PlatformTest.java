package com.gagan.oim.test;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gagan.config.Platform;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.platform.OIMClient;


public class PlatformTest {

    private static final Logger LOG = Logger.getLogger(PlatformTest.class.getName());

    private static String logMessage(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        return caller.getClassName() + "." + caller.getMethodName() + "() - " + message;
    }

    public static void main(String[] args) {
        LOG.info(logMessage("Starting main method"));

        testOIMConnection();
        testGetService();
        testDatabaseConnection();

        LOG.info(logMessage("Main method execution completed"));
    }

    private static void testOIMConnection() {
        OIMClient oimClient = null;
        try {
            oimClient = Platform.getOIMConnection();
            LOG.info(logMessage("OIM connection successful"));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, logMessage("Error while testing OIM connection"), e);
        } finally {
            if (oimClient != null) {
                try {
                    oimClient.logout();
                    LOG.info(logMessage("OIM connection closed"));
                } catch (Exception e) {
                    LOG.log(Level.WARNING, logMessage("Error during OIMClient logout"), e);
                }
            }
        }
    }

    private static void testGetService() {
        OIMClient oimClient = null;
        try {
            oimClient = Platform.getOIMConnection();
            UserManager userManager = Platform.getService(oimClient, UserManager.class);
            if (userManager != null) {
                LOG.info(logMessage("Successfully obtained UserManager service"));
            } else {
                LOG.warning(logMessage("Failed to obtain UserManager service"));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, logMessage("Error while testing getService"), e);
        } finally {
            if (oimClient != null) {
                try {
                    oimClient.logout();
                    LOG.info(logMessage("OIM connection closed"));
                } catch (Exception e) {
                    LOG.log(Level.WARNING, logMessage("Error during OIMClient logout"), e);
                }
            }
        }
    }

    private static void testDatabaseConnection() {
        Connection conn = null;
        try {
            conn = Platform.getDatabaseConnection();
            if (conn != null && !conn.isClosed()) {
                LOG.info(logMessage("Database connection successful"));
            } else {
                LOG.warning(logMessage("Failed to establish database connection"));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, logMessage("Error while testing database connection"), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    LOG.info(logMessage("Database connection closed"));
                } catch (Exception e) {
                    LOG.log(Level.WARNING, logMessage("Error closing database connection"), e);
                }
            }
        }
    }
}
