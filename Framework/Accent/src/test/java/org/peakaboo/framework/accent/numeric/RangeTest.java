package org.peakaboo.framework.accent.numeric;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

@DisplayName("Range Class Tests")
public class RangeTest {

    // ========== CONSTRUCTOR TESTS ==========
    
    @Test
    @DisplayName("Basic constructor with positive step")
    public void testBasicConstructor() {
        Range r = new Range(1, 5);
        assertEquals(1, r.getStart());
        assertEquals(5, r.getStop());
        assertEquals(1, r.getStep());
    }
    
    @Test
    @DisplayName("Constructor with custom step")
    public void testConstructorWithStep() {
        Range r = new Range(2, 10, 3);
        assertEquals(2, r.getStart());
        assertEquals(10, r.getStop());
        assertEquals(3, r.getStep());
    }
    
    @Test
    @DisplayName("Constructor should reject contradictory parameters - positive step with backwards range")
    public void testConstructorRejectsBackwardsWithPositiveStep() {
        assertThrows(IllegalArgumentException.class, () -> new Range(10, 1, 2));
    }
    
    @Test
    @DisplayName("Constructor should reject contradictory parameters - negative step with forwards range")
    public void testConstructorRejectsForwardsWithNegativeStep() {
        assertThrows(IllegalArgumentException.class, () -> new Range(1, 10, -2));
    }
    
    @Test
    @DisplayName("Constructor accepts valid negative step")
    public void testConstructorWithValidNegativeStep() {
        Range r = new Range(10, 1, -2);
        assertEquals(10, r.getStart());
        assertEquals(1, r.getStop());
        assertEquals(-2, r.getStep());
    }
    
    @Test
    @DisplayName("Constructor accepts zero-length range")
    public void testConstructorZeroLength() {
        Range r = new Range(5, 5, 1);
        assertEquals(5, r.getStart());
        assertEquals(5, r.getStop());
        assertEquals(1, r.getStep());
    }

    // ========== ITERATOR FUNCTIONALITY TESTS ==========
    
    @Test
    @DisplayName("Iterator with step size 1")
    public void testIteratorStepOne() {
        Range r = new Range(1, 5, 1);
        List<Integer> values = r.asList();
        assertEquals(List.of(1, 2, 3, 4, 5), values);
    }
    
    @Test
    @DisplayName("Iterator with step size > 1")
    public void testIteratorLargerStep() {
        Range r = new Range(1, 10, 3);
        List<Integer> values = r.asList();
        assertEquals(List.of(1, 4, 7, 10), values);
    }
    
    @Test
    @DisplayName("Iterator with step size > 1 not reaching end exactly")
    public void testIteratorNotReachingEnd() {
        Range r = new Range(1, 9, 3);
        List<Integer> values = r.asList();
        assertEquals(List.of(1, 4, 7), values);
    }
    
    @Test
    @DisplayName("Iterator with negative step")
    public void testIteratorNegativeStep() {
        Range r = new Range(10, 1, -2);
        List<Integer> values = r.asList();
        assertEquals(List.of(10, 8, 6, 4, 2), values);
    }
    
    @Test
    @DisplayName("Iterator with negative step not reaching end exactly")
    public void testIteratorNegativeStepNotReachingEnd() {
        Range r = new Range(10, 2, -3);
        List<Integer> values = r.asList();
        assertEquals(List.of(10, 7, 4), values);
    }
    
    @Test
    @DisplayName("Iterator with single element range")
    public void testIteratorSingleElement() {
        Range r = new Range(5, 5, 1);
        List<Integer> values = r.asList();
        assertEquals(List.of(5), values);
    }
    
    @Test
    @DisplayName("Empty constructor creates empty iterator")
    public void testEmptyConstructor() {
        Range r = new Range();
        List<Integer> values = r.asList();
        assertTrue(values.isEmpty());
    }

    // ========== LAST() METHOD TESTS ==========
    
    @Test
    @DisplayName("last() with step size 1")
    public void testLastStepOne() {
        Range r = new Range(1, 5, 1);
        assertEquals(5, r.last());
    }
    
    @Test
    @DisplayName("last() with step size > 1, reaching end exactly")
    public void testLastReachingEnd() {
        Range r = new Range(1, 10, 3);
        assertEquals(10, r.last());
    }
    
    @Test
    @DisplayName("last() with step size > 1, not reaching end exactly")
    public void testLastNotReachingEnd() {
        Range r = new Range(1, 9, 3);
        assertEquals(7, r.last());
    }
    
