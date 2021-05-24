package front_project;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Area4Inserting extends JFrame {

    private JPanel areaInsertPanel;
    private static JFrame areaInserter;
    JButton button;
    Container container;


    /**
     * Процедура добавления кнопки в контейнер
     * @param container контейнер
     */
    private void addField(String name, Container container)
    {
        JTextField field = new JTextField();
        field.setName(name);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(field);
    }
    /**
     * Процедура добавления кнопки в контейнер
     * @param caption заголовок кнопки
     * @param container контейнер
     */
    private void addLabel(String caption, String name, Container container)
    {
        JLabel label = new JLabel(caption);
        label.setName(name);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
    }
    /**
     * Процедура создания интерфейса формы
     * @param container контейнер
     */
    public void createUI(Container container) {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        addLabel("Хлеб"        , "Bread", container);
        addLabel("Молоко"      , "Milk", container);
        addLabel("Колбасы"     , "Butches", container);
        addLabel("Мясо и птица", "Meals", container);
        addLabel("Мороженные продукты", "IceCream", container);
        JButton button = new JButton("перечислить");
        container.add(button);
    }

    private static final long serialVersionUID = 1L;
    public Area4Inserting(String selectedTable, String selectedSchema)
    {
        super("Интерфейсы кнопок");
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        // Устанавливаем последовательное расположение
        container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        String loadKeysInfo = "SELECT information_schema.key_column_usage.constraint_name as constraint_name, information_schema.key_column_usage.table_name as source_table, information_schema.key_column_usage.column_name as column_name, information_schema.constraint_column_usage.table_name as referenced_table ";
        loadKeysInfo += "FROM information_schema.key_column_usage, information_schema.constraint_column_usage ";
        loadKeysInfo += "WHERE information_schema.constraint_column_usage.constraint_name = information_schema.key_column_usage.constraint_name ";
        loadKeysInfo += "AND information_schema.key_column_usage.table_schema = '";
        loadKeysInfo += selectedSchema + "' ";
        loadKeysInfo += "GROUP BY information_schema.key_column_usage.constraint_name, information_schema.key_column_usage.table_name, information_schema.key_column_usage.column_name, information_schema.constraint_column_usage.table_name;";
        JTable keys = null;
        try {
            keys = ConnectionLibrary.selectData(loadKeysInfo);
        }
        catch (SQLException troubles) {
            troubles.printStackTrace();
        }
        String loadTableInfo = "SELECT column_name ";
        loadTableInfo += "FROM information_schema.columns ";
        loadTableInfo += "WHERE table_catalog = 'postgres' ";
        loadTableInfo += "AND table_schema = '" + selectedSchema + "' ";
        loadTableInfo += "AND table_name = '" + selectedTable + "';";
        JTable columns = null;
        try {
            columns = ConnectionLibrary.selectData(loadTableInfo);
        }
        catch (SQLException troubles) {
            troubles.printStackTrace();
        }
        if (columns != null) {
            for (int i = 1; i < columns.getRowCount(); i++) {
                addLabel(columns.getValueAt(i, 0).toString() + ":", columns.getValueAt(i, 0).toString() + "Label", container);
                addField(columns.getValueAt(i, 0).toString(), container);
            }
        }

        button = new JButton("перечислить");
        button.addActionListener(new ListenerAction());
        button.addChangeListener(new ListenerChange());
        container.add(button);
        container.add(button);
        // выводим окно на экран
        //setSize(400, 350);
        pack();
        setVisible(true);
    }
    class ListenerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            container.getComponents();
            System.out.println("Нажатие кнопки! От - "+
                    e.getActionCommand() + "\n");
        }
    }
    class ListenerChange implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            // Источник события
            Object src = e.getSource();
            System.out.println("Cообщение о смене состояния объекта : "
                    + src.getClass());
        }
    }
    public static void mainMethod(String s, String sch) {
        new Area4Inserting(s, sch);
    };
}
