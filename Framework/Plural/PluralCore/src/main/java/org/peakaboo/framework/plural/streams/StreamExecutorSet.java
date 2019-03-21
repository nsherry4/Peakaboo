package org.peakaboo.framework.plural.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamExecutorSet<T> {

	private List<StreamExecutor<?>> executors;
	
	public StreamExecutorSet(StreamExecutor<?>... executors) {
		this(Arrays.asList(executors));
	}
	
	public StreamExecutorSet(List<StreamExecutor<?>> executors) {
		this.executors = new ArrayList<>(executors);
		
		//link executors together
		for (int i = 0; i < this.executors.size()-1; i++) {
			this.executors.get(i).then(this.executors.get(i+1));
		}
		
	}

	public List<StreamExecutor<?>> getExecutors() {
		return executors;
	}
	
	public void start() {
		this.executors.get(0).start();
	}

	public StreamExecutor<T> last() {
		return (StreamExecutor<T>) this.executors.get(this.executors.size()-1);
	}
	
	
	
}
