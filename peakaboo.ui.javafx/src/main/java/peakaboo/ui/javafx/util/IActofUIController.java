package peakaboo.ui.javafx.util;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Node;
import peakaboo.ui.javafx.change.ChangeController;


public abstract class IActofUIController implements ActofUIController {

    private Node node;
    private ChangeController changes;
    private String id;

    @Override
    public final ChangeController getChangeBus() {
        return changes;
    }

    @Override
    public final void setChangeBus(ChangeController changes) {
        if (this.changes != null) { throw new IllegalArgumentException("Cannot reassign ChangeController"); }
        this.changes = changes;
    }

    @Override
    public final Node getNode() {
        return node;
    }

    @Override
    public final void setNode(Node node) {
        this.node = node;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public abstract void ready() throws IOException;

    @FXML
    protected abstract void initialize() throws Exception;

}
