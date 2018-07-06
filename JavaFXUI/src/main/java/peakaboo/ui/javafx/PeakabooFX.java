package peakaboo.ui.javafx;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import peakaboo.curvefit.peak.table.CombinedPeakTable;
import peakaboo.curvefit.peak.table.KrausPeakTable;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.table.XrayLibPeakTable;
import peakaboo.ui.javafx.plot.window.PlotWindowController;


public class PeakabooFX extends Application {

    PlotWindowController plot;

    public static void main(String[] args) {
    	PeakTable.SYSTEM.setSource(new CombinedPeakTable(new XrayLibPeakTable(), new KrausPeakTable()));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        plot = PlotWindowController.load();

        Scene scene = new Scene((Parent) plot.getNode());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(PeakabooFX.class.getResource("logo/icon.png").openStream()));

        // primaryStage.setOnCloseRequest(event -> {
        // if (!view.modified) { return; }
        //
        // Action reallyquit = Dialogs.create().title("Exit SchemaSpy")
        // .message("Exit SchemaSpy with unsaved changes?").masthead("You have unsaved changes")
        // .actions(Dialog.Actions.YES, Dialog.Actions.NO).showConfirm();
        //
        // if (reallyquit != Dialog.Actions.YES) {
        // event.consume();
        // }
        //
        // });
        primaryStage.show();

    }

}
