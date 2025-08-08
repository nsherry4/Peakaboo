package org.peakaboo.controller.plotter.data.discards;

import java.util.List;

public interface Discards {

	boolean isDiscarded(int scanNo);
	void discard(int scanNo);
	void undiscard(int scanNo);
	default void setDiscarded(int scanNo, boolean discarded) {
		if (discarded) {
			discard(scanNo);
		} else {
			undiscard(scanNo);
		}
	}
	List<Integer> list();
	void clear();
	
}
