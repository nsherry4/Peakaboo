package org.peakaboo.framework.scratch;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;
import org.peakaboo.framework.scratch.list.ScratchLists;

public class Benchmark {

	static List<Serializable> data = new ArrayList<>(); 
	static Class clazz;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println(Runtime.getRuntime().maxMemory() / 1024/1024);
		spectrum();

	}

	private static void spectrum() throws IOException {
		final int SPECTRUM_SIZE = 4096;
		final int SPECTRUM_COUNT = 10000;
		Random r = new Random();
		clazz = float[].class;
		float[] s = null;
		for (int i = 0; i < SPECTRUM_COUNT; i++) {
			s = new float[SPECTRUM_SIZE];
			for (int j = 0; j < SPECTRUM_SIZE; j++) {
				s[j] = j/10; //some repetition
			}
			data.add(s);
		}
		
		System.out.println(s);
		System.out.println(s[10]);
		
		System.out.println(Serializers.fstUnsafe(clazz).encode(s).length);
		System.out.println(Serializers.kryo(clazz).encode(s).length);
		
		System.out.println(Serializers.fstUnsafe(clazz).then(Compressors.snappy()).encode(s).length);
		System.out.println(Serializers.fstUnsafe(clazz).then(Compressors.lz4fast()).encode(s).length);
		System.out.println(Serializers.fstUnsafe(clazz).then(Compressors.lz4good()).encode(s).length);
		//roundtrip();
		encoder();
	}
	
	private static void string() throws IOException {

		clazz = String.class;
		List<String> words = Files.lines(new File("/usr/share/dict/american-english").toPath()).collect(Collectors.toList());
		for (String s1 : words.subList(0, 10)) {
			for (String s2 : words) {
				data.add(s1+s2);
			}
		}
		
		
		//roundtrip();
		encoder();
	}
	
	
	private static void encoder() throws IOException {
		bench_encoder(null);
		
		bench_encoder(Serializers.java());
		bench_encoder(Serializers.java().then(Compressors.snappy()));
		bench_encoder(Serializers.java().then(Compressors.lz4fast()));
		bench_encoder(Serializers.java().then(Compressors.lz4good()));
		
		bench_encoder(Serializers.kryo(clazz));
		bench_encoder(Serializers.kryo(clazz).then(Compressors.snappy()));
		bench_encoder(Serializers.kryo(clazz).then(Compressors.lz4fast()));
		bench_encoder(Serializers.kryo(clazz).then(Compressors.lz4good()));
		
		bench_encoder(Serializers.fstUnsafe(clazz));
		bench_encoder(Serializers.fstUnsafe(clazz).then(Compressors.snappy()));
		bench_encoder(Serializers.fstUnsafe(clazz).then(Compressors.lz4fast()));
		bench_encoder(Serializers.fstUnsafe(clazz).then(Compressors.lz4good()));
		
		bench_encoder(Serializers.fst(clazz));
		bench_encoder(Serializers.fst(clazz).then(Compressors.snappy()));
		bench_encoder(Serializers.fst(clazz).then(Compressors.lz4fast()));
		bench_encoder(Serializers.fst(clazz).then(Compressors.lz4good()));
	}

	private static void bench_encoder(ScratchEncoder<Serializable> encoder) {
		System.out.println("------------------------------");
		System.out.println(encoder);
		long t1, t2, d1, d2;

		//warm up
		for (Serializable word : data) {
			if (encoder != null) {
				encoder.encode(word);
			}
		}
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			for (Serializable word : data) {
				if (encoder != null) {
					encoder.encode(word);
				}
			}
		}
		t2 = System.currentTimeMillis();
		d1 = t2-t1;
		System.out.println("Encode: " + (d1));

		//warm up
		for (Serializable word : data) {
			if (encoder != null) {
				encoder.decode(encoder.encode(word));
			}
		}
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			for (Serializable word : data) {
				if (encoder != null) {
					encoder.decode(encoder.encode(word));
				}
			}
		}
		t2 = System.currentTimeMillis();
		d2 = t2-t1;
		System.out.println("Decode: " + (d2-d1));
	}
	
	
	
	
	private static void roundtrip() throws IOException {
		bench_roundtrip(null);
		
		bench_roundtrip(Serializers.kryo(clazz));
		bench_roundtrip(Serializers.kryo(clazz).then(Compressors.snappy()));
		bench_roundtrip(Serializers.kryo(clazz).then(Compressors.lz4fast()));
		bench_roundtrip(Serializers.kryo(clazz).then(Compressors.lz4good()));

		bench_roundtrip(Serializers.fstUnsafe(clazz));
		bench_roundtrip(Serializers.fstUnsafe(clazz).then(Compressors.snappy()));
		bench_roundtrip(Serializers.fstUnsafe(clazz).then(Compressors.lz4fast()));
		bench_roundtrip(Serializers.fstUnsafe(clazz).then(Compressors.lz4good()));
		
	}
	
	
	private static void bench_roundtrip(ScratchEncoder<Serializable> encoder) throws IOException {
		System.out.println("------------------------------");
		System.out.println(encoder);
		
		long w=0, r=0;
		for (int i = 0; i < 20; i++) {
			Result results = bench_roundtrip1(encoder);
			w += results.first;
			r += results.second;
		}
		
		System.out.println("Write: " + (w));
		System.out.println("Read:  " + (r));
		
	}
	
	private static Result bench_roundtrip1(ScratchEncoder<Serializable> encoder) throws IOException {
		
		List<Serializable> list;
		if (encoder == null) {
			list = new ArrayList<>();
		} else {
			list = ScratchLists.diskBacked(encoder);
		}
		
		long t1 = System.currentTimeMillis();
		
		list.addAll(data);
		
		long t2 = System.currentTimeMillis();
		
		for (Serializable word : list) {
			
		}
		
		long t3 = System.currentTimeMillis();
		
		Result r = new Result();
		r.first = t2-t1;
		r.second = t3-t2;
		return r;
		
	}
	
}

class Result {
	long first, second;
}
