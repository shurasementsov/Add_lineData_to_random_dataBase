package front_project;

import java.awt.*;

public class AttributeField {
    private String data;
    private final String NAME;
    private final boolean NULLABLE;
    private final boolean IS_VARCHAR;

    public AttributeField (String data, String name) {
        this.data = data;
        this.NAME = name;
        this.NULLABLE = true;
        this.IS_VARCHAR = true;
    }

    public AttributeField (String data, String name, boolean is_varchar) {
        this.data = data;
        this.NAME = name;
        this.IS_VARCHAR = is_varchar;
        this.NULLABLE = true;
    }

    public AttributeField (String data, String name, boolean is_varchar, boolean nullable) {
        this.data = data;
        this.NAME = name;
        this.IS_VARCHAR = is_varchar;
        this.NULLABLE = nullable;
    }

    public void addToContainer(Container container)
}
