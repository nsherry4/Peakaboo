package org.peakaboo.framework.cyclops.spectrum;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Comprehensive test suite for ArraySpectrum class.
 * Tests all constructors, operations, and edge cases.
 */
public class ArraySpectrumTest {

    private static final float EPSILON = 1e-6f;
    private static final float[] TEST_DATA = {1.5f, 2.0f, 3.5f, 4.0f, 5.5f};
    private static final double[] TEST_DOUBLE_DATA = {1.5, 2.0, 3.5, 4.0, 5.5};
    
    private ArraySpectrum spectrum;
    
    @Before
    public void setUp() {
        spectrum = new ArraySpectrum(TEST_DATA.length);
    }

    // ========================================
    // Constructor Tests
    // ========================================
    
    @Test
    public void testSizeConstructor() {
        ArraySpectrum s = new ArraySpectrum(10);
        assertEquals(10, s.size());
        assertEquals(0.0f, s.get(0), EPSILON);
        assertEquals(0.0f, s.get(9), EPSILON);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSizeConstructorInvalidSize() {
        new ArraySpectrum(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSizeConstructorNegativeSize() {
        new ArraySpectrum(-1);
    }
    
    @Test
    public void testSizeAndValueConstructor() {
        float initValue = 2.5f;
        ArraySpectrum s = new ArraySpectrum(5, initValue);
        assertEquals(5, s.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(initValue, s.get(i), EPSILON);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSizeAndValueConstructorInvalidSize() {
        new ArraySpectrum(0, 1.0f);
    }
    
    @Test
    public void testFloatArrayConstructor() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        assertEquals(TEST_DATA.length, s.size());
        for (int i = 0; i < TEST_DATA.length; i++) {
            assertEquals(TEST_DATA[i], s.get(i), EPSILON);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFloatArrayConstructorNull() {
        new ArraySpectrum((float[]) null);
    }
    
    @Test
    public void testFloatArrayConstructorCopy() {
        float[] original = {1.0f, 2.0f, 3.0f};
        ArraySpectrum s = new ArraySpectrum(original, true);
        
        // Modify original array
        original[0] = 99.0f;
        
        // Spectrum should not be affected (copy was made)
        assertEquals(1.0f, s.get(0), EPSILON);
    }
    
    @Test
    public void testFloatArrayConstructorNoCopy() {
        float[] original = {1.0f, 2.0f, 3.0f};
        ArraySpectrum s = new ArraySpectrum(original, false);
        
        // Modify original array
        original[0] = 99.0f;
        
        // Spectrum should be affected (same array reference)
        assertEquals(99.0f, s.get(0), EPSILON);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFloatArrayConstructorCopyNull() {
        new ArraySpectrum((float[]) null, true);
    }
    
    @Test
    public void testDoubleArrayConstructor() {
        ArraySpectrum s = new ArraySpectrum(TEST_DOUBLE_DATA);
        assertEquals(TEST_DOUBLE_DATA.length, s.size());
        for (int i = 0; i < TEST_DOUBLE_DATA.length; i++) {
            assertEquals((float) TEST_DOUBLE_DATA[i], s.get(i), EPSILON);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDoubleArrayConstructorNull() {
        new ArraySpectrum((double[]) null);
    }
    
    @Test
    public void testListConstructor() {
        List<Float> list = new ArrayList<>();
        for (float value : TEST_DATA) {
            list.add(value);
        }
        
        ArraySpectrum s = new ArraySpectrum(list);
        assertEquals(list.size(), s.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), s.get(i), EPSILON);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testListConstructorNull() {
        new ArraySpectrum((List<Float>) null);
    }
    
    @Test
    public void testSpectrumViewConstructor() {
        ArraySpectrum original = new ArraySpectrum(TEST_DATA);
        ArraySpectrum copy = new ArraySpectrum((SpectrumView) original);
        
        assertEquals(original.size(), copy.size());
        assertEquals(original, copy);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSpectrumViewConstructorNull() {
        new ArraySpectrum((SpectrumView) null);
    }

    // ========================================
    // Cursor-based Add Operation Tests
    // ========================================
    
    @Test
    public void testAddOperations() {
        ArraySpectrum s = new ArraySpectrum(3);
        
        assertTrue(s.add(1.0f));
        assertEquals(1.0f, s.get(0), EPSILON);
        
        assertTrue(s.add(2.0f));
        assertEquals(2.0f, s.get(1), EPSILON);
        
        assertTrue(s.add(3.0f));
        assertEquals(3.0f, s.get(2), EPSILON);
        
        // Should fail when full
        assertFalse(s.add(4.0f));
    }
    
    @Test
    public void testAddToPrefilledSpectrum() {
        ArraySpectrum s = new ArraySpectrum(3, 1.0f);
        
        // Should fail because cursor is at end
        assertFalse(s.add(2.0f));
    }

    // ========================================
    // Copy Operation Tests  
    // ========================================
    
    @Test
    public void testCopySpectrumView() {
        ArraySpectrum source = new ArraySpectrum(TEST_DATA);
        ArraySpectrum target = new ArraySpectrum(TEST_DATA.length);
        
        target.copy(source);
        assertEquals(source, target);
    }
    
    @Test
    public void testCopyPartialOverlap() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        ArraySpectrum target = new ArraySpectrum(3);
        
        target.copy(source);
        assertEquals(1.0f, target.get(0), EPSILON);
        assertEquals(2.0f, target.get(1), EPSILON); 
        assertEquals(3.0f, target.get(2), EPSILON);
    }
    
    @Test
    public void testCopyDifferentSizes() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3});
        ArraySpectrum target = new ArraySpectrum(1);
        
        // Should copy only what fits
        target.copy(source);
        assertEquals(1.0f, target.get(0), EPSILON);
    }
    
    @Test
    public void testCopyWithRange() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        ArraySpectrum target = new ArraySpectrum(5);
        
        target.copy(source, 1, 3);
        assertEquals(2.0f, target.get(1), EPSILON);
        assertEquals(3.0f, target.get(2), EPSILON);
        assertEquals(4.0f, target.get(3), EPSILON);
    }

    // ========================================
    // Get/Set Operation Tests
    // ========================================
    
    @Test
    public void testGetSetOperations() {
        ArraySpectrum s = new ArraySpectrum(5);
        
        s.set(0, 10.0f);
        s.set(4, 20.0f);
        
        assertEquals(10.0f, s.get(0), EPSILON);
        assertEquals(0.0f, s.get(1), EPSILON);
        assertEquals(20.0f, s.get(4), EPSILON);
    }
    
    @Test
    public void testSize() {
        assertEquals(TEST_DATA.length, new ArraySpectrum(TEST_DATA).size());
        assertEquals(100, new ArraySpectrum(100).size());
    }

    // ========================================
    // Mathematical Operation Tests
    // ========================================
    
    @Test
    public void testSum() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        float expectedSum = 0;
        for (float value : TEST_DATA) {
            expectedSum += value;
        }
        assertEquals(expectedSum, s.sum(), EPSILON);
    }
    
    @Test
    public void testSumEmpty() {
        ArraySpectrum s = new ArraySpectrum(5);
        assertEquals(0.0f, s.sum(), EPSILON);
    }
    
    @Test
    public void testMax() {
        ArraySpectrum s = new ArraySpectrum(new float[]{1.0f, 5.0f, 2.0f, 8.0f, 3.0f});
        assertEquals(8.0f, s.max(), EPSILON);
    }
    
    @Test
    public void testMin() {
        ArraySpectrum s = new ArraySpectrum(new float[]{3.0f, 1.0f, 5.0f, 2.0f, 8.0f});
        assertEquals(1.0f, s.min(), EPSILON);
    }
    
    @Test
    public void testMaxMinSingleValue() {
        ArraySpectrum s = new ArraySpectrum(1, 5.0f);
        assertEquals(5.0f, s.max(), EPSILON);
        assertEquals(5.0f, s.min(), EPSILON);
    }

    // ========================================
    // Utility Operation Tests
    // ========================================
    
    @Test
    public void testZero() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        s.zero();
        
        for (int i = 0; i < s.size(); i++) {
            assertEquals(0.0f, s.get(i), EPSILON);
        }
    }
    
    @Test
    public void testZeroRange() {
        ArraySpectrum s = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        s.zero(1, 3);
        
        assertEquals(1.0f, s.get(0), EPSILON);
        assertEquals(0.0f, s.get(1), EPSILON);
        assertEquals(0.0f, s.get(2), EPSILON);
        assertEquals(0.0f, s.get(3), EPSILON);
        assertEquals(5.0f, s.get(4), EPSILON);
    }
    
    @Test
    public void testEquals() {
        ArraySpectrum s1 = new ArraySpectrum(TEST_DATA);
        ArraySpectrum s2 = new ArraySpectrum(TEST_DATA);
        ArraySpectrum s3 = new ArraySpectrum(new float[]{1, 2, 3});
        
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertNotEquals(s1, null);
        assertNotEquals(s1, "not a spectrum");
    }
    
    @Test
    public void testHashCode() {
        ArraySpectrum s1 = new ArraySpectrum(TEST_DATA);
        ArraySpectrum s2 = new ArraySpectrum(TEST_DATA);
        
        assertEquals(s1.hashCode(), s2.hashCode());
    }
    
    @Test
    public void testHashCodeLargeArray() {
        // Test the sampling-based hash for large arrays
        float[] largeData = new float[1000];
        Arrays.fill(largeData, 1.5f);
        
        ArraySpectrum s1 = new ArraySpectrum(largeData);
        ArraySpectrum s2 = new ArraySpectrum(largeData);
        
        assertEquals(s1.hashCode(), s2.hashCode());
    }
    
    @Test
    public void testToString() {
        ArraySpectrum s = new ArraySpectrum(new float[]{1.0f, 2.0f, 3.0f});
        String result = s.toString();
        
        assertTrue(result.contains("1.0"));
        assertTrue(result.contains("2.0"));
        assertTrue(result.contains("3.0"));
        assertTrue(result.contains(" ")); // Default delimiter
    }
    
    @Test
    public void testToStringCustomDelimiter() {
        ArraySpectrum s = new ArraySpectrum(new float[]{1.0f, 2.0f, 3.0f});
        String result = s.toString(", ");
        
        assertTrue(result.contains("1.0"));
        assertTrue(result.contains("2.0"));
        assertTrue(result.contains("3.0"));
        assertTrue(result.contains(", "));
    }

    // ========================================
    // Array Access Tests
    // ========================================
    
    @Test
    public void testBackingArrayCopy() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        float[] copy = s.backingArrayCopy();
        
        assertArrayEquals(TEST_DATA, copy, EPSILON);
        
        // Verify it's a copy, not the original
        copy[0] = 999.0f;
        assertNotEquals(999.0f, s.get(0), EPSILON);
    }
    
    @Test
    public void testBackingArray() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        float[] backing = s.backingArray();
        
        // Modify backing array directly
        backing[0] = 999.0f;
        
        // Should affect the spectrum
        assertEquals(999.0f, s.get(0), EPSILON);
    }
    
    @Test
    public void testSubSpectrum() {
        ArraySpectrum s = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        ArraySpectrum sub = s.subSpectrum(1, 3);
        
        assertEquals(3, sub.size());
        assertEquals(2.0f, sub.get(0), EPSILON);
        assertEquals(3.0f, sub.get(1), EPSILON);
        assertEquals(4.0f, sub.get(2), EPSILON);
    }

    // ========================================
    // Iterator and Stream Tests
    // ========================================
    
    @Test
    public void testIterator() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        Iterator<Float> iter = s.iterator();
        
        int index = 0;
        while (iter.hasNext()) {
            Float value = iter.next();
            assertEquals(TEST_DATA[index], value, EPSILON);
            index++;
        }
        assertEquals(TEST_DATA.length, index);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        Iterator<Float> iter = s.iterator();
        iter.next();
        iter.remove(); // Should throw UnsupportedOperationException
    }
    
