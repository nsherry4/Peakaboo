package org.peakaboo.framework.cyclops.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.peakaboo.framework.cyclops.log.CyclopsLog;


public class StringInput implements Iterator<String>, Closeable{

	static String linebreak = "\r\n|[\n\r\u2028\u2029\u0085]";
	static String whitespace = "\\s+";
	
	static Pattern linebreakPattern = Pattern.compile(linebreak);
	static Pattern whitespacePattern = Pattern.compile(whitespace);
	
	private Iterator<String> backingIterator;
	private boolean isClosed = false; 
	

	private StringInput() {}

	private StringInput(File file, Pattern delim) throws FileNotFoundException {
		backingIterator = new Scanner(file).useDelimiter(delim);
	}
	
	private StringInput(Readable readable, Pattern delim) {
		backingIterator = new Scanner(readable).useDelimiter(delim);
	}
	
	private StringInput(InputStream instream, Pattern delim) {
		backingIterator = new Scanner(instream).useDelimiter(delim);
	}
	
	private StringInput(ReadableByteChannel channel, Pattern delim) {
		backingIterator = new Scanner(channel).useDelimiter(delim);
	}
	
	private StringInput(String source, Pattern delim) {
		backingIterator = new Scanner(source).useDelimiter(delim);
	}
	
	
	private StringInput(File file, String delim) throws FileNotFoundException {
		backingIterator = new Scanner(file).useDelimiter(delim);
	}
	
	private StringInput(Readable readable, String delim) {
		backingIterator = new Scanner(readable).useDelimiter(delim);
	}
	
	private StringInput(InputStream instream, String delim) {
		backingIterator = new Scanner(instream).useDelimiter(delim);
	}
	
	private StringInput(ReadableByteChannel channel, String delim) {
		backingIterator = new Scanner(channel).useDelimiter(delim);
	}
	
	private StringInput(String source, String delim) {
		backingIterator = new Scanner(source).useDelimiter(delim);
	}
	
	

	
	public static StringInput lines(File file) throws FileNotFoundException {
		StringInput f =  new StringInput();
		f.backingIterator = new LinesReader(file).iterator();
		return f;
	}
	
	public static StringInput lines(Readable readable) {
		StringInput f =  new StringInput();
		f.backingIterator = new LinesReader(readable).iterator();
		return f;
	}
	
	public static StringInput lines(InputStream instream) {
		StringInput f =  new StringInput();
		f.backingIterator = new LinesReader(instream).iterator();
		return f;
	}
	
	
	public static StringInput lines(String source) {
		StringInput f =  new StringInput();
		f.backingIterator = new LinesReader(source).iterator();
		return f;
	}
	
	
	
	
	public static StringInput words(File file) throws FileNotFoundException {
		StringInput f = new StringInput();
		f.backingIterator = new WordsReader(file).iterator();
		return f;
	}
	
	public static StringInput words(Readable readable) {
		StringInput f = new StringInput();
		f.backingIterator = new WordsReader(readable).iterator();
		return f;
	}
	
	public static StringInput words(InputStream instream) {
		StringInput f = new StringInput();
		f.backingIterator = new WordsReader(instream).iterator();
		return f;
	}
	
	
	public static StringInput words(String source) {
		StringInput f = new StringInput();
		f.backingIterator = new WordsReader(source).iterator();
		return f;
	}


	
	
	
	
	
	public static StringInput tokens(File file, String delim) throws FileNotFoundException {
		return new StringInput(file, Pattern.compile(delim));
	}
	
	public static StringInput tokens(Readable readable, String delim) { 
		return new StringInput(readable, Pattern.compile(delim));
	}
	
	public static StringInput tokens(InputStream instream, String delim) { 
		return new StringInput(instream, Pattern.compile(delim));
	}
	
	public static StringInput tokens(ReadableByteChannel channel, String delim) { 
		return new StringInput(channel, Pattern.compile(delim));
	}
	
