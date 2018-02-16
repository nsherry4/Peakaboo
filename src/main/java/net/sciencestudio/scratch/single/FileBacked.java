package net.sciencestudio.scratch.single;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;

public class FileBacked<T> implements ByteStorage<T>{

	File backing;
	ScratchEncoder<T> encoder;
	
		
	public static <T> FileBacked<T> create(T value, ScratchEncoder<T> encoder) throws IOException {
		FileBacked<T> c = new FileBacked<>();
		c.encoder = encoder;
		c.backing = File.createTempFile("FileBacked: ", "");
		c.backing.deleteOnExit();
		c.writeBytes(encoder.encode(value));
		return c;
	}
	
	public static <T> FileBacked<T> create(ByteStorage<T> value) throws IOException {
		FileBacked<T> c = new FileBacked<>();
		c.backing = File.createTempFile("FileBacked: ", "");
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
	
	private void writeBytes(byte[] data) throws IOException {
		FileOutputStream fos = new FileOutputStream(backing);
		fos.write(data);
		fos.close();
	}

	@Override
	public ScratchEncoder<T> getEncoder() {
		return this.encoder;
	}

}
