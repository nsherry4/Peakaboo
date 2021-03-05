package org.peakaboo.controller.mapper.fitting.modes.components;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;

public abstract class AbstractState {

	protected ModeController mode;
	
	public AbstractState(ModeController mode) {
		this.mode = mode;
	}
	
}
