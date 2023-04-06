package org.peakaboo.datasink.model.components.interaction;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

public class CallbackInteraction implements Interaction {

	private IntConsumer callbackScansWritten = i -> {};
	private BooleanSupplier callbackAbortRequested = () -> false;

	public void setCallbackScansWritten(IntConsumer callbackScansWritten) {
		this.callbackScansWritten = callbackScansWritten;
	}

	public void setCallbackAbortRequested(BooleanSupplier callbackAbortRequested) {
		this.callbackAbortRequested = callbackAbortRequested;
	}

	@Override
	public void notifyScanWritten(int count) {
		this.callbackScansWritten.accept(count);
	}

	@Override
	public boolean isAbortedRequested() {
		return this.callbackAbortRequested.getAsBoolean();
	}

}
