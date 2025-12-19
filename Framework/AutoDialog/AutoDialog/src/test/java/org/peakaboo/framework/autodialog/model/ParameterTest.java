package org.peakaboo.framework.autodialog.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.peakaboo.framework.autodialog.model.classinfo.IntegerClassInfo;
import org.peakaboo.framework.autodialog.model.classinfo.StringClassInfo;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

/**
 * Tests for the Parameter class, focusing on validation and notification behavior.
 */
public class ParameterTest {

	@Test
	public void testBasicParameterCreation() {
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 42, new IntegerClassInfo());

		assertEquals("Test", param.getName());
		assertEquals(Integer.valueOf(42), param.getValue());
		assertTrue(param.isEnabled());
	}

	@Test
	public void testSetValue() {
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		boolean result = param.setValue(20);

		assertTrue(result);
		assertEquals(Integer.valueOf(20), param.getValue());
	}

	@Test
	public void testValidationSuccess() {
		// Create parameter with validator that accepts values < 10
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 5, new IntegerClassInfo(),
			p -> p.getValue() != null && p.getValue() < 10);

		// Try to set valid value
		boolean result = param.setValue(7);

		assertTrue(result);
		assertEquals(Integer.valueOf(7), param.getValue());
	}

	@Test
	public void testValidationFailure() {
		// Create parameter with validator that accepts values < 10
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 5, new IntegerClassInfo(),
			p -> p.getValue() != null && p.getValue() < 10);

		// Try to set invalid value
		boolean result = param.setValue(15);

		assertFalse(result);
		// Value should be reverted to original
		assertEquals(Integer.valueOf(5), param.getValue());
	}

	@Test
	public void testValueChangeNotification() {
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		final Integer[] notifiedValue = {null};
		param.getValueHook().addListener(v -> notifiedValue[0] = v);

		param.setValue(20);

		// Listener should be notified with new value
		assertEquals(Integer.valueOf(20), notifiedValue[0]);
	}

	@Test
	public void testValidationFailureNotification() {
		// Create parameter with validator that accepts values < 10
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 5, new IntegerClassInfo(),
			p -> p.getValue() != null && p.getValue() < 10);

		final Integer[] notifiedValue = {null};
		final int[] notificationCount = {0};
		param.getValueHook().addListener(v -> {
			notifiedValue[0] = v;
			notificationCount[0]++;
		});

		// Try to set invalid value
		boolean result = param.setValue(15);

		assertFalse(result);
		// When validation fails, listener is notified with the current (reverted) value
		assertEquals(1, notificationCount[0]);
		// Notified with reverted value, not rejected value
		assertEquals(Integer.valueOf(5), notifiedValue[0]);
		// Parameter value is unchanged
		assertEquals(Integer.valueOf(5), param.getValue());
	}

	@Test
	public void testEnabledState() {
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		assertTrue(param.isEnabled());

		param.setEnabled(false);
		assertFalse(param.isEnabled());

		param.setEnabled(true);
		assertTrue(param.isEnabled());
	}

	@Test
	public void testEnabledNotification() {
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		final Boolean[] notifiedEnabled = {null};
		param.getEnabledHook().addListener(e -> notifiedEnabled[0] = e);

		param.setEnabled(false);

		assertEquals(Boolean.FALSE, notifiedEnabled[0]);
	}

	@Test
	public void testSerializeDeserialize() {
		Parameter<String> param = new Parameter<>("Test", new SimpleStyle<>("text", CoreStyle.TEXT_VALUE), "initial", new StringClassInfo());

		// Serialize
		String serialized = param.serialize();
		assertEquals("initial", serialized);

		// Deserialize
		param.deserialize("updated");
		assertEquals("updated", param.getValue());
	}

	@Test(expected = ClassCastException.class)
	public void testTypeSafety() {
		// Create Integer parameter
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), 10, new IntegerClassInfo());

		// Try to set wrong type value (will throw at runtime due to type erasure)
		// This test verifies the ClassInfo type checking
		@SuppressWarnings("unchecked")
		Parameter<Object> unsafeParam = (Parameter<Object>) (Parameter<?>) param;
		unsafeParam.setValue("wrong type");  // Should throw ClassCastException
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNullValueWithoutClassInfo() {
		// Cannot create parameter with null value and no ClassInfo
		new Parameter<>("Test", new SimpleStyle<>("text", CoreStyle.TEXT_VALUE), null);
	}

	@Test
	public void testNullValueWithClassInfo() {
		// Can create parameter with null value if ClassInfo is provided
		Parameter<Integer> param = new Parameter<>("Test", new SimpleStyle<>("integer", CoreStyle.INTEGER), null, new IntegerClassInfo());

		assertNull(param.getValue());

		// Can set non-null value
		param.setValue(42);
		assertEquals(Integer.valueOf(42), param.getValue());
	}
}
