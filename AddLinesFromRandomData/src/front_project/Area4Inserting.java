package front_project;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.sql.SQLException;

public class Area4Inserting extends JFrame {

    private JPanel areaInsertPanel;
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

    @Serial
    private static final long serialVersionUID = 1L;
    public Area4Inserting(String selectedTable, String selectedSchema)
    {
        super("Fill this areas");
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

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

        button = new JButton("insert data");
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
    }
}