    @Test
    public void testStream() {
        ArraySpectrum s = new ArraySpectrum(TEST_DATA);
        Float[] streamArray = s.stream().toArray(Float[]::new);
        
        assertEquals(TEST_DATA.length, streamArray.length);
        for (int i = 0; i < TEST_DATA.length; i++) {
            assertEquals(TEST_DATA[i], streamArray[i], EPSILON);
        }
    }

    // ========================================
    // Error Condition Tests
    // ========================================
    
    @Test(expected = IllegalArgumentException.class)
    public void testCopyInvalidRange() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        ArraySpectrum target = new ArraySpectrum(5);
        
        target.copy(source, -1, 3); // Invalid first index
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCopyRangeExceedsSource() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3});
        ArraySpectrum target = new ArraySpectrum(5);
        
        target.copy(source, 0, 5); // last index exceeds source size
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCopyRangeExceedsTarget() {
        ArraySpectrum source = new ArraySpectrum(new float[]{1, 2, 3, 4, 5});
        ArraySpectrum target = new ArraySpectrum(3);
        
        target.copy(source, 0, 4); // Range exceeds target size
    }

    // ========================================
    // Edge Case Tests
    // ========================================
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyListConstructor() {
        List<Float> emptyList = new ArrayList<>();
        new ArraySpectrum(emptyList);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyArrayConstructor() {
        float[] emptyArray = new float[0];
        new ArraySpectrum(emptyArray);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testEmptyDoubleArrayConstructor() {
        double[] emptyArray = new double[0];
        new ArraySpectrum(emptyArray);
    }
    
    @Test
    public void testSingleElementSpectrum() {
        ArraySpectrum s = new ArraySpectrum(1, 5.0f);
        assertEquals(1, s.size());
        assertEquals(5.0f, s.get(0), EPSILON);
        assertEquals(5.0f, s.sum(), EPSILON);
        assertEquals(5.0f, s.max(), EPSILON);
        assertEquals(5.0f, s.min(), EPSILON);
    }
    
    @Test
    public void testSpecialFloatValues() {
        ArraySpectrum s = new ArraySpectrum(3);
        s.set(0, Float.NaN);
        s.set(1, Float.POSITIVE_INFINITY);
        s.set(2, Float.NEGATIVE_INFINITY);
        
        assertTrue(Float.isNaN(s.get(0)));
        assertEquals(Float.POSITIVE_INFINITY, s.get(1), 0);
        assertEquals(Float.NEGATIVE_INFINITY, s.get(2), 0);
    }
}