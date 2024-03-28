package org.peakaboo.controller.mapper.fitting.modes.components;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;

public abstract class AbstractState {

	protected ModeController mode;
	
	protected AbstractState(ModeController mode) {
		this.mode = mode;
	}
	
}
