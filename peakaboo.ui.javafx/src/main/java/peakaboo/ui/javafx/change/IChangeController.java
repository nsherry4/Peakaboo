package peakaboo.ui.javafx.change;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;


public class IChangeController implements ChangeController {

    protected final List<Consumer<Change>> listeners;

    public IChangeController() {
        listeners = new LinkedList<Consumer<Change>>();
    }

    /**
     * @see eventful.IEventfulType#listen(eventful.EventfulTypeListener)
     */
    @Override
    public synchronized void listen(final Consumer<Change> l) {
        listeners.add(l);
    }


    /**
     * @see eventful.IEventfulType#unlisten(eventful.EventfulTypeListener)
     */
    @Override
    public synchronized void unlisten(final Consumer<Change> l) {
        listeners.remove(l);
    }

    /**
     * @see eventful.IEventfulType#clear()
     */
    @Override
    public synchronized void clear() {
        listeners.clear();
    }


    /**
     * @see eventful.IEventfulType#broadcast(T)
     */
    @Override
    public synchronized void broadcast(final Change message) {

        if (listeners.size() == 0) return;

        Platform.runLater(() -> {
            synchronized (IChangeController.this) {
                for (Consumer<Change> l : new ArrayList<>(listeners)) {
                    l.accept(message);
                }
            }
        });

    }

}
