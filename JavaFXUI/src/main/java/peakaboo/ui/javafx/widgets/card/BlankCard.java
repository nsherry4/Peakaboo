package peakaboo.ui.javafx.widgets.card;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;


public class BlankCard extends StackPane {

    public StackPane inner;

    private double radius = 3;
    private String colour = "#ffffff";
    private String shadowColour = "#000000a0";
    private double shadowRadius = 5;
    private double shadowOffsetX = 1, shadowOffsetY = 0;
    private boolean hasShadow = true;

    public BlankCard() {

        setPadding(new Insets(0));

        inner = new StackPane();
        getChildren().setAll(inner);
        inner.setPadding(new Insets(10));

        buildStyle();
    }

    public void setContent(Node card) {
        inner.getChildren().setAll(card);
    }

    public Node getContent() {
        if (inner.getChildren().isEmpty()) { return null; }
        return inner.getChildren().get(0);
    }

    public void setRadius(double radius) {
        this.radius = radius;
        buildStyle();
    }

    public double getRadius() {
        return radius;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
        buildStyle();
    }

    public void setShadowColour(String colour) {
        this.shadowColour = colour;
        buildStyle();
    }

    public void setShadowRadius(double rad) {
        this.shadowRadius = rad;
        buildStyle();
    }

    public double getShadowOffsetX() {
        return shadowOffsetX;
    }

    public void setShadowOffsetX(double shadowOffsetX) {
        this.shadowOffsetX = shadowOffsetX;
        buildStyle();
    }

    public double getShadowOffsetY() {
        return shadowOffsetY;
    }

    public void setShadowOffsetY(double shadowOffsetY) {
        this.shadowOffsetY = shadowOffsetY;
        buildStyle();
    }

    public String getShadowColour() {
        return shadowColour;
    }

    public double getShadowRadius() {
        return shadowRadius;
    }

    public boolean isHasShadow() {
        return hasShadow;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        buildStyle();
    }

    public void setInnerPadding(Insets padding) {
        inner.setPadding(padding);
    }

    public Insets getInnerPadding() {
        return inner.getPadding();
    }

    private void buildStyle() {

        String rad = "-fx-background-radius: " + radius + "px; ";
        String col = "-fx-background-color: " + colour + "; ";
        String shadow = "";
        if (hasShadow) {
            shadow = "-fx-effect: dropshadow(gaussian, " + shadowColour + " , " + shadowRadius + ", -2, "
                    + shadowOffsetY + "," + shadowOffsetX + ")";
        }
        inner.setStyle(rad + col + shadow);
    }

}
