package peakaboo.curvefit.peak.escape;

import java.util.Collections;
import java.util.List;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.Transition;

public interface EscapePeak {
	
	boolean hasOffset();
	List<Transition> offset();
	float energyGap();
	float fanoFactor();
	String pretty();
	
	static float intensity(Element e)
	{
		/*
		 * The paper
		 * 
		 * " Measurement and calculation of escape peak intensities in synchrotron radiation X-ray fluorescence analysis
		 * S.X. Kang a, X. Sun a, X. Ju b, Y.Y. Huang b, K. Yao a, Z.Q. Wu a, D.C. Xian b"
		 * 
		 * provides a listing of escape peak intensities relative to the real peak by element. By taking this data into
		 * openoffice and fitting an exponential regression line to it, we arrive at the formula esc(z) = (543268.59
		 * z^-4.48)%
		 */

		return 543268.59f * (float) Math.pow((e.ordinal() + 1), -4.48) / 100.0f;
	}

	
}
