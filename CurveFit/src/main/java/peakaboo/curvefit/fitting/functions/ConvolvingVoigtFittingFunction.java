package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;
import peakaboo.curvefit.transition.Transition;

public class ConvolvingVoigtFittingFunction implements FittingFunction {

	private FittingContext context;
	
	private FittingFunction signal;

	
	@Override
	public void initialize(FittingContext context) {
		this.context = context;
		signal = new LorentzFittingFunction( ) {
			protected float calcGamma() {
				return context.getFWHM()/3.6f;
			}
		};
		signal.initialize(context);
	}

	@Override
	public float forEnergy(float energy) {
		//Create a lorentz fitting function centered around this energy value
		Transition fake = new Transition(energy, context.getTransition().relativeIntensity, "Fake Transition for Voigt Fitting Function");
		FittingContext copy = new FittingContext(context.getFittingParameters(), fake, context.getTransitionSeriesType());
		FittingFunction kernel = new GaussianFittingFunction() {
			protected float calcSigma() {
				return context.getFWHM()/3.6f;
			}
		};
		kernel.initialize(copy);
		
		float max = kernel.forEnergy(energy);
		float deltaEnergy = 0f;
		float sum = 0f;
		float kernelValue, signalValue;
		while (true) {
			//kernel is centered at `energy`, so we can take advantage of 
			//symmetry to only call it once for +/-
			kernelValue = kernel.forEnergy(energy + deltaEnergy);
			if (kernelValue < 0.01 * max) break;
			
			signalValue = signal.forEnergy(energy + deltaEnergy);
			sum += kernelValue * signalValue;
			
			signalValue = signal.forEnergy(energy - deltaEnergy);
			sum += kernelValue * signalValue;
			
			deltaEnergy += 0.01;
		}
		
		return sum;
	}
	
}
