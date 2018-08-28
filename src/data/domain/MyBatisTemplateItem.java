package data.domain;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author boyce - 2018/3/16.
 */
public class MyBatisTemplateItem {
    private SimpleBooleanProperty selected;
    private SimpleStringProperty name;

    public MyBatisTemplateItem(boolean selected, String name) {
        this.name = new SimpleStringProperty(name);
        this.selected = new SimpleBooleanProperty(selected);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
}
