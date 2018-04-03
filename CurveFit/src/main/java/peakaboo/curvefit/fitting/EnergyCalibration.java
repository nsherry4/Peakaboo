package peakaboo.curvefit.fitting;

/**
 * Represents the energy calibration and data size for data. This is 
 * useful for converting between energy values and channel values. 
 * @author NAS
 *
 */
public class EnergyCalibration {

	private float minEnergy, maxEnergy;
	private int dataWidth;
	
	public EnergyCalibration(float min, float max, int dataWidth) {
		if (max < min) {
			throw new RuntimeException("Minimum energy cannot be greater than maximum energy");
		}
		this.minEnergy = min;
		this.maxEnergy = max;
		this.dataWidth = dataWidth;
	}
	
	
	
	public float getMinEnergy() {
		return minEnergy;
	}



	public float getMaxEnergy() {
		return maxEnergy;
	}




	public int getDataWidth() {
		return dataWidth;
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
	
	@Override
	public String toString() {
		return "EnergyCalibration(" + minEnergy + ", " + maxEnergy + ")";
	}
	
}
