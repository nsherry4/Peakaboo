package org.peakaboo.framework.scratch.single;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

/**
 * Represends an object which has been serialized to disk
 */
public class Scratched<T> implements ByteStorage<T>{

	File backing;
	ScratchEncoder<T> encoder;
	
		
	public static <T> Scratched<T> create(T value, ScratchEncoder<T> encoder) throws ScratchException {
		Scratched<T> c = new Scratched<>();
		c.encoder = encoder;
		try {
			c.backing = File.createTempFile("FileBacked: ", "");
		} catch (IOException e) {
			throw new ScratchException(e);
		}
		c.backing.deleteOnExit();
		c.put(value);
		return c;
	}
	
	public static <T> Scratched<T> create(ByteStorage<T> value) throws ScratchException {
		Scratched<T> c = new Scratched<>();
		try {
			c.backing = File.createTempFile("FileBacked: ", "");
		} catch (IOException e) {
			throw new ScratchException(e);
		}
		c.backing.deleteOnExit();
		c.encoder = value.getEncoder();
		c.writeBytes(value.getBytes());
		return c;
	}
	
	@Override
	public byte[] getBytes() {
		try {
			return Files.readAllBytes(backing.toPath());
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}

	@Override
	public T get() {
		return encoder.decode(getBytes());
	}
	
	private void writeBytes(byte[] data) throws ScratchException {
		try {
			FileOutputStream fos = new FileOutputStream(backing, false);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}
	
	@Override
	public void put(T value) throws ScratchException {
		writeBytes(encoder.encode(value));
	}

	@Override
	public ScratchEncoder<T> getEncoder() {
		return this.encoder;
	}

}
