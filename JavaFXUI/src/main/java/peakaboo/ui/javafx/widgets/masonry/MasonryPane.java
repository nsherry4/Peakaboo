package peakaboo.ui.javafx.widgets.masonry;


/**
 * Copyright (c) 2015, ControlsFX All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of ControlsFX, any associated
 * website, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


/**
 * A MasonryPane is used to lay out child nodes in columns similar to Google Now
 * or Pinterest. Child nodes can be laid out in three different modes:
 * 
 * <h3>Shortest Column</h3> Nodes will be added to whichever column consumes the
 * least vertical space<br>
 * <img src="masonryPane-shortestcolumn.png"/>
 * 
 * <h3>Round Robin</h3> Nodes will be added to successive columns.<br>
 * <img src="masonryPane-shortestcolumn.png"/>
 * 
 * <h3>Grid</h3> Nodes will be added to successive columns. All nodes in a row
 * will be vertically aligned, and will clear all nodes in the previous row. <br>
 * <img src="masonryPane-shortestcolumn.png"/>
 */
public class MasonryPane extends Pane {

    /******************************************************
     * 
     * Layout Style Enumeration
     * 
     ******************************************************/

    public enum Layout {
        SHORTEST_COLUMN {

            MasonryLayout layout = new ShortestColumnLayout();

            MasonryLayout impl() {
                return layout;
            }

        },

        ROUND_ROBIN {

            MasonryLayout layout = new RoundRobinLayout();

            MasonryLayout impl() {
                return layout;
            }

        },

        GRID {

            MasonryLayout layout = new GridLayout();

            MasonryLayout impl() {
                return layout;
            }

        };

        MasonryLayout impl() {
            return new RoundRobinLayout();
        }
    }

    /******************************************************
     * 
     * Private Fields
     * 
     ******************************************************/
    private static final double DEFAULT_COLUMN_WIDTH = 400;
    private double columnWidth = DEFAULT_COLUMN_WIDTH;
    private boolean performingLayout = false;

    /******************************************************
     * 
     * Constructor
     * 
     ******************************************************/

    /**
     * Constructs a new MasonryPane
     */
    public MasonryPane() {
        this(DEFAULT_COLUMN_WIDTH, Layout.SHORTEST_COLUMN);
    }

    /**
     * Constructs a new MasonryPane
     * 
     * @param columnWidth
     *            the minimum width of each column of child nodes. Columns may
     *            be wider than this, but never less.
     * @param layout
     *            The preferred layout strategy for positioning child nodes
     */
    public MasonryPane(double columnWidth, Layout layout) {
        this.columnWidth = columnWidth;
        this.layout.set(layout);
        this.horizontalSpacing.addListener((Observable change) -> requestLayout());
        this.verticalSpacing.addListener((Observable change) -> requestLayout());
        this.layout.addListener((Observable change) -> requestLayout());
    }

    /******************************************************
     * 
     * Public Properties
     * 
     ******************************************************/

    private SimpleObjectProperty<Layout> layout = new SimpleObjectProperty<>(Layout.SHORTEST_COLUMN);

    /**
     * Sets the {@link MasonryLayout} used to determine the position and
     * ordering of children.
     * 
     * @param layout
     *            The {@link MasonryLayout} to use.
     */
    public final void setLayout(Layout layout) {
        this.layout.set(layout);
    }

    /**
     * Gets the {@link MasonryLayout} used to determine the position and
     * ordering of children.
     * 
     * @return The active {@link MasonryLayout}
     */
    public final Layout getLayout() {
        return layout.get();
    }

    /**
     * Gets the property containing the {@link MasonryLayout} used to determine
     * the position and ordering of children.
     * 
     * @return the property containing the active {@link MasonryLayout}
     */
    public final ObjectProperty<Layout> layoutProperty() {
        return layout;
    }

    private SimpleDoubleProperty verticalSpacing = new SimpleDoubleProperty(0);

    /**
     * Sets the amount of vertical space between child nodes.
     * 
     * @param spacing
     *            Desired vertical space between nodes
     */
    public final void setVerticalSpacing(double spacing) {
        verticalSpacing.set(spacing);
    }

    /**
     * Gets the amount of vertical space between child nodes.
     * 
     * @return vertical space between nodes
     */
    public final double getVerticalSpacing() {
        return verticalSpacing.get();
    }

    /**
     * Gets the property containing the amount of vertical space between child
     * nodes.
     * 
     * @return the property containing the amount of vertical space between
     *         child nodes
     */
    public final DoubleProperty verticalSpacingProperty() {
        return verticalSpacing;
    }

    private SimpleDoubleProperty horizontalSpacing = new SimpleDoubleProperty(0);

    /**
     * Sets the amount of horizontal space between child nodes.
     * 
     * @param spacing
     *            Desired horizontal space between nodes
     */
    public final void setHorizontalSpacing(double spacing) {
        horizontalSpacing.set(spacing);
    }

