package front_project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class TableFinder {
    private JPanel findPanel;
    private JTable dataTable;
    private JList listTables;
    private JButton findTable;
    private JTextField fieldToInserTable;
    private JLabel labelInsertTable;
    private JLabel tableList;
    private JLabel dataTableHeader;
    private JButton letsGoInsert;
    private String schemaName;

    public TableFinder(String schemaName) {
        this.schemaName = schemaName;
        ConnectionLibrary.creatConnection();

        fieldToInserTable.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInserTable.getText().isBlank());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInserTable.getText().isBlank());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInserTable.getText().isBlank());
            }
        });
        findTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectionLibrary.creatConnection();
                String query = "SELECT * FROM pg_tables WHERE pg_tables.schemaname = '" + schemaName + "'";
                JTable jTable = null;
                Set<String> tables = new HashSet<>();
                try {
                    jTable = ConnectionLibrary.selectData(query);
                    for (int i = 0; i < jTable.getRowCount(); i++) {
                        if ((jTable.getValueAt(i, 1).toString().contains(fieldToInserTable.getText()) || fieldToInserTable.getText().trim().equals("*")) && !jTable.getValueAt(i, 1).toString().trim().toLowerCase().equals(jTable.getTableHeader().getColumnModel().getColumn(1).getHeaderValue().toString())) {
                            tables.add(jTable.getValueAt(i, 1).toString());
                        }
                    }
                    listTables.setListData(tables.toArray());
                }
                catch (SQLException troubles) {
                    troubles.printStackTrace();
                }
            }
        });

        listTables.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                letsGoInsert.setEnabled(true);
                if (evt.getValueIsAdjusting()) { // Игнорируем событие mouseDown
                    // Получаем выбранное значение
                    String val = listTables.getSelectedValue().toString();
                    // Устанавливаем полученное значение в текстовое поле
                    String selectedRow = val;
                    String selectSelectedTable = "SELECT * FROM " + schemaName + "." + selectedRow + ";";
                    try {
                        JTable result = ConnectionLibrary.selectData(selectSelectedTable);
                        dataTable.setTableHeader(result.getTableHeader());
                        dataTable.setModel(result.getModel());
                        dataTable.setShowGrid(true);
                        dataTable.setFillsViewportHeight(true);
                        dataTable.setGridColor(Color.BLACK);
                    }
                    catch (SQLException troubles) {
                        troubles.printStackTrace();
                    }
                }
            }
        });
        letsGoInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Area4Inserting.mainMethod(listTables.getSelectedValue().toString(), schemaName);
            }
        });
    }

    public static void mainMethod(String schemaName) {
        JFrame findTableFrame = new JFrame("StartPage");
        findTableFrame.setContentPane(new TableFinder(schemaName).findPanel);
        findTableFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        findTableFrame.setLocation(128, 128);
        findTableFrame.setSize(1000, 1000);
        findTableFrame.setVisible(true);
    }
}