    @Test
    @DisplayName("last() with negative step")
    public void testLastNegativeStep() {
        Range r = new Range(10, 1, -2);
        assertEquals(2, r.last());
    }
    
    @Test
    @DisplayName("last() with negative step not reaching end exactly")
    public void testLastNegativeStepNotReachingEnd() {
        Range r = new Range(10, 2, -3);
        assertEquals(4, r.last());
    }
    
    @Test
    @DisplayName("last() with single element range")
    public void testLastSingleElement() {
        Range r = new Range(5, 5, 1);
        assertEquals(5, r.last());
    }
    
    @Test
    @DisplayName("last() matches actual last iterated value")
    public void testLastMatchesIteration() {
        Range[] testRanges = {
            new Range(1, 10, 3),
            new Range(2, 15, 4),
            new Range(10, 1, -2),
            new Range(20, 3, -5),
            new Range(1, 9, 3),
            new Range(5, 5, 1)
        };
        
        for (Range r : testRanges) {
            List<Integer> values = r.asList();
            if (!values.isEmpty()) {
                assertEquals(values.get(values.size() - 1), r.last(), 
                    "last() doesn't match final iterated value for range " + r);
            }
        }
    }

    // ========== SIZE AND ELEMENT COUNT TESTS ==========
    
    @Test
    @DisplayName("size() returns span of range")
    public void testSize() {
        assertEquals(5, new Range(1, 5, 1).size());
        assertEquals(10, new Range(1, 10, 3).size());
        assertEquals(10, new Range(10, 1, -2).size());
        assertEquals(1, new Range(5, 5, 1).size());
    }
    
    @Test
    @DisplayName("elementCount() with step size 1")
    public void testElementCountStepOne() {
        Range r = new Range(1, 5, 1);
        assertEquals(5, r.elementCount());
    }
    
    @Test
    @DisplayName("elementCount() with step size > 1")
    public void testElementCountLargerStep() {
        Range r = new Range(1, 10, 3);
        assertEquals(4, r.elementCount()); // [1, 4, 7, 10]
    }
    
    @Test
    @DisplayName("elementCount() with step size > 1 not reaching end")
    public void testElementCountNotReachingEnd() {
        Range r = new Range(1, 9, 3);
        assertEquals(3, r.elementCount()); // [1, 4, 7]
    }
    
    @Test
    @DisplayName("elementCount() matches actual iterated count")
    public void testElementCountMatchesIteration() {
        Range[] testRanges = {
            new Range(1, 10, 1),
            new Range(1, 10, 3),
            new Range(2, 15, 4),
            new Range(10, 1, -2),
            new Range(1, 9, 3),
            new Range(5, 5, 1)
        };
        
        for (Range r : testRanges) {
            List<Integer> values = r.asList();
            assertEquals(values.size(), r.elementCount(), 
                "elementCount() doesn't match actual iteration count for range " + r);
        }
    }

    // ========== PHASE TESTS ==========
    
    @Test
    @DisplayName("phase() calculation")
    public void testPhase() {
        assertEquals(0, new Range(0, 10, 3).phase());
        assertEquals(1, new Range(1, 10, 3).phase());
        assertEquals(2, new Range(2, 10, 3).phase());
        assertEquals(0, new Range(3, 10, 3).phase());
    }

    // ========== FROM LENGTH FACTORY METHODS ==========
    
    @Test
    @DisplayName("fromLength() with default step")
    public void testFromLength() {
        Range r = Range.fromLength(5, 4);
        assertEquals(5, r.getStart());
        assertEquals(8, r.getStop());
        assertEquals(1, r.getStep());
        assertEquals(List.of(5, 6, 7, 8), r.asList());
    }
    
    @Test
    @DisplayName("fromLength() with custom step")
    public void testFromLengthWithStep() {
        Range r = Range.fromLength(1, 6, 2);  // length=6 means span from 1 to 6 (start + length - 1)
        assertEquals(1, r.getStart());
        assertEquals(6, r.getStop());  // 1 + 6 - 1 = 6
        assertEquals(2, r.getStep());
        assertEquals(List.of(1, 3, 5), r.asList());  // elements within the span [1..6] with step 2
    }

    // ========== CONTAINS TESTS ==========
    
    @Test
    @DisplayName("contains() method")
    public void testContains() {
        Range r = new Range(1, 10, 3); // [1, 4, 7, 10]
        assertTrue(r.contains(1));
        assertTrue(r.contains(4));
        assertTrue(r.contains(7));
        assertTrue(r.contains(10));
        assertFalse(r.contains(2));
        assertFalse(r.contains(3));
        assertFalse(r.contains(5));
        assertFalse(r.contains(11));
    }