    /**
     * Gets the amount of horizontal space between child nodes.
     * 
     * @return horizontal space between nodes
     */
    public final double getHorizontalSpacing() {
        return horizontalSpacing.get();
    }

    /**
     * Gets the property containing the amount of horizontal space between child
     * nodes.
     * 
     * @return the property containing the amount of horizontal space between
     *         child nodes
     */
    public final DoubleProperty horizontalSpacingProperty() {
        return horizontalSpacing;
    }

    /**
     * Conveneicen method for setting both horizontal and vertical spacing at
     * once.
     * 
     * @param spacing
     *            Desired space between nodes
     */
    public final void setSpacing(double spacing) {
        setVerticalSpacing(spacing);
        setHorizontalSpacing(spacing);
    }

    /******************************************************
     * 
     * Override/Inherited methods
     * 
     ******************************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren() {

        performingLayout = true;

        double heights[] = new double[columnCount(getWidth())];

        int lastColumn = -1; // round robin will increments
        double clearance = 0; // determines min y position, useful for grid
        int selectedColumn = 0;
        MasonryLayout masonryLayout = layout.get().impl();
        for (Node node : getManagedChildren()) {
            // figure out which column this node should go into, and what the
            // clearance for this row is
            selectedColumn = masonryLayout.pickColumn(heights, lastColumn);
            clearance = masonryLayout.clearance(heights, clearance, selectedColumn);
            lastColumn = selectedColumn;

            // calculate position/size of node
            double width = realColumnWidth(getWidth());
            double height = nodePrefHeight(node, -1, VBox.getMargin(node), realColumnWidth(getWidth()));
            double y = yForNode(selectedColumn, heights, clearance);
            double x = xForColumn(selectedColumn);

            // position the node
            node.resize(snapSize(width), snapSize(height));
            node.relocate(snapPosition(getInsets().getLeft() + x), snapPosition(getInsets().getTop() + y));

            // update the height value for this column
            heights[selectedColumn] = y + height;
        }

        performingLayout = false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinWidth(double height) {
        return computePrefWidth(height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinHeight(double width) {
        return computePrefHeight(width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefWidth(double height) {
        Insets insets = getInsets();
        return snapSize(insets.getLeft() + columnWidth + insets.getRight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefHeight(double width) {
        Insets insets = getInsets();

        double heights[] = new double[columnCount(width)];

        int lastColumn = -1; // round robin will increments
        double clearance = 0; // determines min y position, useful for grid
        int selectedColumn = 0;
        MasonryLayout masonryLayout = layout.get().impl();
        for (Node node : getManagedChildren()) {
            // figure out which column this node should go into, and what the
            // clearance for this row is
            selectedColumn = masonryLayout.pickColumn(heights, lastColumn);
            clearance = masonryLayout.clearance(heights, clearance, selectedColumn);
            lastColumn = selectedColumn;

            // calculate position/size of node (w/o x/width)
            double height = nodePrefHeight(node, -1, VBox.getMargin(node), realColumnWidth(width));
            double y = yForNode(selectedColumn, heights, clearance);

            // update the height value for this column
            heights[selectedColumn] = y + height;
        }

        double height = 0;
        for (double h : heights) {
            height = Math.max(height, h);
        }

        return snapSize(insets.getTop() + height + insets.getBottom());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestLayout() {
        if (performingLayout) { return; }
        super.requestLayout();
    }

    /******************************************************
     * 
     * Private Methods
     * 
     ******************************************************/

    // Determines the number of columns which should be shown for the given
    // width. We pass width in explicitly rather than calling getWidth() so that
    // we can use this method for calculating preferred height as well
    protected int columnCount(double width) {

        int columnCount = 1;
        if (columnWidth > 0) {
            columnCount = (int) Math.floor(width / columnWidth);
        }

        // always at least 1 column
        columnCount = Math.max(columnCount, 1);

        // only as many columns as nodes
        columnCount = Math.min(columnCount, getManagedChildren().size());

        return columnCount;
    }

    // Calculates the real width of a column for a given total width. Columns
    // must be at least as wide as requested, but could be wider to fill extra
    // space.
    private double realColumnWidth(double width) {
        int count = columnCount(width);
        double spacing = horizontalSpacing.get() * (count - 1);
        double insets = getInsets().getLeft() + getInsets().getRight();
        double available = width - insets - spacing;
        return available / (double) count;
    }

    // Determine the starting x position for a node in the given column number
    private double xForColumn(int column) {
        double columnOffset = realColumnWidth(getWidth()) + horizontalSpacing.get();
        return snapPosition(column * columnOffset);
    }

    // Determine the starting y position for a node in the given column number,
    // where existing column heights and a row clearance are specified.
    private double yForNode(int column, double heights[], double clearance) {
        double y = Math.max(clearance, heights[column]);
        double spacingHeight = y == 0 ? 0 : verticalSpacing.get();
        return spacingHeight + y;
    }

