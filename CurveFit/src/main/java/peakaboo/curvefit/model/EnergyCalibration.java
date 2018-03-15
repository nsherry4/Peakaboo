package peakaboo.curvefit.model;

public class EnergyCalibration {

	private float minEnergy, maxEnergy;
	private int dataWidth;
	
	public EnergyCalibration(float min, float max, int dataWidth) {
		this.minEnergy = min;
		this.maxEnergy = max;
		this.dataWidth = dataWidth;
	}
	
	
	
	public float getMinEnergy() {
		return minEnergy;
	}



	public void setMinEnergy(float minEnergy) {
		this.minEnergy = minEnergy;
	}



	public float getMaxEnergy() {
		return maxEnergy;
	}



	public void setMaxEnergy(float maxEnergy) {
		this.maxEnergy = maxEnergy;
	}



	public int getDataWidth() {
		return dataWidth;
	}



	public void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
	}



	/**
	 * Converts an energy value to a channel value
	 */
	public int channelFromEnergy(float energy) {
		int channel = Math.round((energy - minEnergy) / energyPerChannel());
		return channel;
	}

	/**
	 * Converts a channel value to an energy value
	 */
	public float energyFromChannel(int channel) {
		float energy = minEnergy + channel * energyPerChannel();
		return energy;
	}
	
	/**
	 * Converts an energy measurement into channel-scaled units. 
	 * This is used for relative measurements like peak width which 
	 * should not be scaled by minEnergy offset. 
	 */
	public float channelFromEnergyRelative(float energy) {
		
		int channel = Math.round(energy / energyPerChannel());
		return channel;
	}
	
	public float energyPerChannel() {
		float range = maxEnergy - minEnergy;
		float energyPerChannel = range / (float)dataWidth;
		return energyPerChannel;
	}
	
	
}
