package cyclops;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class ISpectrumTests {

	private static final double EPSILON = 1e-15;
	
	@Test
	public void test() {
		
		Spectrum s1, s2;
		s1 = new ArraySpectrum(5);
		s1.add(0);
		s1.add(1);
		s1.add(2);
		s1.add(3);
		s1.add(4);
		
		s2 = new ArraySpectrum(5);
		
		s2.copy(s1);
		Assert.assertEquals(s1, s2);
		
		
		
		s2.zero();
		Assert.assertEquals(s2.sum(), 0f, EPSILON);
		Assert.assertEquals(s2.size(), 5);
		
		s2 = new ArraySpectrum(s1);
		Assert.assertEquals(s1, s2);
		
		s2 = new ArraySpectrum(new float[] {0, 1, 2, 3, 4});
		Assert.assertEquals(s1, s2);
		Assert.assertEquals(s2.subSpectrum(1, 3).sum(), 6f, EPSILON);
		
		s2 = new ArraySpectrum(5, 1f);
		Assert.assertEquals(s2.sum(), 5f, EPSILON);
		
	}
	
}