    // ========== OVERLAP TESTS ==========
    
    @Test
    @DisplayName("isOverlapped() - basic overlap")
    public void testIsOverlapped() {
        Range r1 = new Range(1, 10, 1);
        Range r2 = new Range(5, 15, 1);
        assertTrue(r1.isOverlapped(r2));
        assertTrue(r2.isOverlapped(r1));
    }
    
    @Test
    @DisplayName("isOverlapped() - no overlap")
    public void testIsNotOverlapped() {
        Range r1 = new Range(1, 5, 1);
        Range r2 = new Range(10, 15, 1);
        assertFalse(r1.isOverlapped(r2));
        assertFalse(r2.isOverlapped(r1));
    }
    
    @Test
    @DisplayName("isOverlapped() - complete engulfment")
    public void testIsOverlappedEngulfed() {
        Range r1 = new Range(1, 20, 1);
        Range r2 = new Range(5, 15, 1);
        assertTrue(r1.isOverlapped(r2));
        assertTrue(r2.isOverlapped(r1));
    }
    
    @Test
    @DisplayName("isOverlapped() - touching at boundary (inclusive)")
    public void testIsOverlappedTouching() {
        Range r1 = new Range(1, 10, 1);
        Range r2 = new Range(10, 15, 1);
        assertTrue(r1.isOverlapped(r2));
        assertTrue(r2.isOverlapped(r1));
    }
	
	@Test
	@DisplayName("isOverlapped() - with larger step size")
	public void testIsOverlappedWithStep() {
		Range r1 = new Range(1, 10, 2);
		Range r2 = new Range(5, 15, 2);
		assertTrue(r1.isOverlapped(r2));
		assertTrue(r2.isOverlapped(r1));
	}

    // ========== COINCIDENT TESTS ==========
    
    @Test
    @DisplayName("isCoincident() - same step and phase")
    public void testIsCoincident() {
        Range r1 = new Range(1, 10, 3); // [1, 4, 7, 10]
        Range r2 = new Range(4, 13, 3); // [4, 7, 10, 13]
        assertTrue(r1.isCoincident(r2));
        assertTrue(r2.isCoincident(r1));
    }
    
    @Test
    @DisplayName("isCoincident() - different step sizes")
    public void testNotCoincidentDifferentStep() {
        Range r1 = new Range(1, 10, 2);
        Range r2 = new Range(1, 10, 3);
        assertFalse(r1.isCoincident(r2));
        assertFalse(r2.isCoincident(r1));
    }
    
    @Test
    @DisplayName("isCoincident() - same step, different phase")
    public void testNotCoincidentDifferentPhase() {
        Range r1 = new Range(1, 10, 3); // phase = 1
        Range r2 = new Range(2, 11, 3); // phase = 2
        assertFalse(r1.isCoincident(r2));
        assertFalse(r2.isCoincident(r1));
    }
    
    @Test
    @DisplayName("isCoincident() - no overlap")
    public void testNotCoincidentNoOverlap() {
        Range r1 = new Range(1, 5, 1);
        Range r2 = new Range(10, 15, 1);
        assertFalse(r1.isCoincident(r2));
        assertFalse(r2.isCoincident(r1));
    }

    // ========== ADJACENT TESTS ==========
    
    @Test
    @DisplayName("isAdjacent() - touching ranges")
    public void testIsAdjacent() {
        Range r1 = new Range(1, 7, 3); // [1, 4, 7]
        Range r2 = new Range(10, 13, 3); // [10, 13]
        assertTrue(r1.isAdjacent(r2));
        assertTrue(r2.isAdjacent(r1));
    }
    
    @Test
    @DisplayName("isAdjacent() - different step sizes")
    public void testNotAdjacentDifferentStep() {
        Range r1 = new Range(1, 7, 2);
        Range r2 = new Range(9, 15, 3);
        assertFalse(r1.isAdjacent(r2));
        assertFalse(r2.isAdjacent(r1));
    }
    
    @Test
    @DisplayName("isAdjacent() - not adjacent")
    public void testNotAdjacent() {
        Range r1 = new Range(1, 7, 3);
        Range r2 = new Range(15, 20, 3);
        assertFalse(r1.isAdjacent(r2));
        assertFalse(r2.isAdjacent(r1));
    }

