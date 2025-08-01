package org.peakaboo.framework.scratch.list;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;


public class ListTest{

	
	@Test
	public void test() throws Exception {
		testList(ScratchLists.memoryBacked(Serializers.java()));
		
		// Test disk-backed list with proper resource management
		ScratchList<String> diskList = ScratchLists.diskBacked(Serializers.java());
		try {
			testList(diskList);
		} finally {
			if (diskList instanceof AutoCloseable) {
				((AutoCloseable) diskList).close();
			}
		}
	}
	
	
	private void populate(List<String> list) {
		list.add("Hello World");
		list.add("Hello There");
		list.add("Hi There");
		list.add("Goodbye");
	}
	
	private void testList(List<String> list) {
		populate(list);
		Assert.assertEquals(list.get(0), "Hello World");
		Assert.assertEquals(list.size(), 4);
		list.remove(1);
		Assert.assertEquals(list.get(2), "Goodbye");
		Assert.assertEquals(list.size(), 3);
	}
	

	@Test
	public void twolists() throws Exception {
		ScratchList<String> l1 = ScratchLists.diskBacked(new CompoundEncoder<>(Serializers.kryo(String.class), Compressors.lz4fast()));
		ScratchList<Integer> l2 = ScratchLists.diskBacked(new CompoundEncoder<>(Serializers.kryo(Integer.class), Compressors.lz4fast()));
		
		try {
			l1.add("A");
			l1.add("B");
			
			l2.add(1);
			l2.add(2);
			
			l1.add("E");
			l1.add("F");
				
			Assert.assertEquals(l1.get(0), "A");
			Assert.assertEquals(l2.get(1), new Integer(2));
			Assert.assertEquals(l1.get(3), "F");
		} finally {
			if (l1 instanceof AutoCloseable) {
				((AutoCloseable) l1).close();
			}
			if (l2 instanceof AutoCloseable) {
				((AutoCloseable) l2).close();
			}
		}
	}
	
}
