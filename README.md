---

# Oracle Identity Manager (OIM) and Database Integration

## Overview

This project provides a streamlined solution for integrating with Oracle Identity Manager (OIM) and a relational database using Java. The goal is to simplify the process of connecting to these systems both during local development and in production environments.

### Why This Project Was Created

In software development, connecting to databases and identity management systems can be repetitive and tedious. Each project often requires different connection settings, leading to redundant code. This project addresses this issue by providing a reusable JAR file (`Platform.jar`) that simplifies connection management.

#### Key Benefits:
- **Local Development**: Use the JAR file to easily connect to your local database and OIM server without repeatedly writing connection code.
- **OIM Deployment**: When deploying to an OIM server, use the same JAR file but with adjusted import statements to ensure smooth integration.

## Project Structure

```
src/
├── com/
│   └── gagan/
│       ├── oim/
│       │   ├── config/
│       │   │   └── Platform.java
│       │   └── main/
│       │       └── Main.java
└── README.md
```

- **`Platform.java`**: Contains methods for connecting to OIM and the database.
- **`Main.java`**: Demonstrates how to use `Platform` methods.

## Prerequisites

1. **JDK 8**: Ensure JDK 8 is installed. Verify with:
   ```bash
   java -version
   ```

2. **Oracle Identity Manager (OIM)**: Access credentials and understanding of OIM are required.

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine:
```bash
git clone https://github.com/your-repository.git
```

### 2. Configure Properties

Open `Platform.java` and replace placeholder values (`#`) with actual values:
```bash
// Replace placeholders with actual values
String authwlLocation = "/path/to/authwl.conf";  // Path to authwl.conf file
String t3Url = "t3://oim-server:14000";           // OIM server URL
String username = "your-oim-username";            // OIM username
String password = "your-oim-password";            // OIM password
String dbUrl = "jdbc:oracle:thin:@//db-host:1521/service-name"; // Database URL
String dbUsername = "your-db-username";           // Database username
String dbPassword = "your-db-password";           // Database password

```

### 3. Compile and Create JAR File

#### Compile the `Platform` Class

Navigate to the project directory and compile:
```bash
javac -d bin src/com/gagan/oim/config/Platform.java
```

#### Create the JAR File

Package the compiled class into a JAR file:
```bash
jar cvf Platform.jar -C bin com/gagan/oim/config/Platform.class
```

#### Verify the JAR File

Ensure the JAR file contains the `Platform` class:
```bash
jar tf Platform.jar
```

### 4. Use the JAR File in Your Project

#### Add the JAR to Your Project

Include `Platform.jar` in your project's classpath. In your IDE, add it via project properties or build path configuration.

## Use Cases

Here are examples of how to use the `Platform` class for various tasks:

### 1. Establishing an OIM Connection

```java
import com.gagan.oim.config.Platform;
import oracle.iam.platform.OIMClient;
import java.util.logging.Logger;

public class OIMConnectionExample {
    private static final Logger LOG = Logger.getLogger(OIMConnectionExample.class.getName());

    public void establishConnection() {
        LOG.info("Entering :: OIMConnectionExample::establishConnection");

        // Establish OIM connection
        OIMClient oimClient = Platform.getOIMConnection();
        if (oimClient != null) {
            LOG.info("OIMClient connected successfully.");
            // Use the oimClient for operations

            // Don't forget to close the connection when done
            oimClient.logout();
            LOG.info("OIMClient connection closed.");
        } else {
            LOG.severe("Failed to establish OIM connection.");
        }

        LOG.info("Exiting :: OIMConnectionExample::establishConnection");
    }
}
```

### 2. Retrieving an OIM Service

```java
import com.gagan.oim.config.Platform;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.platform.OIMClient;
import java.util.logging.Logger;

public class OIMServiceExample {
    private static final Logger LOG = Logger.getLogger(OIMServiceExample.class.getName());

    public void retrieveService() {
        LOG.info("Entering :: OIMServiceExample::retrieveService");

        // Retrieve UserManager service
        OIMClient oimClient = Platform.getOIMConnection();
        if (oimClient != null) {
            UserManager userManager = Platform.getService(oimClient, UserManager.class);
            if (userManager != null) {
                LOG.info("UserManager service obtained successfully.");
                // Use the userManager for operations

                // Don't forget to close the OIM connection when done
                oimClient.logout();
                LOG.info("OIMClient connection closed.");
            } else {
                LOG.severe("Failed to obtain UserManager service.");
            }
        } else {
            LOG.severe("Failed to establish OIM connection.");
        }

        LOG.info("Exiting :: OIMServiceExample::retrieveService");
    }
}
```

