package net.sciencestudio.libs.SciTypes;

import org.junit.Assert;
import org.junit.Test;

import cyclops.ISpectrum;
import cyclops.Spectrum;

public class ISpectrumTests {

	@Test
	public void test() {
		
		Spectrum s1, s2;
		s1 = new ISpectrum(5);
		s1.add(0);
		s1.add(1);
		s1.add(2);
		s1.add(3);
		s1.add(4);
		
		s2 = new ISpectrum(5);
		
		s2.copy(s1);
		Assert.assertEquals(s1, s2);
		
		
		
		s2.zero();
		Assert.assertEquals(s2.sum(), 0f);
		Assert.assertEquals(s2.size(), 5);
		
		s2 = new ISpectrum(s1);
		Assert.assertEquals(s1, s2);
		
		s2 = new ISpectrum(new float[] {0, 1, 2, 3, 4});
		Assert.assertEquals(s1, s2);
		Assert.assertEquals(s2.subSpectrum(1, 3).sum(), 6f);
		
		s2 = new ISpectrum(5, 1f);
		Assert.assertEquals(s2.sum(), 5f);
		
	}
	
}
