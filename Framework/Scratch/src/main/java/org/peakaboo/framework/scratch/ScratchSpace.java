package org.peakaboo.framework.scratch;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Temporary storage space allocator and manager for disk-backed data structures. Different
 * platforms may have different implementations and requirements like pre-allocation. Must
 * be thread-safe.
 */
public interface ScratchSpace {

	/** Create a new, empty scratch file. Caller must call {@link #release(File)} to close. */
	File create() throws IOException;

	/**
	 * Requests that storage space be held for this file's future use. May preallocate or raise
	 * the file's reported length, so length of file can not be guaranteed to be the same as
	 * length of data written.
	 * @return the amount of space now reserved, may be more than requested.
	 * @throws IOException if space can't be guaranteed
	 */
	long reserve(RandomAccessFile file, long length) throws IOException;

	/** Clean up a scratch file {@link #create()}. Only call once after closing all handles */
	void release(File file);


	/** System temp directory, no reservation, deleted on release or JVM exit */
	ScratchSpace DEFAULT = new ScratchSpace() {

		@Override
		public File create() throws IOException {
			File file = File.createTempFile("ScratchDiskList [temp - ", "]");
			file.deleteOnExit();
			return file;
		}

		@Override
		public long reserve(RandomAccessFile file, long length) {
			// No reservation required.
			// Returning MAX_VALUE instead of `length` catches misuse of the return value early.
			return Long.MAX_VALUE;
		}

		@Override
		public void release(File file) {
			if (file.exists()) {
				file.delete();
			}
		}

	};

}