### 3. Creating a Database Connection

```java
import com.gagan.oim.config.Platform;
import java.sql.Connection;
import java.util.logging.Logger;

public class DatabaseConnectionExample {
    private static final Logger LOG = Logger.getLogger(DatabaseConnectionExample.class.getName());

    public void createConnection() {
        LOG.info("Entering :: DatabaseConnectionExample::createConnection");

        // Create database connection
        Connection conn = Platform.getDatabaseConnection();
        if (conn != null) {
            LOG.info("Database connection established successfully.");
            // Use the connection for operations

            // Don't forget to close the connection when done
            try {
                conn.close();
                LOG.info("Database connection closed.");
            } catch (Exception e) {
                LOG.severe("Exception while closing database connection: " + e.getMessage());
            }
        } else {
            LOG.severe("Failed to establish database connection.");
        }

        LOG.info("Exiting :: DatabaseConnectionExample::createConnection");
    }
}
```

## Deployment in Oracle Identity Manager (OIM)

When deploying in OIM:

1. **Update Import Statement**:

   Replace:
   ```java
   import com.gagan.oim.config.Platform;
   ```
   With:
   ```java
   import oracle.iam.platform.Platform;
   ```

   This ensures compatibility with the OIM environment.

## Explanation of the Code

### `Platform` Class

#### 1. **`getOperationalDS` Method**
- **Purpose**: Provides an instance of the `OperationalDS` class to manage database connections.
- **Usage**: Call this method to get a database connection.

#### 2. **`getOIMConnection` Method**
- **Purpose**: Connects to the OIM server using configured properties.
- **Configuration**: Set `authwlLocation`, `t3Url`, `username`, and `password` before calling this method.
- **Exception Handling**: Handles `LoginException` if login fails.

#### 3. **`getService` Method**
- **Purpose**: Retrieves a specified OIM service (e.g., `UserManager`).
- **Generic Usage**: Use this method with a class type to get the corresponding OIM service.

#### 4. **`OperationalDS` Class**
- **`getConnection` Method**:
    - **Purpose**: Provides a connection to the database.
    - **Configuration**: Set `dbUrl`, `dbUsername`, and `dbPassword`.
    - **Exception Handling**: Handles `ClassNotFoundException` for missing JDBC drivers and `SQLException` for database errors.

### `Main` Class

#### 1. **`main` Method**
- **Purpose**: Demonstrates how to use `Platform` methods for local and OIM server environments.
- **Features**:
    - **Test OIM Connection**: Calls `Platform.getOIMConnection()` to establish an OIM connection.
    - **Test Service Retrieval**: Calls `Platform.getService(tcLookupOperationsIntf.class)` to get an OIM service.
    - **Test Database Connection**: Uses `Platform.getOperationalDS().getConnection()` to connect to the database and run a test query.

**Logging**: Detailed log entries track method entries, exits, and issues.

## Common Doubts and Troubleshooting

1. **What if the `Platform` class is not found?**
    - Ensure the JAR file is included in your project's classpath. Verify import statements and classpath settings.

2. **What if the database or OIM connection fails?**
    - Double-check credentials and URLs in `Platform.java`. Ensure that the database and OIM servers are reachable.

3. **How do I handle exceptions?**
    - The code logs exceptions with detailed messages. Check logs for specific error details and resolve them accordingly.

4. **How do I ensure compatibility with OIM?**
    - Update import statements for OIM

deployment to use the OIM-provided `Platform` class.

## Creating and Using the JAR File

### Why Create a JAR File?

The `Platform` class is designed to be reusable in different environments. By creating a JAR file, you encapsulate all necessary code into a single, distributable unit. This allows for:

1. **Consistent Usage**: Use the same JAR file in both local development and production environments.
2. **Easy Testing**: The JAR file helps in testing connection code locally without integrating directly into the OIM server.

### How to Create and Use the JAR File

1. **Compile the Class**: Convert `Platform.java` into bytecode.
2. **Package into JAR**: Bundle the bytecode into a JAR file (`Platform.jar`).
3. **Include in Project**: Add the JAR file to your project's classpath.
4. **Adjust Imports for Deployment**: For deployment in OIM, update the import statement to the OIM-provided package.

**Note**: You **do not need to deploy the JAR file** to production or development environments. It is only used locally for testing and development purposes. When deploying your application to OIM, ensure that you use the OIM-specific package by updating the import statements as described.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
