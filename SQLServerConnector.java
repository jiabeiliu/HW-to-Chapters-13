package hwtochapters13;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLServerConnector {
    public static void main(String[] args) {
        // Jing Cao's database connection details
        String serverName = "boyce.coe.neu.edu";
        String database = "Lab4_JingCao"; 
        String username = "INFO6210";
        String password = "NEUHusky!";
        String url = "jdbc:sqlserver://" + serverName + ";databaseName=" + database + ";trustServerCertificate=true";

        // Load the JDBC driver class
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {    
            System.out.println("JDBC driver not found.");
            e.printStackTrace();
            return; // Exit if there is no JDBC driver found
        }

        // Establish a connection to the database
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false); // Start transaction
            System.out.println("Connected to the SQL Server database successfully.");
            
            // Correct the SQL to reference the correct column name for advisorlastname
            String sql = "SELECT * FROM Lab4_JingCao.dbo.Advisor";
            try (PreparedStatement p = connection.prepareStatement(sql);
                 ResultSet rs = p.executeQuery()) {
                System.out.println("Original Table:");
                System.out.println("advisorid\t\tadvisorlastname\t\tadvisorfirstname");
                while (rs.next()) {
                    int advisorid = rs.getInt("advisorid");
                    String advisorlastname = rs.getString("advisorlastname"); // Corrected column name
                    String advisorfirstname = rs.getString("advisorfirstname");
                    System.out.println(advisorid + "\t\t" + advisorlastname + "\t\t" + advisorfirstname);
                }
            }

            // Update operation with correct column name and within a transaction
            String updateSql = "UPDATE Lab4_JingCao.dbo.Advisor SET advisorlastname='Snow' WHERE advisorid=1";
            try (PreparedStatement p2 = connection.prepareStatement(updateSql)) {
                p2.executeUpdate();
                connection.commit(); // Commit the update
                System.out.println("Advisor lastname updated successfully.");
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                throw e;
            }

            // Optionally, re-query and print the table to confirm the update,
            // then rollback the change for demonstration purposes
            try (PreparedStatement p = connection.prepareStatement(sql);
                 ResultSet rs = p.executeQuery()) {
                System.out.println("Modified Table:");
                while (rs.next()) {
                    int advisorid = rs.getInt("advisorid");
                    String advisorlastname = rs.getString("advisorlastname"); // Corrected column name
                    String advisorfirstname = rs.getString("advisorfirstname");
                    System.out.println(advisorid + "\t\t" + advisorlastname + "\t\t" + advisorfirstname);
                }
            }
            
            // Rollback changes to restore original data
            connection.rollback();
            System.out.println("Changes rolled back to maintain original database state.");

        } catch (SQLException e) {
            System.out.println("Database operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

