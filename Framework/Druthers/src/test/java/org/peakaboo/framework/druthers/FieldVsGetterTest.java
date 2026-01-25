package org.peakaboo.framework.druthers;

/**
 * Test class to determine field vs getter/setter precedence.
 * Has both a public field AND public getter/setter with side effects.
 * Also has a package-private field to test default visibility behavior.
 */
public class FieldVsGetterTest {
	// Public field - accessible for direct field access
	public String value;

	// Package-private field (no modifier) - tests default visibility
	String packagePrivateValue;

	// Flags to track which accessor was used
	public boolean getterCalled = false;
	public boolean setterCalled = false;
	public boolean packagePrivateGetterCalled = false;
	public boolean packagePrivateSetterCalled = false;

	public FieldVsGetterTest() {}

	public FieldVsGetterTest(String value) {
		this.value = value;
		this.packagePrivateValue = value + "-package";
	}

	// Getter with side effect
	public String getValue() {
		this.getterCalled = true;
		return this.value;
	}

	// Setter with side effect
	public void setValue(String value) {
		this.setterCalled = true;
		this.value = value;
	}

	// Getter for package-private field with side effect
	public String getPackagePrivateValue() {
		this.packagePrivateGetterCalled = true;
		return this.packagePrivateValue;
	}

	// Setter for package-private field with side effect
	public void setPackagePrivateValue(String value) {
		this.packagePrivateSetterCalled = true;
		this.packagePrivateValue = value;
	}

	// Reset flags for testing
	public void resetFlags() {
		this.getterCalled = false;
		this.setterCalled = false;
		this.packagePrivateGetterCalled = false;
		this.packagePrivateSetterCalled = false;
	}
}
