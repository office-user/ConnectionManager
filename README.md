---

# README

## Overview

This project demonstrates how to establish a connection to both a database and Oracle Identity Manager (OIM) using a custom `Platform` class. The `Platform` class provides methods to handle these connections dynamically, ensuring that the required configurations are managed efficiently.

### Key Components
- **Platform Class**: Handles database and OIM connections via `getOperationalDS().getConnection()` and `getService(Class<?> serviceClass)`.
- **OperationalDS Class**: Nested within the `Platform` class, this class simulates a data source and provides a method to establish a JDBC connection.
- **Main Class**: Demonstrates how to use the `Platform` class to establish and test both database and OIM connections.

## Structure

- **Platform Class**
  - `getOperationalDS()`: Returns an instance of `OperationalDS` for database connections.
  - `getService(Class<?> serviceClass)`: Returns an `OIMClient` instance based on the service class provided (e.g., `tcLookupOperationsIntf.class`).
  - `getOIMClientConnection(Class<?> serviceClass)`: Manages the connection to OIM using the provided service class.

- **OperationalDS Class**
  - `getConnection()`: Establishes and returns a JDBC connection.

- **Main Class**
  - Tests database and OIM connections using the `Platform` class.

## Usage

### Database Connection

The `Platform.getOperationalDS().getConnection()` method provides a connection to the database. The connection details, such as URL, username, and password, are defined in the `Platform` class. Modify these values as needed.

### OIMClient Connection

The `Platform.getService(Class<?> serviceClass)` method provides an `OIMClient` connection. Pass the required service class as a parameter, such as `tcLookupOperationsIntf.class`, to establish a connection to OIM.

### Example

```java
import java.sql.Connection;
import oracle.iam.platform.OIMClient;
import com.thortech.xl.dataobj.tcLookupOperationsIntf;

public class Main {
    public static void main(String[] args) {
        // Database connection test
        Connection dbConnection = Platform.getOperationalDS().getConnection();
        
        // OIMClient connection test
        OIMClient oimClient = Platform.getService(tcLookupOperationsIntf.class);
    }
}
```

### Dependencies

Ensure that the following dependencies are included in your project:

- **Oracle JDBC Driver**: For database connections.
- **Oracle Identity Manager API**: For interacting with OIM.

### Logging

Logging is implemented using the `java.util.logging.Logger` class, providing detailed information on the connection processes.

## Customization

To use this code in your environment:
1. Update the database connection details in the `getConnection()` method of the `OperationalDS` class.
2. Configure the OIM connection details in the `getOIMClientConnection()` method of the `Platform` class.
3. Modify the `Main` class as needed for your specific use cases.

## Conclusion

This project offers a flexible structure for managing both database and OIM connections. By centralizing connection logic in the `Platform` class, the code ensures easy maintenance and scalability.

---
