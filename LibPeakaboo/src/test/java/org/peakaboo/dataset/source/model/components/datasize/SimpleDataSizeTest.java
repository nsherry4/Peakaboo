package org.peakaboo.dataset.source.model.components.datasize;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.accent.Coord;

public class SimpleDataSizeTest {

    private SimpleDataSize makeSize(int width, int height) {
        SimpleDataSize size = new SimpleDataSize();
        size.setDataWidth(width);
        size.setDataHeight(height);
        return size;
    }

    @Test
    public void testNonSquareWide() {
        // 5 columns x 3 rows
        SimpleDataSize size = makeSize(5, 3);

        // index 0 => (0,0)
        Coord<Integer> c0 = size.getDataCoordinatesAtIndex(0);
        Assert.assertEquals(0, (int) c0.x);
        Assert.assertEquals(0, (int) c0.y);

        // index 4 => (4,0) — end of first row
        Coord<Integer> c4 = size.getDataCoordinatesAtIndex(4);
        Assert.assertEquals(4, (int) c4.x);
        Assert.assertEquals(0, (int) c4.y);

        // index 5 => (0,1) — start of second row
        Coord<Integer> c5 = size.getDataCoordinatesAtIndex(5);
        Assert.assertEquals(0, (int) c5.x);
        Assert.assertEquals(1, (int) c5.y);

        // index 11 => (1,2)
        Coord<Integer> c11 = size.getDataCoordinatesAtIndex(11);
        Assert.assertEquals(1, (int) c11.x);
        Assert.assertEquals(2, (int) c11.y);

        // index 14 => (4,2) — last element
        Coord<Integer> c14 = size.getDataCoordinatesAtIndex(14);
        Assert.assertEquals(4, (int) c14.x);
        Assert.assertEquals(2, (int) c14.y);
    }

    @Test
    public void testNonSquareTall() {
        // 3 columns x 5 rows
        SimpleDataSize size = makeSize(3, 5);

        // index 4 => (1,1)
        Coord<Integer> c4 = size.getDataCoordinatesAtIndex(4);
        Assert.assertEquals(1, (int) c4.x);
        Assert.assertEquals(1, (int) c4.y);

        // index 14 => (2,4) — last element
        Coord<Integer> c14 = size.getDataCoordinatesAtIndex(14);
        Assert.assertEquals(2, (int) c14.x);
        Assert.assertEquals(4, (int) c14.y);
    }

    @Test
    public void testSquare() {
        SimpleDataSize size = makeSize(4, 4);

        Coord<Integer> c5 = size.getDataCoordinatesAtIndex(5);
        Assert.assertEquals(1, (int) c5.x);
        Assert.assertEquals(1, (int) c5.y);

        Coord<Integer> c15 = size.getDataCoordinatesAtIndex(15);
        Assert.assertEquals(3, (int) c15.x);
        Assert.assertEquals(3, (int) c15.y);
    }

    @Test
    public void testRoundTrip() {
        // For every index in a non-square grid, verify index -> coords -> index
        SimpleDataSize size = makeSize(5, 3);
        int total = 5 * 3;
        for (int i = 0; i < total; i++) {
            Coord<Integer> c = size.getDataCoordinatesAtIndex(i);
            int reconstructed = c.y * 5 + c.x;
            Assert.assertEquals("Round-trip failed for index " + i, i, reconstructed);
        }
    }

    @Test
    public void testDimensions() {
        SimpleDataSize size = makeSize(7, 3);
        Coord<Integer> dims = size.getDataDimensions();
        Assert.assertEquals(7, (int) dims.x);
        Assert.assertEquals(3, (int) dims.y);
    }

}
