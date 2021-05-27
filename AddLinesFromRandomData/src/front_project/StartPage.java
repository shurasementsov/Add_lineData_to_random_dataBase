package front_project;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class StartPage {
    private JPanel panelMain;
    private JTextArea dmlReport;
    private JTextArea ddlReport;
    private JButton buttonToConnectDataBase;
    private JTextField nameDataBaseField;
    private JLabel statusLabel;
    private JLabel labelDataBaseNameAbout;

    private final String DEFAULT_INPUT_TEXT = "In default will 'public' or input in DDL";

    private String dmlQuery, ddlQuery;

    /** release DML-query */
    public void loadData(String dmlQuery) throws SQLException {
        ConnectionLibrary.insertRows(dmlQuery);
    }

    /** release DDL-query */
    public void loadScheme(String ddlQuery) throws SQLException {
        ConnectionLibrary.createTable(ddlQuery);
    }

    /** recovery information
     * @param ddlQuery query editing model of database
     * @param dmlQuery query editing data in database*/
    public void dataRecovery(String ddlQuery, String dmlQuery) {
        ConnectionLibrary.creatConnection();
        try {
            loadScheme(ddlQuery);
        }
        catch (SQLException e) {
            if (!e.getSQLState().equals("42P06")) {
                e.printStackTrace();
                System.out.println("Что-то пошло не так. Смотрите выше.");
                return;
            } else {
                ConnectionLibrary.continueCreatingInserting();
            }
        }
        try {
            loadData(dmlQuery);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("23505")) {
                e.printStackTrace();
                System.out.println("Что-то пошло не так. Смотрите выше.");
                return;
            } else {
                ConnectionLibrary.continueCreatingInserting();
            }
        }
        try {
            ConnectionLibrary.closeConnection();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
            return;
        }
        statusLabel.setText("данные успешно восстановились");
        System.out.println("данные успешно восстановились");
        TableFinder.mainMethod(nameDataBaseField.getText());
    }

    /** special method adding in tips apostrophe
     * @param strings array of words/sentence */
    private List<String> modifyElements(String[] strings) {
        for(int i = 0; i < strings.length; i++) {
            strings[i] = "'" + strings[i] + "'";
        }
        return Arrays.asList(strings);
    }

    /** parsing information for saving queries with editing data in database
     * @param sqlDocument source query */
    public String dmlParser(String[] sqlDocument) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sqlDocument.length; i++) {
            if (sqlDocument[i].contains("COPY")) {
                sqlDocument[i] = sqlDocument[i].replaceAll("COPY", "INSERT INTO");
                sqlDocument[i] = sqlDocument[i++].replaceAll("FROM stdin;", "VALUES");

                int j = i;
                List<String> valuesForInsert = new ArrayList<>();
                if (!sqlDocument[j].contains("\\.")){
                    result.append(sqlDocument[i - 1]).append("\n");
                    for (; !sqlDocument[j].contains("\\."); j++) {
                        sqlDocument[j] = String.join(", ", modifyElements(sqlDocument[j].split("\t")));
                        valuesForInsert.add("(" + sqlDocument[j] + ")");
                    }
                }
                result.append(String.join(", \n", valuesForInsert));
                result.append(";\n\n");
                i = j;
            }
        }
        return result.toString();
    }

    /** parsing information for saving queries with editing model of database
     * @param sqlDocument source query */
    public String ddlParser(String[] sqlDocument) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sqlDocument.length; i++) {
            if (sqlDocument[i].contains("COPY")) {
                for (int j = i; !sqlDocument[j].contains("\\."); j++) {
                    i = j;
                }
                i++;
            } else {
                result.append(sqlDocument[i]);
                result.append("\n");
            }
        }
        return result.toString();
    }

    /** rename name of schema to new name
     * @param newSchemaName new name of schema */
    private void renameSchema(String oldSchemaName, String newSchemaName) {
        newSchemaName = newSchemaName.replaceAll("\s+", "");
        ddlQuery = ddlQuery.replaceAll(oldSchemaName, newSchemaName);
        dmlQuery = dmlQuery.replaceAll(oldSchemaName, newSchemaName);
    }

    /** get current schema name */
    private String getCurrentSchemaName() {
        int positionSchemaName = ddlQuery.indexOf("CREATE SCHEMA ");
        if (positionSchemaName != -1){
            String currentSchemaName = ddlQuery.substring(positionSchemaName).split("\n")[0];
            currentSchemaName = currentSchemaName.replaceAll("CREATE SCHEMA ", "");
            return currentSchemaName.replaceAll(";", "").trim();
        } else {
            final String publicConst = "public";
            ddlQuery = "CREATE SCHEMA '" + publicConst + "'\n" + ddlQuery;
            return publicConst;
        }
    }

    /** load script with backup information */
    private String loadQuery(String filePath) throws IOException {
        StringBuilder futureQuery = new StringBuilder();
        FileReader fileReader = new FileReader(filePath, StandardCharsets.UTF_8);
        int c;
        while ((c = fileReader.read()) != -1) {
            futureQuery.append((char) c);
        }
        return futureQuery.toString();
    }

    /** to reveal file manager to select path of issue file */
    private String findPathToBackupFile() {
        //перед тем, как открыть файл, надо создать объект для вызова диспетчера файлов
        //и вызвать отображение данного вызова
        JFileChooser file = new JFileChooser();
        file.showOpenDialog(null);

        //теперь можем работать с файлами
        return file.getSelectedFile().getAbsolutePath();
    }

    public StartPage() {

        buttonToConnectDataBase.addActionListener(e -> {
            String pathQuery = findPathToBackupFile();
            String query = "";
            try {
                query = loadQuery(pathQuery);
            }
            catch (IOException troubles) {
                troubles.printStackTrace();
            }
            ddlQuery = ddlParser(query.split("\n"));
            dmlQuery = dmlParser(query.split("\n"));
            String currentSchemaName = getCurrentSchemaName();
            if (nameDataBaseField.getText().isBlank() || nameDataBaseField.getText().equals(DEFAULT_INPUT_TEXT)) {
                nameDataBaseField.setText("");
            }
            else {
                renameSchema(currentSchemaName, nameDataBaseField.getText());
            }
            ddlReport.setText(ddlQuery);
            dmlReport.setText(dmlQuery);
            nameDataBaseField.setText(currentSchemaName);
            dataRecovery(ddlQuery, dmlQuery);
        });
        nameDataBaseField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (nameDataBaseField.getText().equals(DEFAULT_INPUT_TEXT)) {
                    nameDataBaseField.setText("");
                }
            }
        });
    }

    public static void main (String[] args) {
        JFrame mainFrame = new JFrame("StartPage");
        mainFrame.setContentPane(new StartPage().panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocation(220, 176);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);
    }
}
