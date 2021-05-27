package front_project;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLibrary {

    private final static boolean DEFAULT_AUTO_COMMIT_OPTION = false;
    private final static String DEFAULT_CONNECT_REF = "jdbc:postgresql://localhost:5432/postgres";

    private static Connection c;
    private static Statement stmt;

    /** actual function to connect to database
     * @param autoCommit option to choose auto commit
     * @throws SQLException usual, when server isn't started */
    private static void connectDataBase(boolean autoCommit) throws SQLException {
        c = DriverManager
                .getConnection(DEFAULT_CONNECT_REF);
        c.setAutoCommit(autoCommit);
    }

    /** connect to dataBase with default options */
    public static void creatConnection() {
        try {
            connectDataBase(DEFAULT_AUTO_COMMIT_OPTION);
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    /** connect to database with input options
     * @param autoCommit option to choose auto commit */
    public static void creatConnection(boolean autoCommit) {
        try {
            connectDataBase(autoCommit);
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    /** if exception was called, this function completes transaction */
    public static void continueCreatingInserting() {
        try {
            stmt.close();
            c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** create table
     * @param ddlQuery query type of editing model of database
     * @throws SQLException usual, when previous transaction wasn't completed or table/relation/schema had been exists */
    public static void createTable(String ddlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.execute(ddlQuery);
        stmt.close();
        c.commit();
    }

    /** add new information
     * @param dmlQuery query type of editing data in the schema
     * @throws SQLException when data is exists, out of this relation or other constraint */
    public static void insertRows(String dmlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.executeUpdate(dmlQuery);
        stmt.close();
        c.commit();
    }

    /** update information
     * @param dmlQuery query type of editing data in the schema
     * @throws SQLException when data is exists, out of this relation or other constraint */
    public static void updateDeleteRows(String dmlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.executeUpdate(dmlQuery);
        c.commit();
        stmt.close();
    }

    /** show data from query
     * @param selectQuery query what find in the schema
     * @throws SQLException usual, when information schema isn't exists */
    public static JTable selectData(String selectQuery) throws SQLException {
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( selectQuery );

        int columnsCount = rs.getMetaData().getColumnCount();
        String[] header = new String[columnsCount];
        for (int i = 1; i <= columnsCount; i++) {
            header[i-1] = rs.getMetaData().getColumnName(i);
        }

        List<String[]> content = new ArrayList<>();
        String[] lineResult;
        lineResult = new String[columnsCount];
        for (int i = 0; i < columnsCount; i++) {
            lineResult[i] = "\s".repeat(2) + header[i].toUpperCase() + "\s".repeat(2);
        }
        content.add(lineResult);
        while(rs.next()) {
            lineResult = new String[columnsCount];
            for (int i = 1; i <= columnsCount; i++) {
                lineResult[i-1] = rs.getString(i);
            }
            content.add(lineResult);
        }

        rs.close();
        stmt.close();
        c.commit();

        return new JTable(content.toArray(new String[content.size()][columnsCount]), header);
    }

    /** close connection with database
     * @throws SQLException some process wasn't completed */
    public static void closeConnection() throws SQLException {
        c.close();
    }
}
