package net.sciencestudio.scratch;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import net.sciencestudio.scratch.encoders.serializers.Serializers;
import net.sciencestudio.scratch.list.ScratchLists;

import org.junit.Assert;


public class ListTest{

	
	@Test
	public void test() throws IOException {
		testList(ScratchLists.memoryBacked(Serializers.java()));
		testList(ScratchLists.diskBacked(Serializers.java()));
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
	
	
}
