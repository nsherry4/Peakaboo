package peakaboo.ui.javafx.util;


import peakaboo.ui.javafx.change.IChangeController;




public class ITopController extends IActofController {

    public ITopController() {
        super(new IChangeController());
    }

}