	public static StringInput tokens(String source, String delim) { 
		return new StringInput(source, Pattern.compile(delim));
	}
	
	
	
	public static StringInput tokens(File file, Pattern delim) throws FileNotFoundException {
		return new StringInput(file, delim);
	}
	
	public static StringInput tokens(Readable readable, Pattern delim) { 
		return new StringInput(readable, delim);
	}
	
	public static StringInput tokens(InputStream instream, Pattern delim) { 
		return new StringInput(instream, delim);
	}
	
	public static StringInput tokens(ReadableByteChannel channel, Pattern delim) { 
		return new StringInput(channel, delim);
	}
	
	public static StringInput tokens(String source, Pattern delim) { 
		return new StringInput(source, delim);
	}
	
	
	
	
	public static String contents(File file) throws FileNotFoundException {
		return tokens(file, "\\Z").stream().findFirst().orElse("");
	}
	
	public static String contents(Readable readable) { 
		return tokens(readable, "\\Z").stream().findFirst().orElse("");
	}
	
	public static String contents(InputStream instream) { 
		return tokens(instream, "\\Z").stream().findFirst().orElse("");
	}
	
	public static String contents(ReadableByteChannel channel) { 
		return tokens(channel, "\\Z").stream().findFirst().orElse("");
	}
	
	public static String contents(String source) { 
		return tokens(source, "\\Z").stream().findFirst().orElse("");
	}
	
	

	
	@Override
	public void close() throws IOException{
		
		isClosed = true;
		
		if (backingIterator instanceof Scanner)
		{
			((Scanner)backingIterator).close();
			return;
		}
		if (backingIterator instanceof CustomReader)
		{
			((CustomReader)backingIterator).close();
		}
	}
	

	public static void test(boolean verbose, int times, String filename) throws FileNotFoundException
	{

		long t1, t2;
		
		File file = new File(filename);
		
		List<String> o1 = null;
		List<String> o2 = null;
		
		
		
		
		
		//LINES
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){ 
			
			o1 = StringInput.lines(file).stream().collect(Collectors.toList());
			
		}
		
				
		t2 = System.currentTimeMillis();
		if (verbose) System.out.println("Custom - Lines: " + (t2-t1) + "ms");
		
		
		
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){ 
			
			o2 = new StringInput(file, StringInput.linebreakPattern).stream().collect(Collectors.toList());
					
		}
		
		
		t2 = System.currentTimeMillis();
		if (verbose) System.out.println("Scanner - Lines: " + (t2-t1) + "ms");
		
	
		
		
		
		
		
		//WORDS
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){ 
			
			o1 = StringInput.words(file).stream().collect(Collectors.toList());
			
		}
		
		
		t2 = System.currentTimeMillis();
		if (verbose) System.out.println("Custom - Words: " + (t2-t1) + "ms");
		
		
		System.out.println(o1.size());
		
		
		
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){ 
			
			o2 = new StringInput(file, StringInput.whitespacePattern).stream().collect(Collectors.toList());
			
		}
		
		
		
		
		t2 = System.currentTimeMillis();
		if (verbose) System.out.println("Scanner - Words: " + (t2-t1) + "ms");

		
		System.out.println(o2.size());

		
	}
	
	
	public static void main(String[] args) throws IOException {
		
		
		//test(false, 1, "/home/nathaniel/Downloads/LocalTorrents/hugo/1953 - Alfred Bester - The Demolished Man (HTML).htm");
		test(true, 1, "/home/nathaniel/Downloads/LocalTorrents/hugo/1953 - Alfred Bester - The Demolished Man (HTML).htm");
		

		test(true, 1, "/home/nathaniel/Projects/Peakaboo Data/ScratchPlainText.txt");
		
	}

	@Override
	public boolean hasNext() {
		return backingIterator.hasNext();
	}

	@Override
	public String next() {
		return backingIterator.next();
	}

	@Override
	public void remove() {
		backingIterator.remove();
	}
	
	
	

	public Stream<String> stream() {
		Iterable<String> iterable = () -> backingIterator;
		return StreamSupport.stream(iterable.spliterator(), false);
	}	

	
}

