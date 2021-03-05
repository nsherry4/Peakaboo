package org.peakaboo.controller.mapper.fitting.modes.components;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;

public class BinState extends AbstractState {

	private int bins;
	
	public BinState(ModeController mode) {
		super(mode);
		bins = 100;
	}

	public int getCount() {
		return bins;
	}

	public void setCount(int bins) {
		if (bins != this.bins) {
			this.bins = bins;
			mode.updateListeners();
		}
	}
	
}
