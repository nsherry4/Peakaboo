package org.peakaboo.framework.scratch;

/**
 * Thrown when a disk-backed scratch store can't secure or write to its backing file,
 * most often because the disk is full.
 */
public class ScratchSpaceException extends ScratchException {

	public ScratchSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

}
