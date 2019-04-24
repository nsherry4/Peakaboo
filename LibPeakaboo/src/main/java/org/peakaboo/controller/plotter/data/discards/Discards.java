package org.peakaboo.controller.plotter.data.discards;

import java.util.List;

public interface Discards {

	boolean isDiscarded(int scanNo);
	void discard(int scanNo);
	void undiscard(int scanNo);
	List<Integer> list();
	void clear();
	
}
