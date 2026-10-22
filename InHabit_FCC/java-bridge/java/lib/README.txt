Optional MySQL JDBC driver folder.

The Jugaad launcher also works in the original Java application's in-memory fallback mode.
If you want the Java web bridge to connect to MySQL, place the MySQL Connector/J .jar in this folder,
configure java/db.properties for your MySQL account/database, and ensure MySQL is running.
The launchers automatically include all .jar files in this folder on the Java classpath.

Official driver: https://dev.mysql.com/downloads/connector/j/
