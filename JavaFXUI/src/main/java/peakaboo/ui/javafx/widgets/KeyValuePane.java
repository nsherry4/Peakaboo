package peakaboo.ui.javafx.widgets;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


public class KeyValuePane extends GridPane {

    private Map<String, Object> properties = new LinkedHashMap<>();
    private SimpleStringProperty keyStyle = new SimpleStringProperty("-fx-text-fill: #999999;");
    private SimpleStringProperty valueStyle = new SimpleStringProperty("-fx-text-fill: #555555;");
    private HPos keyHAlignment = HPos.LEFT;
    private VPos keyVAlignment = VPos.TOP;

    public KeyValuePane() {}

    public void put(String key, Object value) {
        properties.put(key, value);
        setHgap(10);
        updateUI();
    }

    public void remove(String key) {
        properties.remove(key);
        updateUI();
    }

    public void clear() {
        properties.clear();
        updateUI();
    }

    public Set<String> keySet() {
        return properties.keySet();
    }

    public Collection<Object> values() {
        return properties.values();
    }

    private void updateUI() {

        getChildren().clear();
        int row = 0;

        for (String key : properties.keySet()) {
            Object value = properties.get(key);

            Label lKey = new Label(key);
            lKey.setMinWidth(Label.USE_PREF_SIZE);
            Node lValue = null;
            if (value instanceof Node) {
                lValue = (Node) value;
            } else {
                lValue = new Label(value.toString());
            }

            GridPane.setHalignment(lKey, keyHAlignment);
            GridPane.setValignment(lKey, keyVAlignment);

            add(lKey, 0, row);
            GridPane.setHgrow(lValue, Priority.ALWAYS);
            add(lValue, 1, row);
            row++;

            lKey.styleProperty().bind(keyStyle);
            lValue.styleProperty().bind(valueStyle);

        }
    }

    public String getKeyStyle() {
        return keyStyle.get();
    }

    public void setKeyStyle(String keyStyle) {
        this.keyStyle.set(keyStyle);
    }

    public Property<String> keyStyleProperty() {
        return keyStyle;
    }

    public String getValueStyle() {
        return valueStyle.get();
    }

    public void setValueStyle(String valueStyle) {
        this.valueStyle.set(valueStyle);
    }

    public Property<String> valueStyleProperty() {
        return valueStyle;
    }

    public HPos getKeyHAlignment() {
        return keyHAlignment;
    }

    public void setKeyHAlignment(HPos keyAlignment) {
        this.keyHAlignment = keyAlignment;
        updateUI();
    }

    public VPos getKeyVAlignment() {
        return keyVAlignment;
    }

    public void setKeyVAlignment(VPos keyAlignment) {
        this.keyVAlignment = keyAlignment;
        updateUI();
    }

}
