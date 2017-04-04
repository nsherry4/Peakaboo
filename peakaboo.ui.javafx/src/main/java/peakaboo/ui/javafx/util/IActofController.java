package peakaboo.ui.javafx.util;


import peakaboo.ui.javafx.change.ChangeController;





public class IActofController implements ActofController {

    private ChangeController changes;


    public IActofController(ChangeController changes) {
        this.changes = changes;
    }

    @Override
    public final ChangeController getChangeBus() {
        return changes;
    }


}