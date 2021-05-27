package front_project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TableFinder {
    private JPanel findPanel;
    private JTable dataTable;
    private JList<String> listTables;
    private JButton findTable;
    private JTextField fieldToInsertTable;
    private JLabel labelInsertTable;
    private JLabel tableList;
    private JLabel dataTableHeader;
    private JButton letsGoInsert;

    public TableFinder(String schemaName) {
        ConnectionLibrary.creatConnection();

        fieldToInsertTable.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInsertTable.getText().isBlank());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInsertTable.getText().isBlank());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                findTable.setEnabled(!fieldToInsertTable.getText().isBlank());
            }
        });
        findTable.addActionListener(e -> {
            ConnectionLibrary.creatConnection();
            String query = "SELECT * FROM pg_tables WHERE pg_tables.schemaname = '" + schemaName + "'";
            JTable jTable;
            Set<String> tables = new HashSet<>();
            try {
                jTable = ConnectionLibrary.selectData(query);
                for (int i = 0; i < jTable.getRowCount(); i++) {
                    if ((jTable.getValueAt(i, 1).toString().contains(fieldToInsertTable.getText()) || fieldToInsertTable.getText().trim().equals("*")) && !jTable.getValueAt(i, 1).toString().trim().toLowerCase().equals(jTable.getTableHeader().getColumnModel().getColumn(1).getHeaderValue().toString())) {
                        tables.add(jTable.getValueAt(i, 1).toString());
                    }
                }
                listTables.setListData(tables.toArray(new String[tables.size()]));
            }
            catch (SQLException troubles) {
                troubles.printStackTrace();
            }
        });

        listTables.addListSelectionListener(evt -> {
            letsGoInsert.setEnabled(true);
            if (evt.getValueIsAdjusting()) {
                String selectedRow = listTables.getSelectedValue();
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
        });
        letsGoInsert.addActionListener(e -> Area4Inserting.mainMethod(listTables.getSelectedValue(), schemaName));
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
