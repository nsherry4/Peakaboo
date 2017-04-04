package peakaboo.ui.javafx.util;


import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import peakaboo.ui.javafx.change.ChangeController;


public class FXUtil {



    public static <T extends ActofUIController> T load(Class<T> cls, String filename, ChangeController changes)
            throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(cls.getResource(filename));
        Node node = loader.load();

        T controller = loader.getController();
        controller.setNode(node);
        controller.setChangeBus(changes);

        controller.ready();

        return controller;

    }
}