    // ========== TOUCHING TESTS ==========
    
    @Test
    @DisplayName("isTouching() - coincident ranges")
    public void testIsTouchingCoincident() {
        Range r1 = new Range(1, 10, 3);
        Range r2 = new Range(4, 13, 3);
        assertTrue(r1.isTouching(r2));
    }
    
    @Test
    @DisplayName("isTouching() - adjacent ranges")
    public void testIsTouchingAdjacent() {
        Range r1 = new Range(1, 7, 3);
        Range r2 = new Range(10, 13, 3);
        assertTrue(r1.isTouching(r2));
    }
    
    @Test
    @DisplayName("isTouching() - not touching")
    public void testNotTouching() {
        Range r1 = new Range(1, 5, 1);
        Range r2 = new Range(10, 15, 1);
        assertFalse(r1.isTouching(r2));
    }

    // ========== MERGE TESTS ==========
    
    @Test
    @DisplayName("merge() - touching ranges")
    public void testMerge() {
        Range r1 = new Range(1, 7, 3);
        Range r2 = new Range(10, 13, 3);
        Range merged = r1.merge(r2);
        assertNotNull(merged);
        assertEquals(1, merged.getStart());
        assertEquals(13, merged.getStop());
        assertEquals(3, merged.getStep());
    }
    
    @Test
    @DisplayName("merge() - non-touching ranges")
    public void testMergeNonTouching() {
        Range r1 = new Range(1, 5, 1);
        Range r2 = new Range(10, 15, 1);
        Range merged = r1.merge(r2);
        assertNull(merged);
    }

    // ========== DIFFERENCE TESTS ==========
    
    @Test
    @DisplayName("difference() - overlapping ranges")
    public void testDifference() {
        Range r1 = new Range(1, 15, 3); // [1, 4, 7, 10, 13]
        Range r2 = new Range(4, 10, 3);  // [4, 7, 10]
        RangeSet diff = r1.difference(r2);
        
        // Should result in two ranges: [1, 1] and [13, 15]
        assertEquals(2, diff.getRanges().size());
		assertEquals(1, diff.getRanges().get(0).elementCount());
    }
    
    @Test
    @DisplayName("difference() - non-overlapping ranges")
    public void testDifferenceNonOverlapping() {
        Range r1 = new Range(1, 5, 1);
        Range r2 = new Range(10, 15, 1);
        RangeSet diff = r1.difference(r2);
        
        // Should result in original range
        assertEquals(1, diff.getRanges().size());
        assertEquals(r1.getStart(), diff.getRanges().get(0).getStart());
        assertEquals(r1.getStop(), diff.getRanges().get(0).getStop());
    }

    // ========== STRING REPRESENTATION TESTS ==========
    
    @Test
    @DisplayName("toString() representation")
    public void testToString() {
        assertEquals("[1..5]", new Range(1, 5, 1).toString());
        assertEquals("[1..10:3]", new Range(1, 10, 3).toString());
        assertEquals("[10..1:-2]", new Range(10, 1, -2).toString());
    }

    // ========== STEP SIZE TESTS ==========
    
    @Test
    @DisplayName("stepSize() returns absolute value")
    public void testStepSize() {
        assertEquals(1, new Range(1, 5, 1).stepSize());
        assertEquals(3, new Range(1, 10, 3).stepSize());
        assertEquals(2, new Range(10, 1, -2).stepSize()); // abs(-2) = 2
        assertEquals(5, new Range(20, 1, -5).stepSize()); // abs(-5) = 5
    }

    // ========== SIZE WITH NEGATIVE RANGES ==========
    
    @Test
    @DisplayName("size() works correctly with negative ranges")
    public void testSizeNegativeRanges() {
        assertEquals(5, new Range(1, 5, 1).size());      // forward: 5-1+1 = 5
        assertEquals(10, new Range(10, 1, -1).size());   // backward: abs(1-10)+1 = 10
        assertEquals(20, new Range(1, 20, 1).size());    // forward: 20-1+1 = 20
        assertEquals(20, new Range(20, 1, -1).size());   // backward: abs(1-20)+1 = 20
    }

    // ========== ELEMENT COUNT ACCURACY ==========
    
