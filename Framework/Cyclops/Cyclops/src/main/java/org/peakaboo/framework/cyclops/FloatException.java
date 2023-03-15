package org.peakaboo.framework.cyclops;

public class FloatException extends RuntimeException {

	public static enum Issue {
		NaN,
		Infinite,
	}
		
	private Issue issue;
	
	public FloatException(Issue issue, String message) {
		super(message);
		this.issue = issue;
	}
	
	/**
	 * Return the Issue indicating the specific cause of the failure
	 */
	public Issue getIssue() {
		return issue;
	}


	/**
	 * Returns true if the value is a 'normal' number
	 */
	public static boolean valid(float value) {
		if (Float.isNaN(value)) return false;
		if (Float.isInfinite(value)) return false;
		return true;
	}
	
	/**
	 * Returns true if the value is a 'normal' number
	 */
	public static boolean valid(double value) {
		if (Double.isNaN(value)) return false;
		if (Double.isInfinite(value)) return false;
		return true;
	}
	
	/**
	 * Raises a FloatException if this value is not a 'normal' number. Includes a custom message
	 */
	public static void guard(double value, String message) {
		if (Double.isNaN(value)) throw new FloatException(Issue.NaN, message);
		if (Double.isInfinite(value)) throw new FloatException(Issue.Infinite, message);
	}
	
	/**
	 * Raises a FloatException if this value is not a 'normal' number. Includes a custom message
	 */
	public static void guard(float value, String message) {
		if (Float.isNaN(value)) throw new FloatException(Issue.NaN, message);
		if (Float.isInfinite(value)) throw new FloatException(Issue.Infinite, message);
	}

	/**
	 * Raises a FloatException if this value is not a 'normal' number.
	 */
	public static void guard(double value) {
		guard(value, "Floating point calculation failed");
	}
	
	/**
	 * Raises a FloatException if this value is not a 'normal' number.
	 */
	public static void guard(float value) {
		guard(value, "Floating point calculation failed");
	}
	
	
}
