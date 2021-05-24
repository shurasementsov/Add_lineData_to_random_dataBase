package front_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartPage {
    private JPanel panelMain;
    private JButton loadPathButton;
    private JTextArea dmlReport;
    private JTextArea ddlReport;
    private JButton buttonToConnectDataBase;
    private JTextField nameDataBaseField;
    private JLabel statusLabel;
    private JLabel labelDataBaseNameAbout;

    private int responseLocation; //положение курсора
    private final String DEFAULT_INPUT_TEXT = "In default will 'public' or input in DDL";

    public void loadData(String dmlQuery) throws SQLException {
        ConnectionLibrary.insertRows(dmlQuery);
    }

    public void loadScheme(String ddlQuery) throws SQLException {
        ConnectionLibrary.createTable(ddlQuery);
    }

    public void dataRecovery(String schemaName, String ddlQuery, String dmlQuery) {
        ConnectionLibrary.creatConnection();
        try {
            loadScheme(ddlQuery);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Что-то пошло не так. Смотрите выше.");
        }
        try {
            loadData(dmlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Что-то пошло не так. Смотрите выше.");
        }
        try {
            ConnectionLibrary.closeConnection();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
        statusLabel.setText("данные успешно восстановились");
        System.out.println("данные успешно восстановились");
    }

    private List<String> modifyElements(String[] strings) {
        for(int i = 0; i < strings.length; i++) {
            strings[i] = "'" + strings[i] + "'";
        }
        return Arrays.asList(strings);
    }

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

    public StartPage() {

        buttonToConnectDataBase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //перед тем, как открыть файл, надо создать объект для вызова диспетчера файлов
                //и вызвать отображение данного вызова
                JFileChooser file = new JFileChooser();
                responseLocation = file.showOpenDialog(null); //желательно сохранить положение курсора

                //теперь можем работать с файлами
                String pathQuery = file.getSelectedFile().getAbsolutePath();
                StringBuilder query = new StringBuilder();
                String ddlQuery;
                String dmlQuery;
                try {
                    FileReader fileReader = new FileReader(pathQuery, StandardCharsets.UTF_8);
                    int c = 0;
                    while ((c = fileReader.read()) != -1) {
                        query.append((char) c);
                    }
                }
                catch (IOException whenOpenSQLFile) {
                    whenOpenSQLFile.printStackTrace();
                }
                ddlQuery = ddlParser(query.toString().split("\n"));
                dmlQuery = dmlParser(query.toString().split("\n"));
                if (nameDataBaseField.getText() == null || nameDataBaseField.getText().equals(DEFAULT_INPUT_TEXT)) {
                    nameDataBaseField.setText("");
                }
                else {
                    nameDataBaseField.setText(nameDataBaseField.getText().replaceAll("\s+", ""));
                    System.out.println(ddlQuery);
                    String oldSchemaName = ddlQuery.substring(ddlQuery.indexOf("CREATE SCHEMA ")).split("\n")[0];
                    oldSchemaName = oldSchemaName.replaceAll("CREATE SCHEMA ", "");
                    oldSchemaName = oldSchemaName.replaceAll(";", "").trim();
                    ddlQuery = ddlQuery.replaceAll(oldSchemaName, nameDataBaseField.getText());
                    dmlQuery = dmlQuery.replaceAll(oldSchemaName, nameDataBaseField.getText());
                }
                ddlReport.setText(ddlQuery);
                dmlReport.setText(dmlQuery);
                dataRecovery(nameDataBaseField.getText(), ddlQuery, dmlQuery);
                nameDataBaseField.setText(DEFAULT_INPUT_TEXT);
            }
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
        MetaData.tables = new TreeMap<>();
        JFrame mainFrame = new JFrame("StartPage");
        mainFrame.setContentPane(new StartPage().panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocation(220, 176);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);
    }
}
