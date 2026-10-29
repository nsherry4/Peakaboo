package org.peakaboo.framework.scratch.list.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.scratch.ScratchSpace;
import org.peakaboo.framework.scratch.ScratchSpaceException;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class ScratchSpaceTest {

	private static final int CHUNK = 4096;
	private static final int ENTRIES = 100;
	private static final int ENTRY_SIZE = 1000;

	/** Preallocates the way Android's StorageManager.allocateBytes does, by extending the file */
	private static class RecordingSpace implements ScratchSpace {
		File created;
		int creates, reserves, releases;

		@Override
		public File create() throws IOException {
			creates++;
			created = ScratchSpace.DEFAULT.create();
			return created;
		}

		@Override
		public long reserve(RandomAccessFile file, long length) throws IOException {
			reserves++;
			long target = length + CHUNK;
			file.setLength(target);
			return target;
		}

		@Override
		public void release(File file) {
			releases++;
			ScratchSpace.DEFAULT.release(file);
		}
	}

	private static byte[] entry(int i) {
		byte[] data = new byte[ENTRY_SIZE];
		Arrays.fill(data, (byte) i);
		return data;
	}

	@Test
	public void lifecycle() throws Exception {
		RecordingSpace space = new RecordingSpace();
		ScratchDiskList<String> list = new ScratchDiskList<>(Serializers.java(), space);
		list.add("Hello World");
		list.add("Goodbye");
		Assert.assertEquals("Goodbye", list.get(1));
		list.close();
		list.close();

		Assert.assertEquals(1, space.creates);
		Assert.assertEquals(1, space.releases);
		Assert.assertFalse(space.created.exists());
	}

	@Test
	public void reservationDoesNotMoveAppends() throws Exception {
		RecordingSpace space = new RecordingSpace();
		ScratchDiskBacking backing = new ScratchDiskBacking(space);
		try {
			for (int i = 0; i < ENTRIES; i++) {
				backing.add(i, entry(i));
			}
			for (int i = 0; i < ENTRIES; i++) {
				Assert.assertArrayEquals(entry(i), backing.get(i));
			}

			// If appends followed the file's length, each reservation would push the next write past it
			long length = space.created.length();
			Assert.assertTrue("file grew to " + length, length <= (long) ENTRIES * ENTRY_SIZE + CHUNK);
			Assert.assertTrue(space.reserves < ENTRIES);
		} finally {
			backing.close();
		}
	}

	@Test
	public void reserveFailureIsReported() throws Exception {
		IOException diskFull = new IOException("disk full");
		ScratchSpace space = new RecordingSpace() {
			@Override
			public long reserve(RandomAccessFile file, long length) throws IOException {
				throw diskFull;
			}
		};
		ScratchDiskBacking backing = new ScratchDiskBacking(space);
		try {
			backing.add(0, entry(0));
			Assert.fail("expected a ScratchSpaceException");
		} catch (ScratchSpaceException e) {
			Assert.assertSame(diskFull, e.getCause());
		} finally {
			backing.close();
		}
	}

	@Test
	public void defaultReservesOnce() throws Exception {
		int[] reserves = {0};
		ScratchSpace counting = new ScratchSpace() {
			@Override
			public File create() throws IOException {
				return DEFAULT.create();
			}

			@Override
			public long reserve(RandomAccessFile file, long length) throws IOException {
				reserves[0]++;
				return DEFAULT.reserve(file, length);
			}

			@Override
			public void release(File file) {
				DEFAULT.release(file);
			}
		};
		ScratchDiskBacking backing = new ScratchDiskBacking(counting);
		try {
			for (int i = 0; i < ENTRIES; i++) {
				backing.add(i, entry(i));
			}
		} finally {
			backing.close();
		}
		Assert.assertEquals(1, reserves[0]);
	}

}
