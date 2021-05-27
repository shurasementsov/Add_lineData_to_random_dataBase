package front_project;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AttributeField {
    private final String NAME;
    private final boolean NULLABLE;
    private final boolean IS_VARCHAR;
    private JTextField fieldAttribute;
    private final String numericAlphabet = "[-+]?\\d*\\.?\\d+";

    private void initTextField(boolean is_varchar, boolean nullable) {
        this.fieldAttribute = new JTextField();
        fieldAttribute.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (is_varchar) {
                    if (fieldAttribute.getText().charAt(0) != '\'') {
                        fieldAttribute.setText("'" + fieldAttribute.getText());
                    }
                    if (fieldAttribute.getText().charAt(fieldAttribute.getText().length() - 1) != '\'') {
                        fieldAttribute.setText(fieldAttribute.getText() + "'");
                    }
                    if (nullable && fieldAttribute.getText().substring(1, fieldAttribute.getText().length() - 1).isBlank()) {
                        // disable button
                    } else {
                        //enable button
                    }
                } else if (!fieldAttribute.getText().matches(numericAlphabet)) {
                    fieldAttribute.setToolTipText("it isn't number");
                    // disable button
                } else if (nullable && fieldAttribute.getText().isBlank()) {
                    // disable button
                } else {
                    //enable button
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void initAttributeField(boolean is_varchar, boolean nullable) {
        ToolTipManager.sharedInstance().setDismissDelay(1500);
        initTextField(is_varchar, nullable);
    }

    public AttributeField (String name) {

        //эти действия необходимо прописывать в конструктуре
        this.NAME = name;
        this.NULLABLE = true;
        this.IS_VARCHAR = true;
    }

    public AttributeField (String name, boolean is_varchar) {
        this.NAME = name;
        this.IS_VARCHAR = is_varchar;
        this.NULLABLE = true;
    }

    public AttributeField (String name, boolean is_varchar, boolean nullable) {
        this.NAME = name;
        this.IS_VARCHAR = is_varchar;
        this.NULLABLE = nullable;
    }
}
