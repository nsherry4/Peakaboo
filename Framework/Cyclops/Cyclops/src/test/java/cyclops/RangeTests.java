package cyclops;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.RangeSet;

public class RangeTests {

	Range r1 = new Range(1, 4);
	Range r2 = new Range(2, 7);
	Range r3 = new Range(4, 8);
	Range r4 = new Range(5, 8);
	Range r5 = new Range(0, 8, 2);
	Range r6 = new Range(8, 16, 2);
	Range r7 = new Range(4, 1, -1);
	Range r8 = new Range(1, 9, 3);
	Range r9 = new Range(2, 9, 3);
	Range r10 = new Range(-20, -10);
	Range r11 = new Range(-15, -14);
	
	@Test
	public void range() {
		

		
		//test bounds
		assertFalse(r1.contains(0));
		assertTrue(r1.contains(1));
		assertTrue(r1.contains(3));
		assertFalse(r1.contains(4));
		assertTrue(r5.contains(0));
		assertFalse(r5.contains(1));
		
		assertFalse(r10.contains(-10));
		assertTrue(r10.contains(-11));
		assertTrue(r10.contains(-20));
		assertFalse(r10.contains(-21));
				
		assertTrue(r5.last() == 6);
		assertTrue(r8.last() == 7);
		assertTrue(r9.last() == 8);
		
		assertTrue(r8.phase() == 1);
		assertTrue(r9.phase() == 2);
		
		assertTrue(r1.elementCount() == 3);
		assertTrue(r1.next(1) == 2);
		assertTrue(r1.next(3) == null);
		
		assertTrue(r1.isOverlapped(r2));
		assertFalse(r1.isOverlapped(r7)); //TODO: This is currently returning False because the step is -1 so start and stop are flipped
		assertTrue(r1.isOverlapped(r5));
		assertFalse(r1.isOverlapped(r3));
		assertFalse(r3.isOverlapped(r1));
		assertTrue(r7.isOverlapped(r1));
		
		assertFalse(r1.isAdjacent(r2));
		assertTrue(r1.isAdjacent(r3));
		assertFalse(r1.isAdjacent(r4));
		assertTrue(r5.isAdjacent(r6));
		
		assertTrue(r1.isCoincident(r2));
		assertFalse(r5.isCoincident(r6));
		assertTrue(r5.isCoincident(new Range(2, 10, 2)));
		
		assertTrue(r1.isTouching(r2));
		assertTrue(r1.isTouching(r3));
		assertFalse(r1.isTouching(r4));
		
	}
	
	@Test
	public void rangeset() {
		
		//Two adjacent ranges
		var rs1 = new RangeSet();
		rs1.addRange(r1);
		rs1.addRange(r3);
		
		//Two non-adjacent ranges
		var rs2 = new RangeSet();
		rs2.addRange(r1);
		rs2.addRange(r4);
		
		//Two ranges which occupy the same rough bounds, but are out of phase
		var rs3 = new RangeSet();
		rs3.addRange(r8);
		rs3.addRange(r9);
		
		//Same as rs3, but we remove part of one of the earlier ranges
		var rs4 = new RangeSet(rs3);
		rs4.removeRange(new Range(1, 6, 3));
		
		
		var rs5 = new RangeSet();
		rs5.addRange(r10);
		rs5.removeRange(r11);
		
		//adjacent ranges should be merged
		assertTrue(rs1.getRanges().size() == 1);
		assertTrue(rs2.getRanges().size() == 2);
		assertTrue(rs3.getRanges().size() == 2);
		assertTrue(rs4.getRanges().size() == 2);
		
		assertTrue(rs1.contains(4));
		assertFalse(rs2.contains(4));
		assertTrue(rs3.contains(4));
		assertFalse(rs4.contains(4));
		
		assertTrue(rs5.contains(-16));
		assertFalse(rs5.contains(-15));
		assertTrue(rs5.contains(-14));
		
	}
	
}
