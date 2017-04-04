package peakaboo.ui.javafx.change;


import java.util.function.Consumer;

import peakaboo.ui.javafx.util.ActofUIController;


public abstract class Change {

    private ActofUIController source;


    public Change(ActofUIController source) {
        this.source = source;
    }

    public String getSourceId() {
        return source.getId();
    }

    public ActofUIController getSource() {
        return source;
    }



    public <T extends Change> boolean is(Class<T> cls) {
        return cls.isInstance(this);
    }

    public <T extends Change> void when(Class<T> cls, Runnable runnable) {

        if (cls.isInstance(this)) {
            runnable.run();
        }

    }

    public <T extends Change> void when(Class<T> cls, Consumer<T> consumer) {

        if (cls.isInstance(this)) {
            @SuppressWarnings("unchecked")
            T t = (T) this;
            consumer.accept(t);
        }

    }


}
