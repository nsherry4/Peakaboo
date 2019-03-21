package cyclops;

import java.util.ArrayList;

import org.junit.Test;

import cyclops.SparsedList;
import junit.framework.Assert;

public class SparsedListTests {

	@Test
	public void test() {

		ArrayList<Integer> backer = new ArrayList<>();
		SparsedList<Integer> list = new SparsedList<>(backer);
		
		list.add(10, 10);
		Assert.assertEquals(list.get(10), new Integer(10));
		Assert.assertEquals(list.get(9), null);

		list.set(20, 20);
		Assert.assertEquals(list.get(20), new Integer(20));
		Assert.assertEquals(list.get(15), null);

		list.set(5, 5);
		Assert.assertEquals(list.get(5), new Integer(5));
		Assert.assertEquals(list.get(10), new Integer(10));
		Assert.assertEquals(list.get(15), null);

	}
	
}