    // Borrowed from Region computeChildPrefAreaHeight -- it appears to be
    // package private?
    private double nodePrefHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
        double top = margin != null ? snapSpace(margin.getTop()) : 0;
        double bottom = margin != null ? snapSpace(margin.getBottom()) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height
                                                                                       // depends
                                                                                       // on
                                                                                       // width
            double left = margin != null ? snapSpace(margin.getLeft()) : 0;
            double right = margin != null ? snapSpace(margin.getRight()) : 0;
            alt = snapSize(boundedSize(child.minWidth(-1), width != -1 ? width - left - right : child.prefWidth(-1),
                    child.maxWidth(-1)));
        }

        if (prefBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                // When baseline is same as height, the preferred height of the
                // node will be above the baseline, so we need to add
                // the preferred complement to it
                return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt)))
                        + bottom + prefBaselineComplement;
            } else {
                // For all other Nodes, it's just their baseline and the
                // complement.
                // Note that the complement already contain the Node's preferred
                // (or fixed) height
                return top + baseline + prefBaselineComplement + bottom;
            }
        } else {
            return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt)))
                    + bottom;
        }
    }

    // Borrowed from Region -- it appears to be package private?
    private static double boundedSize(double min, double pref, double max) {
        double a = pref >= min ? pref : min;
        double b = min >= max ? min : max;
        return a <= b ? a : b;
    }

    /**
     * A MasonryLayout determines how a MasonryPane lays out its child nodes. It
     * provides logic for determining which column a new child node will be
     * placed in, and at what height from the top of the MasonryPane.
     */
    private interface MasonryLayout {

        /**
         * Chooses which column the next child node to be laid out should be
         * placed in.
         * 
         * @param heights
         *            an array of heights representing the amount of vertical
         *            space used in each column by child nodes already laid out.
         * @param lastColumn
         *            a value representing which column was used to lay out the
         *            most recent node. This will be set to -1 for the first
         *            node.
         * @return An integer representing which column the next child node
         *         should be placed in. This value should be between 0 and
         *         heights.length-1
         */
        int pickColumn(double heights[], int lastColumn);

        /**
         * Determines what the <i>minimum</i> clearance (ie y coordinate) of the
         * next child node to be laid out should be.
         * 
         * @param heights
         *            an array of heights representing the amount of vertical
         *            space used in each column by child nodes already laid out.
         * @param lastClearance
         *            The clearance value used for the most recently laid out
         *            node
         * @param column
         *            the column in which this node will be laid out.
         * @return a double representing the minimum clearance (ie y coordinate)
         *         at which the next node to be laid out should be placed.
         */
        double clearance(double heights[], double lastClearance, int column);

    }

    /**
     * ShortestColumnMasonryLayout lays out child nodes in a MasonryPane by
     * placing them in the column with the least used vertical space
     */
    private static class ShortestColumnLayout implements MasonryLayout {

        /**
         * {@inheritDoc}
         */
        @Override
        public int pickColumn(double[] heights, int lastColumn) {
            int selectedColumn = 0;
            for (int i = 1; i < heights.length; i++) {
                if (heights[i] < heights[selectedColumn]) {
                    selectedColumn = i;
                }
            }
            return selectedColumn;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double clearance(double[] heights, double lastClearance, int column) {
            return 0;
        }

    }

    /**
     * RoundRobinMasonryLayout lays out child nodes in a MasonryPane by placing
     * one node in each column successively, looping once reaching the last
     * column.
     */
    private static class RoundRobinLayout implements MasonryLayout {

        /**
         * {@inheritDoc}
         */
        @Override
        public int pickColumn(double[] heights, int lastColumn) {
            int selectedColumn = lastColumn + 1;
            if (selectedColumn >= heights.length) {
                selectedColumn = 0;
            }
            return selectedColumn;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double clearance(double[] heights, double lastClearance, int column) {
            return 0;
        }

    }

    /**
     * GridMasonryLayout lays out child nodes in a MasonryPane by placing one
     * node in each column successively, and requiring that all nodes in
     * successive rows clear (ie be lower than) the bottom of all nodes in
     * previous rows.
     */
    private static class GridLayout implements MasonryLayout {

        /**
         * {@inheritDoc}
         */
        @Override
        public int pickColumn(double[] heights, int lastColumn) {
            int selectedColumn = lastColumn + 1;
            if (selectedColumn >= heights.length) {
                selectedColumn = 0;
            }
            return selectedColumn;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double clearance(double[] heights, double lastClearance, int column) {
            // only compute new clearance if we're starting a new row
            if (column == 0) {
                double height = 0;
                for (double h : heights) {
                    height = Math.max(height, h);
                }
                return height;
            } else {
                return lastClearance;
            }
        }

    }

}
