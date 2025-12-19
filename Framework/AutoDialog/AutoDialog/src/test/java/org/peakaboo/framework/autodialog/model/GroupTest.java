package org.peakaboo.framework.autodialog.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.autodialog.model.classinfo.IntegerClassInfo;
import org.peakaboo.framework.autodialog.model.classinfo.StringClassInfo;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

/**
 * Tests for the Group class, focusing on proper constructor usage,
 * serialization/deserialization, and visitor pattern.
 */
public class GroupTest {

	@Test
	public void testNestedGroups() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());
		Parameter<Integer> p2 = new Parameter<>("Param2", new SimpleStyle<>("integer", CoreStyle.INTEGER), 20, new IntegerClassInfo());

		// Create groups using constructor (correct pattern)
		Group subGroup = new Group("SubGroup", p1);
		Group topGroup = new Group("TopGroup", subGroup, p2);

		// Verify structure
		assertEquals(2, topGroup.getValue().size());
		assertTrue(topGroup.getValue().get(0) instanceof Group);
		assertTrue(topGroup.getValue().get(1) instanceof Parameter);
	}

	@Test
	public void testVisitor() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());
		Parameter<Integer> p2 = new Parameter<>("Param2", new SimpleStyle<>("integer", CoreStyle.INTEGER), 20, new IntegerClassInfo());
		Parameter<Integer> p3 = new Parameter<>("Param3", new SimpleStyle<>("integer", CoreStyle.INTEGER), 30, new IntegerClassInfo());

		// Create nested structure
		Group subGroup = new Group("SubGroup", p2);
		Group topGroup = new Group("TopGroup", p1, subGroup, p3);

		// Visit all values and count them
		final int[] count = {0};
		topGroup.visit(v -> count[0]++);

		// Should visit: p1, subGroup (and its children: p2), p3 = 4 values total
		assertEquals(4, count[0]);
	}

	@Test
	public void testMapSerialization() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 42, new IntegerClassInfo());
		Parameter<String> p2 = new Parameter<>("Param2", new SimpleStyle<>("text", CoreStyle.TEXT_VALUE), "test", new StringClassInfo());

		// Create group using constructor
		Group group = new Group("Test Group", p1, p2);

		// Serialize
		Map<String, Object> serialized = group.serialize();

		// Verify serialization
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertTrue(serialized.containsKey(p1.getSlug()));
		assertTrue(serialized.containsKey(p2.getSlug()));
	}

	@Test
	public void testMapDeserialization() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 0, new IntegerClassInfo());
		Parameter<String> p2 = new Parameter<>("Param2", new SimpleStyle<>("text", CoreStyle.TEXT_VALUE), "", new StringClassInfo());

		// Create group using constructor
		Group group = new Group("Test Group", p1, p2);

		// Create serialized data
		Map<String, Object> data = new HashMap<>();
		data.put(p1.getSlug(), "99");
		data.put(p2.getSlug(), "deserialized");

		// Deserialize
		group.deserialize(data);

		// Verify
		assertEquals(Integer.valueOf(99), p1.getValue());
		assertEquals("deserialized", p2.getValue());
	}

	@Test
	public void testNestedGroupSerialization() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());
		Parameter<Integer> p2 = new Parameter<>("Param2", new SimpleStyle<>("integer", CoreStyle.INTEGER), 20, new IntegerClassInfo());

		// Create nested groups using constructor
		Group subGroup = new Group("SubGroup", p2);
		Group topGroup = new Group("TopGroup", p1, subGroup);

		// Serialize
		Map<String, Object> serialized = topGroup.serialize();

		// Verify
		assertNotNull(serialized);
		assertTrue(serialized.containsKey(p1.getSlug()));
		assertTrue(serialized.containsKey(subGroup.getSlug()));
		assertTrue(serialized.get(subGroup.getSlug()) instanceof Map);
	}

	@Test
	public void testNestedGroupDeserialization() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 0, new IntegerClassInfo());
		Parameter<Integer> p2 = new Parameter<>("Param2", new SimpleStyle<>("integer", CoreStyle.INTEGER), 0, new IntegerClassInfo());

		// Create nested groups using constructor
		Group subGroup = new Group("SubGroup", p2);
		Group topGroup = new Group("TopGroup", p1, subGroup);

		// Create nested serialized data
		Map<String, Object> subData = new HashMap<>();
		subData.put(p2.getSlug(), "50");

		Map<String, Object> topData = new HashMap<>();
		topData.put(p1.getSlug(), "100");
		topData.put(subGroup.getSlug(), subData);

		// Deserialize
		topGroup.deserialize(topData);

		// Verify
		assertEquals(Integer.valueOf(100), p1.getValue());
		assertEquals(Integer.valueOf(50), p2.getValue());
	}

	@Test
	public void testValueChangeNotification() {
		// Create parameter
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		// Create group using constructor
		Group group = new Group("Test Group", p1);

		// Track notifications
		final int[] notificationCount = {0};
		group.getValueHook().addListener(v -> notificationCount[0]++);

		// Change parameter value
		p1.setValue(20);

		// Verify group was notified
		assertEquals(1, notificationCount[0]);
	}

	@Test
	public void testDeserializationWithMissingKeys() {
		// Create parameters
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());
		Parameter<Integer> p2 = new Parameter<>("Param2", new SimpleStyle<>("integer", CoreStyle.INTEGER), 20, new IntegerClassInfo());

		// Create group using constructor
		Group group = new Group("Test Group", p1, p2);

		// Create incomplete serialized data (missing p2)
		Map<String, Object> data = new HashMap<>();
		data.put(p1.getSlug(), "99");

		// Deserialize (should not throw, just skip missing keys)
		group.deserialize(data);

		// Verify p1 was updated, p2 unchanged
		assertEquals(Integer.valueOf(99), p1.getValue());
		assertEquals(Integer.valueOf(20), p2.getValue());
	}

	@Test
	public void testSlugVersusNameDeserialization() {
		// Create parameter
		Parameter<Integer> p1 = new Parameter<>("Param1", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		// Create group using constructor
		Group group = new Group("Test Group", p1);

		// Create data using slug (preferred over name)
		Map<String, Object> dataWithSlug = new HashMap<>();
		dataWithSlug.put(p1.getSlug(), "50");
		dataWithSlug.put(p1.getName(), "25");  // Should be ignored in favor of slug

		// Deserialize
		group.deserialize(dataWithSlug);

		// Verify slug value was used
		assertEquals(Integer.valueOf(50), p1.getValue());
	}
}
