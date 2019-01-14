package peakaboo.curvefit.curve.fitting;

/**
 * Represents the energy calibration and data size for data. This is 
 * useful for converting between energy values and channel values. It
 * is also immutable.
 * @author NAS
 *
 */
public class EnergyCalibration {

	private float minEnergy, maxEnergy;
	private int dataWidth;
	
	private float energyPerChannel;
	private float[] channelEnergies;
	
	public EnergyCalibration(float min, float max, int dataWidth) {
		if (max < min) {
			throw new RuntimeException("Minimum energy cannot be greater than maximum energy");
		}
		this.minEnergy = min;
		this.maxEnergy = max;
		this.dataWidth = dataWidth;
		
		//calculate energy per channel
		float range = maxEnergy - minEnergy;
		energyPerChannel = range / (float)dataWidth;
		
		//calculate energy for channels
		channelEnergies = new float[dataWidth];
		for (int channel = 0; channel < dataWidth; channel++) {
			channelEnergies[channel] = calcEnergyFromChannel(channel);
		}
		
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


	public boolean isZero() {
		return (minEnergy == 0f && maxEnergy == 0f) || (maxEnergy - minEnergy <= 0f);
	}


	/**
	 * Converts an energy value to a channel value
	 */
	public int channelFromEnergy(float energy) {
		return Math.round(fractionalChannelFromEnergy(energy));
	}
	
	public float fractionalChannelFromEnergy(float energy) {
		return (energy - minEnergy) / energyPerChannel();
	}

	/**
	 * Converts a channel value to an energy value
	 */
	public float energyFromChannel(int channel) {
		if (channel < 0 || channel >= dataWidth) {
			//calculate it on the fly
			return calcEnergyFromChannel(channel);
		}
		return channelEnergies[channel];
	}
	
	public float calcEnergyFromChannel(int channel) {
		return minEnergy + channel * energyPerChannel;
	}
	
	
	public float energyPerChannel() {
		return energyPerChannel;
	}
	
	@Override
	public String toString() {
		return "EnergyCalibration(" + minEnergy + ", " + maxEnergy + ")";
	}
	
}
