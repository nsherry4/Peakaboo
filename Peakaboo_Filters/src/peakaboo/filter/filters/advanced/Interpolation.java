package peakaboo.filter.filters.advanced;

import peakaboo.common.Version;
import peakaboo.filter.filters.AbstractSimpleFilter;
import scitypes.Spectrum;
import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;

public class Interpolation extends AbstractSimpleFilter
{
	
	
	private Parameter<Integer> size;
	
	@Override
	public void initialize()
	{
		size = new Parameter<>("New Size", new IntegerEditor(), 2048);
		addParameter(size);
	}

	@Override
	public boolean validateParameters()
	{
		return size.getValue() > 10;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		Spectrum scaled = new Spectrum(size.getValue());
		for (int i = 0; i < size.getValue(); i++)
		{
			scaled.set(i, (float)calcNewPoint(data, size.getValue(), i));
		}
		
		return scaled;
	}

	private double calcNewPoint(Spectrum original, int newsize, int newindex)
	{

		//p = point, real-valued location in original spectrum
		//i = index, index for the data under a point
		//v = value, value at index
		//w = weight, amount of index in the window
		//f = full, indexes which are fully in the window
		

		//calculate how far into the spectrum the start of this window should be
		double percent = newindex / (double)newsize;
		double p0 = percent * original.size();
		
		//how far into the spectrum should the end of this window be
		double pointwidth = original.size() / newsize;
		double p1 = p0 + pointwidth;

		
		
		//get the values for the two points under the start and end of the window
		int i0 = (int)Math.floor(p0);
		int i1 = (int)Math.floor(p1);
		if (i1 == original.size()) i1 -= 1; //for the case at the very end where we go over the size by one
		double v0 = original.get(i0);
		double v1 = original.get(i1);
		
		double w0 = 1.0 - (p0 - i0); //1.0 minus the portion that is not in the window
		double w1 = p1 - i1;
		
		if (newsize > original.size())
		{

			//since the new size is larger than the old size, the pointwidht is less than 1, meaning the two points
			//under the start and end of the window are either the same, or adjacent.
			//calculate the weighting for d1 and d2 based on how much of the window is over each point		
			return v0*w0 + v1*w1;

		}
		else if (newsize < original.size())
		{
			//since the new size is smaller than the old size, there is the possibility that the window spans 3 
			//or more points. This means there is a possibility of losing some signal if we just look at the 
			//edge points. We have to calculate the average, and weight the two edge points by how much of them are
			//in the window
			
			//number of full indexes between i0 and i1 which we need to account for
			int f = i1 - i0 - 1;
			
			//if there are none
			if (f <= 0) 
			{
				//Just the two indexes, weighted
				return v0*w0 + v1*w1;
			}
			else
			{
				//sum the full indexes
				double sum = 0;
				for (int j = i0+1; j < i1; j++)
				{
					sum += original.get(j);
				}
				
				double totalweight = w0 + w1 + f;
				double wf = f / totalweight;
				w0 = w0 / totalweight;
				w1 = w1 / totalweight;
				
				return v0*w0 + v1*w1 + sum*wf;
				
			}
			
		}
		else
		{
			return original.get(newindex);
		}
	}
	
	

	@Override
	public boolean pluginEnabled()
	{
		return !Version.release;
	}

	@Override
	public String getFilterName()
	{
		return "Interpolation";
	}

	@Override
	public String getFilterDescription()
	{
		return "For spectra with few, widely spaced channels, this filter uses linear interpolation to " +
				"increase the number of points in the spectrum, making it easier to do curve fitting.";
	}

	@Override
	public FilterType getFilterType()
	{
		return FilterType.ADVANCED;
	}


	@Override
	public boolean canFilterSubset()
	{
		return false;
	}

}
