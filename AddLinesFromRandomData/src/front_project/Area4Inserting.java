package front_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Area4Inserting {

    private JPanel areaInsertPanel;
    private static JFrame areaInserter;



    /**
     * Процедура добавления кнопки в контейнер
     * @param caption заголовок кнопки
     * @param container контейнер
     */
    private static void addLabel(String caption, String name, Container container)
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
    public static void createUI(Container container) {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        addLabel("Хлеб"        , "Bread", container);
        addLabel("Молоко"      , "Milk", container);
        addLabel("Колбасы"     , "Butches", container);
        addLabel("Мясо и птица", "Meals", container);
        addLabel("Мороженные продукты", "IceCream", container);
        JButton button = new JButton("перечислить");
        container.add(button);
    }

    public static void mainMethod() {
        areaInserter = new JFrame("Insert values");
        areaInserter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Определение интерфейса окна
        createUI(areaInserter.getContentPane());

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Открытие окна
                areaInserter.pack();
                areaInserter.setVisible(true);
            }
        });
    }
}
