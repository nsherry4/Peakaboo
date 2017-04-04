package peakaboo.ui.javafx.util;


import java.io.IOException;

import javafx.scene.Node;
import peakaboo.ui.javafx.change.ChangeController;


public interface ActofUIController extends ActofController {

    Node getNode();

    void setNode(Node node);

    void setChangeBus(ChangeController change);

    void ready() throws IOException;

    String getId();

    void setId(String id);

}