interface CustomReader extends Iterable<String>, Closeable
{
	
}


class LinesReader implements CustomReader
{

	private LineNumberReader reader;
	
	public LinesReader(Reader r) {
		reader = new LineNumberReader(r);
	}
	
	public LinesReader(String s) {
		this(new StringReader(s));
	}
		
	public LinesReader(File f) throws FileNotFoundException {
		this(new FileReader(f));
	}
	
	public LinesReader(InputStream i) {
		this(new InputStreamReader(i));
	}
	
	public LinesReader(Readable r) {
		this(new ReadableReader(r));
	}
	
	@Override
	public Iterator<String> iterator() {
		
		return new Iterator<String>(){

			private boolean done = false;
			private String line = null;
			
			//hasnext guaranteed to make the next line available in 'line'
			//if it isn't already there
			@Override
			public boolean hasNext() {
				
				if (line != null) return true;
				if (done) {
					try {
						close();
					} catch (IOException e) {
						CyclopsLog.get().log(Level.WARNING, "Failed to close the file", e);
					}
					return false;
				}
				
				//so line is null
				try {
					line = reader.readLine();
					if (line == null) {
						done = true;
						try {
							close();
						} catch (IOException e) {
							CyclopsLog.get().log(Level.WARNING, "Failed to close the file", e);
						}
						return false;
					}
				} catch (IOException e) {
					//not really an error
					done = true;
					try {
						close();
					} catch (IOException e2) {
						CyclopsLog.get().log(Level.WARNING, "Failed to close the file", e2);
					}
					return false;
				}
				
				return true;
				
				
			}

			@Override
			public String next() {
				
				if (!hasNext()) throw new IndexOutOfBoundsException();
				
				String curLine = line;
				line = null;
				return curLine;
								
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
	}
	
	public void close() throws IOException{
		reader.close();
	}
	
}

class WordsReader implements CustomReader
{
	private LinesReader linesReader;
	private Iterator<String> linesIterator;

	public WordsReader(Reader r) {
		linesReader = new LinesReader(r);
		linesIterator = linesReader.iterator();
	}
	
	public WordsReader(String s) {
		this(new StringReader(s));
	}
	
	public WordsReader(File f) throws FileNotFoundException {
		this(new FileReader(f));
	}
	
	public WordsReader(InputStream i) {
		this(new InputStreamReader(i));
	}
	
	public WordsReader(Readable r) {
		this(new ReadableReader(r));
	}
	
	@Override
	public void close() throws IOException {
		linesReader.close();
	}

	@Override
	public Iterator<String> iterator() {
		
		return new Iterator<String>(){

			List<String> words = new ArrayList<>();
			int wordIndex = 0;
			
			@Override
			public boolean hasNext() {
				
				if (wordIndex < words.size()) return true;
				
				//words is empty
				//while word is empty, or only holding a blank line
				while (wordIndex >= words.size() || words.size() == 0 || (words.size() == 1 && words.get(0).equals(""))) {
					if (linesIterator.hasNext()){
						words = new ArrayList<>(Arrays.asList(linesIterator.next().trim().split(StringInput.whitespace)));
						wordIndex = 0;
					} else {
						try {
							close();
						} catch (IOException e) {
							CyclopsLog.get().log(Level.WARNING, "Failed to close the file", e);
						}
						return false;
					}
										
				}
				
				return true;
				
			}

			@Override
			public String next() {
				if (!hasNext()) throw new IndexOutOfBoundsException();
				return words.get(wordIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}};
		
	}
	
}

class ClosedInputException extends RuntimeException
{
	
}
