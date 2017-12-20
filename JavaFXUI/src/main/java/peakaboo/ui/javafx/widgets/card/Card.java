package peakaboo.ui.javafx.widgets.card;


import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class Card extends BlankCard {

    private HBox contentBox;
    private AnchorPane titleBox, descriptionBox, titleContentBox;
    private Separator sep;

    public Card() {

        BorderPane card = new BorderPane();
        super.setContent(card);

        titleContentBox = new AnchorPane();
        contentBox = new HBox();
        titleBox = new AnchorPane();
        descriptionBox = new AnchorPane();
        sep = new Separator(Orientation.HORIZONTAL);

        sep.setVisible(false);
        AnchorPane sepAnchor = new AnchorPane(anchored(sep));
        HBox.setHgrow(sepAnchor, Priority.ALWAYS);
        HBox top = new HBox(titleBox, titleContentBox, sepAnchor, descriptionBox);
        card.setTop(top);
        card.setCenter(contentBox);

        setTitle("");
        setDescription("");

    }

    public void setTitle(String titleString) {
        if (titleString == null) {
            titleString = "";
        }
        Label label = new Label(titleString);
        label.setStyle("-fx-font-size: 13pt;");
        setTitle(label);

        fixPadding();
    }

    public void setTitle(Node titleNode) {
        titleBox.getChildren().clear();
        if (titleNode == null) { return; }
        titleBox.getChildren().add(anchored(titleNode));
    }

    public Node getTitle() {
        return titleBox.getChildren().get(0);
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        Label label = new Label(description);
        label.setStyle("-fx-text-fill: #777777;");
        setDescription(label);
    }

    public void setDescription(Node descriptionNode) {
        descriptionBox.getChildren().clear();
        if (descriptionNode == null) { return; }
        descriptionBox.getChildren().add(anchored(descriptionNode));
        fixPadding();
    }

    public Node getDescription() {
        return descriptionBox.getChildren().get(0);
    }

    public Node getContent() {
        if (contentBox.getChildren().size() == 0) { return null; }
        return contentBox.getChildren().get(0);
    }

    public void setContent(Node node) {
        setContent(node, true);
    }

    public void setContent(Node node, boolean expanding) {
        contentBox.getChildren().clear();
        if (node == null) {
            fixPadding();
            return;
        }

        BorderPane.setAlignment(node, Pos.CENTER_LEFT);
        if (expanding) {
            HBox.setHgrow(node, Priority.ALWAYS);
        } else {
            HBox.setHgrow(node, Priority.NEVER);
        }
        contentBox.getChildren().add(node);

        fixPadding();

    }

    public void setTitleContent(String text) {
        if (text == null) {
            text = "";
        }
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #999999; -fx-font-size: 13pt;");
        setTitleContent(label);
    }

    public void setTitleContent(Node node) {
        titleContentBox.getChildren().clear();
        if (node == null) { return; }
        titleContentBox.getChildren().add(anchored(node));
    }

    public Node getTitleContent() {
        return titleContentBox.getChildren().get(0);
    }

    private void fixPadding() {

        if (contentBox.getChildren().size() == 0) {
            titleBox.setPadding(new Insets(0, 10, 0, 0));
            descriptionBox.setPadding(new Insets(0, 0, 0, 0));
            titleContentBox.setPadding(new Insets(0, 0, 0, 0));
        } else {
            titleBox.setPadding(new Insets(0, 10, 6, 0));
            descriptionBox.setPadding(new Insets(0, 0, 6, 0));
            titleContentBox.setPadding(new Insets(0, 0, 6, 0));
        }

    }

    public Separator getSeparator() {
        return sep;
    }

    private Node anchored(Node node) {
        AnchorPane.setTopAnchor(node, 0d);
        AnchorPane.setBottomAnchor(node, 0d);
        AnchorPane.setLeftAnchor(node, 0d);
        AnchorPane.setRightAnchor(node, 0d);
        return node;
    }
}
