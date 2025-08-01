package org.peakaboo.framework.scratch.encoders.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

public class JavaSerializingEncoder<T extends Serializable> implements ScratchEncoder<T> {


	@Override
	public byte[] encode(T element)
	{
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(baos);
			oos.writeObject(element);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}
	
	@Override
	public T decode(byte[] byteArray)
	{

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T)ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw new ScratchException(e);
		}
	}
	
	public String toString() {
		return "Java Serializer";
	}

	
}
