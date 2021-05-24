package front_project;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLibrary {

    private final static boolean DEFAULT_AUTO_COMMIT_OPTION = false;
    private final static String DEFAULT_CONNECT_REF = "jdbc:postgresql://localhost:5432";

    private static Connection c;
    private static Statement stmt;

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

    /** connect to database with input options */
    public static void creatConnection(boolean autoCommit) {
        try {
            connectDataBase(autoCommit);
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    /** create table */
    public static void createTable(String ddlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.executeUpdate(ddlQuery);
        stmt.close();
        c.commit();
    }

    /** ADD NEW INFORMATION (that's it) */
    public static void insertRows(String dmlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.executeUpdate(dmlQuery);
        stmt.close();
        c.commit();
    }

    /** update information */
    public static void updateDeleteRows(String dmlQuery) throws SQLException {
        stmt = c.createStatement();
        stmt.executeUpdate(dmlQuery);
        c.commit();
        stmt.close();
    }

    /** show data from query */
    public static JTable selectData(String selectQuery) throws SQLException {
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( selectQuery );

        int columnsCount = rs.getMetaData().getColumnCount();
        String[] header = new String[columnsCount];
        for (int i = 1; i <= columnsCount; i++) {
            header[i] = rs.getMetaData().getColumnName(i);
        }

        List<String[]> content = new ArrayList<>();
        String[] lineResult;
        while(rs.next()) {
            lineResult = new String[columnsCount];
            for (int i = 1; i <= columnsCount; i++) {
                lineResult[i] = rs.getString(i);
            }
            content.add(lineResult);
        }

        rs.close();
        stmt.close();
        c.commit();

        return new JTable(content.toArray(new String[content.size()][columnsCount]), header);
    }

    /** close connection with database */
    public static void closeConnection() throws SQLException {
        c.close();
    }
}
