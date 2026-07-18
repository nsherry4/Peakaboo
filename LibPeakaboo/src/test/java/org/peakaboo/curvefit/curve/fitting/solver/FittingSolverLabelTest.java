package org.peakaboo.curvefit.curve.fitting.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.peakaboo.datalabel.DataLabel;

public class FittingSolverLabelTest {

	@Test
	public void testBuiltInSolversUnlabelled() {
		assertTrue(new GreedyFittingSolver().getDataLabels().isEmpty());
		assertTrue(new OptimizingFittingSolver().getDataLabels().isEmpty());
	}

	@Test
	public void testSolverCanMarkOutputSpeculative() {
		FittingSolver speculative = new GreedyFittingSolver() {
			@Override
			public List<DataLabel> getDataLabels() {
				return List.of(DataLabel.SPECULATIVE);
			}
		};
		assertEquals(List.of(DataLabel.SPECULATIVE), speculative.getDataLabels());
	}

}
