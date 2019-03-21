package org.peakaboo.framework.plural.executor;

public class DummyExecutor extends AbstractExecutor<Void> {

	public DummyExecutor() {
		super();
	}
	
	public DummyExecutor(boolean stall) {
		super();
		super.setStalling(stall);
	}
	
	public DummyExecutor(int workunits) {
		super();
		super.setWorkUnits(workunits);
	}
	
	@Override
	protected void workForExecutor() {		
	}

	@Override
	public int getDataSize() {
		return 0;
	}
	
	public synchronized void advanceState()
	{
		super.advanceState();
	}
	
	public synchronized void setWorkUnits(int count)
	{
		super.setWorkUnits(count);
	}
	
	public synchronized void workUnitCompleted(int count)
	{
		super.workUnitCompleted(count);
	}
	
	public synchronized void workUnitCompleted()
	{
		super.workUnitCompleted();
	}

	@Override
	public Void executeBlocking() {
		return null;
	}

}
