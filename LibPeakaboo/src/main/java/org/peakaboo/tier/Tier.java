package org.peakaboo.tier;

public class Tier {

	private static TierProvider provider;
	
	public static TierProvider provider() {
		if (provider == null) {
			 provider = new BasicTierProvider();
		}
		return provider;
	}
	
	public static void setProvider(TierProvider newProvider) {
		if (provider != null) {
			throw new RuntimeException("Cannot change Tier provider once initialized");
		}
		provider = newProvider;
	}
	
	
	
}
