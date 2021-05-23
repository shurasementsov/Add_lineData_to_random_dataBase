package front_project;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class StartPage {
    private JPanel panelMain;
    private JButton loadPathButton;
    private JLabel labelPath;
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
        if (schemaName.isBlank()) {
            ConnectionLibrary.creatConnection();
        }
        else{
            ConnectionLibrary.creatConnection(schemaName);
        }
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
            } else {
                continue;
            }
        }
        return result.toString();
    }

    public String ddlParser(String[] sqlDocument) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sqlDocument.length; i++) {
            if (sqlDocument[i].contains("COPY")) {
                int j = i;
                for (; !sqlDocument[j].contains("\\."); j++) {
                    continue;
                }
                i = j;
            } else {
                result.append(sqlDocument[i]);
                result.append("\n");
            }
        }
        return result.toString();
    }

    public StartPage() {

        nameDataBaseField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //перед тем, как открыть файл, надо создать объект для вызова диспетчера файлов
                //и вызвать отображение данного вызова
                JFileChooser file = new JFileChooser();
                responseLocation = file.showOpenDialog(null); //желательно сохранить положение курсора

                //теперь можем работать с файлами
                labelPath.setText(file.getSelectedFile().getAbsolutePath());
                String pathQuery = labelPath.getText();
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
                ddlReport.setText(ddlQuery);
                dmlReport.setText(dmlQuery);
                if (nameDataBaseField.getText() == null || nameDataBaseField.getText().equals(DEFAULT_INPUT_TEXT)) {
                    nameDataBaseField.setText("");
                }
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
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