    @Test
    @DisplayName("elementCount() accuracy for various scenarios")
    public void testElementCountAccuracy() {
        // Test cases that previously might have had rounding issues
        assertEquals(4, new Range(0, 9, 3).elementCount());   // [0, 3, 6, 9]
        assertEquals(3, new Range(0, 8, 3).elementCount());   // [0, 3, 6]
        assertEquals(4, new Range(1, 10, 3).elementCount());  // [1, 4, 7, 10]
        assertEquals(3, new Range(1, 9, 3).elementCount());   // [1, 4, 7]
        
        // Negative step cases
        assertEquals(5, new Range(10, 2, -2).elementCount()); // [10, 8, 6, 4, 2]
        assertEquals(4, new Range(10, 3, -2).elementCount()); // [10, 8, 6, 4]
    }

    // ========== PHASE EDGE CASES ==========
    
    @Test
    @DisplayName("phase() with negative steps")
    public void testPhaseNegativeSteps() {
        // Valid negative step ranges: start > stop when step < 0
        assertEquals(1, new Range(10, 1, -3).phase());  // 10 % -3 = 1
        assertEquals(2, new Range(11, 1, -3).phase());  // 11 % -3 = 2  
        assertEquals(0, new Range(12, 1, -3).phase());  // 12 % -3 = 0
    }

    // ========== ZERO STEP VALIDATION ==========
    
    @Test
    @DisplayName("Constructor should reject zero step")
    public void testConstructorRejectsZeroStep() {
        assertThrows(IllegalArgumentException.class, () -> new Range(1, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> new Range(10, 1, 0));
    }

    // ========== CONTAINS METHOD OPTIMIZATION ==========
    
    @Test
    @DisplayName("contains() with large ranges (performance test)")
    public void testContainsLargeRange() {
        Range r = new Range(1, 1000, 7);
        assertTrue(r.contains(1));
        assertTrue(r.contains(8));   // 1 + 7
        assertTrue(r.contains(15));  // 1 + 2*7
        assertFalse(r.contains(2));
        assertFalse(r.contains(9));
        assertFalse(r.contains(1001));
    }

    // ========== EDGE CASES ==========
    
    @Test
    @DisplayName("Large step sizes")
    public void testLargeStepSizes() {
        Range r = new Range(1, 100, 50);
        List<Integer> values = r.asList();
        assertEquals(List.of(1, 51), values);
        assertEquals(51, r.last());
        assertEquals(2, r.elementCount());
    }
    
    @Test
    @DisplayName("Step size larger than range")
    public void testStepLargerThanRange() {
        Range r = new Range(1, 5, 10);
        List<Integer> values = r.asList();
        assertEquals(List.of(1), values);
        assertEquals(1, r.last());
        assertEquals(1, r.elementCount());
    }
    
    @Test
    @DisplayName("Negative ranges with large steps")
    public void testNegativeRangeLargeStep() {
        Range r = new Range(100, 1, -30);
        List<Integer> values = r.asList();
        assertEquals(List.of(100, 70, 40, 10), values);
        assertEquals(10, r.last());
        assertEquals(4, r.elementCount());
    }

    // ========== BOUNDARY CONDITIONS ==========
    
    @Test
    @DisplayName("Minimum and maximum integer ranges")
    public void testExtremeIntegerRanges() {
        // Test with large integers (avoiding overflow)
        Range r1 = new Range(Integer.MAX_VALUE - 10, Integer.MAX_VALUE, 5);
        assertEquals(3, r1.elementCount()); // Should have 3 elements
        
        Range r2 = new Range(Integer.MIN_VALUE, Integer.MIN_VALUE + 10, 5);
        assertEquals(3, r2.elementCount()); // Should have 3 elements
    }

    // ========== CONSISTENCY CHECKS ==========
    
    @Test
    @DisplayName("Consistency between size, elementCount, and actual iteration")
    public void testConsistencyChecks() {
        Range[] testRanges = {
            new Range(1, 10, 1),
            new Range(1, 10, 3),
            new Range(0, 9, 3),
            new Range(10, 1, -1),
            new Range(10, 1, -3),
            new Range(5, 5, 1),
            new Range(1, 100, 50)
        };
        
        for (Range r : testRanges) {
            List<Integer> values = r.asList();
            
            // elementCount should match actual iteration count
            assertEquals(values.size(), r.elementCount(), 
                "elementCount mismatch for " + r);
            
            // last() should match final iterated value (if not empty)
            if (!values.isEmpty()) {
                assertEquals(values.get(values.size() - 1), r.last(),
                    "last() mismatch for " + r);
            }
            
            // size() should be the span regardless of step
            int expectedSize = Math.abs(r.getStop() - r.getStart()) + 1;
            assertEquals(expectedSize, r.size(),
                "size() mismatch for " + r);
        }
    }
}