package peakaboo.ui.javafx.change;


import java.util.function.Consumer;




public interface ChangeController {

    void listen(Consumer<Change> l);




    default <T extends Change> void listen(Class<T> cls, Consumer<T> l) {
        listen(change -> change.when(cls, l));
    }

    default <T extends Change> void listen(Class<T> cls, Runnable l) {
        listen(change -> change.when(cls, l));
    }




    default <T extends Change> void listen(Class<T> cls, String id, Consumer<T> l) {
        listen(cls, change -> {
            if (change.getSourceId().equals(id)) {
                l.accept(change);
            }
        });
    }


    default <T extends Change> void listen(Class<T> cls, String id, Runnable l) {
        listen(cls, change -> {
            if (change.getSourceId().equals(id)) {
                l.run();
            }
        });
    }

    void unlisten(final Consumer<Change> l);


    void clear();


    void broadcast(final Change message);


}